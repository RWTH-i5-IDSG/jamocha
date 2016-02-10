/*
 * Copyright 2002-2016 The Jamocha Team
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.jamocha.org/
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the License.
 */
package test.jamocha.languages.common;

import org.jamocha.dn.ConstructCache.Defrule;
import org.jamocha.dn.memory.SlotType;
import org.jamocha.dn.memory.Template;
import org.jamocha.filter.SymbolCollector;
import org.jamocha.function.Predicate;
import org.jamocha.function.fwa.*;
import org.jamocha.function.impls.predicates.Equals;
import org.jamocha.languages.clips.parser.SFPToCETranslator;
import org.jamocha.languages.clips.parser.generated.ParseException;
import org.jamocha.languages.clips.parser.generated.SFPParser;
import org.jamocha.languages.clips.parser.generated.SFPStart;
import org.jamocha.languages.common.ConditionalElement;
import org.jamocha.languages.common.ConditionalElement.*;
import org.jamocha.languages.common.RuleCondition;
import org.jamocha.languages.common.RuleConditionProcessor;
import org.jamocha.languages.common.ScopeStack.VariableSymbol;
import org.jamocha.languages.common.SingleFactVariable.SingleSlotVariable;
import org.jamocha.languages.common.Warning;
import org.jamocha.logging.formatter.ConditionalElementFormatter;
import org.junit.Test;
import test.jamocha.util.NetworkMockup;
import test.jamocha.util.RegexMatcher;

import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 */
public class RuleConditionProcessorTest {

    private static final String TEMPLATE_STRING =
            "(deftemplate templ1 (slot slot1 (type INTEGER)))\n(deftemplate templ2 (slot slot1 (type INTEGER)))\n";
    private static final String PRE_RULE = "(defrule rule1 ";
    private static final String POST_RULE = " => )\n";

    private static Queue<Warning> run(final SFPParser parser, final SFPToCETranslator visitor) throws ParseException {
        while (true) {
            final SFPStart n = parser.Start();
            if (n == null) return visitor.getWarnings();
            n.jjtAccept(visitor, null);
        }
    }

    private static RuleCondition clipsToCondition(final String condition) throws ParseException {
        final StringReader parserInput = new StringReader(
                new StringBuilder().append(TEMPLATE_STRING).append(PRE_RULE).append(condition).append(POST_RULE)
                        .toString());
        final SFPParser parser = new SFPParser(parserInput);
        final NetworkMockup ptn = new NetworkMockup();
        final SFPToCETranslator visitor = new SFPToCETranslator(ptn, ptn);
        run(parser, visitor);
        final Defrule rule = ptn.getRule("rule1");
        return rule.getCondition();
    }

    @Test
    public void trivialTest() throws ParseException {
        final String input = "(templ1 (slot1 10))";
        final RuleCondition ruleCondition = clipsToCondition(input);
        final List<ConditionalElement<SymbolLeaf>> conditionalElements = ruleCondition.getConditionalElements();
        RuleConditionProcessor.flattenInPlace(ruleCondition.getConditionalElements());

        assertThat(conditionalElements, hasSize(1));
        final ConditionalElement<SymbolLeaf> andCE = conditionalElements.get(0);
        assertThat(andCE, instanceOf(AndFunctionConditionalElement.class));
        final List<ConditionalElement<SymbolLeaf>> andChildren = andCE.getChildren();
        assertThat(andChildren, hasSize(2));
        final ConditionalElement<SymbolLeaf> tpce = andChildren.get(0);
        assertThat(tpce, instanceOf(TemplatePatternConditionalElement.class));
        assertEquals("templ1",
                ((TemplatePatternConditionalElement<SymbolLeaf>) tpce).getFactVariable().getTemplate().getName());
        final ConditionalElement<SymbolLeaf> testce = andChildren.get(1);
        assertThat(testce, instanceOf(TestConditionalElement.class));
        final PredicateWithArguments<SymbolLeaf> fwa =
                ((TestConditionalElement<SymbolLeaf>) testce).getPredicateWithArguments();
        final Predicate predicate = ((PredicateWithArgumentsComposite<SymbolLeaf>) fwa).getFunction();
        assertEquals(Equals.IN_CLIPS, predicate.inClips());
        final FunctionWithArguments<SymbolLeaf>[] args = ((PredicateWithArgumentsComposite<SymbolLeaf>) fwa).getArgs();
        final FunctionWithArguments<SymbolLeaf> symbolLeaf = args[0];
        assertThat(symbolLeaf, instanceOf(SymbolLeaf.class));
        final VariableSymbol symbol = ((SymbolLeaf) symbolLeaf).getSymbol();
        final LinkedList<SingleSlotVariable> positiveSlotVariables = symbol.getEqual().getSlotVariables();
        assertThat(positiveSlotVariables, hasSize(1));
        final SingleSlotVariable singleSlotVariable = positiveSlotVariables.get(0);
        final Template slotTemplate = singleSlotVariable.getFactVariable().getTemplate();
        assertEquals("templ1", slotTemplate.getName());
        assertEquals("slot1", slotTemplate.getSlotName(singleSlotVariable.getSlot()));
        final FunctionWithArguments<SymbolLeaf> constantLeaf = args[1];
        assertThat(constantLeaf, instanceOf(ConstantLeaf.class));
        assertEquals(SlotType.LONG, ((ConstantLeaf<SymbolLeaf>) constantLeaf).getType());
        assertEquals(Long.valueOf(10), ((ConstantLeaf<SymbolLeaf>) constantLeaf).getValue());
    }

