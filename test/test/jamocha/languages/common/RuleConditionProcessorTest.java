/*
 * Copyright 2002-2014 The Jamocha Team
 * 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.jamocha.org/
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package test.jamocha.languages.common;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.StringJoiner;

import org.jamocha.dn.ConstructCache.Defrule;
import org.jamocha.dn.memory.SlotType;
import org.jamocha.dn.memory.Template;
import org.jamocha.filter.SymbolCollector;
import org.jamocha.function.Predicate;
import org.jamocha.function.fwa.ConstantLeaf;
import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.function.fwa.PredicateWithArguments;
import org.jamocha.function.fwa.PredicateWithArgumentsComposite;
import org.jamocha.function.fwa.SymbolLeaf;
import org.jamocha.function.impls.predicates.Equals;
import org.jamocha.languages.clips.parser.SFPVisitorImpl;
import org.jamocha.languages.clips.parser.generated.ParseException;
import org.jamocha.languages.clips.parser.generated.SFPParser;
import org.jamocha.languages.clips.parser.generated.SFPStart;
import org.jamocha.languages.common.ConditionalElement;
import org.jamocha.languages.common.ConditionalElement.AndFunctionConditionalElement;
import org.jamocha.languages.common.ConditionalElement.ExistentialConditionalElement;
import org.jamocha.languages.common.ConditionalElement.InitialFactConditionalElement;
import org.jamocha.languages.common.ConditionalElement.OrFunctionConditionalElement;
import org.jamocha.languages.common.ConditionalElement.SharedConditionalElementWrapper;
import org.jamocha.languages.common.ConditionalElement.TemplatePatternConditionalElement;
import org.jamocha.languages.common.ConditionalElement.TestConditionalElement;
import org.jamocha.languages.common.RuleConditionProcessor;
import org.jamocha.languages.common.ScopeStack.Symbol;
import org.jamocha.languages.common.SingleFactVariable.SingleSlotVariable;
import org.jamocha.languages.common.Warning;
import org.jamocha.logging.formatter.ConditionalElementFormatter;
import org.junit.Test;

import test.jamocha.util.NetworkMockup;
import test.jamocha.util.RegexMatcher;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 */
public class RuleConditionProcessorTest {

	private static String ruleName = "rule1";
	private static String templateName = "templ1";
	private static String slot1Name = "slot1";

	private static String templateString = "(deftemplate " + templateName + " (slot " + slot1Name
			+ " (type INTEGER)))\n";
	private static String preRule = "(defrule " + ruleName;
	private static String postRule = "=> )\n";

	private static Queue<Warning> run(final SFPParser parser, final SFPVisitorImpl visitor)
			throws ParseException {
		while (true) {
			final SFPStart n = parser.Start();
			if (n == null)
				return visitor.getWarnings();
			n.jjtAccept(visitor, null);
		}
	}

	private static List<ConditionalElement> clipsToCondition(final String condition)
			throws ParseException {
		final StringReader parserInput =
				new StringReader(new StringJoiner(" ").add(templateString).add(preRule)
						.add(condition).add(postRule).toString());
		final SFPParser parser = new SFPParser(parserInput);
		final NetworkMockup ptn = new NetworkMockup();
		final SFPVisitorImpl visitor = new SFPVisitorImpl(ptn, ptn);
		run(parser, visitor);
		final Defrule rule = ptn.getRule(ruleName);
		return rule.getCondition().getConditionalElements();
	}

