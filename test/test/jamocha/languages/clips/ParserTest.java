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
package test.jamocha.languages.clips;

import static java.util.stream.Collectors.toSet;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.jamocha.util.ToArray.toArray;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;

import org.jamocha.dn.ConstructCache.Defrule;
import org.jamocha.dn.memory.SlotType;
import org.jamocha.dn.memory.Template;
import org.jamocha.filter.SymbolCollector;
import org.jamocha.function.Function;
import org.jamocha.function.FunctionDictionary;
import org.jamocha.function.fwa.ConstantLeaf;
import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.function.fwa.GenericWithArgumentsComposite;
import org.jamocha.function.fwa.SymbolLeaf;
import org.jamocha.languages.clips.parser.SFPVisitorImpl;
import org.jamocha.languages.clips.parser.generated.ParseException;
import org.jamocha.languages.clips.parser.generated.SFPParser;
import org.jamocha.languages.clips.parser.generated.SFPStart;
import org.jamocha.languages.common.ConditionalElement;
import org.jamocha.languages.common.ConditionalElement.AndFunctionConditionalElement;
import org.jamocha.languages.common.ConditionalElement.InitialFactConditionalElement;
import org.jamocha.languages.common.ConditionalElement.NegatedExistentialConditionalElement;
import org.jamocha.languages.common.ConditionalElement.OrFunctionConditionalElement;
import org.jamocha.languages.common.ConditionalElement.TemplatePatternConditionalElement;
import org.jamocha.languages.common.ConditionalElement.TestConditionalElement;
import org.jamocha.languages.common.RuleCondition;
import org.jamocha.languages.common.ScopeStack.Symbol;
import org.jamocha.languages.common.SingleFactVariable;
import org.jamocha.languages.common.SingleFactVariable.SingleSlotVariable;
import org.jamocha.languages.common.Warning;
import org.jamocha.languages.common.errors.NameClashError;
import org.jamocha.languages.common.errors.VariableNotDeclaredError;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import test.jamocha.util.NetworkMockup;

