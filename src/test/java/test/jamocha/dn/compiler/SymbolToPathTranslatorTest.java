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
package test.jamocha.dn.compiler;

import org.jamocha.dn.ConstructCache.Defrule;
import org.jamocha.dn.ParserToNetwork;
import org.jamocha.dn.SideEffectFunctionToNetwork;
import org.jamocha.dn.compiler.ShallowFactVariableCollector;
import org.jamocha.filter.Path;
import org.jamocha.filter.SymbolCollector;
import org.jamocha.function.fwa.*;
import org.jamocha.function.fwatransformer.FWASymbolToPathTranslator;
import org.jamocha.languages.clips.parser.SFPToCETranslator;
import org.jamocha.languages.clips.parser.generated.ParseException;
import org.jamocha.languages.clips.parser.generated.SFPParser;
import org.jamocha.languages.clips.parser.generated.SFPStart;
import org.jamocha.languages.common.*;
import org.jamocha.languages.common.ConditionalElement.TemplatePatternConditionalElement;
import org.jamocha.languages.common.ConditionalElement.TestConditionalElement;
import org.jamocha.languages.common.RuleCondition.EquivalenceClass;
import org.jamocha.languages.common.ScopeStack.VariableSymbol;
import org.jamocha.languages.common.SingleFactVariable.SingleSlotVariable;
import org.junit.Test;
import test.jamocha.util.NetworkMockup;

import java.io.StringReader;
import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 */
public class SymbolToPathTranslatorTest {

    private static final String templateString = "(deftemplate templ1 (slot slot1 (type INTEGER)))\n" +
            "(deftemplate templ2 (slot slot1 (type INTEGER)))\n" + "(deftemplate templ3 (slot slot1 (type INTEGER)))" +
            "\n";
    private static final String preRule = "(defrule rule1 ";
    private static final String postRule = " => )\n";

    private static Queue<Warning> run(final SFPParser parser, final SFPToCETranslator visitor) throws ParseException {
        while (true) {
            final SFPStart n = parser.Start();
            if (n == null) return visitor.getWarnings();
            n.jjtAccept(visitor, null);
        }
    }

    private static <T extends SideEffectFunctionToNetwork & ParserToNetwork> RuleCondition clipsToCondition(final T ptn,
            final String condition) throws ParseException {
        final StringReader parserInput = new StringReader(
                new StringBuilder().append(templateString).append(preRule).append(condition).append(postRule)
                        .toString());
        final SFPParser parser = new SFPParser(parserInput);
        final SFPToCETranslator visitor = new SFPToCETranslator(ptn, ptn);
        run(parser, visitor);
        final Defrule rule = ptn.getRule("rule1");
        final RuleCondition ruleCondition = rule.getCondition();
        RuleConditionProcessor.flattenInPlace(ruleCondition);
        return ruleCondition;
    }

