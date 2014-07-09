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
import static org.jamocha.util.ToArray.toArray;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.jamocha.dn.memory.SlotType;
import org.jamocha.dn.memory.javaimpl.Template;
import org.jamocha.filter.Function;
import org.jamocha.languages.clips.parser.SFPVisitorImpl;
import org.jamocha.languages.clips.parser.generated.ParseException;
import org.jamocha.languages.clips.parser.generated.SFPParser;
import org.jamocha.languages.clips.parser.generated.SFPStart;
import org.jamocha.languages.common.ConditionalElement;
import org.jamocha.languages.common.ConditionalElement.TestConditionalElement;
import org.jamocha.languages.common.Constant;
import org.jamocha.languages.common.Expression;
import org.jamocha.languages.common.FunctionCall;
import org.jamocha.languages.common.NameClashError;
import org.jamocha.languages.common.RuleCondition;
import org.jamocha.languages.common.ScopeStack.Symbol;
import org.jamocha.languages.common.SingleVariable;
import org.jamocha.languages.common.VariableNotDeclaredError;
import org.jamocha.languages.common.Warning;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

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

	private static Symbol getSymbol(final SFPVisitorImpl visitor, final String image) {
		return visitor.getScope().getSymbol(image);
	}

	private static Symbol getSymbol(final Set<Symbol> symbols, final String image) {
		final Symbol[] array =
				toArray(symbols.stream().filter(s -> s.getImage().equals(image)), Symbol[]::new);
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
		final SFPVisitorImpl visitor = new SFPVisitorImpl();
		run(parser, visitor);
		final HashMap<Symbol, Template> symbolTableTemplates = visitor.getSymbolTableTemplates();
		final Template template = symbolTableTemplates.get(getSymbol(visitor, "f1"));
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
		final SFPVisitorImpl visitor = new SFPVisitorImpl();
		run(parser, visitor);
	}

	@Test(expected = NameClashError.class)
	public void testDefruleUniqueNames() throws ParseException {
		final Reader parserInput =
				new StringReader("(deftemplate f1 (slot s1 (type INTEGER)))\n"
						+ "(defrule r1 (f1 (s1 ?x))=>)\n" + "(defrule r1 (f1 (s1 ?x))=>)\n");
		final SFPParser parser = new SFPParser(parserInput);
		final SFPVisitorImpl visitor = new SFPVisitorImpl();
		run(parser, visitor);
	}

	@Test(expected = VariableNotDeclaredError.class)
	public void testDefruleUndeclaredVariable() throws ParseException {
		final Reader parserInput =
				new StringReader("(deftemplate f1 (slot s1 (type INTEGER)))\n"
						+ "(defrule r1 (test (> 2 ?x))=>)\n");
		final SFPParser parser = new SFPParser(parserInput);
		final SFPVisitorImpl visitor = new SFPVisitorImpl();
		run(parser, visitor);
	}

	@Test(expected = VariableNotDeclaredError.class)
	public void testDefruleVariableInExScope() throws ParseException {
		final Reader parserInput =
				new StringReader("(deftemplate f1 (slot s1 (type INTEGER)))\n"
						+ "(defrule r1 (exists (f1 (s1 ?x))) (test (> 2 ?x))=>)\n");
		final SFPParser parser = new SFPParser(parserInput);
		final SFPVisitorImpl visitor = new SFPVisitorImpl();
		run(parser, visitor);
	}

	@Test(expected = VariableNotDeclaredError.class)
	public void testDefruleVariableInNegExScope() throws ParseException {
		final Reader parserInput =
				new StringReader("(deftemplate f1 (slot s1 (type INTEGER)))\n"
						+ "(defrule r1 (not (f1 (s1 ?x))) (test (> 2 ?x))=>)\n");
		final SFPParser parser = new SFPParser(parserInput);
		final SFPVisitorImpl visitor = new SFPVisitorImpl();
		run(parser, visitor);
	}

	@Test(expected = VariableNotDeclaredError.class)
	public void testDefruleVariableInForallScope1() throws ParseException {
		final Reader parserInput =
				new StringReader("(deftemplate f1 (slot s1 (type INTEGER)))\n"
						+ "(defrule r1 (forall (f1 (s1 ?x))(f1 (s1 2))) (test (> 2 ?x))=>)\n");
		final SFPParser parser = new SFPParser(parserInput);
		final SFPVisitorImpl visitor = new SFPVisitorImpl();
		run(parser, visitor);
	}

	@Test(expected = VariableNotDeclaredError.class)
	public void testDefruleVariableInForallScope2() throws ParseException {
		final Reader parserInput =
				new StringReader("(deftemplate f1 (slot s1 (type INTEGER)))\n"
						+ "(defrule r1 (forall (f1 (s1 2))(f1 (s1 ?x))) (test (> 2 ?x))=>)\n");
		final SFPParser parser = new SFPParser(parserInput);
		final SFPVisitorImpl visitor = new SFPVisitorImpl();
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
		final SFPVisitorImpl visitor = new SFPVisitorImpl();
		run(parser, visitor);
		final HashMap<Symbol, Template> symbolTableTemplates = visitor.getSymbolTableTemplates();
		{
			final Template template = symbolTableTemplates.get(getSymbol(visitor, "f1"));
			assertEquals(SlotType.LONG, template.getSlotType(template.getSlotAddress("s1")));
			assertEquals(SlotType.DOUBLE, template.getSlotType(template.getSlotAddress("s2")));
		}
		{
			final Template template = symbolTableTemplates.get(getSymbol(visitor, "f2"));
			assertEquals(SlotType.LONG, template.getSlotType(template.getSlotAddress("s1")));
			assertEquals(SlotType.DOUBLE, template.getSlotType(template.getSlotAddress("s2")));
		}
		final RuleCondition condition =
				visitor.getSymbolTableRules().get(getSymbol(visitor, "r1")).getCondition();
		final Map<Symbol, List<SingleVariable>> variables = condition.getVariables();
		final SingleVariable x;
		{
			final List<SingleVariable> list = variables.get(getSymbol(variables.keySet(), "?x"));
			assertNotNull(list);
			assertEquals(1, list.size());
			final SingleVariable var = list.get(0);
			assertEquals("?x", var.getSymbol().getImage());
			assertFalse(var.isNegated());
			assertEquals(SlotType.LONG, var.getType());
			final Template template = symbolTableTemplates.get(getSymbol(visitor, "f1"));
			assertEquals(template, var.getTemplate());
			assertEquals(template.getSlotAddress("s1"), var.getSlot());
			x = var;
		}
		final SingleVariable y;
		{
			final List<SingleVariable> list = variables.get(getSymbol(variables.keySet(), "?y"));
			assertNotNull(list);
			assertEquals(1, list.size());
			final SingleVariable var = list.get(0);
			assertEquals("?y", var.getSymbol().getImage());
			assertFalse(var.isNegated());
			assertEquals(SlotType.DOUBLE, var.getType());
			final Template template = symbolTableTemplates.get(getSymbol(visitor, "f2"));
			assertEquals(template, var.getTemplate());
			assertEquals(template.getSlotAddress("s2"), var.getSlot());
			y = var;
		}
		{
			final List<SingleVariable> list = variables.get(getSymbol(variables.keySet(), "?z"));
			assertNotNull(list);
			assertEquals(1, list.size());
			final SingleVariable var = list.get(0);
			assertEquals("?z", var.getSymbol().getImage());
			assertFalse(var.isNegated());
			assertEquals(null, var.getType());
			assertEquals(null, var.getSlot());
			final Template template = symbolTableTemplates.get(getSymbol(visitor, "f2"));
			assertEquals(template, var.getTemplate());
		}
		final List<ConditionalElement> conditionalElements = condition.getConditionalElements();
		assertEquals(2, conditionalElements.size());
		{
			final ConditionalElement conditionalElement = conditionalElements.get(0);
			assertThat(conditionalElement, instanceOf(TestConditionalElement.class));
			final FunctionCall functionCall =
					((TestConditionalElement) conditionalElement).getFunctionCall();
			final Function<?> function = functionCall.getFunction();
			assertEquals(org.jamocha.filter.impls.predicates.Greater.inClips, function.inClips());
			final List<? extends Expression> arguments = functionCall.getArguments();
			assertEquals(2, arguments.size());
			final Expression firstArg = arguments.get(0);
			assertThat(firstArg, instanceOf(SingleVariable.class));
			assertEquals(x, (SingleVariable) firstArg);
			final Expression secondArg = arguments.get(1);
			assertThat(secondArg, instanceOf(Constant.class));
			assertEquals(2L, ((Constant) secondArg).getValue());
		}
		{
			final ConditionalElement conditionalElement = conditionalElements.get(1);
			assertThat(conditionalElement, instanceOf(TestConditionalElement.class));
			final FunctionCall functionCall =
					((TestConditionalElement) conditionalElement).getFunctionCall();
			final Function<?> function = functionCall.getFunction();
			assertEquals(org.jamocha.filter.impls.predicates.Less.inClips, function.inClips());
			final List<? extends Expression> arguments = functionCall.getArguments();
			assertEquals(2, arguments.size());
			final Expression firstArg = arguments.get(0);
			assertThat(firstArg, instanceOf(SingleVariable.class));
			assertEquals(y, (SingleVariable) firstArg);
			final Expression secondArg = arguments.get(1);
			assertThat(secondArg, instanceOf(Constant.class));
			assertEquals(0.0, ((Constant) secondArg).getValue());
		}
	}
}
