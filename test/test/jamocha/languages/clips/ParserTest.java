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

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.isIn;
import static org.jamocha.util.ToArray.toArray;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;

import java.io.Reader;
import java.io.StringReader;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.jamocha.dn.ConstructCache.Defrule;
import org.jamocha.dn.compiler.DeepFactVariableCollector;
import org.jamocha.dn.memory.SlotType;
import org.jamocha.dn.memory.Template;
import org.jamocha.filter.SymbolCollector;
import org.jamocha.function.Function;
import org.jamocha.function.FunctionDictionary;
import org.jamocha.function.fwa.ConstantLeaf;
import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.function.fwa.GenericWithArgumentsComposite;
import org.jamocha.function.fwa.SymbolLeaf;
import org.jamocha.languages.clips.parser.SFPToCETranslator;
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
import org.jamocha.languages.common.ScopeStack.VariableSymbol;
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

	private static Queue<Warning> run(final SFPParser parser, final SFPToCETranslator visitor) throws ParseException {
		while (true) {
			final SFPStart n = parser.Start();
			if (n == null)
				return visitor.getWarnings();
			n.jjtAccept(visitor, null);
		}
	}

	private static Set<SingleFactVariable> getFactVariablesForCE(final ConditionalElement ce) {
		return new HashSet<>(DeepFactVariableCollector.collect(ce));
	}

	private static VariableSymbol getSymbol(final RuleCondition condition, final String image) {
		final VariableSymbol[] array =
				toArray(new SymbolCollector(condition).getSymbols().stream().filter(s -> s.getImage().equals(image)),
						VariableSymbol[]::new);
		assertEquals(1, array.length);
		return array[0];
	}

	@Test
	public void testDeftemplateTypes() throws ParseException {
		final Reader parserInput =
				new StringReader("(deftemplate f1 " + "(slot s1 (type INTEGER))" + "(slot s2 (type FLOAT))"
						+ "(slot s3 (type INTEGER))" + "(slot s4 (type BOOLEAN))" + "(slot s5 (type SYMBOL))"
						+ "(slot s6 (type STRING))" + "(slot s7 (type STRING))" + "(slot s8 (type DATETIME))" + ")\n");
		final SFPParser parser = new SFPParser(parserInput);
		final NetworkMockup network = new NetworkMockup();
		final SFPToCETranslator visitor = new SFPToCETranslator(network, network);
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
		final SFPToCETranslator visitor = new SFPToCETranslator(network, network);
		run(parser, visitor);
	}

	@Test(expected = NameClashError.class)
	public void testDefruleUniqueNames() throws ParseException {
		final Reader parserInput =
				new StringReader("(deftemplate f1 (slot s1 (type INTEGER)))\n" + "(defrule r1 (f1 (s1 ?x))=>)\n"
						+ "(defrule r1 (f1 (s1 ?x))=>)\n");
		final SFPParser parser = new SFPParser(parserInput);
		final NetworkMockup network = new NetworkMockup();
		final SFPToCETranslator visitor = new SFPToCETranslator(network, network);
		run(parser, visitor);
	}

	@Test
	public void testDefruleAssignedPatternCENameReuse() throws ParseException {
		final Reader parserInput =
				new StringReader("(deftemplate f1 (slot s1 (type INTEGER)))\n"
						+ "(defrule r1 ?x <- (f1) ?x <- (f1) =>)\n");
		final SFPParser parser = new SFPParser(parserInput);
		final NetworkMockup network = new NetworkMockup();
		final SFPToCETranslator visitor = new SFPToCETranslator(network, network);
		run(parser, visitor);
	}

	@Test(expected = VariableNotDeclaredError.class)
	public void testDefruleUndeclaredVariable() throws ParseException {
		final Reader parserInput =
				new StringReader("(deftemplate f1 (slot s1 (type INTEGER)))\n" + "(defrule r1 (test (> 2 ?x))=>)\n");
		final SFPParser parser = new SFPParser(parserInput);
		final NetworkMockup network = new NetworkMockup();
		final SFPToCETranslator visitor = new SFPToCETranslator(network, network);
		run(parser, visitor);
	}

	@Test(expected = VariableNotDeclaredError.class)
	public void testDefruleVariableInExScope() throws ParseException {
		final Reader parserInput =
				new StringReader("(deftemplate f1 (slot s1 (type INTEGER)))\n"
						+ "(defrule r1 (exists (f1 (s1 ?x))) (test (> 2 ?x))=>)\n");
		final SFPParser parser = new SFPParser(parserInput);
		final NetworkMockup network = new NetworkMockup();
		final SFPToCETranslator visitor = new SFPToCETranslator(network, network);
		run(parser, visitor);
	}

	@Test(expected = VariableNotDeclaredError.class)
	public void testDefruleVariableInNegExScope() throws ParseException {
		final Reader parserInput =
				new StringReader("(deftemplate f1 (slot s1 (type INTEGER)))\n"
						+ "(defrule r1 (not (f1 (s1 ?x))) (test (> 2 ?x))=>)\n");
		final SFPParser parser = new SFPParser(parserInput);
		final NetworkMockup network = new NetworkMockup();
		final SFPToCETranslator visitor = new SFPToCETranslator(network, network);
		run(parser, visitor);
	}

	@Test
	public void testDefruleVariableInNegExScopePseudoReuse() throws ParseException {
		final Reader parserInput =
				new StringReader("(deftemplate f1 (slot s1 (type INTEGER)))\n"
						+ "(defrule r1 (not (f1 (s1 ?x))) (f1 (s1 ?x)) (test (> 2 ?x))=>)\n");
		final SFPParser parser = new SFPParser(parserInput);
		final NetworkMockup network = new NetworkMockup();
		final SFPToCETranslator visitor = new SFPToCETranslator(network, network);
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
		final SFPToCETranslator visitor = new SFPToCETranslator(network, network);
		run(parser, visitor);
	}

	@Test(expected = VariableNotDeclaredError.class)
	public void testDefruleVariableInForallScope2() throws ParseException {
		final Reader parserInput =
				new StringReader("(deftemplate f1 (slot s1 (type INTEGER)))\n"
						+ "(defrule r1 (forall (f1 (s1 2))(f1 (s1 ?x))) (test (> 2 ?x))=>)\n");
		final SFPParser parser = new SFPParser(parserInput);
		final NetworkMockup network = new NetworkMockup();
		final SFPToCETranslator visitor = new SFPToCETranslator(network, network);
		run(parser, visitor);
	}

	@Test(expected = VariableNotDeclaredError.class)
	public void testDefruleVariableInLineConnectedConstraint() throws ParseException {
		final Reader parserInput =
				new StringReader("(deftemplate f1 (slot s1 (type INTEGER)))\n" + "(defrule r1 (f1 (s1 ?x|?y))=>)\n");
		final SFPParser parser = new SFPParser(parserInput);
		final NetworkMockup network = new NetworkMockup();
		final SFPToCETranslator visitor = new SFPToCETranslator(network, network);
		run(parser, visitor);
	}

	@Test
	public void testSimpleRule() throws ParseException {
		final Reader parserInput =
				new StringReader("(deftemplate f1 (slot s1 (type INTEGER))(slot s2 (type FLOAT)))\n"
						+ "(deftemplate f2 (slot s1 (type INTEGER))(slot s2 (type FLOAT)))\n"
						+ "(defrule r1 (f1 (s1 ?x)) ?z <- (f2 (s2 ?y))" + "(test (> ?x 2)) (test (< ?y 0.0)) =>)\n");
		final SFPParser parser = new SFPParser(parserInput);
		final NetworkMockup network = new NetworkMockup();
		final SFPToCETranslator visitor = new SFPToCETranslator(network, network);
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
		final VariableSymbol x, y, z;
		{
			x = getSymbol(condition, "?x");
			final List<SingleSlotVariable> list = x.getEqual().getSlotVariables();
			assertNotNull(list);
			assertEquals(1, list.size());
			final SingleSlotVariable var = list.get(0);
			assertEquals(SlotType.LONG, var.getType());
			final Template template = network.getTemplate("f1");
			assertSame(template, var.getFactVariable().getTemplate());
			assertEquals(template.getSlotAddress("s1"), var.getSlot());
		}
		{
			y = getSymbol(condition, "?y");
			final List<SingleSlotVariable> list = y.getEqual().getSlotVariables();
			assertNotNull(list);
			assertEquals(1, list.size());
			final SingleSlotVariable var = list.get(0);
			assertEquals(SlotType.DOUBLE, var.getType());
			final Template template = network.getTemplate("f2");
			assertSame(template, var.getFactVariable().getTemplate());
			assertEquals(template.getSlotAddress("s2"), var.getSlot());
		}
		{
			z = getSymbol(condition, "?z");
			final LinkedList<SingleFactVariable> factVariables = z.getEqual().getFactVariables();
			assertThat(factVariables, hasSize(1));
			final SingleFactVariable var = factVariables.getFirst();
			final Template template = network.getTemplate("f2");
			assertSame(template, var.getTemplate());
		}
		final List<ConditionalElement> conditionalElements = condition.getConditionalElements();
		assertEquals(4, conditionalElements.size());
		{
			final ConditionalElement conditionalElement = conditionalElements.get(1);
			assertThat(conditionalElement, instanceOf(TemplatePatternConditionalElement.class));
			final SingleFactVariable factVariable =
					((TemplatePatternConditionalElement) conditionalElement).getFactVariable();
			final LinkedList<SingleFactVariable> factVariables = z.getEqual().getFactVariables();
			assertThat(factVariables, hasSize(1));
			assertSame(factVariables.getFirst(), factVariable);
		}
		{
			final ConditionalElement conditionalElement = conditionalElements.get(2);
			assertThat(conditionalElement, instanceOf(TestConditionalElement.class));
			final FunctionWithArguments<SymbolLeaf> functionCall =
					((TestConditionalElement) conditionalElement).getPredicateWithArguments();
			assertThat(functionCall, instanceOf(GenericWithArgumentsComposite.class));
			@SuppressWarnings("unchecked")
			final Function<?> function = ((GenericWithArgumentsComposite<?, ?, SymbolLeaf>) functionCall).getFunction();
			assertEquals(org.jamocha.function.impls.predicates.Greater.inClips, function.inClips());
			@SuppressWarnings("unchecked")
			final FunctionWithArguments<SymbolLeaf>[] arguments =
					((GenericWithArgumentsComposite<?, ?, SymbolLeaf>) functionCall).getArgs();
			assertEquals(2, arguments.length);
			final FunctionWithArguments<SymbolLeaf> firstArg = arguments[0];
			assertThat(firstArg, instanceOf(SymbolLeaf.class));
			assertSame(x, ((SymbolLeaf) firstArg).getSymbol());
			final FunctionWithArguments<SymbolLeaf> secondArg = arguments[1];
			assertThat(secondArg, instanceOf(ConstantLeaf.class));
			assertEquals(2L, ((ConstantLeaf<SymbolLeaf>) secondArg).getValue());
		}
		{
			final ConditionalElement conditionalElement = conditionalElements.get(3);
			assertThat(conditionalElement, instanceOf(TestConditionalElement.class));
			final FunctionWithArguments<SymbolLeaf> functionCall =
					((TestConditionalElement) conditionalElement).getPredicateWithArguments();
			assertThat(functionCall, instanceOf(GenericWithArgumentsComposite.class));
			@SuppressWarnings("unchecked")
			final Function<?> function = ((GenericWithArgumentsComposite<?, ?, SymbolLeaf>) functionCall).getFunction();
			assertEquals(org.jamocha.function.impls.predicates.Less.inClips, function.inClips());
			@SuppressWarnings("unchecked")
			final FunctionWithArguments<SymbolLeaf>[] arguments =
					((GenericWithArgumentsComposite<?, ?, SymbolLeaf>) functionCall).getArgs();
			assertEquals(2, arguments.length);
			final FunctionWithArguments<SymbolLeaf> firstArg = arguments[0];
			assertThat(firstArg, instanceOf(SymbolLeaf.class));
			assertSame(y, ((SymbolLeaf) firstArg).getSymbol());
			final FunctionWithArguments<SymbolLeaf> secondArg = arguments[1];
			assertThat(secondArg, instanceOf(ConstantLeaf.class));
			assertEquals(0.0, ((ConstantLeaf<SymbolLeaf>) secondArg).getValue());
		}
	}

	@Test
	public void testConnectedConstraints() throws ParseException {
		final Reader parserInput =
				new StringReader("(deftemplate f1 (slot s1 (type INTEGER))(slot s2 (type FLOAT)))\n"
						+ "(deftemplate f2 (slot s1 (type INTEGER))(slot s2 (type FLOAT)))\n"
						+ "(defrule r1 (f1 (s1 ?x&2|3&4|5)) =>)\n");
		// => ?x & (2 | (3 & 4) | 5)
		final SFPParser parser = new SFPParser(parserInput);
		final NetworkMockup network = new NetworkMockup();
		final SFPToCETranslator visitor = new SFPToCETranslator(network, network);
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
		final VariableSymbol x;
		{
			x = getSymbol(condition, "?x");
			final List<SingleSlotVariable> list = x.getEqual().getSlotVariables();
			assertNotNull(list);
			assertEquals(1, list.size());
			final SingleSlotVariable var = list.get(0);
			assertEquals(SlotType.LONG, var.getType());
			final Template template = network.getTemplate("f1");
			assertEquals(template, var.getFactVariable().getTemplate());
			assertEquals(template.getSlotAddress("s1"), var.getSlot());
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
				assertSame(network.getTemplate("f1"), ((TemplatePatternConditionalElement) tpce).getFactVariable()
						.getTemplate());
				conditionalElement = andChildren.get(1);
			}
			assertThat(conditionalElement, instanceOf(OrFunctionConditionalElement.class));
			final List<ConditionalElement> children = ((OrFunctionConditionalElement) conditionalElement).getChildren();
			assertEquals(3, children.size());
			{
				final ConditionalElement child = children.get(0);
				assertThat(child, instanceOf(TestConditionalElement.class));
				final FunctionWithArguments<SymbolLeaf> functionCall =
						((TestConditionalElement) child).getPredicateWithArguments();
				assertThat(functionCall, instanceOf(GenericWithArgumentsComposite.class));
				@SuppressWarnings("unchecked")
				final Function<?> function =
						((GenericWithArgumentsComposite<?, ?, SymbolLeaf>) functionCall).getFunction();
				assertEquals(FunctionDictionary.lookup(org.jamocha.function.impls.predicates.Equals.inClips,
						SlotType.LONG, SlotType.LONG), function);
				@SuppressWarnings("unchecked")
				final FunctionWithArguments<SymbolLeaf>[] arguments =
						((GenericWithArgumentsComposite<?, ?, SymbolLeaf>) functionCall).getArgs();
				assertEquals(2, arguments.length);
				final FunctionWithArguments<SymbolLeaf> firstArg = arguments[0];
				assertThat(firstArg, instanceOf(SymbolLeaf.class));
				assertEquals(x, ((SymbolLeaf) firstArg).getSymbol());
				final FunctionWithArguments<SymbolLeaf> secondArg = arguments[1];
				assertThat(secondArg, instanceOf(ConstantLeaf.class));
				assertEquals(2L, ((ConstantLeaf<SymbolLeaf>) secondArg).getValue());
			}
			{
				final ConditionalElement child = children.get(1);
				assertThat(child, instanceOf(AndFunctionConditionalElement.class));
				final List<ConditionalElement> andChildren = ((AndFunctionConditionalElement) child).getChildren();
				assertEquals(2, andChildren.size());
				{
					final ConditionalElement andChild = andChildren.get(0);
					assertThat(andChild, instanceOf(TestConditionalElement.class));
					final FunctionWithArguments<SymbolLeaf> functionCall =
							((TestConditionalElement) andChild).getPredicateWithArguments();
					assertThat(functionCall, instanceOf(GenericWithArgumentsComposite.class));
					@SuppressWarnings("unchecked")
					final Function<?> function =
							((GenericWithArgumentsComposite<?, ?, SymbolLeaf>) functionCall).getFunction();
					assertEquals(FunctionDictionary.lookup(org.jamocha.function.impls.predicates.Equals.inClips,
							SlotType.LONG, SlotType.LONG), function);
					@SuppressWarnings("unchecked")
					final FunctionWithArguments<SymbolLeaf>[] arguments =
							((GenericWithArgumentsComposite<?, ?, SymbolLeaf>) functionCall).getArgs();
					assertEquals(2, arguments.length);
					final FunctionWithArguments<SymbolLeaf> firstArg = arguments[0];
					assertThat(firstArg, instanceOf(SymbolLeaf.class));
					assertEquals(x, ((SymbolLeaf) firstArg).getSymbol());
					final FunctionWithArguments<SymbolLeaf> secondArg = arguments[1];
					assertThat(secondArg, instanceOf(ConstantLeaf.class));
					assertEquals(3L, ((ConstantLeaf<SymbolLeaf>) secondArg).getValue());
				}
				{
					final ConditionalElement andChild = andChildren.get(1);
					assertThat(andChild, instanceOf(TestConditionalElement.class));
					final FunctionWithArguments<SymbolLeaf> functionCall =
							((TestConditionalElement) andChild).getPredicateWithArguments();
					assertThat(functionCall, instanceOf(GenericWithArgumentsComposite.class));
					@SuppressWarnings("unchecked")
					final Function<?> function =
							((GenericWithArgumentsComposite<?, ?, SymbolLeaf>) functionCall).getFunction();
					assertEquals(FunctionDictionary.lookup(org.jamocha.function.impls.predicates.Equals.inClips,
							SlotType.LONG, SlotType.LONG), function);
					@SuppressWarnings("unchecked")
					final FunctionWithArguments<SymbolLeaf>[] arguments =
							((GenericWithArgumentsComposite<?, ?, SymbolLeaf>) functionCall).getArgs();
					assertEquals(2, arguments.length);
					final FunctionWithArguments<SymbolLeaf> firstArg = arguments[0];
					assertThat(firstArg, instanceOf(SymbolLeaf.class));
					assertEquals(x, ((SymbolLeaf) firstArg).getSymbol());
					final FunctionWithArguments<SymbolLeaf> secondArg = arguments[1];
					assertThat(secondArg, instanceOf(ConstantLeaf.class));
					assertEquals(4L, ((ConstantLeaf<SymbolLeaf>) secondArg).getValue());
				}
			}
			{
				final ConditionalElement child = children.get(2);
				assertThat(child, instanceOf(TestConditionalElement.class));
				final FunctionWithArguments<SymbolLeaf> functionCall =
						((TestConditionalElement) child).getPredicateWithArguments();
				assertThat(functionCall, instanceOf(GenericWithArgumentsComposite.class));
				@SuppressWarnings("unchecked")
				final Function<?> function =
						((GenericWithArgumentsComposite<?, ?, SymbolLeaf>) functionCall).getFunction();
				assertEquals(FunctionDictionary.lookup(org.jamocha.function.impls.predicates.Equals.inClips,
						SlotType.LONG, SlotType.LONG), function);
				@SuppressWarnings("unchecked")
				final FunctionWithArguments<SymbolLeaf>[] arguments =
						((GenericWithArgumentsComposite<?, ?, SymbolLeaf>) functionCall).getArgs();
				assertEquals(2, arguments.length);
				final FunctionWithArguments<SymbolLeaf> firstArg = arguments[0];
				assertThat(firstArg, instanceOf(SymbolLeaf.class));
				assertEquals(x, ((SymbolLeaf) firstArg).getSymbol());
				final FunctionWithArguments<SymbolLeaf> secondArg = arguments[1];
				assertThat(secondArg, instanceOf(ConstantLeaf.class));
				assertEquals(5L, ((ConstantLeaf<SymbolLeaf>) secondArg).getValue());
			}
		}
	}

	@Test
	public void testComplexRule() throws ParseException {
		final Reader parserInput =
				new StringReader("(deftemplate f1 (slot s1 (type INTEGER))(slot s2 (type FLOAT)))\n"
						+ "(deftemplate f2 (slot s1 (type INTEGER))(slot s2 (type FLOAT)))\n"
						+ "(defrule r1 (not (and (f1 (s1 ?x) (s2 ?y)) (not (f2 (s1 ?x))) (test (>= ?y 0.5)) )) =>)\n");
		final SFPParser parser = new SFPParser(parserInput);
		final NetworkMockup network = new NetworkMockup();
		final SFPToCETranslator visitor = new SFPToCETranslator(network, network);
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
		final VariableSymbol x = getSymbol(condition, "?x"), y = getSymbol(condition, "?y");
		final SingleSlotVariable x1, x2, ySlot;
		{
			final List<SingleSlotVariable> list = x.getEqual().getSlotVariables();
			assertNotNull(list);
			assertThat(list, hasSize(2));
			{
				final SingleSlotVariable var = list.get(0);
				assertEquals(SlotType.LONG, var.getType());
				final Template template = network.getTemplate("f1");
				assertEquals(template, var.getFactVariable().getTemplate());
				assertEquals(template.getSlotAddress("s1"), var.getSlot());
				x1 = var;
			}
			{
				final SingleSlotVariable var = list.get(1);
				assertEquals(SlotType.LONG, var.getType());
				final Template template = network.getTemplate("f2");
				assertEquals(template, var.getFactVariable().getTemplate());
				assertEquals(template.getSlotAddress("s1"), var.getSlot());
				x2 = var;
			}
		}
		{
			final List<SingleSlotVariable> list = y.getEqual().getSlotVariables();
			assertNotNull(list);
			assertThat(list, hasSize(1));
			final SingleSlotVariable var = list.get(0);
			assertEquals(SlotType.DOUBLE, var.getType());
			final Template template = network.getTemplate("f1");
			assertEquals(template, var.getFactVariable().getTemplate());
			assertEquals(template.getSlotAddress("s2"), var.getSlot());
			ySlot = var;
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
			assertEquals(x1.getFactVariable(), ySlot.getFactVariable());

			assertThat(x1.getFactVariable(), isIn(getFactVariablesForCE(negatedExistentialConditionalElement)));
			final List<ConditionalElement> negChildren = negatedExistentialConditionalElement.getChildren();
			assertThat(negChildren, hasSize(1));
			final ConditionalElement negChild = negChildren.get(0);
			assertThat(negChild, instanceOf(AndFunctionConditionalElement.class));
			final List<ConditionalElement> andChildren = ((AndFunctionConditionalElement) negChild).getChildren();
			assertThat(andChildren, hasSize(3));
			{
				final ConditionalElement child = andChildren.get(0);
				assertThat(child, instanceOf(TemplatePatternConditionalElement.class));
				final TemplatePatternConditionalElement tpce = (TemplatePatternConditionalElement) child;
				assertThat(tpce.getChildren(), hasSize(0));
				assertSame(x1.getFactVariable(), tpce.getFactVariable());
				assertSame(ySlot.getFactVariable(), tpce.getFactVariable());
			}
			{
				final ConditionalElement child = andChildren.get(1);
				assertThat(child, instanceOf(NegatedExistentialConditionalElement.class));
				final NegatedExistentialConditionalElement innerNegExCE = (NegatedExistentialConditionalElement) child;
				assertThat(innerNegExCE.getChildren(), hasSize(1));
				assertThat(x2.getFactVariable(), isIn(getFactVariablesForCE(innerNegExCE)));
				final ConditionalElement tpce = innerNegExCE.getChildren().get(0);
				assertThat(tpce, instanceOf(TemplatePatternConditionalElement.class));
				assertSame(x2.getFactVariable(), ((TemplatePatternConditionalElement) tpce).getFactVariable());
			}
			{
				final ConditionalElement child = andChildren.get(2);
				assertThat(child, instanceOf(TestConditionalElement.class));
				final TestConditionalElement testCE = (TestConditionalElement) child;
				assertThat(testCE.getChildren(), hasSize(0));
				final FunctionWithArguments<SymbolLeaf> functionCall = testCE.getPredicateWithArguments();
				assertThat(functionCall, instanceOf(GenericWithArgumentsComposite.class));
				@SuppressWarnings("unchecked")
				final Function<?> function =
						((GenericWithArgumentsComposite<?, ?, SymbolLeaf>) functionCall).getFunction();
				assertEquals(FunctionDictionary.lookup(org.jamocha.function.impls.predicates.GreaterOrEqual.inClips,
						SlotType.DOUBLE, SlotType.DOUBLE), function);
				@SuppressWarnings("unchecked")
				final FunctionWithArguments<SymbolLeaf>[] arguments =
						((GenericWithArgumentsComposite<?, ?, SymbolLeaf>) functionCall).getArgs();
				assertEquals(2, arguments.length);
				final FunctionWithArguments<SymbolLeaf> firstArg = arguments[0];
				assertThat(firstArg, instanceOf(SymbolLeaf.class));
				assertEquals(y, ((SymbolLeaf) firstArg).getSymbol());
				final FunctionWithArguments<SymbolLeaf> secondArg = arguments[1];
				assertThat(secondArg, instanceOf(ConstantLeaf.class));
				assertEquals(0.5, ((ConstantLeaf<SymbolLeaf>) secondArg).getValue());
			}
		}
	}
}