    @Test
    public void simpleTest() throws ParseException {
        final String input = "(and (templ1 (slot1 ?x)) (test (> ?x 10)))";
        final NetworkMockup ptn = new NetworkMockup();
        final RuleCondition ruleCondition = clipsToCondition(ptn, input);
        final List<ConditionalElement<SymbolLeaf>> conditionalElements = ruleCondition.getConditionalElements();
        assertThat(conditionalElements, hasSize(1));
        final ConditionalElement<SymbolLeaf> andCe = conditionalElements.get(0);
        final Map<EquivalenceClass, Path> ec2Path =
                ShallowFactVariableCollector.generatePaths(ptn.getInitialFactTemplate(), andCe).getRight();
        final Set<VariableSymbol> collectedSymbols = new SymbolCollector(ruleCondition).getNonDummySymbols();
        assertThat(collectedSymbols, hasSize(1));
        final VariableSymbol xSymbol = collectedSymbols.iterator().next();
        final LinkedList<SingleSlotVariable> xEqualSVs = xSymbol.getEqual().getSlotVariables();
        assertThat(xEqualSVs, hasSize(1));
        final Map<EquivalenceClass, PathLeaf> symbolToPathLeaf =
                Collections.singletonMap(xSymbol.getEqual(), xEqualSVs.get(0).getPathLeaf(ec2Path));

        final List<ConditionalElement<SymbolLeaf>> andChildren = andCe.getChildren();
        assertThat(andChildren, hasSize(2));
        final SingleFactVariable xFactVar;
        {
            final ConditionalElement<SymbolLeaf> templCe = andChildren.get(0);
            assertThat(templCe, instanceOf(TemplatePatternConditionalElement.class));
            xFactVar = ((TemplatePatternConditionalElement<SymbolLeaf>) templCe).getFactVariable();
        }
        {
            final ConditionalElement<SymbolLeaf> testCe = andChildren.get(1);
            assertThat(testCe, instanceOf(TestConditionalElement.class));
            final PredicateWithArguments<PathLeaf> translated = FWASymbolToPathTranslator
                    .translate(((TestConditionalElement<SymbolLeaf>) testCe).getPredicateWithArguments(),
                            symbolToPathLeaf);
            assertThat(translated, instanceOf(PredicateWithArgumentsComposite.class));
            final FunctionWithArguments<PathLeaf>[] args =
                    ((PredicateWithArgumentsComposite<PathLeaf>) translated).getArgs();
            assertThat(args, is(arrayWithSize(2)));
            final FunctionWithArguments<PathLeaf> x = args[0];
            assertThat(x, instanceOf(PathLeaf.class));
            final Path path = ((PathLeaf) x).getPath();
            assertEquals(path.getTemplate().getName(), "templ1");
            assertSame(path, ec2Path.get(xFactVar.getEqual()));
        }
    }

    @Test
    public void samePathInAndTest() throws ParseException {
        final String input = "(and (templ1 (slot1 ?x)) (test (> ?x 10)) (test (< ?x 20)))";
        final NetworkMockup ptn = new NetworkMockup();
        final RuleCondition ruleCondition = clipsToCondition(ptn, input);
        final List<ConditionalElement<SymbolLeaf>> conditionalElements = ruleCondition.getConditionalElements();
        assertThat(conditionalElements, hasSize(1));
        final ConditionalElement<SymbolLeaf> andCe = conditionalElements.get(0);
        final Map<EquivalenceClass, Path> ec2Path =
                ShallowFactVariableCollector.generatePaths(ptn.getInitialFactTemplate(), andCe).getRight();
        final Set<VariableSymbol> collectedSymbols = new SymbolCollector(ruleCondition).getNonDummySymbols();
        assertThat(collectedSymbols, hasSize(1));
        final VariableSymbol xSymbol = collectedSymbols.iterator().next();
        final LinkedList<SingleSlotVariable> xEqualSVs = xSymbol.getEqual().getSlotVariables();
        assertThat(xEqualSVs, hasSize(1));
        final Map<EquivalenceClass, PathLeaf> symbolToPathLeaf =
                Collections.singletonMap(xSymbol.getEqual(), xEqualSVs.get(0).getPathLeaf(ec2Path));

        final List<ConditionalElement<SymbolLeaf>> andChildren = andCe.getChildren();
        assertThat(andChildren, hasSize(3));
        final SingleFactVariable xFactVar;
        {
            final ConditionalElement<SymbolLeaf> templCe = andChildren.get(0);
            assertThat(templCe, instanceOf(TemplatePatternConditionalElement.class));
            xFactVar = ((TemplatePatternConditionalElement<SymbolLeaf>) templCe).getFactVariable();
        }
        {
            final ConditionalElement<SymbolLeaf> testCe = andChildren.get(1);
            assertThat(testCe, instanceOf(TestConditionalElement.class));
            final PredicateWithArguments<PathLeaf> translated = FWASymbolToPathTranslator
                    .translate(((TestConditionalElement<SymbolLeaf>) testCe).getPredicateWithArguments(),
                            symbolToPathLeaf);
            assertThat(translated, instanceOf(PredicateWithArgumentsComposite.class));
            final FunctionWithArguments<PathLeaf>[] args =
                    ((PredicateWithArgumentsComposite<PathLeaf>) translated).getArgs();
            assertThat(args, is(arrayWithSize(2)));
            final FunctionWithArguments<PathLeaf> x = args[0];
            assertThat(x, instanceOf(PathLeaf.class));
            final Path path = ((PathLeaf) x).getPath();
            assertEquals(path.getTemplate().getName(), "templ1");
            assertSame(path, ec2Path.get(xFactVar.getEqual()));
        }
        {
            final ConditionalElement<SymbolLeaf> testCe = andChildren.get(2);
            assertThat(testCe, instanceOf(TestConditionalElement.class));
            final PredicateWithArguments<PathLeaf> translated = FWASymbolToPathTranslator
                    .translate(((TestConditionalElement<SymbolLeaf>) testCe).getPredicateWithArguments(),
                            symbolToPathLeaf);
            assertThat(translated, instanceOf(PredicateWithArgumentsComposite.class));
            final FunctionWithArguments<PathLeaf>[] args =
                    ((PredicateWithArgumentsComposite<PathLeaf>) translated).getArgs();
            assertThat(args, is(arrayWithSize(2)));
            final FunctionWithArguments<PathLeaf> x = args[0];
            assertThat(x, instanceOf(PathLeaf.class));
            final Path path = ((PathLeaf) x).getPath();
            assertEquals(path.getTemplate().getName(), "templ1");
            assertSame(path, ec2Path.get(xFactVar.getEqual()));
        }
    }

