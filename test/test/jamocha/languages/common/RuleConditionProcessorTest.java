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
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 *
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
		assertEquals(templ1, andChildren.get(0));
		assertEquals(test10, andChildren.get(1));
		assertEquals(test15, andChildren.get(2));
	}

	@Test
	public void simpleUnexandableOr() throws ParseException {
		final String input = "(templ1 (slot1 ?x)) (or (test (> ?x 10)) (test (< ?x 15)))";
		final List<ConditionalElement> conditionalElements = clipsToCondition(input);
		RuleConditionProcessor.flatten(conditionalElements);
		fail();
		// (templ1 Dummy:d* (slot1 ?x)) (or (test (> ?x 10)) (test (< ?x 15)))
	}

	@Test
	public void simpleExandableOr() throws ParseException {
		final String input =
				"(templ1 (slot1 ?x)) (or (test (> ?x 10)) (test (< ?x 15))) (test (< ?x 16))";
		final List<ConditionalElement> conditionalElements = clipsToCondition(input);
		RuleConditionProcessor.flatten(conditionalElements);
		fail();
		// (templ1 Dummy:d* (slot1 ?x)) (or (and (shared (test (< ?x 16))) (test (> ?x 10))) (and
		// (shared (test (< ?x 16))) (test (< ?x 15))))
	}

	@Test
	public void complexExandableOr() throws ParseException {
		final String input =
				"(templ1 (slot1 ?x)) (or (test (< ?x 1)) (test (< ?x 2))) (or (test (< ?x 3)) (test (< ?x 4))) (test (< ?x 5)) (test (< ?x 6))";
		final List<ConditionalElement> conditionalElements = clipsToCondition(input);
		RuleConditionProcessor.flatten(conditionalElements);
		fail();
		// (templ1 Dummy:d* (slot1 ?x)) (or
		// (and (shared (and (test (< ?x 5)) (test (< ?x 6)))) (shared (test (< ?x (1|2)))) (shared
		// (test (< ?x (3|4)))))
		// (and (shared (and (test (< ?x 5)) (test (< ?x 6)))) (shared (test (< ?x (1|2)))) (shared
		// (test (< ?x (3|4)))))
		// (and (shared (and (test (< ?x 5)) (test (< ?x 6)))) (shared (test (< ?x (1|2)))) (shared
		// (test (< ?x (3|4)))))
		// (and (shared (and (test (< ?x 5)) (test (< ?x 6)))) (shared (test (< ?x (1|2)))) (shared
		// (test (< ?x (3|4)))))
		// )
	}
}