	@Test
	public void trivialTest() throws ParseException {
		final String input = "(" + templateName + " (" + slot1Name + " 10))";
		final List<ConditionalElement> conditionalElements = clipsToCondition(input);
		RuleConditionProcessor.flatten(conditionalElements);

		assertThat(conditionalElements, hasSize(1));
		final ConditionalElement andCE = conditionalElements.get(0);
		assertThat(andCE, instanceOf(AndFunctionConditionalElement.class));
		final List<ConditionalElement> andChildren = andCE.getChildren();
		assertThat(andChildren, hasSize(2));
		final ConditionalElement tpce = andChildren.get(0);
		assertThat(tpce, instanceOf(TemplatePatternConditionalElement.class));
		assertEquals(templateName, ((TemplatePatternConditionalElement) tpce).getFactVariable()
				.getTemplate().getName());
		final ConditionalElement testce = andChildren.get(1);
		assertThat(testce, instanceOf(TestConditionalElement.class));
		final PredicateWithArguments fwa =
				((TestConditionalElement) testce).getPredicateWithArguments();
		final Predicate predicate = ((PredicateWithArgumentsComposite) fwa).getFunction();
		assertEquals(Equals.inClips, predicate.inClips());
		final FunctionWithArguments[] args = ((PredicateWithArgumentsComposite) fwa).getArgs();
		final FunctionWithArguments symbolLeaf = args[0];
		assertThat(symbolLeaf, instanceOf(SymbolLeaf.class));
		final Symbol symbol = ((SymbolLeaf) symbolLeaf).getSymbol();
		final ArrayList<SingleSlotVariable> positiveSlotVariables =
				symbol.getPositiveSlotVariables();
		assertThat(positiveSlotVariables, hasSize(1));
		final SingleSlotVariable singleSlotVariable = positiveSlotVariables.get(0);
		final Template slotTemplate = singleSlotVariable.getFactVariable().getTemplate();
		assertEquals(templateName, slotTemplate.getName());
		assertEquals(slot1Name, slotTemplate.getSlotName(singleSlotVariable.getSlot()));
		final FunctionWithArguments constantLeaf = args[1];
		assertThat(constantLeaf, instanceOf(ConstantLeaf.class));
		assertEquals(SlotType.LONG, ((ConstantLeaf) constantLeaf).getType());
		assertEquals(Long.valueOf(10), ((ConstantLeaf) constantLeaf).getValue());
	}

	@Test
	public void surroundingAddTest() throws ParseException {
		final String input =
				"(" + templateName + " (" + slot1Name + " ?x)) (test (> ?x 10)) (test (< ?x 15))";
		final List<ConditionalElement> conditionalElements = clipsToCondition(input);

		assertThat(conditionalElements, hasSize(3));
		final ConditionalElement templ1 = conditionalElements.get(0);
		final ConditionalElement test10 = conditionalElements.get(1);
		final ConditionalElement test15 = conditionalElements.get(2);

		final ConditionalElementFormatter cef =
				new ConditionalElementFormatter(SymbolCollector.newHashSet()
						.collect(conditionalElements).toSlotVariablesByFactVariable());

		assertThat(
				cef.format(templ1),
				RegexMatcher.matches("\\(template " + templateName + " Dummy:\\d* \\(" + slot1Name
						+ " \\?x\\)\\)"));
		assertThat(cef.format(test10), RegexMatcher.matches("\\(test \\(> \\?x 10\\)\\)"));
		assertThat(cef.format(test15), RegexMatcher.matches("\\(test \\(< \\?x 15\\)\\)"));

		RuleConditionProcessor.flatten(conditionalElements);

		assertThat(conditionalElements, hasSize(1));
		final ConditionalElement andCE = conditionalElements.get(0);
		assertThat(andCE, instanceOf(AndFunctionConditionalElement.class));
		final List<ConditionalElement> andChildren = andCE.getChildren();
		assertThat(andChildren, hasSize(3));
		assertSame(templ1, andChildren.get(0));
		assertSame(test10, andChildren.get(1));
		assertSame(test15, andChildren.get(2));
	}

