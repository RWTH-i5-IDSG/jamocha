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
import java.util.Queue;
import java.util.StringJoiner;

import javax.accessibility.AccessibleRelationSet;

import org.hamcrest.Matchers;
import org.jamocha.dn.ConstructCache.Defrule;
import org.jamocha.dn.compiler.FactVariableCollector;
import org.jamocha.dn.compiler.SymbolToPathTranslator;
import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.function.fwa.FunctionWithArgumentsComposite;
import org.jamocha.function.impls.predicates.Greater;
import org.jamocha.languages.clips.parser.SFPVisitorImpl;
import org.jamocha.languages.clips.parser.generated.ParseException;
import org.jamocha.languages.clips.parser.generated.SFPParser;
import org.jamocha.languages.clips.parser.generated.SFPStart;
import org.jamocha.languages.common.ConditionalElement;
import org.jamocha.languages.common.Warning;
import org.jamocha.languages.common.ConditionalElement.TestConditionalElement;
import org.junit.Test;

import test.jamocha.util.NetworkMockup;

/**
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 *
 */
public class SymbolToPathTranslatorTest {

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
				new StringReader(new StringJoiner(" ").add(templateString).add(template2String)
						.add(template3String).add(preRule).add(condition).add(postRule).toString());
		final SFPParser parser = new SFPParser(parserInput);
		final NetworkMockup ptn = new NetworkMockup();
		final SFPVisitorImpl visitor = new SFPVisitorImpl(ptn, ptn);
		run(parser, visitor);
		final Defrule rule = ptn.getRule(ruleName);
		return rule.getCondition().getConditionalElements();
	}

	@Test
	public void simpleTest() throws ParseException {
		final String input = "(and (" + templateName + " (" + slot1Name + " ?x)) (test (> ?x 10)))";
		final List<ConditionalElement> conditionalElements = clipsToCondition(input);
		assertEquals(1, conditionalElements.size());
		assertEquals(2, conditionalElements.get(0).getChildren().size());
		assertThat(conditionalElements.get(0).getChildren().get(1), Matchers.instanceOf(TestConditionalElement.class));
		FunctionWithArguments fwa = ((TestConditionalElement)(conditionalElements.get(0).getChildren().get(1))).getPredicateWithArguments().accept(new SymbolToPathTranslator(FactVariableCollector.collectPaths(conditionalElements.get(0)))).getResult();
		// FIXME hier weitermachen
	}

}