    @Test
    public void surroundingAddTest() throws ParseException {
        final String input = "(templ1 (slot1 ?x)) (test (> ?x 10)) (test (< ?x 15))";
        final RuleCondition ruleCondition = clipsToCondition(input);
        final List<ConditionalElement<SymbolLeaf>> conditionalElements = ruleCondition.getConditionalElements();

        assertThat(conditionalElements, hasSize(3));
        final ConditionalElement<SymbolLeaf> templ1 = conditionalElements.get(0);
        final ConditionalElement<SymbolLeaf> test10 = conditionalElements.get(1);
        final ConditionalElement<SymbolLeaf> test15 = conditionalElements.get(2);

        final ConditionalElementFormatter cef =
                new ConditionalElementFormatter(new SymbolCollector(ruleCondition).toSlotVariablesByFactVariable());

        assertThat(cef.format(templ1), RegexMatcher.matches("\\(template templ1 Dummy:\\d* \\(slot1 \\?x\\)\\)"));
        assertThat(cef.format(test10), RegexMatcher.matches("\\(test \\(> \\?x 10\\)\\)"));
        assertThat(cef.format(test15), RegexMatcher.matches("\\(test \\(< \\?x 15\\)\\)"));

        RuleConditionProcessor.flattenInPlace(ruleCondition);

        assertThat(conditionalElements, hasSize(1));
        final ConditionalElement<SymbolLeaf> andCE = conditionalElements.get(0);
        assertThat(andCE, instanceOf(AndFunctionConditionalElement.class));
        final List<ConditionalElement<SymbolLeaf>> andChildren = andCE.getChildren();
        assertThat(andChildren, hasSize(3));
        assertSame(templ1, andChildren.get(0));
        assertSame(test10, andChildren.get(1));
        assertSame(test15, andChildren.get(2));
    }

    @Test
    public void simpleUnexandableOr() throws ParseException {
        final String input = "(templ1 (slot1 ?x)) (or (test (> ?x 10)) (test (< ?x 15)))";
        final RuleCondition ruleCondition = clipsToCondition(input);
        final List<ConditionalElement<SymbolLeaf>> conditionalElements = ruleCondition.getConditionalElements();

        assertThat(conditionalElements, hasSize(2));
        final ConditionalElement<SymbolLeaf> templ1 = conditionalElements.get(0);
        final ConditionalElement<SymbolLeaf> or = conditionalElements.get(1);
        final List<ConditionalElement<SymbolLeaf>> orElements = or.getChildren();
        assertThat(orElements, hasSize(2));
        final ConditionalElement<SymbolLeaf> test10 = orElements.get(0);
        final ConditionalElement<SymbolLeaf> test15 = orElements.get(1);

        final ConditionalElementFormatter cef =
                new ConditionalElementFormatter(new SymbolCollector(ruleCondition).toSlotVariablesByFactVariable());

        assertThat(cef.format(templ1), RegexMatcher.matches("\\(template templ1 Dummy:\\d* \\(slot1 \\?x\\)\\)"));
        assertThat(cef.format(test10), RegexMatcher.matches("\\(test \\(> \\?x 10\\)\\)"));
        assertThat(cef.format(test15), RegexMatcher.matches("\\(test \\(< \\?x 15\\)\\)"));

        RuleConditionProcessor.flattenInPlace(conditionalElements);

        assertThat(conditionalElements, hasSize(1));
        final ConditionalElement<SymbolLeaf> orCE = conditionalElements.get(0);
        assertThat(orCE, instanceOf(OrFunctionConditionalElement.class));
        final List<ConditionalElement<SymbolLeaf>> orChildren = orCE.getChildren();
        assertThat(orChildren, hasSize(2));
        final ConditionalElement<SymbolLeaf> sharedCE;
        {
            final ConditionalElement<SymbolLeaf> andCE = orChildren.get(0);
            final List<ConditionalElement<SymbolLeaf>> andChildren = andCE.getChildren();
            assertThat(andChildren, hasSize(2));
            final ConditionalElement<SymbolLeaf> sharedTempl1 = andChildren.get(0);
            sharedCE = sharedTempl1;
            assertSame(templ1, sharedTempl1);
            assertSame(test10, andChildren.get(1));
        }
        {
            final ConditionalElement<SymbolLeaf> andCE = orChildren.get(1);
            final List<ConditionalElement<SymbolLeaf>> andChildren = andCE.getChildren();
            assertThat(andChildren, hasSize(2));
            final ConditionalElement<SymbolLeaf> sharedTempl1 = andChildren.get(0);
            assertSame(sharedCE, sharedTempl1);
            assertSame(test15, andChildren.get(1));
        }
    }

    @Test
    public void simpleExpandableOr() throws ParseException {
        final String input = "(templ1 (slot1 ?x)) (or (test (> ?x 10)) (test (< ?x 15))) (test (< ?x 16))";
        final RuleCondition ruleCondition = clipsToCondition(input);
        final List<ConditionalElement<SymbolLeaf>> conditionalElements = ruleCondition.getConditionalElements();

        assertThat(conditionalElements, hasSize(3));
        final ConditionalElement<SymbolLeaf> templ1 = conditionalElements.get(0);
        final ConditionalElement<SymbolLeaf> test16 = conditionalElements.get(2);
        final ConditionalElement<SymbolLeaf> or = conditionalElements.get(1);
        final List<ConditionalElement<SymbolLeaf>> orElements = or.getChildren();
        assertThat(orElements, hasSize(2));
        final ConditionalElement<SymbolLeaf> test10 = orElements.get(0);
        final ConditionalElement<SymbolLeaf> test15 = orElements.get(1);

        final ConditionalElementFormatter cef =
                new ConditionalElementFormatter(new SymbolCollector(ruleCondition).toSlotVariablesByFactVariable());

        assertThat(cef.format(templ1), RegexMatcher.matches("\\(template templ1 Dummy:\\d* \\(slot1 \\?x\\)\\)"));
        assertThat(cef.format(test10), RegexMatcher.matches("\\(test \\(> \\?x 10\\)\\)"));
        assertThat(cef.format(test15), RegexMatcher.matches("\\(test \\(< \\?x 15\\)\\)"));
        assertThat(cef.format(test16), RegexMatcher.matches("\\(test \\(< \\?x 16\\)\\)"));

        RuleConditionProcessor.flattenInPlace(conditionalElements);

        assertThat(conditionalElements, hasSize(1));
        final ConditionalElement<SymbolLeaf> orCE = conditionalElements.get(0);
        assertThat(orCE, instanceOf(OrFunctionConditionalElement.class));
        final List<ConditionalElement<SymbolLeaf>> orChildren = orCE.getChildren();
        assertThat(orChildren, hasSize(2));
        final ConditionalElement<SymbolLeaf> sharedCE;
        {
            final ConditionalElement<SymbolLeaf> andCE = orChildren.get(0);
            final List<ConditionalElement<SymbolLeaf>> andChildren = andCE.getChildren();
            assertThat(andChildren, hasSize(2));
            final ConditionalElement<SymbolLeaf> shared = andChildren.get(0);
            sharedCE = shared;
            assertThat(shared, instanceOf(AndFunctionConditionalElement.class));
            final List<ConditionalElement<SymbolLeaf>> sharedAndChildren = shared.getChildren();
            assertThat(sharedAndChildren, hasSize(2));
            assertSame(templ1, sharedAndChildren.get(0));
            assertSame(test16, sharedAndChildren.get(1));
            assertSame(test10, andChildren.get(1));
        }
        {
            final ConditionalElement<SymbolLeaf> andCE = orChildren.get(1);
            final List<ConditionalElement<SymbolLeaf>> andChildren = andCE.getChildren();
            assertThat(andChildren, hasSize(2));
            final ConditionalElement<SymbolLeaf> shared = andChildren.get(0);
            assertSame(sharedCE, shared);
            assertSame(test15, andChildren.get(1));
        }
    }

