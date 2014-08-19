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

import static org.junit.Assert.*;

import java.io.StringReader;
import java.util.Queue;

import org.jamocha.dn.ConstructCache.Defrule;
import org.jamocha.languages.clips.parser.SFPVisitorImpl;
import org.jamocha.languages.clips.parser.generated.ParseException;
import org.jamocha.languages.clips.parser.generated.SFPParser;
import org.jamocha.languages.clips.parser.generated.SFPStart;
import org.jamocha.languages.common.RuleCondition;
import org.jamocha.languages.common.Warning;
import org.jamocha.logging.formatter.RuleConditionFormatter;
import org.junit.Test;

import test.jamocha.util.NetworkMockup;

/**
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 *
 */
public class RuleConditionProcessorTest {

	private static String templateString = "(deftemplate templ1 (slot slot1 (type INTEGER)))\n";

	private static Queue<Warning> run(final SFPParser parser, final SFPVisitorImpl visitor)
			throws ParseException {
		while (true) {
			final SFPStart n = parser.Start();
			if (n == null)
				return visitor.getWarnings();
			n.jjtAccept(visitor, null);
		}
	}

	private static String clipsToCondition(String input) throws ParseException {
		StringReader parserInput = new StringReader(input);
		SFPParser parser = new SFPParser(parserInput);
		NetworkMockup ptn = new NetworkMockup();
		final SFPVisitorImpl visitor = new SFPVisitorImpl(ptn, ptn);
		run(parser, visitor);
		Defrule rule = ptn.getRule("rule1");
		rule.getCondition().flatten();
		return RuleConditionFormatter.formatRC(rule.getCondition());
	}

	@Test
	public void trivialTest() throws ParseException {
		String input = templateString + "(defrule rule1 (templ1 (slot1 10)) => )\n";
		String output = clipsToCondition(input);
		assertTrue(output
				.matches("\\(\\(templ1 Dummy:\\d* \\(slot1 Dummy:(\\d*)\\)\\) \\(test \\(= Dummy:\\1 10\\)\\)\\)"));
	}

	@Test
	public void surroundingAddTest() throws ParseException {
		String input =
				templateString
						+ "(defrule rule1 (templ1 (slot1 ?x)) (test (> ?x 10)) (test (< ?x 15)) => )\n";
		String output = clipsToCondition(input);
		assertTrue(output
				.matches("\\(\\(templ1 Dummy:\\d* \\(slot1 \\?x\\)\\) \\(and \\(test \\(> \\?x 10\\)\\) \\(test \\(< \\?x 15\\)\\)\\)\\)"));
	}

	@Test
	public void simpleUnexandableOr() throws ParseException {
		String input =
				templateString
						+ "(defrule rule1 (templ1 (slot1 ?x)) (or (test (> ?x 10)) (test (< ?x 15))) => )\n";
		String output = clipsToCondition(input);
		assertTrue(output
				.matches("\\(\\(templ1 Dummy:\\d* \\(slot1 \\?x\\)\\) \\(or \\(test \\(> \\?x 10\\)\\) \\(test \\(< \\?x 15\\)\\)\\)\\)"));
	}

	@Test
	public void simpleExandableOr() throws ParseException {
		String input =
				templateString
						+ "(defrule rule1 (templ1 (slot1 ?x)) (or (test (> ?x 10)) (test (< ?x 15))) (test (< ?x 16)) => )\n";
		String output = clipsToCondition(input);
		assertTrue(output
				.matches("\\(\\(templ1 Dummy:\\d* \\(slot1 \\?x\\)\\) \\(or \\(and \\(shared \\(test \\(< \\?x 16\\)\\)\\) \\(test \\(> \\?x 10\\)\\)\\) \\(and \\(shared \\(test \\(< \\?x 16\\)\\)\\) \\(test \\(< \\?x 15\\)\\)\\)\\)\\)"));
	}

	@Test
	public void complexExandableOr() throws ParseException {
		String input =
				templateString
						+ "(defrule rule1 (templ1 (slot1 ?x)) (or (test (< ?x 1)) (test (< ?x 2))) (or (test (< ?x 3)) (test (< ?x 4))) (test (< ?x 5)) (test (< ?x 6)) => )\n";
		String output = clipsToCondition(input);
		assertTrue(output
				.matches("\\(\\(templ1 Dummy:\\d* \\(slot1 \\?x\\)\\) \\(or "
						+ "\\(and \\(shared \\(and \\(test \\(< \\?x 5\\)\\) \\(test \\(< \\?x 6\\)\\)\\)\\) \\(shared \\(test \\(< \\?x (1|2)\\)\\)\\) \\(shared \\(test \\(< \\?x (3|4)\\)\\)\\)\\) "
						+ "\\(and \\(shared \\(and \\(test \\(< \\?x 5\\)\\) \\(test \\(< \\?x 6\\)\\)\\)\\) \\(shared \\(test \\(< \\?x (1|2)\\)\\)\\) \\(shared \\(test \\(< \\?x (3|4)\\)\\)\\)\\) "
						+ "\\(and \\(shared \\(and \\(test \\(< \\?x 5\\)\\) \\(test \\(< \\?x 6\\)\\)\\)\\) \\(shared \\(test \\(< \\?x (1|2)\\)\\)\\) \\(shared \\(test \\(< \\?x (3|4)\\)\\)\\)\\) "
						+ "\\(and \\(shared \\(and \\(test \\(< \\?x 5\\)\\) \\(test \\(< \\?x 6\\)\\)\\)\\) \\(shared \\(test \\(< \\?x (1|2)\\)\\)\\) \\(shared \\(test \\(< \\?x (3|4)\\)\\)\\)\\)"
						+ "\\)\\)"));
	}
}
