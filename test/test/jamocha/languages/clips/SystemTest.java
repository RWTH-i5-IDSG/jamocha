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

import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.commons.lang3.tuple.Pair;
import org.jamocha.dn.Network;
import org.jamocha.languages.clips.parser.SFPVisitorImpl;
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

	private static Pair<Queue<Object>, Queue<Warning>> run(final Network network,
			final String parserInput) throws ParseException {
		final SFPParser parser = new SFPParser(new StringReader(parserInput));
		final SFPVisitorImpl visitor = new SFPVisitorImpl(network, network);
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

	@Test
	public void testSimpleWatchedFactAssertion() throws ParseException {
		final Network network = new Network();
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		network.addAppender(out, true);
		{
			final Pair<Queue<Object>, Queue<Warning>> returnValues =
					run(network, "(unwatch all)\n(watch facts)\n");
			assertTrue(returnValues.getLeft().isEmpty());
			assertTrue(returnValues.getRight().isEmpty());
			assertThat(out.toString(), isEmptyString());
		}
		{
			final Pair<Queue<Object>, Queue<Warning>> returnValues =
					run(network, "(deftemplate t1 (slot s1 (type INTEGER)))\n");
			assertTrue(returnValues.getLeft().isEmpty());
			assertTrue(returnValues.getRight().isEmpty());
			assertThat(out.toString(), isEmptyString());
		}
		{
			final Pair<Queue<Object>, Queue<Warning>> returnValues =
					run(network, "(assert (t1 (s1 2)))\n");
			final Queue<Object> values = returnValues.getLeft();
			assertThat(values, hasSize(1));
			final Object value = values.iterator().next();
			assertThat(value, instanceOf(String.class));
			assertTrue(String.valueOf(value).equals("<Fact-2>"));
			assertTrue(returnValues.getRight().isEmpty());
			final String output = out.toString();
			assertThat(output, not(isEmptyString()));
			final String[] lines = output.split("\\v");
			assertThat(lines, arrayWithSize(1));
			assertEquals(lines[0], "==> f-2\t(t1 (s1 2))");
			out.reset();
		}
	}

	@Test
	public void testSimpleRuleExecution() throws ParseException {
		final Network network = new Network();
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		network.addAppender(out, true);
		{
			final Pair<Queue<Object>, Queue<Warning>> returnValues =
					run(network, "(unwatch all)\n(watch facts)\n");
			assertTrue(returnValues.getLeft().isEmpty());
			assertTrue(returnValues.getRight().isEmpty());
			assertThat(out.toString(), isEmptyString());
		}
		{
			final Pair<Queue<Object>, Queue<Warning>> returnValues =
					run(network, "(deftemplate t1 (slot s1 (type INTEGER)))\n");
			assertTrue(returnValues.getLeft().isEmpty());
			assertTrue(returnValues.getRight().isEmpty());
			assertThat(out.toString(), isEmptyString());
		}
		{
			final Pair<Queue<Object>, Queue<Warning>> returnValues =
					run(network, "(defrule r1 (t1 (s1 5)) => (assert (t1 (s1 999))) )\n");
			assertTrue(returnValues.getLeft().isEmpty());
			assertTrue(returnValues.getRight().isEmpty());
			assertThat(out.toString(), isEmptyString());
		}
		{
			final Pair<Queue<Object>, Queue<Warning>> returnValues =
					run(network, "(assert (t1 (s1 2)))\n");
			final Queue<Object> values = returnValues.getLeft();
			assertThat(values, hasSize(1));
			final Object value = values.iterator().next();
			assertThat(value, instanceOf(String.class));
			assertTrue(String.valueOf(value).equals("<Fact-2>"));
			assertTrue(returnValues.getRight().isEmpty());
			final String[] lines = out.toString().split("\\v");
			assertThat(lines, arrayWithSize(1));
			assertEquals(lines[0], "==> f-2\t(t1 (s1 2))");
			out.reset();
		}
		{
			final Pair<Queue<Object>, Queue<Warning>> returnValues =
					run(network, "(assert (t1 (s1 5)))\n");
			final Queue<Object> values = returnValues.getLeft();
			assertThat(values, hasSize(1));
			final Object value = values.iterator().next();
			assertThat(value, instanceOf(String.class));
			assertTrue(String.valueOf(value).equals("<Fact-3>"));
			assertTrue(returnValues.getRight().isEmpty());
			final String[] lines = out.toString().split("\\v");
			assertThat(lines, arrayWithSize(1));
			assertEquals(lines[0], "==> f-3\t(t1 (s1 5))");
			out.reset();
		}
		{
			final Pair<Queue<Object>, Queue<Warning>> returnValues = run(network, "(run)\n");
			assertTrue(returnValues.getLeft().isEmpty());
			assertTrue(returnValues.getRight().isEmpty());
			final String[] lines = out.toString().split("\\v");
			assertThat(lines, arrayWithSize(1));
			assertEquals(lines[0], "==> f-4\t(t1 (s1 999))");
			out.reset();
		}
	}

}