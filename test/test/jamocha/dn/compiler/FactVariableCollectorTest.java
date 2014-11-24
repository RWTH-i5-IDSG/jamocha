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
package test.jamocha.dn.compiler;

import static org.junit.Assert.*;

import java.io.StringReader;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.StringJoiner;

import org.jamocha.dn.ConstructCache.Defrule;
import org.jamocha.dn.compiler.FactVariableCollector;
import org.jamocha.filter.Path;
import org.jamocha.languages.clips.parser.SFPVisitorImpl;
import org.jamocha.languages.clips.parser.generated.ParseException;
import org.jamocha.languages.clips.parser.generated.SFPParser;
import org.jamocha.languages.clips.parser.generated.SFPStart;
import org.jamocha.languages.common.ConditionalElement;
import org.jamocha.languages.common.SingleFactVariable;
import org.jamocha.languages.common.Warning;
import org.junit.Test;

import test.jamocha.util.NetworkMockup;

/**
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 *
 */
public class FactVariableCollectorTest {

	private static String ruleName = "rule1";
	private static String templateName = "templ1";
	private static String template2Name = "templ2";
	private static String template3Name = "templ3";
	private static String slot1Name = "slot1";

	private static String templateString = "(deftemplate " + templateName + " (slot " + slot1Name
			+ " (type INTEGER)))\n";
	private static String template2String = "(deftemplate " + template2Name + " (slot " + slot1Name
			+ " (type INTEGER)))\n";
	private static String template3String = "(deftemplate " + template3Name + " (slot " + slot1Name
			+ " (type INTEGER)))\n";
	private static String preRule = "(defrule " + ruleName;
	private static String postRule = "=> )\n";

	private static Queue<Warning> run(final SFPParser parser, final SFPVisitorImpl visitor) throws ParseException {
		while (true) {
			final SFPStart n = parser.Start();
			if (n == null)
				return visitor.getWarnings();
			n.jjtAccept(visitor, null);
		}
	}

	private static List<ConditionalElement> clipsToCondition(final NetworkMockup ptn, final String condition)
			throws ParseException {
		final StringReader parserInput =
				new StringReader(new StringJoiner(" ").add(templateString).add(template2String).add(template3String)
						.add(preRule).add(condition).add(postRule).toString());
		final SFPParser parser = new SFPParser(parserInput);
		final SFPVisitorImpl visitor = new SFPVisitorImpl(ptn, ptn);
		run(parser, visitor);
		final Defrule rule = ptn.getRule(ruleName);
		return rule.getCondition().getConditionalElements();
	}

	@Test
	public void simpleTest() throws ParseException {
		final String input = "(and (" + templateName + " (" + slot1Name + " ?x)) (test (> ?x 10)) (test (< ?x 15)))";
		final NetworkMockup ptn = new NetworkMockup();
		final List<ConditionalElement> conditionalElements = clipsToCondition(ptn, input);
		assertEquals(1, conditionalElements.size());
		final Map<SingleFactVariable, Path> variables =
				FactVariableCollector.generatePaths(ptn.getInitialFactTemplate(), conditionalElements.get(0)).getRight();
		assertEquals(1, variables.size());
		final Entry<SingleFactVariable, Path> entry = variables.entrySet().iterator().next();
		assertEquals("Dummy", entry.getKey().getSymbol().getImage());
		assertEquals(templateName, entry.getKey().getTemplate().getName());
		assertSame(entry.getKey().getTemplate(), entry.getValue().getTemplate());
	}

	@Test
	public void aLitteMoreComplexTest() throws ParseException {
		final String input =
				"(and (" + templateName + " (" + slot1Name + " ?x)) (and ?y <- (" + template2Name + " (" + slot1Name
						+ " ?x)) (" + template3Name + " (" + slot1Name + " ?x))) (test (> ?x 10)) (test (< ?x 15)))";
		final NetworkMockup ptn = new NetworkMockup();
		final List<ConditionalElement> conditionalElements = clipsToCondition(ptn, input);
		assertEquals(1, conditionalElements.size());
		// FIXME see above
		final List<SingleFactVariable> variables =
				conditionalElements.get(0).accept(new FactVariableCollector()).getFactVariables();
		assertEquals(3, variables.size());
		assertEquals("Dummy", variables.get(0).getSymbol().getImage());
		assertEquals(templateName, variables.get(0).getTemplate().getName());
		assertEquals("?y", variables.get(1).getSymbol().getImage());
		assertEquals(template2Name, variables.get(1).getTemplate().getName());
		assertEquals("Dummy", variables.get(2).getSymbol().getImage());
		assertEquals(template3Name, variables.get(2).getTemplate().getName());
	}

}