    @Test
    public void complexExpandableOr() throws ParseException {
        final String input =
                "(templ1 (slot1 ?x)) (or (test (< ?x 1)) (test (< ?x 2))) (or (test (< ?x 3)) (test (< ?x 4))) (test "
                        + "(< ?x 5)) (test (< ?x 6))";
        final RuleCondition ruleCondition = clipsToCondition(input);
        final List<ConditionalElement<SymbolLeaf>> conditionalElements = ruleCondition.getConditionalElements();

        assertThat(conditionalElements, hasSize(5));
        final ConditionalElement<SymbolLeaf> templ1 = conditionalElements.get(0);
        final ConditionalElement<SymbolLeaf> test1, test2, test3, test4;
        {
            final ConditionalElement<SymbolLeaf> orCE = conditionalElements.get(1);
            assertThat(orCE, instanceOf(OrFunctionConditionalElement.class));
            final List<ConditionalElement<SymbolLeaf>> orChildren = orCE.getChildren();
            assertThat(orChildren, hasSize(2));
            test1 = orChildren.get(0);
            test2 = orChildren.get(1);
        }
        {
            final ConditionalElement<SymbolLeaf> orCE = conditionalElements.get(2);
            assertThat(orCE, instanceOf(OrFunctionConditionalElement.class));
            final List<ConditionalElement<SymbolLeaf>> orChildren = orCE.getChildren();
            assertThat(orChildren, hasSize(2));
            test3 = orChildren.get(0);
            test4 = orChildren.get(1);
        }
        final ConditionalElement<SymbolLeaf> test5 = conditionalElements.get(3);
        final ConditionalElement<SymbolLeaf> test6 = conditionalElements.get(4);

        final ConditionalElementFormatter cef =
                new ConditionalElementFormatter(new SymbolCollector(ruleCondition).toSlotVariablesByFactVariable());

        assertThat(cef.format(templ1), RegexMatcher.matches("\\(template templ1 Dummy:\\d* \\(slot1 \\?x\\)\\)"));
        assertThat(cef.format(test1), RegexMatcher.matches("\\(test \\(< \\?x 1\\)\\)"));
        assertThat(cef.format(test2), RegexMatcher.matches("\\(test \\(< \\?x 2\\)\\)"));
        assertThat(cef.format(test3), RegexMatcher.matches("\\(test \\(< \\?x 3\\)\\)"));
        assertThat(cef.format(test4), RegexMatcher.matches("\\(test \\(< \\?x 4\\)\\)"));
        assertThat(cef.format(test5), RegexMatcher.matches("\\(test \\(< \\?x 5\\)\\)"));
        assertThat(cef.format(test6), RegexMatcher.matches("\\(test \\(< \\?x 6\\)\\)"));

        RuleConditionProcessor.flattenInPlace(conditionalElements);

        assertThat(conditionalElements, hasSize(1));
        final ConditionalElement<SymbolLeaf> orCE = conditionalElements.get(0);
        assertThat(orCE, instanceOf(OrFunctionConditionalElement.class));
        final List<ConditionalElement<SymbolLeaf>> orChildren = orCE.getChildren();
        assertThat(orChildren, hasSize(4));
        final ConditionalElement<SymbolLeaf> sharedAnd, sharedTest1, sharedTest2, sharedTest3, sharedTest4,
                sharedTest5,
                sharedTest6, sharedTest7, sharedTest8;
        {
            // 1 3
            final ConditionalElement<SymbolLeaf> andCE = orChildren.get(0);
            final List<ConditionalElement<SymbolLeaf>> andChildren = andCE.getChildren();
            assertThat(andChildren, hasSize(3));
            {
                sharedAnd = andChildren.get(0);
                assertThat(sharedAnd, instanceOf(AndFunctionConditionalElement.class));
                final List<ConditionalElement<SymbolLeaf>> sharedAndChildren = sharedAnd.getChildren();
                assertThat(sharedAndChildren, hasSize(3));
                assertSame(templ1, sharedAndChildren.get(0));
                assertSame(test5, sharedAndChildren.get(1));
                assertSame(test6, sharedAndChildren.get(2));
            }
            {
                sharedTest1 = andChildren.get(1);
            }
            {
                sharedTest2 = andChildren.get(2);
            }
        }
        {
            // 2 3
            final ConditionalElement<SymbolLeaf> andCE = orChildren.get(1);
            final List<ConditionalElement<SymbolLeaf>> andChildren = andCE.getChildren();
            assertThat(andChildren, hasSize(3));
            {
                final ConditionalElement<SymbolLeaf> shared = andChildren.get(0);
                assertSame(sharedAnd, shared);
            }
            {
                sharedTest3 = andChildren.get(1);
            }
            {
                sharedTest4 = andChildren.get(2);

            }
        }
        {
            // 1 4
            final ConditionalElement<SymbolLeaf> andCE = orChildren.get(2);
            final List<ConditionalElement<SymbolLeaf>> andChildren = andCE.getChildren();
            assertThat(andChildren, hasSize(3));
            {
                final ConditionalElement<SymbolLeaf> shared = andChildren.get(0);
                assertSame(sharedAnd, shared);
            }
            {
                sharedTest5 = andChildren.get(1);

            }
            {
                sharedTest6 = andChildren.get(2);
            }
        }
        {
            // 2 4
            final ConditionalElement<SymbolLeaf> andCE = orChildren.get(3);
            final List<ConditionalElement<SymbolLeaf>> andChildren = andCE.getChildren();
            assertThat(andChildren, hasSize(3));
            {
                final ConditionalElement<SymbolLeaf> shared = andChildren.get(0);
                assertSame(sharedAnd, shared);
            }
            {
                sharedTest7 = andChildren.get(1);
            }
            {
                sharedTest8 = andChildren.get(2);
            }
        }
        final List<ConditionalElement<SymbolLeaf>> sharedTests =
                Stream.of(sharedTest1, sharedTest2, sharedTest3, sharedTest4, sharedTest5, sharedTest6, sharedTest7,
                        sharedTest8).collect(toCollection(LinkedList::new));
        final List<ConditionalElement<SymbolLeaf>> tests =
                Stream.of(test1, test2, test3, test4).collect(toCollection(LinkedList::new));
        assertThat(sharedTests, hasSize(8));
        assertThat(tests, hasSize(4));
        for (int i = 0; i < 4; ++i) {
            assertThat(sharedTests, not(empty()));
            // get one of the shared elements
            final ConditionalElement<SymbolLeaf> sharedTest = sharedTests.remove(sharedTests.size() - 1);
            // one more occurrence
            assertTrue(sharedTests.remove(sharedTest));
            // now there is no further one
            assertFalse(sharedTests.remove(sharedTest));
            // the shared CE is one of the relevant tests
            assertTrue(tests.remove(sharedTest));
            // now its gone
            assertFalse(tests.remove(sharedTest));
        }
        assertThat(sharedTests, empty());
        assertThat(sharedTests, empty());
    }