	@Test
	public void simpleUnexandableOr() throws ParseException {
		final String input = "(templ1 (slot1 ?x)) (or (test (> ?x 10)) (test (< ?x 15)))";
		final List<ConditionalElement> conditionalElements = clipsToCondition(input);

		assertThat(conditionalElements, hasSize(2));
		final ConditionalElement templ1 = conditionalElements.get(0);
		final ConditionalElement or = conditionalElements.get(1);
		final List<ConditionalElement> orElements = or.getChildren();
		assertThat(orElements, hasSize(2));
		final ConditionalElement test10 = orElements.get(0);
		final ConditionalElement test15 = orElements.get(1);

		final ConditionalElementFormatter cef =
				new ConditionalElementFormatter(SymbolCollector.newHashSet()
						.collect(conditionalElements).toSlotVariablesByFactVariable());

		assertThat(
				cef.format(templ1),
				RegexMatcher.matches("\\(template " + templateName + " Dummy:\\d* \\(" + slot1Name
						+ " \\?x\\)\\)"));
		assertThat(cef.format(test10), RegexMatcher.matches("\\(test \\(> \\?x 10\\)\\)"));
		assertThat(cef.format(test15), RegexMatcher.matches("\\(test \\(< \\?x 15\\)\\)"));

		RuleConditionProcessor.flatten(conditionalElements);

		assertThat(conditionalElements, hasSize(1));
		final ConditionalElement orCE = conditionalElements.get(0);
		assertThat(orCE, instanceOf(OrFunctionConditionalElement.class));
		final List<ConditionalElement> orChildren = orCE.getChildren();
		assertThat(orChildren, hasSize(2));
		final ConditionalElement sharedCE;
		{
			final ConditionalElement andCE = orChildren.get(0);
			final List<ConditionalElement> andChildren = andCE.getChildren();
			assertThat(andChildren, hasSize(2));
			final ConditionalElement sharedTempl1 = andChildren.get(0);
			assertThat(sharedTempl1, instanceOf(SharedConditionalElementWrapper.class));
			sharedCE = sharedTempl1;
			assertSame(templ1, ((SharedConditionalElementWrapper) sharedTempl1).getCe());
			assertSame(test10, andChildren.get(1));
		}
		{
			final ConditionalElement andCE = orChildren.get(1);
			final List<ConditionalElement> andChildren = andCE.getChildren();
			assertThat(andChildren, hasSize(2));
			final ConditionalElement sharedTempl1 = andChildren.get(0);
			assertThat(sharedTempl1, instanceOf(SharedConditionalElementWrapper.class));
			assertSame(sharedCE, sharedTempl1);
			assertSame(test15, andChildren.get(1));
		}
	}

	@Test
	public void simpleExpandableOr() throws ParseException {
		final String input =
				"(templ1 (slot1 ?x)) (or (test (> ?x 10)) (test (< ?x 15))) (test (< ?x 16))";
		final List<ConditionalElement> conditionalElements = clipsToCondition(input);

		assertThat(conditionalElements, hasSize(3));
		final ConditionalElement templ1 = conditionalElements.get(0);
		final ConditionalElement test16 = conditionalElements.get(2);
		final ConditionalElement or = conditionalElements.get(1);
		final List<ConditionalElement> orElements = or.getChildren();
		assertThat(orElements, hasSize(2));
		final ConditionalElement test10 = orElements.get(0);
		final ConditionalElement test15 = orElements.get(1);

		final ConditionalElementFormatter cef =
				new ConditionalElementFormatter(SymbolCollector.newHashSet()
						.collect(conditionalElements).toSlotVariablesByFactVariable());

		assertThat(
				cef.format(templ1),
				RegexMatcher.matches("\\(template " + templateName + " Dummy:\\d* \\(" + slot1Name
						+ " \\?x\\)\\)"));
		assertThat(cef.format(test10), RegexMatcher.matches("\\(test \\(> \\?x 10\\)\\)"));
		assertThat(cef.format(test15), RegexMatcher.matches("\\(test \\(< \\?x 15\\)\\)"));
		assertThat(cef.format(test16), RegexMatcher.matches("\\(test \\(< \\?x 16\\)\\)"));

		RuleConditionProcessor.flatten(conditionalElements);

		assertThat(conditionalElements, hasSize(1));
		final ConditionalElement orCE = conditionalElements.get(0);
		assertThat(orCE, instanceOf(OrFunctionConditionalElement.class));
		final List<ConditionalElement> orChildren = orCE.getChildren();
		assertThat(orChildren, hasSize(2));
		final ConditionalElement sharedCE;
		{
			final ConditionalElement andCE = orChildren.get(0);
			final List<ConditionalElement> andChildren = andCE.getChildren();
			assertThat(andChildren, hasSize(2));
			final ConditionalElement shared = andChildren.get(0);
			assertThat(shared, instanceOf(SharedConditionalElementWrapper.class));
			sharedCE = shared;
			final ConditionalElement sharedAnd = ((SharedConditionalElementWrapper) shared).getCe();
			assertThat(sharedAnd, instanceOf(AndFunctionConditionalElement.class));
			final List<ConditionalElement> sharedAndChildren = sharedAnd.getChildren();
			assertThat(sharedAndChildren, hasSize(2));
			assertSame(templ1, sharedAndChildren.get(0));
			assertSame(test16, sharedAndChildren.get(1));
			assertSame(test10, andChildren.get(1));
		}
		{
			final ConditionalElement andCE = orChildren.get(1);
			final List<ConditionalElement> andChildren = andCE.getChildren();
			assertThat(andChildren, hasSize(2));
			final ConditionalElement shared = andChildren.get(0);
			assertThat(shared, instanceOf(SharedConditionalElementWrapper.class));
			assertSame(sharedCE, shared);
			assertSame(test15, andChildren.get(1));
		}
	}

