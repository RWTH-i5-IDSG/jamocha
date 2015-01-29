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

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.isEmptyString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

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
public class BindTest {

	final static String linesep = System.lineSeparator();

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

	private static Pair<Queue<Object>, Queue<Warning>> run(final Network network, final String parserInput)
			throws ParseException {
		final SFPParser parser = new SFPParser(new StringReader(parserInput + linesep));
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

	private static ByteArrayOutputStream initializeAppender(Network network) {
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		network.clearAppender();
		network.addAppender(out, true);
		return out;
	}

	@Test
	public void testVariableBinding() throws ParseException {
		final Network network = new Network();
		final ByteArrayOutputStream out = initializeAppender(network);
		run(network, "(unwatch all)");
		{
			final Pair<Queue<Object>, Queue<Warning>> returnValues = run(network, "(bind ?x 2)");
			final Queue<Object> values = returnValues.getLeft();
			assertThat(values, hasSize(1));
			final Object value = values.iterator().next();
			assertThat(value, instanceOf(String.class));
			final long two = Long.parseLong((String) value);
			assertEquals(2L, two);
			assertThat(returnValues.getRight(), empty());
			assertThat(out.toString(), isEmptyString());
			out.reset();
		}
	}

	@Test
	public void testVariableUnbinding() throws ParseException {
		final Network network = new Network();
		final ByteArrayOutputStream out = initializeAppender(network);
		run(network, "(unwatch all)");
		{
			final Pair<Queue<Object>, Queue<Warning>> returnValues = run(network, "(bind ?x 2)");
			final Queue<Object> values = returnValues.getLeft();
			assertThat(values, hasSize(1));
			final Object value = values.iterator().next();
			assertThat(value, instanceOf(String.class));
			final long two = Long.parseLong((String) value);
			assertEquals(2L, two);
			assertThat(returnValues.getRight(), empty());
			assertThat(out.toString(), isEmptyString());
			out.reset();
		}
		{
			final Pair<Queue<Object>, Queue<Warning>> returnValues = run(network, "(bind ?x)");
			final Queue<Object> values = returnValues.getLeft();
			assertThat(values, hasSize(0));
			assertThat(returnValues.getRight(), empty());
			assertThat(out.toString(), isEmptyString());
			out.reset();
		}
	}
}