    @Test
    public void pullUpChildrenOfNegExists() throws ParseException {
        final String input = "(not (and (templ1) (templ1)))";
        final RuleCondition ruleCondition = clipsToCondition(input);
        final List<ConditionalElement<SymbolLeaf>> conditionalElements = ruleCondition.getConditionalElements();

        RuleConditionProcessor.flattenInPlace(conditionalElements);

        assertThat(conditionalElements, hasSize(1));
        final ConditionalElement<SymbolLeaf> tlAndCE = conditionalElements.get(0);
        assertThat(tlAndCE, instanceOf(AndFunctionConditionalElement.class));
        final List<ConditionalElement<SymbolLeaf>> tlCEs = tlAndCE.getChildren();
        assertThat(tlCEs, hasSize(2));
        {
            final ConditionalElement<SymbolLeaf> initial = tlCEs.get(0);
            assertThat(initial, instanceOf(InitialFactConditionalElement.class));
            final ConditionalElement<SymbolLeaf> notExistsCE = tlCEs.get(1);
            assertThat(notExistsCE, instanceOf(NegatedExistentialConditionalElement.class));
            final List<ConditionalElement<SymbolLeaf>> existentialChildren = notExistsCE.getChildren();
            assertThat(existentialChildren, hasSize(2));
            {
                final ConditionalElement<SymbolLeaf> templCE = existentialChildren.get(0);
                assertThat(templCE, instanceOf(TemplatePatternConditionalElement.class));
                assertEquals("templ1",
                        ((TemplatePatternConditionalElement<SymbolLeaf>) templCE).getFactVariable().getTemplate()
                                .getName());
            }
            {
                final ConditionalElement<SymbolLeaf> templCE = existentialChildren.get(1);
                assertThat(templCE, instanceOf(TemplatePatternConditionalElement.class));
                assertEquals("templ1",
                        ((TemplatePatternConditionalElement<SymbolLeaf>) templCE).getFactVariable().getTemplate()
                                .getName());
            }
        }
    }

    @Test
    public void pullUpChildrenOfExists() throws ParseException {
        final String input = "(exists (and (templ1) (templ1)))";
        final RuleCondition ruleCondition = clipsToCondition(input);
        final List<ConditionalElement<SymbolLeaf>> conditionalElements = ruleCondition.getConditionalElements();

        RuleConditionProcessor.flattenInPlace(conditionalElements);

        assertThat(conditionalElements, hasSize(1));
        final ConditionalElement<SymbolLeaf> tlAndCE = conditionalElements.get(0);
        assertThat(tlAndCE, instanceOf(AndFunctionConditionalElement.class));
        final List<ConditionalElement<SymbolLeaf>> tlCEs = tlAndCE.getChildren();
        assertThat(tlCEs, hasSize(2));
        {
            final ConditionalElement<SymbolLeaf> initial = tlCEs.get(0);
            assertThat(initial, instanceOf(InitialFactConditionalElement.class));
            final ConditionalElement<SymbolLeaf> existsCE = tlCEs.get(1);
            assertThat(existsCE, instanceOf(ExistentialConditionalElement.class));
            final List<ConditionalElement<SymbolLeaf>> existentialChildren = existsCE.getChildren();
            assertThat(existentialChildren, hasSize(2));
            {
                final ConditionalElement<SymbolLeaf> templCE = existentialChildren.get(0);
                assertThat(templCE, instanceOf(TemplatePatternConditionalElement.class));
                assertEquals("templ1",
                        ((TemplatePatternConditionalElement<SymbolLeaf>) templCE).getFactVariable().getTemplate()
                                .getName());
            }
            {
                final ConditionalElement<SymbolLeaf> templCE = existentialChildren.get(1);
                assertThat(templCE, instanceOf(TemplatePatternConditionalElement.class));
                assertEquals("templ1",
                        ((TemplatePatternConditionalElement<SymbolLeaf>) templCE).getFactVariable().getTemplate()
                                .getName());
            }
        }
    }