	@Test
	public void complexExpandableOr() throws ParseException {
		final String input =
				"(templ1 (slot1 ?x)) (or (test (< ?x 1)) (test (< ?x 2))) (or (test (< ?x 3)) (test (< ?x 4))) (test (< ?x 5)) (test (< ?x 6))";
		final List<ConditionalElement> conditionalElements = clipsToCondition(input);

		assertThat(conditionalElements, hasSize(5));
		final ConditionalElement templ1 = conditionalElements.get(0);
		final ConditionalElement test1, test2, test3, test4;
		{
			final ConditionalElement orCE = conditionalElements.get(1);
			assertThat(orCE, instanceOf(OrFunctionConditionalElement.class));
			final List<ConditionalElement> orChildren = orCE.getChildren();
			assertThat(orChildren, hasSize(2));
			test1 = orChildren.get(0);
			test2 = orChildren.get(1);
		}
		{
			final ConditionalElement orCE = conditionalElements.get(2);
			assertThat(orCE, instanceOf(OrFunctionConditionalElement.class));
			final List<ConditionalElement> orChildren = orCE.getChildren();
			assertThat(orChildren, hasSize(2));
			test3 = orChildren.get(0);
			test4 = orChildren.get(1);
		}
		final ConditionalElement test5 = conditionalElements.get(3);
		final ConditionalElement test6 = conditionalElements.get(4);

		final ConditionalElementFormatter cef =
				new ConditionalElementFormatter(SymbolCollector.newHashSet()
						.collect(conditionalElements).toSlotVariablesByFactVariable());

		assertThat(
				cef.format(templ1),
				RegexMatcher.matches("\\(template " + templateName + " Dummy:\\d* \\(" + slot1Name
						+ " \\?x\\)\\)"));
		assertThat(cef.format(test1), RegexMatcher.matches("\\(test \\(< \\?x 1\\)\\)"));
		assertThat(cef.format(test2), RegexMatcher.matches("\\(test \\(< \\?x 2\\)\\)"));
		assertThat(cef.format(test3), RegexMatcher.matches("\\(test \\(< \\?x 3\\)\\)"));
		assertThat(cef.format(test4), RegexMatcher.matches("\\(test \\(< \\?x 4\\)\\)"));
		assertThat(cef.format(test5), RegexMatcher.matches("\\(test \\(< \\?x 5\\)\\)"));
		assertThat(cef.format(test6), RegexMatcher.matches("\\(test \\(< \\?x 6\\)\\)"));

		RuleConditionProcessor.flatten(conditionalElements);

		assertThat(conditionalElements, hasSize(1));
		final ConditionalElement orCE = conditionalElements.get(0);
		assertThat(orCE, instanceOf(OrFunctionConditionalElement.class));
		final List<ConditionalElement> orChildren = orCE.getChildren();
		assertThat(orChildren, hasSize(4));
		final ConditionalElement sharedAnd, sharedTest1, sharedTest2, sharedTest3, sharedTest4;
		{
			// 1 3
			final ConditionalElement andCE = orChildren.get(0);
			final List<ConditionalElement> andChildren = andCE.getChildren();
			assertThat(andChildren, hasSize(3));
			{
				final ConditionalElement shared = andChildren.get(0);
				assertThat(shared, instanceOf(SharedConditionalElementWrapper.class));
				sharedAnd = shared;
				final ConditionalElement sharedLocalAnd =
						((SharedConditionalElementWrapper) shared).getCe();
				assertThat(sharedLocalAnd, instanceOf(AndFunctionConditionalElement.class));
				final List<ConditionalElement> sharedAndChildren = sharedLocalAnd.getChildren();
				assertThat(sharedAndChildren, hasSize(3));
				assertSame(templ1, sharedAndChildren.get(0));
				assertSame(test5, sharedAndChildren.get(1));
				assertSame(test6, sharedAndChildren.get(2));
			}
			{
				final ConditionalElement shared = andChildren.get(1);
				assertThat(shared, instanceOf(SharedConditionalElementWrapper.class));
				sharedTest1 = shared;
				assertSame(test1, ((SharedConditionalElementWrapper) shared).getCe());
			}
			{
				final ConditionalElement shared = andChildren.get(2);
				assertThat(shared, instanceOf(SharedConditionalElementWrapper.class));
				sharedTest3 = shared;
				assertSame(test3, ((SharedConditionalElementWrapper) shared).getCe());
			}
		}
		{
			// 2 3
			final ConditionalElement andCE = orChildren.get(1);
			final List<ConditionalElement> andChildren = andCE.getChildren();
			assertThat(andChildren, hasSize(3));
			{
				final ConditionalElement shared = andChildren.get(0);
				assertThat(shared, instanceOf(SharedConditionalElementWrapper.class));
				assertSame(sharedAnd, shared);
			}
			{
				final ConditionalElement shared = andChildren.get(1);
				assertThat(shared, instanceOf(SharedConditionalElementWrapper.class));
				sharedTest2 = shared;
				assertSame(test2, ((SharedConditionalElementWrapper) shared).getCe());
			}
			{
				final ConditionalElement shared = andChildren.get(2);
				assertThat(shared, instanceOf(SharedConditionalElementWrapper.class));
				assertSame(sharedTest3, shared);
			}
		}
		{
			// 1 4
			final ConditionalElement andCE = orChildren.get(2);
			final List<ConditionalElement> andChildren = andCE.getChildren();
			assertThat(andChildren, hasSize(3));
			{
				final ConditionalElement shared = andChildren.get(0);
				assertThat(shared, instanceOf(SharedConditionalElementWrapper.class));
				assertSame(sharedAnd, shared);
			}
			{
				final ConditionalElement shared = andChildren.get(1);
				assertThat(shared, instanceOf(SharedConditionalElementWrapper.class));
				assertSame(sharedTest1, shared);
			}
			{
				final ConditionalElement shared = andChildren.get(2);
				assertThat(shared, instanceOf(SharedConditionalElementWrapper.class));
				sharedTest4 = shared;
				assertSame(test4, ((SharedConditionalElementWrapper) shared).getCe());
			}
		}
		{
			// 2 4
			final ConditionalElement andCE = orChildren.get(3);
			final List<ConditionalElement> andChildren = andCE.getChildren();
			assertThat(andChildren, hasSize(3));
			{
				final ConditionalElement shared = andChildren.get(0);
				assertThat(shared, instanceOf(SharedConditionalElementWrapper.class));
				assertSame(sharedAnd, shared);
			}
			{
				final ConditionalElement shared = andChildren.get(1);
				assertThat(shared, instanceOf(SharedConditionalElementWrapper.class));
				assertSame(sharedTest2, shared);
			}
			{
				final ConditionalElement shared = andChildren.get(2);
				assertThat(shared, instanceOf(SharedConditionalElementWrapper.class));
				assertSame(sharedTest4, shared);
			}
		}
	}