    @Test
    public void differentPathsInOrTest() throws ParseException {
        final String input = "(and (templ1 (slot1 ?x)) (or (test (> ?x 10)) (test (< ?x 20))))";
        final NetworkMockup ptn = new NetworkMockup();
        final RuleCondition ruleCondition = clipsToCondition(ptn, input);
        final List<ConditionalElement<SymbolLeaf>> conditionalElements = ruleCondition.getConditionalElements();
        assertThat(conditionalElements, hasSize(1));
        final ConditionalElement<SymbolLeaf> orCe = conditionalElements.get(0);
        final List<ConditionalElement<SymbolLeaf>> orChildren = orCe.getChildren();
        assertThat(orChildren, hasSize(2));
        final Path firstPath;
        {
            final ConditionalElement<SymbolLeaf> andCe = orChildren.get(0);
            final Map<EquivalenceClass, Path> ec2Path =
                    ShallowFactVariableCollector.generatePaths(ptn.getInitialFactTemplate(), andCe).getRight();
            final Set<VariableSymbol> collectedSymbols = new SymbolCollector(ruleCondition).getNonDummySymbols();
            assertThat(collectedSymbols, hasSize(1));
            final VariableSymbol xSymbol = collectedSymbols.iterator().next();
            final LinkedList<SingleSlotVariable> xEqualSVs = xSymbol.getEqual().getSlotVariables();
            assertThat(xEqualSVs, hasSize(1));
            final Map<EquivalenceClass, PathLeaf> symbolToPathLeaf =
                    Collections.singletonMap(xSymbol.getEqual(), xEqualSVs.get(0).getPathLeaf(ec2Path));

            final List<ConditionalElement<SymbolLeaf>> andChildren = andCe.getChildren();
            assertThat(andChildren, hasSize(2));
            final SingleFactVariable xFactVar;
            {
                final ConditionalElement<SymbolLeaf> templCe = andChildren.get(0);
                assertThat(templCe, instanceOf(TemplatePatternConditionalElement.class));
                xFactVar = ((TemplatePatternConditionalElement<SymbolLeaf>) templCe).getFactVariable();
                assertThat(ec2Path, hasKey(xFactVar.getEqual()));
            }
            {
                final ConditionalElement<SymbolLeaf> testCe = andChildren.get(1);
                assertThat(testCe, instanceOf(TestConditionalElement.class));
                final PredicateWithArguments<PathLeaf> translated = FWASymbolToPathTranslator
                        .translate(((TestConditionalElement<SymbolLeaf>) testCe).getPredicateWithArguments(),
                                symbolToPathLeaf);
                assertThat(translated, instanceOf(PredicateWithArgumentsComposite.class));
                final FunctionWithArguments<PathLeaf>[] args =
                        ((PredicateWithArgumentsComposite<PathLeaf>) translated).getArgs();
                assertThat(args, is(arrayWithSize(2)));
                final FunctionWithArguments<PathLeaf> x = args[0];
                assertThat(x, instanceOf(PathLeaf.class));
                final Path path = ((PathLeaf) x).getPath();
                assertEquals(path.getTemplate().getName(), "templ1");
                assertSame(path, ec2Path.get(xFactVar.getEqual()));
                firstPath = path;
            }
        }
        {
            final ConditionalElement<SymbolLeaf> andCe = orChildren.get(1);
            final Map<EquivalenceClass, Path> ec2Path =
                    ShallowFactVariableCollector.generatePaths(ptn.getInitialFactTemplate(), andCe).getRight();
            final Set<VariableSymbol> collectedSymbols = new SymbolCollector(ruleCondition).getNonDummySymbols();
            assertThat(collectedSymbols, hasSize(1));
            final VariableSymbol xSymbol = collectedSymbols.iterator().next();
            final LinkedList<SingleSlotVariable> xEqualSVs = xSymbol.getEqual().getSlotVariables();
            assertThat(xEqualSVs, hasSize(1));
            final Map<EquivalenceClass, PathLeaf> symbolToPathLeaf =
                    Collections.singletonMap(xSymbol.getEqual(), xEqualSVs.get(0).getPathLeaf(ec2Path));

            final List<ConditionalElement<SymbolLeaf>> andChildren = andCe.getChildren();
            assertThat(andChildren, hasSize(2));
            final SingleFactVariable xFactVar;
            {
                final ConditionalElement<SymbolLeaf> templCe = andChildren.get(0);
                assertThat(templCe, instanceOf(TemplatePatternConditionalElement.class));
                xFactVar = ((TemplatePatternConditionalElement<SymbolLeaf>) templCe).getFactVariable();
                assertThat(ec2Path, hasKey(xFactVar.getEqual()));
            }
            {
                final ConditionalElement<SymbolLeaf> testCe = andChildren.get(1);
                assertThat(testCe, instanceOf(TestConditionalElement.class));
                final PredicateWithArguments<PathLeaf> translated = FWASymbolToPathTranslator
                        .translate(((TestConditionalElement<SymbolLeaf>) testCe).getPredicateWithArguments(),
                                symbolToPathLeaf);
                assertThat(translated, instanceOf(PredicateWithArgumentsComposite.class));
                final FunctionWithArguments<PathLeaf>[] args =
                        ((PredicateWithArgumentsComposite<PathLeaf>) translated).getArgs();
                assertThat(args, is(arrayWithSize(2)));
                final FunctionWithArguments<PathLeaf> x = args[0];
                assertThat(x, instanceOf(PathLeaf.class));
                final Path path = ((PathLeaf) x).getPath();
                assertEquals(path.getTemplate().getName(), "templ1");
                assertSame(path, ec2Path.get(xFactVar.getEqual()));
                assertNotSame(firstPath, path);
            }
        }
    }

    @Test(expected = Error.class)
    public void testCantUseFactVariableCollectorOnOrCE() throws ParseException {
        final String input = "(and (templ1 (slot1 ?x)) (or (test (> ?x 10)) (test (< ?x 20))))";
        final NetworkMockup ptn = new NetworkMockup();
        final RuleCondition ruleCondition = clipsToCondition(ptn, input);
        final List<ConditionalElement<SymbolLeaf>> conditionalElements = ruleCondition.getConditionalElements();
        assertThat(conditionalElements, hasSize(1));
        final ConditionalElement<SymbolLeaf> orCe = conditionalElements.get(0);
        ShallowFactVariableCollector.generatePaths(ptn.getInitialFactTemplate(), orCe).getRight();
    }
}