    @Test
    public void simpleOrWithinExists() throws ParseException {
        final String input = "(exists (or (templ1 (slot1 1)) (templ1 (slot1 2))))";
        final RuleCondition ruleCondition = clipsToCondition(input);
        final List<ConditionalElement<SymbolLeaf>> conditionalElements = ruleCondition.getConditionalElements();

        final ConditionalElement<SymbolLeaf> tpce1, tpce2, test1, test2;
        assertThat(conditionalElements, hasSize(2));
        {
            final ConditionalElement<SymbolLeaf> initial = conditionalElements.get(0);
            assertThat(initial, instanceOf(InitialFactConditionalElement.class));
            final ConditionalElement<SymbolLeaf> existsCE = conditionalElements.get(1);
            assertThat(existsCE, instanceOf(ExistentialConditionalElement.class));
            final List<ConditionalElement<SymbolLeaf>> existentialChildren = existsCE.getChildren();
            assertThat(existentialChildren, hasSize(1));
            final ConditionalElement<SymbolLeaf> orCE = existentialChildren.get(0);
            assertThat(orCE, instanceOf(OrFunctionConditionalElement.class));
            final List<ConditionalElement<SymbolLeaf>> orChildren = orCE.getChildren();
            assertThat(orChildren, hasSize(2));
            {
                final ConditionalElement<SymbolLeaf> andCE = orChildren.get(0);
                assertThat(andCE, instanceOf(AndFunctionConditionalElement.class));
                final List<ConditionalElement<SymbolLeaf>> andChildren = andCE.getChildren();
                assertThat(andChildren, hasSize(2));
                final ConditionalElement<SymbolLeaf> template = andChildren.get(0);
                assertThat(template, instanceOf(TemplatePatternConditionalElement.class));
                tpce1 = template;
                final ConditionalElement<SymbolLeaf> test = andChildren.get(1);
                assertThat(test, instanceOf(TestConditionalElement.class));
                test1 = test;
            }
            {
                final ConditionalElement<SymbolLeaf> andCE = orChildren.get(1);
                assertThat(andCE, instanceOf(AndFunctionConditionalElement.class));
                final List<ConditionalElement<SymbolLeaf>> andChildren = andCE.getChildren();
                assertThat(andChildren, hasSize(2));
                final ConditionalElement<SymbolLeaf> templ1 = andChildren.get(0);
                assertThat(templ1, instanceOf(TemplatePatternConditionalElement.class));
                tpce2 = templ1;
                final ConditionalElement<SymbolLeaf> test = andChildren.get(1);
                assertThat(test, instanceOf(TestConditionalElement.class));
                test2 = test;
            }
        }

        final ConditionalElementFormatter cef =
                new ConditionalElementFormatter(new SymbolCollector(ruleCondition).toSlotVariablesByFactVariable());

        assertThat(cef.format(tpce1), RegexMatcher.matches("\\(template templ1 Dummy:\\d* \\(slot1 Dummy:\\d*\\)\\)"));
        assertThat(cef.format(tpce2), RegexMatcher.matches("\\(template templ1 Dummy:\\d* \\(slot1 Dummy:\\d*\\)\\)"));
        assertThat(cef.format(test1), RegexMatcher.matches("\\(test \\(= Dummy:\\d* 1\\)\\)"));
        assertThat(cef.format(test2), RegexMatcher.matches("\\(test \\(= Dummy:\\d* 2\\)\\)"));

        RuleConditionProcessor.flattenInPlace(conditionalElements);

        assertThat(conditionalElements, hasSize(1));
        final ConditionalElement<SymbolLeaf> orCE = conditionalElements.get(0);
        assertThat(orCE, instanceOf(OrFunctionConditionalElement.class));
        final List<ConditionalElement<SymbolLeaf>> orChildren = orCE.getChildren();
        assertThat(orChildren, hasSize(2));
        final ConditionalElement<SymbolLeaf> shared;
        {
            final ConditionalElement<SymbolLeaf> exAndInit = orChildren.get(0);
            assertThat(exAndInit, instanceOf(AndFunctionConditionalElement.class));
            final List<ConditionalElement<SymbolLeaf>> exAndInitChildren = exAndInit.getChildren();
            assertThat(exAndInitChildren, hasSize(2));
            final ConditionalElement<SymbolLeaf> sharedInit = exAndInitChildren.get(0);
            assertThat(sharedInit, instanceOf(InitialFactConditionalElement.class));
            shared = sharedInit;
            final ConditionalElement<SymbolLeaf> existsCE = exAndInitChildren.get(1);
            assertThat(existsCE, instanceOf(ExistentialConditionalElement.class));
            final List<ConditionalElement<SymbolLeaf>> existsChildren = existsCE.getChildren();
            assertThat(existsChildren, hasSize(1));
            final ConditionalElement<SymbolLeaf> andCE = existsChildren.get(0);
            assertThat(andCE, instanceOf(AndFunctionConditionalElement.class));
            final List<ConditionalElement<SymbolLeaf>> andChildren = andCE.getChildren();
            assertThat(andChildren, hasSize(2));
            assertSame(tpce1, andChildren.get(0));
            assertSame(test1, andChildren.get(1));
        }
        {
            final ConditionalElement<SymbolLeaf> exAndInit = orChildren.get(1);
            assertThat(exAndInit, instanceOf(AndFunctionConditionalElement.class));
            final List<ConditionalElement<SymbolLeaf>> exAndInitChildren = exAndInit.getChildren();
            assertThat(exAndInitChildren, hasSize(2));
            final ConditionalElement<SymbolLeaf> sharedInit = exAndInitChildren.get(0);
            assertThat(sharedInit, instanceOf(InitialFactConditionalElement.class));
            assertSame(shared, sharedInit);
            final ConditionalElement<SymbolLeaf> existsCE = exAndInitChildren.get(1);
            assertThat(existsCE, instanceOf(ExistentialConditionalElement.class));
            final List<ConditionalElement<SymbolLeaf>> existsChildren = existsCE.getChildren();
            final ConditionalElement<SymbolLeaf> andCE = existsChildren.get(0);
            assertThat(andCE, instanceOf(AndFunctionConditionalElement.class));
            final List<ConditionalElement<SymbolLeaf>> andChildren = andCE.getChildren();
            assertThat(andChildren, hasSize(2));
            assertSame(tpce2, andChildren.get(0));
            assertSame(test2, andChildren.get(1));
        }
    }

