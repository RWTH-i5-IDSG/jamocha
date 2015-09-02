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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.either;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.isOneOf;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.commons.lang3.tuple.Pair;
import org.jamocha.dn.Network;
import org.jamocha.languages.clips.parser.SFPToCETranslator;
import org.jamocha.languages.clips.parser.generated.ParseException;
import org.jamocha.languages.clips.parser.generated.SFPParser;
import org.jamocha.languages.clips.parser.generated.SFPStart;
import org.jamocha.languages.common.Warning;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class SystemTest {

	final String linesep = System.lineSeparator();

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Get the calling method name. <br />
	 * Utility function
	 *
	 * @return method name
	 */
	public static String getMethodName() {
		// index 0 is getStackTrace, index 1 is getMethodName, index 2 is invoking method
		final StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[2];
		return stackTraceElement.getClassName() + "::" + stackTraceElement.getMethodName();
	}

	private static Pair<Queue<Object>, Queue<Warning>> run(final Network network, final String parserInput)
			throws ParseException {
		final SFPParser parser = new SFPParser(new StringReader(parserInput));
		final SFPToCETranslator visitor = new SFPToCETranslator(network, network);
		final Queue<Object> values = new LinkedList<>();

		while (true) {
			final SFPStart n = parser.Start();
			if (n == null)
				return Pair.of(values, visitor.getWarnings());
			final Object value = n.jjtAccept(visitor, null);
			if (null != value) {
				values.add(value);
			}
		}
	}

	private static ByteArrayOutputStream initializeAppender(final Network network) {
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		network.clearAppender();
		network.addAppender(out, true);
		return out;
	}

	@Test
	public void testOrCondition() throws ParseException {
		final Network network = new Network();
		initializeAppender(network);
		{
			run(network, "(deftemplate templ1 (slot slot1 (type INTEGER)))\n" + "(defrule rule1 \n"
					+ "(templ1 (slot1 ?x)) (templ1 (slot1 ?y)) (templ1 (slot1 ?z))\n" + "	(or\n"
					+ "		(test (> ?x ?y))\n" + "		(test (> ?x ?z))\n" + "	)\n" + "	(test (< ?x ?y))\n" + "=> )\n");
		}
	}

	@Test
	public void testOnlyOrCondition() throws ParseException {
		final Network network = new Network();
		initializeAppender(network);
		{
			run(network, "(deftemplate templ1 (slot slot1 (type INTEGER)))\n" + "(defrule rule1 \n"
					+ "(templ1 (slot1 ?x)) (templ1 (slot1 ?y)) (templ1 (slot1 ?z))\n" + "	(or\n"
					+ "		(test (< ?x ?y))\n" + "		(test (< ?x ?z))\n" + "	)\n" + "=> )\n");
		}
	}

	@Test
	public void testUnderUsedTemplatesCondition() throws ParseException {
		final Network network = new Network();
		initializeAppender(network);
		{
			run(network, "(deftemplate templ1 (slot slot1 (type INTEGER)))\n" + "(defrule rule1 \n"
					+ "(templ1 (slot1 ?x)) (templ1 (slot1 ?y)) (templ1 (slot1 ?z))\n" + "	(test (> ?x ?y))\n"
					+ "=> )\n");
		}
	}

	@Test
	public void testUnderfullOrCondition() throws ParseException {
		final Network network = new Network();
		initializeAppender(network);
		{
			run(network, "(deftemplate templ1 (slot slot1 (type INTEGER)))\n" + "(defrule rule1 \n"
					+ "(templ1 (slot1 ?x)) (templ1 (slot1 ?y)) (templ1 (slot1 ?z))\n" + "	(or\n"
					+ "		(test (> ?x ?z))\n" + "	)\n" + "	(test (> ?x ?y))\n" + "=> )\n");
		}
	}

	@Test
	public void testSimpleWatchedFactAssertion() throws ParseException {
		final Network network = new Network();
		final ByteArrayOutputStream out = initializeAppender(network);
		{
			final Pair<Queue<Object>, Queue<Warning>> returnValues = run(network, "(unwatch all)\n(watch facts)\n");
			assertThat(returnValues.getLeft(), empty());
			assertThat(returnValues.getRight(), empty());
			assertThat(out.toString(), isEmptyString());
		}
		{
			final Pair<Queue<Object>, Queue<Warning>> returnValues =
					run(network, "(deftemplate t1 (slot s1 (type INTEGER)))\n");
			assertThat(returnValues.getLeft(), empty());
			assertThat(returnValues.getRight(), empty());
			assertThat(out.toString(), isEmptyString());
		}
		{
			final Pair<Queue<Object>, Queue<Warning>> returnValues = run(network, "(assert (t1 (s1 2)))\n");
			final Queue<Object> values = returnValues.getLeft();
			assertThat(values, hasSize(1));
			final Object value = values.iterator().next();
			assertThat(value, instanceOf(String.class));
			assertEquals("<Fact-2>", value);
			assertThat(returnValues.getRight(), empty());
			final String output = out.toString();
			assertThat(output, not(isEmptyString()));
			final String[] lines = output.split(linesep);
			assertThat(lines, arrayWithSize(1));
			assertEquals("==> f-2\t(t1 (s1 2))", lines[0]);
			out.reset();
		}
	}

	@Test
	public void testSimpleRuleExecution() throws ParseException {
		final Network network = new Network();
		final ByteArrayOutputStream out = initializeAppender(network);
		{
			final Pair<Queue<Object>, Queue<Warning>> returnValues = run(network, "(unwatch all)\n(watch facts)\n");
			assertThat(returnValues.getLeft(), empty());
			assertThat(returnValues.getRight(), empty());
			assertThat(out.toString(), isEmptyString());
		}
		{
			final Pair<Queue<Object>, Queue<Warning>> returnValues =
					run(network, "(deftemplate t1 (slot s1 (type INTEGER)))\n");
			assertThat(returnValues.getLeft(), empty());
			assertThat(returnValues.getRight(), empty());
			assertThat(out.toString(), isEmptyString());
		}
		{
			final Pair<Queue<Object>, Queue<Warning>> returnValues =
					run(network, "(defrule r1 (t1 (s1 5)) => (assert (t1 (s1 999))) )\n");
			assertThat(returnValues.getLeft(), empty());
			assertThat(returnValues.getRight(), empty());
			assertThat(out.toString(), isEmptyString());
		}
		{
			final Pair<Queue<Object>, Queue<Warning>> returnValues = run(network, "(assert (t1 (s1 2)))\n");
			final Queue<Object> values = returnValues.getLeft();
			assertThat(values, hasSize(1));
			final Object value = values.iterator().next();
			assertThat(value, instanceOf(String.class));
			assertEquals("<Fact-2>", value);
			assertThat(returnValues.getRight(), empty());
			final String[] lines = out.toString().split(linesep);
			assertThat(lines, arrayWithSize(1));
			assertEquals("==> f-2\t(t1 (s1 2))", lines[0]);
			out.reset();
		}
		{
			final Pair<Queue<Object>, Queue<Warning>> returnValues = run(network, "(assert (t1 (s1 5)))\n");
			final Queue<Object> values = returnValues.getLeft();
			assertThat(values, hasSize(1));
			final Object value = values.iterator().next();
			assertThat(value, instanceOf(String.class));
			assertEquals("<Fact-3>", value);
			assertThat(returnValues.getRight(), empty());
			final String[] lines = out.toString().split(linesep);
			assertThat(lines, arrayWithSize(1));
			assertEquals("==> f-3\t(t1 (s1 5))", lines[0]);
			out.reset();
		}
		{
			final Pair<Queue<Object>, Queue<Warning>> returnValues = run(network, "(run)\n");
			assertThat(returnValues.getLeft(), empty());
			assertThat(returnValues.getRight(), empty());
			final String[] lines = out.toString().split(linesep);
			assertThat(lines, arrayWithSize(1));
			assertEquals("==> f-4\t(t1 (s1 999))", lines[0]);
			out.reset();
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testNodeSharingAllButTerminal() throws ParseException {
		final Network network = new Network();
		final ByteArrayOutputStream out = initializeAppender(network);
		{
			final Pair<Queue<Object>, Queue<Warning>> returnValues = run(network, "(unwatch all)\n(watch facts)\n");
			assertThat(returnValues.getLeft(), empty());
			assertThat(returnValues.getRight(), empty());
			assertThat(out.toString(), isEmptyString());
		}
		{
			final Pair<Queue<Object>, Queue<Warning>> returnValues =
					run(network, "(deftemplate t1 (slot s1 (type INTEGER)))\n");
			assertThat(returnValues.getLeft(), empty());
			assertThat(returnValues.getRight(), empty());
			assertThat(out.toString(), isEmptyString());
		}
		{
			final Pair<Queue<Object>, Queue<Warning>> returnValues =
					run(network, "(defrule r1 (t1 (s1 5)) => (assert (t1 (s1 999))) )\n");
			assertThat(returnValues.getLeft(), empty());
			assertThat(returnValues.getRight(), empty());
			assertThat(out.toString(), isEmptyString());
		}
		{
			final Pair<Queue<Object>, Queue<Warning>> returnValues = run(network, "(assert (t1 (s1 5)))\n");
			final Queue<Object> values = returnValues.getLeft();
			assertThat(values, hasSize(1));
			final Object value = values.iterator().next();
			assertThat(value, instanceOf(String.class));
			assertEquals("<Fact-2>", value);
			assertThat(returnValues.getRight(), empty());
			final String[] lines = out.toString().split(linesep);
			assertThat(lines, arrayWithSize(1));
			assertEquals("==> f-2\t(t1 (s1 5))", lines[0]);
			out.reset();
		}
		{
			final Pair<Queue<Object>, Queue<Warning>> returnValues =
					run(network, "(defrule r2 (t1 (s1 5)) => (assert (t1 (s1 888))) )\n");
			assertThat(returnValues.getLeft(), empty());
			assertThat(returnValues.getRight(), empty());
			assertThat(out.toString(), isEmptyString());
		}
		{
			final Pair<Queue<Object>, Queue<Warning>> returnValues = run(network, "(run)\n");
			assertThat(returnValues.getLeft(), empty());
			assertThat(returnValues.getRight(), empty());
			final String[] lines = out.toString().split(linesep);
			assertThat(
					lines,
					either(arrayContaining(equalTo("==> f-3\t(t1 (s1 888))"), equalTo("==> f-4\t(t1 (s1 999))"))).or(
							arrayContaining(equalTo("==> f-3\t(t1 (s1 999))"), equalTo("==> f-4\t(t1 (s1 888))"))));
			assertThat(lines, arrayWithSize(2));
			out.reset();
		}
	}

	@Test
	public void testEquivalenceClasses() throws ParseException {
		final Network network = new Network();
		final ByteArrayOutputStream out = initializeAppender(network);
		{
			final Pair<Queue<Object>, Queue<Warning>> returnValues = run(network, "(unwatch all)\n(watch facts)\n");
			assertThat(returnValues.getLeft(), empty());
			assertThat(returnValues.getRight(), empty());
			assertThat(out.toString(), isEmptyString());
		}
		{
			final Pair<Queue<Object>, Queue<Warning>> returnValues =
					run(network, "(deftemplate t1 (slot s1 (type INTEGER)))\n");
			assertThat(returnValues.getLeft(), empty());
			assertThat(returnValues.getRight(), empty());
			assertThat(out.toString(), isEmptyString());
		}
		{
			final Pair<Queue<Object>, Queue<Warning>> returnValues =
					run(network, "(deftemplate t2 (slot s1 (type INTEGER)))\n");
			assertThat(returnValues.getLeft(), empty());
			assertThat(returnValues.getRight(), empty());
			assertThat(out.toString(), isEmptyString());
		}
		{
			final Pair<Queue<Object>, Queue<Warning>> returnValues =
					run(network, "(defrule r1 (t1 (s1 ?x)) (t2 (s1 ?x)) => (assert (t1 (s1 999))) )\n");
			assertThat(returnValues.getLeft(), empty());
			assertThat(returnValues.getRight(), empty());
			assertThat(out.toString(), isEmptyString());
		}
		{
			final Pair<Queue<Object>, Queue<Warning>> returnValues = run(network, "(assert (t1 (s1 5)))\n");
			final Queue<Object> values = returnValues.getLeft();
			assertThat(values, hasSize(1));
			final Object value = values.iterator().next();
			assertThat(value, instanceOf(String.class));
			assertEquals("<Fact-2>", value);
			assertThat(returnValues.getRight(), empty());
			final String[] lines = out.toString().split(linesep);
			assertThat(lines, arrayWithSize(1));
			assertEquals("==> f-2\t(t1 (s1 5))", lines[0]);
			out.reset();
		}
		{
			final Pair<Queue<Object>, Queue<Warning>> returnValues = run(network, "(assert (t2 (s1 9)))\n");
			final Queue<Object> values = returnValues.getLeft();
			assertThat(values, hasSize(1));
			final Object value = values.iterator().next();
			assertThat(value, instanceOf(String.class));
			assertEquals("<Fact-3>", value);
			assertThat(returnValues.getRight(), empty());
			final String[] lines = out.toString().split(linesep);
			assertThat(lines, arrayWithSize(1));
			assertEquals("==> f-3\t(t2 (s1 9))", lines[0]);
			out.reset();
		}
		{
			final Pair<Queue<Object>, Queue<Warning>> returnValues = run(network, "(run)\n");
			assertThat(returnValues.getLeft(), empty());
			assertThat(returnValues.getRight(), empty());
			assertThat(out.toString(), isEmptyString());
			out.reset();
		}
		{
			final Pair<Queue<Object>, Queue<Warning>> returnValues = run(network, "(assert (t2 (s1 5)))\n");
			final Queue<Object> values = returnValues.getLeft();
			assertThat(values, hasSize(1));
			final Object value = values.iterator().next();
			assertThat(value, instanceOf(String.class));
			assertEquals("<Fact-4>", value);
			assertThat(returnValues.getRight(), empty());
			final String[] lines = out.toString().split(linesep);
			assertThat(lines, arrayWithSize(1));
			assertEquals("==> f-4\t(t2 (s1 5))", lines[0]);
			out.reset();
		}
		{
			final Pair<Queue<Object>, Queue<Warning>> returnValues = run(network, "(run)\n");
			assertThat(returnValues.getLeft(), empty());
			assertThat(returnValues.getRight(), empty());
			final String[] lines = out.toString().split(linesep);
			assertThat(lines, arrayWithSize(1));
			assertEquals("==> f-5\t(t1 (s1 999))", lines[0]);
			out.reset();
		}
	}

	@Test
	public void testSimpleNegatedExistentialRule() throws ParseException {
		final Network network = new Network();
		final ByteArrayOutputStream out = initializeAppender(network);
		{
			final Pair<Queue<Object>, Queue<Warning>> returnValues = run(network, "(unwatch all)\n(watch facts)\n");
			assertThat(returnValues.getLeft(), empty());
			assertThat(returnValues.getRight(), empty());
			assertThat(out.toString(), isEmptyString());
		}
		{
			final Pair<Queue<Object>, Queue<Warning>> returnValues =
					run(network, "(deftemplate t1 (slot s1 (type INTEGER)))\n");
			assertThat(returnValues.getLeft(), empty());
			assertThat(returnValues.getRight(), empty());
			assertThat(out.toString(), isEmptyString());
		}
		{
			final Pair<Queue<Object>, Queue<Warning>> returnValues =
					run(network, "(defrule r1 (not (t1)) => (assert (t1 (s1 999))) )\n");
			assertThat(returnValues.getLeft(), empty());
			assertThat(returnValues.getRight(), empty());
			assertThat(out.toString(), isEmptyString());
		}
		{
			final Pair<Queue<Object>, Queue<Warning>> returnValues = run(network, "(run)\n");
			assertThat(returnValues.getLeft(), empty());
			assertThat(returnValues.getRight(), empty());
			final String[] lines = out.toString().split(linesep);
			assertThat(lines, arrayWithSize(1));
			assertEquals("==> f-2\t(t1 (s1 999))", lines[0]);
			out.reset();
		}
		{
			final Pair<Queue<Object>, Queue<Warning>> returnValues = run(network, "(retract 2)\n");
			assertThat(returnValues.getLeft(), empty());
			assertThat(returnValues.getRight(), empty());
			final String[] lines = out.toString().split(linesep);
			assertThat(lines, arrayWithSize(1));
			assertEquals("<== f-2\t(t1 (s1 999))", lines[0]);
			out.reset();
		}
		{
			final Pair<Queue<Object>, Queue<Warning>> returnValues = run(network, "(run)\n");
			assertThat(returnValues.getLeft(), empty());
			assertThat(returnValues.getRight(), empty());
			final String[] lines = out.toString().split(linesep);
			assertThat(lines, arrayWithSize(1));
			assertEquals("==> f-3\t(t1 (s1 999))", lines[0]);
			out.reset();
		}
	}

	@Test
	public void testSimpleNegatedTest() throws ParseException {
		final Network network = new Network();
		final ByteArrayOutputStream out = initializeAppender(network);
		{
			final Pair<Queue<Object>, Queue<Warning>> returnValues = run(network, "(unwatch all)\n(watch facts)\n");
			assertThat(returnValues.getLeft(), empty());
			assertThat(returnValues.getRight(), empty());
			assertThat(out.toString(), isEmptyString());
		}
		{
			final Pair<Queue<Object>, Queue<Warning>> returnValues =
					run(network, "(deftemplate t1 (slot s1 (type INTEGER)))\n");
			assertThat(returnValues.getLeft(), empty());
			assertThat(returnValues.getRight(), empty());
			assertThat(out.toString(), isEmptyString());
		}
		{
			final Pair<Queue<Object>, Queue<Warning>> returnValues =
					run(network, "(defrule r1 (t1 (s1 ?x)) (not (test (> ?x 5))) => (assert (t1 (s1 999))) )\n");
			assertThat(returnValues.getLeft(), empty());
			assertThat(returnValues.getRight(), empty());
			assertThat(out.toString(), isEmptyString());
		}
		{
			final Pair<Queue<Object>, Queue<Warning>> returnValues = run(network, "(assert (t1 (s1 111)))\n");
			final Queue<Object> values = returnValues.getLeft();
			assertThat(values, hasSize(1));
			final Object value = values.iterator().next();
			assertThat(value, instanceOf(String.class));
			assertEquals("<Fact-2>", value);
			assertThat(returnValues.getRight(), empty());
			final String[] lines = out.toString().split(linesep);
			assertThat(lines, arrayWithSize(1));
			assertEquals("==> f-2\t(t1 (s1 111))", lines[0]);
			out.reset();
		}
		{
			final Pair<Queue<Object>, Queue<Warning>> returnValues = run(network, "(run)\n");
			assertThat(returnValues.getLeft(), empty());
			assertThat(returnValues.getRight(), empty());
			assertThat(out.toString(), isEmptyString());
			out.reset();
		}
		{
			final Pair<Queue<Object>, Queue<Warning>> returnValues = run(network, "(assert (t1 (s1 1)))\n");
			final Queue<Object> values = returnValues.getLeft();
			assertThat(values, hasSize(1));
			final Object value = values.iterator().next();
			assertThat(value, instanceOf(String.class));
			assertEquals("<Fact-3>", value);
			assertThat(returnValues.getRight(), empty());
			final String[] lines = out.toString().split(linesep);
			assertThat(lines, arrayWithSize(1));
			assertEquals("==> f-3\t(t1 (s1 1))", lines[0]);
			out.reset();
		}
		{
			final Pair<Queue<Object>, Queue<Warning>> returnValues = run(network, "(run)\n");
			assertThat(returnValues.getLeft(), empty());
			assertThat(returnValues.getRight(), empty());
			final String[] lines = out.toString().split(linesep);
			assertThat(lines, arrayWithSize(1));
			assertEquals("==> f-4\t(t1 (s1 999))", lines[0]);
			out.reset();
		}
	}

	@Test
	public void testRegularSlotVariableExistentialFactVariable() throws ParseException {
		final Network network = new Network();
		final ByteArrayOutputStream out = initializeAppender(network);
		{
			final Pair<Queue<Object>, Queue<Warning>> returnValues = run(network, "(unwatch all)\n(watch facts)\n");
			assertThat(returnValues.getLeft(), empty());
			assertThat(returnValues.getRight(), empty());
			assertThat(out.toString(), isEmptyString());
		}
		{
			final Pair<Queue<Object>, Queue<Warning>> returnValues =
					run(network, "(deftemplate t1 (slot s1 (type FACT-ADDRESS)))\n");
			assertThat(returnValues.getLeft(), empty());
			assertThat(returnValues.getRight(), empty());
			assertThat(out.toString(), isEmptyString());
		}
		{
			final Pair<Queue<Object>, Queue<Warning>> returnValues =
					run(network, "(defrule r1 ?x <- (initial-fact) (not (t1)) => (assert (t1 (s1 ?x))) )\n");
			assertThat(returnValues.getLeft(), empty());
			assertThat(returnValues.getRight(), empty());
			assertThat(out.toString(), isEmptyString());
		}
		{
			final Pair<Queue<Object>, Queue<Warning>> returnValues =
					run(network, "(defrule r2 (t1 (s1 ?x)) (exists ?x <- (initial-fact)) => )\n");
			assertThat(returnValues.getLeft(), empty());
			assertThat(returnValues.getRight(), empty());
			assertThat(out.toString(), isEmptyString());
		}
		{
			final Pair<Queue<Object>, Queue<Warning>> returnValues = run(network, "(run 1)\n");
			assertThat(returnValues.getLeft(), empty());
			assertThat(returnValues.getRight(), empty());
			final String[] lines = out.toString().split(linesep);
			assertThat(lines, arrayWithSize(1));
			assertEquals("==> f-2\t(t1 (s1 <Fact-1>))", lines[0]);
			out.reset();
		}
		{
			final Pair<Queue<Object>, Queue<Warning>> returnValues = run(network, "(watch rules)\n");
			assertThat(returnValues.getLeft(), empty());
			assertThat(returnValues.getRight(), empty());
			assertThat(out.toString(), isEmptyString());
		}
		{
			final Pair<Queue<Object>, Queue<Warning>> returnValues = run(network, "(run 1)\n");
			assertThat(returnValues.getLeft(), empty());
			assertThat(returnValues.getRight(), empty());
			final String[] lines = out.toString().split(linesep);
			assertThat(lines, arrayWithSize(1));
			assertThat(lines[0], isOneOf("FIRE r2 : *,f-2", "FIRE r2 : f-2,*"));
			out.reset();
		}
	}

	@Test
	public void testExistentialSlotAndFactVariable() throws ParseException {
		final Network network = new Network();
		final ByteArrayOutputStream out = initializeAppender(network);
		{
			final Pair<Queue<Object>, Queue<Warning>> returnValues = run(network, "(unwatch all)\n(watch facts)\n");
			assertThat(returnValues.getLeft(), empty());
			assertThat(returnValues.getRight(), empty());
			assertThat(out.toString(), isEmptyString());
		}
		{
			final Pair<Queue<Object>, Queue<Warning>> returnValues =
					run(network, "(deftemplate t1 (slot s1 (type FACT-ADDRESS)))\n");
			assertThat(returnValues.getLeft(), empty());
			assertThat(returnValues.getRight(), empty());
			assertThat(out.toString(), isEmptyString());
		}
		{
			final Pair<Queue<Object>, Queue<Warning>> returnValues =
					run(network, "(defrule r1 ?x <- (initial-fact) (not (t1)) => (assert (t1 (s1 ?x))) )\n");
			assertThat(returnValues.getLeft(), empty());
			assertThat(returnValues.getRight(), empty());
			assertThat(out.toString(), isEmptyString());
		}
		{
			final Pair<Queue<Object>, Queue<Warning>> returnValues =
					run(network, "(defrule r2 (exists ?x <- (t1 (s1 ~?x))) => )\n");
			assertThat(returnValues.getLeft(), empty());
			assertThat(returnValues.getRight(), empty());
			assertThat(out.toString(), isEmptyString());
		}
		{
			final Pair<Queue<Object>, Queue<Warning>> returnValues = run(network, "(run 1)\n");
			assertThat(returnValues.getLeft(), empty());
			assertThat(returnValues.getRight(), empty());
			final String[] lines = out.toString().split(linesep);
			assertThat(lines, arrayWithSize(1));
			assertEquals("==> f-2\t(t1 (s1 <Fact-1>))", lines[0]);
			out.reset();
		}
		{
			final Pair<Queue<Object>, Queue<Warning>> returnValues = run(network, "(watch rules)\n");
			assertThat(returnValues.getLeft(), empty());
			assertThat(returnValues.getRight(), empty());
			assertThat(out.toString(), isEmptyString());
		}
		{
			final Pair<Queue<Object>, Queue<Warning>> returnValues = run(network, "(run 1)\n");
			assertThat(returnValues.getLeft(), empty());
			assertThat(returnValues.getRight(), empty());
			final String[] lines = out.toString().split(linesep);
			assertThat(lines, arrayWithSize(1));
			assertThat(lines[0], isOneOf("FIRE r2 : *,f-1", "FIRE r2 : f-1,*"));
			out.reset();
		}
	}
}