	@Test
	public void simpleOrWithinExists() throws ParseException {
		final String input = "(exists (or (templ1 (slot1 1)) (templ1 (slot1 2))))";
		final List<ConditionalElement> conditionalElements = clipsToCondition(input);

		final ConditionalElement tpce1, tpce2, test1, test2;
		assertThat(conditionalElements, hasSize(2));
		{
			final ConditionalElement initial = conditionalElements.get(0);
			assertThat(initial, instanceOf(InitialFactConditionalElement.class));
			final ConditionalElement existsCE = conditionalElements.get(1);
			assertThat(existsCE, instanceOf(ExistentialConditionalElement.class));
			final List<ConditionalElement> existentialChildren = existsCE.getChildren();
			assertThat(existentialChildren, hasSize(1));
			final ConditionalElement orCE = existentialChildren.get(0);
			assertThat(orCE, instanceOf(OrFunctionConditionalElement.class));
			final List<ConditionalElement> orChildren = orCE.getChildren();
			assertThat(orChildren, hasSize(2));
			{
				final ConditionalElement andCE = orChildren.get(0);
				assertThat(andCE, instanceOf(AndFunctionConditionalElement.class));
				final List<ConditionalElement> andChildren = andCE.getChildren();
				assertThat(andChildren, hasSize(2));
				final ConditionalElement template = andChildren.get(0);
				assertThat(template, instanceOf(TemplatePatternConditionalElement.class));
				tpce1 = template;
				final ConditionalElement test = andChildren.get(1);
				assertThat(test, instanceOf(TestConditionalElement.class));
				test1 = test;
			}
			{
				final ConditionalElement andCE = orChildren.get(1);
				assertThat(andCE, instanceOf(AndFunctionConditionalElement.class));
				final List<ConditionalElement> andChildren = andCE.getChildren();
				assertThat(andChildren, hasSize(2));
				final ConditionalElement templ1 = andChildren.get(0);
				assertThat(templ1, instanceOf(TemplatePatternConditionalElement.class));
				tpce2 = templ1;
				final ConditionalElement test = andChildren.get(1);
				assertThat(test, instanceOf(TestConditionalElement.class));
				test2 = test;
			}
		}

		final ConditionalElementFormatter cef =
				new ConditionalElementFormatter(SymbolCollector.newHashSet()
						.collect(conditionalElements).toSlotVariablesByFactVariable());

		assertThat(
				cef.format(tpce1),
				RegexMatcher.matches("\\(template " + templateName + " Dummy:\\d* \\(" + slot1Name
						+ " Dummy:\\d*\\)\\)"));
		assertThat(
				cef.format(tpce2),
				RegexMatcher.matches("\\(template " + templateName + " Dummy:\\d* \\(" + slot1Name
						+ " Dummy:\\d*\\)\\)"));
		assertThat(cef.format(test1), RegexMatcher.matches("\\(test \\(= Dummy:\\d* 1\\)\\)"));
		assertThat(cef.format(test2), RegexMatcher.matches("\\(test \\(= Dummy:\\d* 2\\)\\)"));

		RuleConditionProcessor.flatten(conditionalElements);

		assertThat(conditionalElements, hasSize(1));
		final ConditionalElement orCE = conditionalElements.get(0);
		assertThat(orCE, instanceOf(OrFunctionConditionalElement.class));
		final List<ConditionalElement> orChildren = orCE.getChildren();
		assertThat(orChildren, hasSize(2));
		final ConditionalElement sharedAnd, sharedTest1, sharedTest2, sharedTest3, sharedTest4;
		{

		}
	}
}