    @SuppressWarnings("checkstyle:methodlength")
    @Test
    public void complexOrWithinExists() throws ParseException {
        final String input =
                "(templ1 (slot1 ?x)) (or (test (> ?x 1)) (test (< ?x 2)) (exists (and (or (templ1 (slot1 ?y)) (templ2"
                        + " (slot1 ?y)) ) (test (= ?x ?y)) )) )";
        final RuleCondition ruleCondition = clipsToCondition(input);
        final List<ConditionalElement<SymbolLeaf>> conditionalElements = ruleCondition.getConditionalElements();

        final ConditionalElement<SymbolLeaf> tpce1x, tpce1y, tpce2y, test1, test2, test3;
        assertThat(conditionalElements, hasSize(2));
        {
            {
                final ConditionalElement<SymbolLeaf> tpce = conditionalElements.get(0);
                assertThat(tpce, instanceOf(TemplatePatternConditionalElement.class));
                tpce1x = tpce;
            }
            final ConditionalElement<SymbolLeaf> outerOrCE = conditionalElements.get(1);
            assertThat(outerOrCE, instanceOf(OrFunctionConditionalElement.class));
            final List<ConditionalElement<SymbolLeaf>> outerOrChildren = outerOrCE.getChildren();
            assertThat(outerOrChildren, hasSize(3));
            {
                final ConditionalElement<SymbolLeaf> test = outerOrChildren.get(0);
                assertThat(test, instanceOf(TestConditionalElement.class));
                test1 = test;
            }
            {
                final ConditionalElement<SymbolLeaf> test = outerOrChildren.get(1);
                assertThat(test, instanceOf(TestConditionalElement.class));
                test2 = test;
            }
            {
                final ConditionalElement<SymbolLeaf> existsCE = outerOrChildren.get(2);
                assertThat(existsCE, instanceOf(ExistentialConditionalElement.class));
                final List<ConditionalElement<SymbolLeaf>> existsChildren = existsCE.getChildren();
                assertThat(existsChildren, hasSize(1));
                final ConditionalElement<SymbolLeaf> andCE = existsChildren.get(0);
                assertThat(andCE, instanceOf(AndFunctionConditionalElement.class));
                final List<ConditionalElement<SymbolLeaf>> andChildren = andCE.getChildren();
                assertThat(andChildren, hasSize(2));
                {
                    final ConditionalElement<SymbolLeaf> innerOrCE = andChildren.get(0);
                    assertThat(innerOrCE, instanceOf(OrFunctionConditionalElement.class));
                    final List<ConditionalElement<SymbolLeaf>> innerOrChildren = innerOrCE.getChildren();
                    assertThat(innerOrChildren, hasSize(2));
                    {
                        {
                            final ConditionalElement<SymbolLeaf> tpce = innerOrChildren.get(0);
                            assertThat(tpce, instanceOf(TemplatePatternConditionalElement.class));
                            tpce1y = tpce;
                        }
                        {
                            final ConditionalElement<SymbolLeaf> tpce = innerOrChildren.get(1);
                            assertThat(tpce, instanceOf(TemplatePatternConditionalElement.class));
                            tpce2y = tpce;
                        }
                    }
                }
                {
                    final ConditionalElement<SymbolLeaf> test = andChildren.get(1);
                    assertThat(test, instanceOf(TestConditionalElement.class));
                    test3 = test;
                }
            }
        }

        final ConditionalElementFormatter cef =
                new ConditionalElementFormatter(new SymbolCollector(ruleCondition).toSlotVariablesByFactVariable());

        assertThat(cef.format(tpce1x), RegexMatcher.matches("\\(template templ1 Dummy:\\d* \\(slot1 \\?x\\)\\)"));
        assertThat(cef.format(tpce1y), RegexMatcher.matches("\\(template templ1 Dummy:\\d* \\(slot1 \\?y\\)\\)"));
        assertThat(cef.format(tpce2y), RegexMatcher.matches("\\(template templ2 Dummy:\\d* \\(slot1 \\?y\\)\\)"));
        assertThat(cef.format(test1), RegexMatcher.matches("\\(test \\(> \\?x 1\\)\\)"));
        assertThat(cef.format(test2), RegexMatcher.matches("\\(test \\(< \\?x 2\\)\\)"));
        assertThat(cef.format(test3), RegexMatcher.matches("\\(test \\(= \\?x \\?y\\)\\)"));

        RuleConditionProcessor.flattenInPlace(conditionalElements);

        final ConditionalElement<SymbolLeaf> sharedTpce, sharedTest;

        assertThat(conditionalElements, hasSize(1));
        final ConditionalElement<SymbolLeaf> outerOrCE = conditionalElements.get(0);
        assertThat(outerOrCE, instanceOf(OrFunctionConditionalElement.class));
        final List<ConditionalElement<SymbolLeaf>> outerOrChildren = outerOrCE.getChildren();
        assertThat(outerOrChildren, hasSize(4));
        {
            final ConditionalElement<SymbolLeaf> outerAndCE = outerOrChildren.get(0);
            assertThat(outerAndCE, instanceOf(AndFunctionConditionalElement.class));
            final List<ConditionalElement<SymbolLeaf>> outerAndChildren = outerAndCE.getChildren();
            assertThat(outerAndChildren, hasSize(2));
            {
                final ConditionalElement<SymbolLeaf> sharedCE = outerAndChildren.get(0);
                sharedTpce = sharedCE;
                assertThat(sharedTpce, instanceOf(TemplatePatternConditionalElement.class));
                assertSame(tpce1x, sharedTpce);
            }
            {
                final ConditionalElement<SymbolLeaf> test = outerAndChildren.get(1);
                assertThat(test, instanceOf(TestConditionalElement.class));
                assertSame(test1, test);
            }
        }
        {
            final ConditionalElement<SymbolLeaf> outerAndCE = outerOrChildren.get(1);
            assertThat(outerAndCE, instanceOf(AndFunctionConditionalElement.class));
            final List<ConditionalElement<SymbolLeaf>> outerAndChildren = outerAndCE.getChildren();
            assertThat(outerAndChildren, hasSize(2));
            {
                final ConditionalElement<SymbolLeaf> sharedCE = outerAndChildren.get(0);
                assertSame(sharedTpce, sharedCE);
            }
            {
                final ConditionalElement<SymbolLeaf> test = outerAndChildren.get(1);
                assertThat(test, instanceOf(TestConditionalElement.class));
                assertSame(test2, test);
            }
        }
        {
            final ConditionalElement<SymbolLeaf> outerAndCE = outerOrChildren.get(2);
            assertThat(outerAndCE, instanceOf(AndFunctionConditionalElement.class));
            final List<ConditionalElement<SymbolLeaf>> outerAndChildren = outerAndCE.getChildren();
            assertThat(outerAndChildren, hasSize(2));
            {
                final ConditionalElement<SymbolLeaf> sharedCE = outerAndChildren.get(0);
                assertSame(sharedTpce, sharedCE);
            }
            {
                final ConditionalElement<SymbolLeaf> existsCE = outerAndChildren.get(1);
                assertThat(existsCE, instanceOf(ExistentialConditionalElement.class));
                final List<ConditionalElement<SymbolLeaf>> existsChildren = existsCE.getChildren();
                assertThat(existsChildren, hasSize(1));
                final ConditionalElement<SymbolLeaf> innerAndCE = existsChildren.get(0);
                assertThat(innerAndCE, instanceOf(AndFunctionConditionalElement.class));
                final List<ConditionalElement<SymbolLeaf>> innerAndChildren = innerAndCE.getChildren();
                assertThat(innerAndChildren, hasSize(2));
                {
                    final ConditionalElement<SymbolLeaf> shared = innerAndChildren.get(0);
                    sharedTest = shared;
                    final ConditionalElement<SymbolLeaf> test = sharedTest;
                    assertThat(test, instanceOf(TestConditionalElement.class));
                    assertSame(test3, test);
                }
                {
                    final ConditionalElement<SymbolLeaf> tpce = innerAndChildren.get(1);
                    assertThat(tpce, instanceOf(TemplatePatternConditionalElement.class));
                    assertSame(tpce1y, tpce);
                }
            }
        }
        {
            final ConditionalElement<SymbolLeaf> outerAndCE = outerOrChildren.get(2);
            assertThat(outerAndCE, instanceOf(AndFunctionConditionalElement.class));
            final List<ConditionalElement<SymbolLeaf>> outerAndChildren = outerAndCE.getChildren();
            assertThat(outerAndChildren, hasSize(2));
            {
                final ConditionalElement<SymbolLeaf> sharedCE = outerAndChildren.get(0);
                assertSame(sharedTpce, sharedCE);
            }
            {
                final ConditionalElement<SymbolLeaf> existsCE = outerAndChildren.get(1);
                assertThat(existsCE, instanceOf(ExistentialConditionalElement.class));
                final List<ConditionalElement<SymbolLeaf>> existsChildren = existsCE.getChildren();
                assertThat(existsChildren, hasSize(1));
                final ConditionalElement<SymbolLeaf> innerAndCE = existsChildren.get(0);
                assertThat(innerAndCE, instanceOf(AndFunctionConditionalElement.class));
                final List<ConditionalElement<SymbolLeaf>> innerAndChildren = innerAndCE.getChildren();
                assertThat(innerAndChildren, hasSize(2));
                {
                    final ConditionalElement<SymbolLeaf> shared = innerAndChildren.get(0);
                    assertSame(sharedTest, shared);
                }
                {
                    final ConditionalElement<SymbolLeaf> tpce = innerAndChildren.get(1);
                    assertThat(tpce, instanceOf(TemplatePatternConditionalElement.class));
                    assertSame(tpce1y, tpce);
                }
            }
        }
    }