public class ParserTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	private static Queue<Warning> run(final SFPParser parser, final SFPVisitorImpl visitor)
			throws ParseException {
		while (true) {
			final SFPStart n = parser.Start();
			if (n == null)
				return visitor.getWarnings();
			n.jjtAccept(visitor, null);
		}
	}

	private static Set<SingleFactVariable> getFactVariablesForCE(final ConditionalElement ce) {
		return SymbolCollector.newHashSet().collect(ce).getSymbols().stream()
				.map(s -> s.getFactVariable()).filter(Optional::isPresent).map(Optional::get)
				.collect(toSet());
	}

	private static Symbol getSymbol(final RuleCondition condition, final String image) {
		// final SymbolCollector<ArrayList<Symbol>> symbolCollector =
		// SymbolCollector.newArrayList();
		// condition.getConditionalElements().forEach(ce -> ce.accept(symbolCollector));
		// Stream.concat(symbolCollector.getSymbols().stream(),
		// condition.getSingleFactVariables().stream().map(SingleFactVariable::getSymbol))
		final Symbol[] array =
				toArray(SymbolCollector.newHashSet().collect(condition).getSymbols().stream()
						.distinct().filter(s -> s.getImage().equals(image)), Symbol[]::new);
		assertEquals(1, array.length);
		return array[0];
	}

	@Test
	public void testDeftemplateTypes() throws ParseException {
		final Reader parserInput =
				new StringReader("(deftemplate f1 " + "(slot s1 (type INTEGER))"
						+ "(slot s2 (type FLOAT))" + "(slot s3 (type INTEGER))"
						+ "(slot s4 (type BOOLEAN))" + "(slot s5 (type SYMBOL))"
						+ "(slot s6 (type STRING))" + "(slot s7 (type STRING))"
						+ "(slot s8 (type DATETIME))" + ")\n");
		final SFPParser parser = new SFPParser(parserInput);
		final NetworkMockup network = new NetworkMockup();
		final SFPVisitorImpl visitor = new SFPVisitorImpl(network, network);
		run(parser, visitor);
		final Template template = network.getTemplate("f1");
		assertEquals(SlotType.LONG, template.getSlotType(template.getSlotAddress("s1")));
		assertEquals(SlotType.DOUBLE, template.getSlotType(template.getSlotAddress("s2")));
		assertEquals(SlotType.LONG, template.getSlotType(template.getSlotAddress("s3")));
		assertEquals(SlotType.BOOLEAN, template.getSlotType(template.getSlotAddress("s4")));
		assertEquals(SlotType.SYMBOL, template.getSlotType(template.getSlotAddress("s5")));
		assertEquals(SlotType.STRING, template.getSlotType(template.getSlotAddress("s6")));
		assertEquals(SlotType.STRING, template.getSlotType(template.getSlotAddress("s7")));
		assertEquals(SlotType.DATETIME, template.getSlotType(template.getSlotAddress("s8")));
	}

	@Test(expected = NameClashError.class)
	public void testDeftemplateUniqueNames() throws ParseException {
		final Reader parserInput =
				new StringReader("(deftemplate f1 (slot s1 (type INTEGER)))\n"
						+ "(deftemplate f1 (slot s1 (type FLOAT)))\n");
		final SFPParser parser = new SFPParser(parserInput);
		final NetworkMockup network = new NetworkMockup();
		final SFPVisitorImpl visitor = new SFPVisitorImpl(network, network);
		run(parser, visitor);
	}

	@Test(expected = NameClashError.class)
	public void testDefruleUniqueNames() throws ParseException {
		final Reader parserInput =
				new StringReader("(deftemplate f1 (slot s1 (type INTEGER)))\n"
						+ "(defrule r1 (f1 (s1 ?x))=>)\n" + "(defrule r1 (f1 (s1 ?x))=>)\n");
		final SFPParser parser = new SFPParser(parserInput);
		final NetworkMockup network = new NetworkMockup();
		final SFPVisitorImpl visitor = new SFPVisitorImpl(network, network);
		run(parser, visitor);
	}

	@Test(expected = VariableNotDeclaredError.class)
	public void testDefruleUndeclaredVariable() throws ParseException {
		final Reader parserInput =
				new StringReader("(deftemplate f1 (slot s1 (type INTEGER)))\n"
						+ "(defrule r1 (test (> 2 ?x))=>)\n");
		final SFPParser parser = new SFPParser(parserInput);
		final NetworkMockup network = new NetworkMockup();
		final SFPVisitorImpl visitor = new SFPVisitorImpl(network, network);
		run(parser, visitor);
	}

	@Test(expected = VariableNotDeclaredError.class)
	public void testDefruleVariableFirstNegated() throws ParseException {
		final Reader parserInput =
				new StringReader("(deftemplate f1 (slot s1 (type INTEGER)))\n"
						+ "(defrule r1 (f1 (s1 ~?x)) (f1 (s1 ?x)) =>)\n");
		final SFPParser parser = new SFPParser(parserInput);
		final NetworkMockup network = new NetworkMockup();
		final SFPVisitorImpl visitor = new SFPVisitorImpl(network, network);
		run(parser, visitor);
	}

	@Test(expected = VariableNotDeclaredError.class)
	public void testDefruleVariableInExScope() throws ParseException {
		final Reader parserInput =
				new StringReader("(deftemplate f1 (slot s1 (type INTEGER)))\n"
						+ "(defrule r1 (exists (f1 (s1 ?x))) (test (> 2 ?x))=>)\n");
		final SFPParser parser = new SFPParser(parserInput);
		final NetworkMockup network = new NetworkMockup();
		final SFPVisitorImpl visitor = new SFPVisitorImpl(network, network);
		run(parser, visitor);
	}

	@Test(expected = VariableNotDeclaredError.class)
	public void testDefruleVariableInNegExScope() throws ParseException {
		final Reader parserInput =
				new StringReader("(deftemplate f1 (slot s1 (type INTEGER)))\n"
						+ "(defrule r1 (not (f1 (s1 ?x))) (test (> 2 ?x))=>)\n");
		final SFPParser parser = new SFPParser(parserInput);
		final NetworkMockup network = new NetworkMockup();
		final SFPVisitorImpl visitor = new SFPVisitorImpl(network, network);
		run(parser, visitor);
	}

	@Test
	public void testDefruleVariableInNegExScopePseudoReuse() throws ParseException {
		final Reader parserInput =
				new StringReader("(deftemplate f1 (slot s1 (type INTEGER)))\n"
						+ "(defrule r1 (not (f1 (s1 ?x))) (f1 (s1 ?x)) (test (> 2 ?x))=>)\n");
		final SFPParser parser = new SFPParser(parserInput);
		final NetworkMockup network = new NetworkMockup();
		final SFPVisitorImpl visitor = new SFPVisitorImpl(network, network);
		run(parser, visitor);
		assertEquals(1, visitor.getWarnings().size());
	}

	@Test(expected = VariableNotDeclaredError.class)
	public void testDefruleVariableInForallScope1() throws ParseException {
		final Reader parserInput =
				new StringReader("(deftemplate f1 (slot s1 (type INTEGER)))\n"
						+ "(defrule r1 (forall (f1 (s1 ?x))(f1 (s1 2))) (test (> 2 ?x))=>)\n");
		final SFPParser parser = new SFPParser(parserInput);
		final NetworkMockup network = new NetworkMockup();
		final SFPVisitorImpl visitor = new SFPVisitorImpl(network, network);
		run(parser, visitor);
	}

	@Test(expected = VariableNotDeclaredError.class)
	public void testDefruleVariableInForallScope2() throws ParseException {
		final Reader parserInput =
				new StringReader("(deftemplate f1 (slot s1 (type INTEGER)))\n"
						+ "(defrule r1 (forall (f1 (s1 2))(f1 (s1 ?x))) (test (> 2 ?x))=>)\n");
		final SFPParser parser = new SFPParser(parserInput);
		final NetworkMockup network = new NetworkMockup();
		final SFPVisitorImpl visitor = new SFPVisitorImpl(network, network);
		run(parser, visitor);
	}

	@Test(expected = VariableNotDeclaredError.class)
	public void testDefruleVariableInAmpersandConnectedConstraint() throws ParseException {
		final Reader parserInput =
				new StringReader("(deftemplate f1 (slot s1 (type INTEGER)))\n"
						+ "(defrule r1 (f1 (s1 ?x&?y))=>)\n");
		final SFPParser parser = new SFPParser(parserInput);
		final NetworkMockup network = new NetworkMockup();
		final SFPVisitorImpl visitor = new SFPVisitorImpl(network, network);
		run(parser, visitor);
	}

	@Test(expected = VariableNotDeclaredError.class)
	public void testDefruleVariableInLineConnectedConstraint() throws ParseException {
		final Reader parserInput =
				new StringReader("(deftemplate f1 (slot s1 (type INTEGER)))\n"
						+ "(defrule r1 (f1 (s1 ?x|?y))=>)\n");
		final SFPParser parser = new SFPParser(parserInput);
		final NetworkMockup network = new NetworkMockup();
		final SFPVisitorImpl visitor = new SFPVisitorImpl(network, network);
		run(parser, visitor);
	}

	@Test
	public void testSimpleRule() throws ParseException {
		final Reader parserInput =
				new StringReader(
						"(deftemplate f1 (slot s1 (type INTEGER))(slot s2 (type FLOAT)))\n"
								+ "(deftemplate f2 (slot s1 (type INTEGER))(slot s2 (type FLOAT)))\n"
								+ "(defrule r1 (f1 (s1 ?x)) ?z <- (f2 (s2 ?y))"
								+ "(test (> ?x 2)) (test (< ?y 0.0)) =>)\n");
		final SFPParser parser = new SFPParser(parserInput);
		final NetworkMockup network = new NetworkMockup();
		final SFPVisitorImpl visitor = new SFPVisitorImpl(network, network);
		run(parser, visitor);
		{
			final Template template = network.getTemplate("f1");
			assertEquals(SlotType.LONG, template.getSlotType(template.getSlotAddress("s1")));
			assertEquals(SlotType.DOUBLE, template.getSlotType(template.getSlotAddress("s2")));
		}
		{
			final Template template = network.getTemplate("f2");
			assertEquals(SlotType.LONG, template.getSlotType(template.getSlotAddress("s1")));
			assertEquals(SlotType.DOUBLE, template.getSlotType(template.getSlotAddress("s2")));
		}
		final Defrule rule = network.getRule("r1");
		assertNotNull(rule);
		final RuleCondition condition = rule.getCondition();
		assertNotNull(condition);
		final SingleSlotVariable x, y;
		final SingleFactVariable z;
		{
			final List<SingleSlotVariable> list =
					getSymbol(condition, "?x").getPositiveSlotVariables();
			assertNotNull(list);
			assertEquals(1, list.size());
			final SingleSlotVariable var = list.get(0);
			assertEquals("?x", var.getSymbol().getImage());
			assertFalse(var.isNegated());
			assertEquals(SlotType.LONG, var.getType());
			final Template template = network.getTemplate("f1");
			assertSame(template, var.getFactVariable().getTemplate());
			assertEquals(template.getSlotAddress("s1"), var.getSlot());
			x = var;
		}
		{
			final List<SingleSlotVariable> list =
					getSymbol(condition, "?y").getPositiveSlotVariables();
			assertNotNull(list);
			assertEquals(1, list.size());
			final SingleSlotVariable var = list.get(0);
			assertEquals("?y", var.getSymbol().getImage());
			assertFalse(var.isNegated());
			assertEquals(SlotType.DOUBLE, var.getType());
			final Template template = network.getTemplate("f2");
			assertSame(template, var.getFactVariable().getTemplate());
			assertEquals(template.getSlotAddress("s2"), var.getSlot());
			y = var;
		}
		{
			final Optional<SingleFactVariable> optVar =
					getSymbol(condition, "?z").getFactVariable();
			assertNotNull(optVar);
			assertTrue(optVar.isPresent());
			final SingleFactVariable var = optVar.get();
			assertEquals("?z", var.getSymbol().getImage());
			final Template template = network.getTemplate("f2");
			assertSame(template, var.getTemplate());
			z = var;
		}
		final List<ConditionalElement> conditionalElements = condition.getConditionalElements();
		assertEquals(4, conditionalElements.size());
		{
			final ConditionalElement conditionalElement = conditionalElements.get(1);
			assertThat(conditionalElement, instanceOf(TemplatePatternConditionalElement.class));
			final SingleFactVariable factVariable =
					((TemplatePatternConditionalElement) conditionalElement).getFactVariable();
			assertSame(z, factVariable);
		}
		{
			final ConditionalElement conditionalElement = conditionalElements.get(2);
			assertThat(conditionalElement, instanceOf(TestConditionalElement.class));
			final FunctionWithArguments functionCall =
					((TestConditionalElement) conditionalElement).getPredicateWithArguments();
			assertThat(functionCall, instanceOf(GenericWithArgumentsComposite.class));
			final Function<?> function =
					((GenericWithArgumentsComposite<?, ?>) functionCall).getFunction();
			assertEquals(org.jamocha.function.impls.predicates.Greater.inClips, function.inClips());
			final FunctionWithArguments[] arguments =
					((GenericWithArgumentsComposite<?, ?>) functionCall).getArgs();
			assertEquals(2, arguments.length);
			final FunctionWithArguments firstArg = arguments[0];
			assertThat(firstArg, instanceOf(SymbolLeaf.class));
			assertSame(x.getSymbol(), ((SymbolLeaf) firstArg).getSymbol());
			final FunctionWithArguments secondArg = arguments[1];
			assertThat(secondArg, instanceOf(ConstantLeaf.class));
			assertEquals(2L, ((ConstantLeaf) secondArg).getValue());
		}
		{
			final ConditionalElement conditionalElement = conditionalElements.get(3);
			assertThat(conditionalElement, instanceOf(TestConditionalElement.class));
			final FunctionWithArguments functionCall =
					((TestConditionalElement) conditionalElement).getPredicateWithArguments();
			assertThat(functionCall, instanceOf(GenericWithArgumentsComposite.class));
			final Function<?> function =
					((GenericWithArgumentsComposite<?, ?>) functionCall).getFunction();
			assertEquals(org.jamocha.function.impls.predicates.Less.inClips, function.inClips());
			final FunctionWithArguments[] arguments =
					((GenericWithArgumentsComposite<?, ?>) functionCall).getArgs();
			assertEquals(2, arguments.length);
			final FunctionWithArguments firstArg = arguments[0];
			assertThat(firstArg, instanceOf(SymbolLeaf.class));
			assertSame(y.getSymbol(), ((SymbolLeaf) firstArg).getSymbol());
			final FunctionWithArguments secondArg = arguments[1];
			assertThat(secondArg, instanceOf(ConstantLeaf.class));
			assertEquals(0.0, ((ConstantLeaf) secondArg).getValue());
		}
	}

	@Test
	public void testConnectedConstraints() throws ParseException {
		final Reader parserInput =
				new StringReader(
						"(deftemplate f1 (slot s1 (type INTEGER))(slot s2 (type FLOAT)))\n"
								+ "(deftemplate f2 (slot s1 (type INTEGER))(slot s2 (type FLOAT)))\n"
								+ "(defrule r1 (f1 (s1 ?x&2|3&4|5)) =>)\n");
		// => ?x & (2 | (3 & 4) | 5)
		final SFPParser parser = new SFPParser(parserInput);
		final NetworkMockup network = new NetworkMockup();
		final SFPVisitorImpl visitor = new SFPVisitorImpl(network, network);
		run(parser, visitor);
		{
			final Template template = network.getTemplate("f1");
			assertEquals(SlotType.LONG, template.getSlotType(template.getSlotAddress("s1")));
			assertEquals(SlotType.DOUBLE, template.getSlotType(template.getSlotAddress("s2")));
		}
		{
			final Template template = network.getTemplate("f2");
			assertEquals(SlotType.LONG, template.getSlotType(template.getSlotAddress("s1")));
			assertEquals(SlotType.DOUBLE, template.getSlotType(template.getSlotAddress("s2")));
		}
		final Defrule rule = network.getRule("r1");
		assertNotNull(rule);
		final RuleCondition condition = rule.getCondition();
		assertNotNull(condition);
		final SingleSlotVariable x;
		{
			final List<SingleSlotVariable> list =
					getSymbol(condition, "?x").getPositiveSlotVariables();
			assertNotNull(list);
			assertEquals(1, list.size());
			final SingleSlotVariable var = list.get(0);
			assertEquals("?x", var.getSymbol().getImage());
			assertFalse(var.isNegated());
			assertEquals(SlotType.LONG, var.getType());
			final Template template = network.getTemplate("f1");
			assertEquals(template, var.getFactVariable().getTemplate());
			assertEquals(template.getSlotAddress("s1"), var.getSlot());
			x = var;
		}
		final List<ConditionalElement> conditionalElements = condition.getConditionalElements();
		assertEquals(1, conditionalElements.size());
		{
			final ConditionalElement conditionalElement;
			{
				final ConditionalElement andCE = conditionalElements.get(0);
				assertThat(andCE, instanceOf(AndFunctionConditionalElement.class));
				final List<ConditionalElement> andChildren = andCE.getChildren();
				assertThat(andChildren, hasSize(2));
				final ConditionalElement tpce = andChildren.get(0);
				assertThat(tpce, instanceOf(TemplatePatternConditionalElement.class));
				assertSame(network.getTemplate("f1"), ((TemplatePatternConditionalElement) tpce)
						.getFactVariable().getTemplate());
				conditionalElement = andChildren.get(1);
			}
			assertThat(conditionalElement, instanceOf(OrFunctionConditionalElement.class));
			final List<ConditionalElement> children =
					((OrFunctionConditionalElement) conditionalElement).getChildren();
			assertEquals(3, children.size());
			{
				final ConditionalElement child = children.get(0);
				assertThat(child, instanceOf(TestConditionalElement.class));
				final FunctionWithArguments functionCall =
						((TestConditionalElement) child).getPredicateWithArguments();
				assertThat(functionCall, instanceOf(GenericWithArgumentsComposite.class));
				final Function<?> function =
						((GenericWithArgumentsComposite<?, ?>) functionCall).getFunction();
				assertEquals(FunctionDictionary.lookup(
						org.jamocha.function.impls.predicates.Equals.inClips, SlotType.LONG,
						SlotType.LONG), function);
				final FunctionWithArguments[] arguments =
						((GenericWithArgumentsComposite<?, ?>) functionCall).getArgs();
				assertEquals(2, arguments.length);
				final FunctionWithArguments firstArg = arguments[0];
				assertThat(firstArg, instanceOf(SymbolLeaf.class));
				assertEquals(x.getSymbol(), ((SymbolLeaf) firstArg).getSymbol());
				final FunctionWithArguments secondArg = arguments[1];
				assertThat(secondArg, instanceOf(ConstantLeaf.class));
				assertEquals(2L, ((ConstantLeaf) secondArg).getValue());
			}
			{
				final ConditionalElement child = children.get(1);
				assertThat(child, instanceOf(AndFunctionConditionalElement.class));
				final List<ConditionalElement> andChildren =
						((AndFunctionConditionalElement) child).getChildren();
				assertEquals(2, andChildren.size());
				{
					final ConditionalElement andChild = andChildren.get(0);
					assertThat(andChild, instanceOf(TestConditionalElement.class));
					final FunctionWithArguments functionCall =
							((TestConditionalElement) andChild).getPredicateWithArguments();
					assertThat(functionCall, instanceOf(GenericWithArgumentsComposite.class));
					final Function<?> function =
							((GenericWithArgumentsComposite<?, ?>) functionCall).getFunction();
					assertEquals(FunctionDictionary.lookup(
							org.jamocha.function.impls.predicates.Equals.inClips, SlotType.LONG,
							SlotType.LONG), function);
					final FunctionWithArguments[] arguments =
							((GenericWithArgumentsComposite<?, ?>) functionCall).getArgs();
					assertEquals(2, arguments.length);
					final FunctionWithArguments firstArg = arguments[0];
					assertThat(firstArg, instanceOf(SymbolLeaf.class));
					assertEquals(x.getSymbol(), ((SymbolLeaf) firstArg).getSymbol());
					final FunctionWithArguments secondArg = arguments[1];
					assertThat(secondArg, instanceOf(ConstantLeaf.class));
					assertEquals(3L, ((ConstantLeaf) secondArg).getValue());
				}
				{
					final ConditionalElement andChild = andChildren.get(1);
					assertThat(andChild, instanceOf(TestConditionalElement.class));
					final FunctionWithArguments functionCall =
							((TestConditionalElement) andChild).getPredicateWithArguments();
					assertThat(functionCall, instanceOf(GenericWithArgumentsComposite.class));
					final Function<?> function =
							((GenericWithArgumentsComposite<?, ?>) functionCall).getFunction();
					assertEquals(FunctionDictionary.lookup(
							org.jamocha.function.impls.predicates.Equals.inClips, SlotType.LONG,
							SlotType.LONG), function);
					final FunctionWithArguments[] arguments =
							((GenericWithArgumentsComposite<?, ?>) functionCall).getArgs();
					assertEquals(2, arguments.length);
					final FunctionWithArguments firstArg = arguments[0];
					assertThat(firstArg, instanceOf(SymbolLeaf.class));
					assertEquals(x.getSymbol(), ((SymbolLeaf) firstArg).getSymbol());
					final FunctionWithArguments secondArg = arguments[1];
					assertThat(secondArg, instanceOf(ConstantLeaf.class));
					assertEquals(4L, ((ConstantLeaf) secondArg).getValue());
				}
			}
			{
				final ConditionalElement child = children.get(2);
				assertThat(child, instanceOf(TestConditionalElement.class));
				final FunctionWithArguments functionCall =
						((TestConditionalElement) child).getPredicateWithArguments();
				assertThat(functionCall, instanceOf(GenericWithArgumentsComposite.class));
				final Function<?> function =
						((GenericWithArgumentsComposite<?, ?>) functionCall).getFunction();
				assertEquals(FunctionDictionary.lookup(
						org.jamocha.function.impls.predicates.Equals.inClips, SlotType.LONG,
						SlotType.LONG), function);
				final FunctionWithArguments[] arguments =
						((GenericWithArgumentsComposite<?, ?>) functionCall).getArgs();
				assertEquals(2, arguments.length);
				final FunctionWithArguments firstArg = arguments[0];
				assertThat(firstArg, instanceOf(SymbolLeaf.class));
				assertEquals(x.getSymbol(), ((SymbolLeaf) firstArg).getSymbol());
				final FunctionWithArguments secondArg = arguments[1];
				assertThat(secondArg, instanceOf(ConstantLeaf.class));
				assertEquals(5L, ((ConstantLeaf) secondArg).getValue());
			}
		}
	}

	@Test
	public void testComplexRule() throws ParseException {
		final Reader parserInput =
				new StringReader(
						"(deftemplate f1 (slot s1 (type INTEGER))(slot s2 (type FLOAT)))\n"
								+ "(deftemplate f2 (slot s1 (type INTEGER))(slot s2 (type FLOAT)))\n"
								+ "(defrule r1 (not (and (f1 (s1 ?x) (s2 ?y)) (not (f2 (s1 ?x))) (test (>= ?y 0.5)) )) =>)\n");
		final SFPParser parser = new SFPParser(parserInput);
		final NetworkMockup network = new NetworkMockup();
		final SFPVisitorImpl visitor = new SFPVisitorImpl(network, network);
		run(parser, visitor);
		{
			final Template template = network.getTemplate("f1");
			assertEquals(SlotType.LONG, template.getSlotType(template.getSlotAddress("s1")));
			assertEquals(SlotType.DOUBLE, template.getSlotType(template.getSlotAddress("s2")));
		}
		{
			final Template template = network.getTemplate("f2");
			assertEquals(SlotType.LONG, template.getSlotType(template.getSlotAddress("s1")));
			assertEquals(SlotType.DOUBLE, template.getSlotType(template.getSlotAddress("s2")));
		}
		final Defrule rule = network.getRule("r1");
		assertNotNull(rule);
		final RuleCondition condition = rule.getCondition();
		assertNotNull(condition);
		final SingleSlotVariable x1, x2, y;
		{
			final List<SingleSlotVariable> list =
					getSymbol(condition, "?x").getPositiveSlotVariables();
			assertNotNull(list);
			assertThat(list, hasSize(2));
			{
				final SingleSlotVariable var = list.get(0);
				assertEquals("?x", var.getSymbol().getImage());
				assertFalse(var.isNegated());
				assertEquals(SlotType.LONG, var.getType());
				final Template template = network.getTemplate("f1");
				assertEquals(template, var.getFactVariable().getTemplate());
				assertEquals(template.getSlotAddress("s1"), var.getSlot());
				x1 = var;
			}
			{
				final SingleSlotVariable var = list.get(1);
				assertEquals("?x", var.getSymbol().getImage());
				assertFalse(var.isNegated());
				assertEquals(SlotType.LONG, var.getType());
				final Template template = network.getTemplate("f2");
				assertEquals(template, var.getFactVariable().getTemplate());
				assertEquals(template.getSlotAddress("s1"), var.getSlot());
				x2 = var;
			}
		}
		{
			final List<SingleSlotVariable> list =
					getSymbol(condition, "?y").getPositiveSlotVariables();
			assertNotNull(list);
			assertThat(list, hasSize(1));
			final SingleSlotVariable var = list.get(0);
			assertEquals("?y", var.getSymbol().getImage());
			assertFalse(var.isNegated());
			assertEquals(SlotType.DOUBLE, var.getType());
			final Template template = network.getTemplate("f1");
			assertEquals(template, var.getFactVariable().getTemplate());
			assertEquals(template.getSlotAddress("s2"), var.getSlot());
			y = var;
		}
		final List<ConditionalElement> conditionalElements = condition.getConditionalElements();
		assertThat(conditionalElements, hasSize(2));
		{
			final ConditionalElement conditionalElement = conditionalElements.get(0);
			assertThat(conditionalElement, instanceOf(InitialFactConditionalElement.class));
		}
		{
			final ConditionalElement conditionalElement = conditionalElements.get(1);
			assertThat(conditionalElement, instanceOf(NegatedExistentialConditionalElement.class));
			final NegatedExistentialConditionalElement negatedExistentialConditionalElement =
					(NegatedExistentialConditionalElement) conditionalElement;
			assertEquals(x1.getFactVariable(), y.getFactVariable());

			assertThat(getFactVariablesForCE(negatedExistentialConditionalElement),
					contains(x1.getFactVariable()));
			final List<ConditionalElement> negChildren =
					negatedExistentialConditionalElement.getChildren();
			assertThat(negChildren, hasSize(1));
			final ConditionalElement negChild = negChildren.get(0);
			assertThat(negChild, instanceOf(AndFunctionConditionalElement.class));
			final List<ConditionalElement> andChildren =
					((AndFunctionConditionalElement) negChild).getChildren();
			assertThat(andChildren, hasSize(2));
			{
				final ConditionalElement child = andChildren.get(0);
				assertThat(child, instanceOf(NegatedExistentialConditionalElement.class));
				final NegatedExistentialConditionalElement innerNegExCE =
						(NegatedExistentialConditionalElement) child;
				assertThat(innerNegExCE.getChildren(), hasSize(0));
				assertThat(getFactVariablesForCE(innerNegExCE), contains(x2.getFactVariable()));
			}
			{
				final ConditionalElement child = andChildren.get(1);
				assertThat(child, instanceOf(TestConditionalElement.class));
				final TestConditionalElement testCE = (TestConditionalElement) child;
				assertThat(testCE.getChildren(), hasSize(0));
				final FunctionWithArguments functionCall = testCE.getPredicateWithArguments();
				assertThat(functionCall, instanceOf(GenericWithArgumentsComposite.class));
				final Function<?> function =
						((GenericWithArgumentsComposite<?, ?>) functionCall).getFunction();
				assertEquals(FunctionDictionary.lookup(
						org.jamocha.function.impls.predicates.GreaterOrEqual.inClips,
						SlotType.DOUBLE, SlotType.DOUBLE), function);
				final FunctionWithArguments[] arguments =
						((GenericWithArgumentsComposite<?, ?>) functionCall).getArgs();
				assertEquals(2, arguments.length);
				final FunctionWithArguments firstArg = arguments[0];
				assertThat(firstArg, instanceOf(SymbolLeaf.class));
				assertEquals(y.getSymbol(), ((SymbolLeaf) firstArg).getSymbol());
				final FunctionWithArguments secondArg = arguments[1];
				assertThat(secondArg, instanceOf(ConstantLeaf.class));
				assertEquals(0.5, ((ConstantLeaf) secondArg).getValue());
			}
		}
	}
}