    @SuppressWarnings("checkstyle:methodlength")
    @Test
    public void complexOrWithinNotExists() throws ParseException {
        final String input =
                "(templ1 (slot1 ?x)) (or (test (> ?x 1)) (test (< ?x 2)) (not (and (or (templ1 (slot1 ?y)) (templ2 "
                        + "(slot1 ?y)) ) (test (= ?x ?y)) )) )";
        final RuleCondition ruleCondition = clipsToCondition(input);
        final List<ConditionalElement<SymbolLeaf>> conditionalElements = ruleCondition.getConditionalElements();

        final ConditionalElement<SymbolLeaf> tpce1x, tpce1y, tpce2y, test1, test2, test3;
        assertThat(conditionalElements, hasSize(2));
        {
            {
                final ConditionalElement<SymbolLeaf> tpce = conditionalElements.get(0);
                assertThat(tpce, instanceOf(TemplatePatternConditionalElement.class));
                tpce1x = tpce;
            }
            final ConditionalElement<SymbolLeaf> outerOrCE = conditionalElements.get(1);
            assertThat(outerOrCE, instanceOf(OrFunctionConditionalElement.class));
            final List<ConditionalElement<SymbolLeaf>> outerOrChildren = outerOrCE.getChildren();
            assertThat(outerOrChildren, hasSize(3));
            {
                final ConditionalElement<SymbolLeaf> test = outerOrChildren.get(0);
                assertThat(test, instanceOf(TestConditionalElement.class));
                test1 = test;
            }
            {
                final ConditionalElement<SymbolLeaf> test = outerOrChildren.get(1);
                assertThat(test, instanceOf(TestConditionalElement.class));
                test2 = test;
            }
            {
                final ConditionalElement<SymbolLeaf> notCE = outerOrChildren.get(2);
                assertThat(notCE, instanceOf(NegatedExistentialConditionalElement.class));
                final List<ConditionalElement<SymbolLeaf>> notChildren = notCE.getChildren();
                assertThat(notChildren, hasSize(1));
                final ConditionalElement<SymbolLeaf> andCE = notChildren.get(0);
                assertThat(andCE, instanceOf(AndFunctionConditionalElement.class));
                final List<ConditionalElement<SymbolLeaf>> andChildren = andCE.getChildren();
                assertThat(andChildren, hasSize(2));
                {
                    final ConditionalElement<SymbolLeaf> innerOrCE = andChildren.get(0);
                    assertThat(innerOrCE, instanceOf(OrFunctionConditionalElement.class));
                    final List<ConditionalElement<SymbolLeaf>> innerOrChildren = innerOrCE.getChildren();
                    assertThat(innerOrChildren, hasSize(2));
                    {
                        {
                            final ConditionalElement<SymbolLeaf> tpce = innerOrChildren.get(0);
                            assertThat(tpce, instanceOf(TemplatePatternConditionalElement.class));
                            tpce1y = tpce;
                        }
                        {
                            final ConditionalElement<SymbolLeaf> tpce = innerOrChildren.get(1);
                            assertThat(tpce, instanceOf(TemplatePatternConditionalElement.class));
                            tpce2y = tpce;
                        }
                    }
                }
                {
                    final ConditionalElement<SymbolLeaf> test = andChildren.get(1);
                    assertThat(test, instanceOf(TestConditionalElement.class));
                    test3 = test;
                }
            }
        }

        final ConditionalElementFormatter cef =
                new ConditionalElementFormatter(new SymbolCollector(ruleCondition).toSlotVariablesByFactVariable());

        assertThat(cef.format(tpce1x), RegexMatcher.matches("\\(template templ1 Dummy:\\d* \\(slot1 \\?x\\)\\)"));
        assertThat(cef.format(tpce1y), RegexMatcher.matches("\\(template templ1 Dummy:\\d* \\(slot1 \\?y\\)\\)"));
        assertThat(cef.format(tpce2y), RegexMatcher.matches("\\(template templ2 Dummy:\\d* \\(slot1 \\?y\\)\\)"));
        assertThat(cef.format(test1), RegexMatcher.matches("\\(test \\(> \\?x 1\\)\\)"));
        assertThat(cef.format(test2), RegexMatcher.matches("\\(test \\(< \\?x 2\\)\\)"));
        assertThat(cef.format(test3), RegexMatcher.matches("\\(test \\(= \\?x \\?y\\)\\)"));

        RuleConditionProcessor.flattenInPlace(conditionalElements);

        final ConditionalElement<SymbolLeaf> sharedTpce, sharedTest;

        assertThat(conditionalElements, hasSize(1));
        final ConditionalElement<SymbolLeaf> outerOrCE = conditionalElements.get(0);
        assertThat(outerOrCE, instanceOf(OrFunctionConditionalElement.class));
        final List<ConditionalElement<SymbolLeaf>> outerOrChildren = outerOrCE.getChildren();
        assertThat(outerOrChildren, hasSize(3));
        {
            final ConditionalElement<SymbolLeaf> outerAndCE = outerOrChildren.get(0);
            assertThat(outerAndCE, instanceOf(AndFunctionConditionalElement.class));
            final List<ConditionalElement<SymbolLeaf>> outerAndChildren = outerAndCE.getChildren();
            assertThat(outerAndChildren, hasSize(2));
            {
                final ConditionalElement<SymbolLeaf> sharedCE = outerAndChildren.get(0);
                sharedTpce = sharedCE;
                final ConditionalElement<SymbolLeaf> tpce = sharedTpce;
                assertThat(tpce, instanceOf(TemplatePatternConditionalElement.class));
                assertSame(tpce1x, tpce);
            }
            {
                final ConditionalElement<SymbolLeaf> test = outerAndChildren.get(1);
                assertThat(test, instanceOf(TestConditionalElement.class));
                assertSame(test1, test);
            }
        }
        {
            final ConditionalElement<SymbolLeaf> outerAndCE = outerOrChildren.get(1);
            assertThat(outerAndCE, instanceOf(AndFunctionConditionalElement.class));
            final List<ConditionalElement<SymbolLeaf>> outerAndChildren = outerAndCE.getChildren();
            assertThat(outerAndChildren, hasSize(2));
            {
                final ConditionalElement<SymbolLeaf> sharedCE = outerAndChildren.get(0);
                assertSame(sharedTpce, sharedCE);
            }
            {
                final ConditionalElement<SymbolLeaf> test = outerAndChildren.get(1);
                assertThat(test, instanceOf(TestConditionalElement.class));
                assertSame(test2, test);
            }
        }
        {
            final ConditionalElement<SymbolLeaf> outerAndCE = outerOrChildren.get(2);
            assertThat(outerAndCE, instanceOf(AndFunctionConditionalElement.class));
            final List<ConditionalElement<SymbolLeaf>> outerAndChildren = outerAndCE.getChildren();
            assertThat(outerAndChildren, hasSize(2));
            {
                final ConditionalElement<SymbolLeaf> sharedCE = outerAndChildren.get(0);
                assertSame(sharedTpce, sharedCE);
            }
            {
                final ConditionalElement<SymbolLeaf> middleAndCE = outerAndChildren.get(1);
                assertThat(middleAndCE, instanceOf(AndFunctionConditionalElement.class));
                final List<ConditionalElement<SymbolLeaf>> middleAndChildren = middleAndCE.getChildren();
                assertThat(middleAndChildren, hasSize(2));
                {
                    final ConditionalElement<SymbolLeaf> notCE = middleAndChildren.get(0);
                    assertThat(notCE, instanceOf(NegatedExistentialConditionalElement.class));
                    final List<ConditionalElement<SymbolLeaf>> notChildren = notCE.getChildren();
                    assertThat(notChildren, hasSize(1));
                    final ConditionalElement<SymbolLeaf> innerAndCE = notChildren.get(0);
                    assertThat(innerAndCE, instanceOf(AndFunctionConditionalElement.class));
                    final List<ConditionalElement<SymbolLeaf>> innerAndChildren = innerAndCE.getChildren();
                    assertThat(innerAndChildren, hasSize(2));
                    {
                        final ConditionalElement<SymbolLeaf> shared = innerAndChildren.get(0);
                        sharedTest = shared;
                        final ConditionalElement<SymbolLeaf> test = sharedTest;
                        assertThat(test, instanceOf(TestConditionalElement.class));
                        assertSame(test3, test);
                    }
                    {
                        final ConditionalElement<SymbolLeaf> tpce = innerAndChildren.get(1);
                        assertThat(tpce, instanceOf(TemplatePatternConditionalElement.class));
                        assertSame(tpce1y, tpce);
                    }
                }
                {
                    final ConditionalElement<SymbolLeaf> notCE = middleAndChildren.get(1);
                    assertThat(notCE, instanceOf(NegatedExistentialConditionalElement.class));
                    final List<ConditionalElement<SymbolLeaf>> notChildren = notCE.getChildren();
                    assertThat(notChildren, hasSize(1));
                    final ConditionalElement<SymbolLeaf> innerAndCE = notChildren.get(0);
                    assertThat(innerAndCE, instanceOf(AndFunctionConditionalElement.class));
                    final List<ConditionalElement<SymbolLeaf>> innerAndChildren = innerAndCE.getChildren();
                    assertThat(innerAndChildren, hasSize(2));
                    {
                        final ConditionalElement<SymbolLeaf> shared = innerAndChildren.get(0);
                        assertSame(sharedTest, shared);
                    }
                    {
                        final ConditionalElement<SymbolLeaf> tpce = innerAndChildren.get(1);
                        assertThat(tpce, instanceOf(TemplatePatternConditionalElement.class));
                        assertSame(tpce2y, tpce);
                    }
                }
            }
        }
    }
}
