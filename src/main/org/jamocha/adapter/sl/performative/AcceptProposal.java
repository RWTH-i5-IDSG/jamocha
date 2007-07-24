/*
 * Copyright 2007 Markus Kucay
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://jamocha.sourceforge.net/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jamocha.adapter.sl.performative;

import java.util.List;

import org.jamocha.adapter.AdapterTranslationException;
import org.jamocha.adapter.sl.configurations.ContentSLConfiguration;
import org.jamocha.adapter.sl.configurations.SLCompileType;
import org.jamocha.adapter.sl.configurations.SLConfiguration;
import org.jamocha.parser.sl.ParseException;
import org.jamocha.parser.sl.SLParser;

/**
 * This class walks through an SL code tree and translates it to CLIPS depending
 * on the given performative.
 * 
 * @author Markus Kucay
 * 
 */
public class AcceptProposal {

	private static long uniqueId = 1;

	/**
	 * A private constructor to force access only in a static way.
	 * 
	 */
	private AcceptProposal() {
	}

	/**
	 * Translates SL code of an accept-propose to CLIPS code. An accept-propose
	 * contains an action and a proposition. The action will be performed when
	 * the given preconditions, given by the proposition, come true. This
	 * proposition was accepted by another agent after he received a propose by
	 * this agent.
	 * 
	 * @param slContent
	 *            The SL content we have to translate.
	 * @return CLIPS commands that represent the given SL code.
	 * @throws AdapterTranslationException
	 *             if the SLParser throws an Exception or anything else unnormal
	 *             happens.
	 */
	public static String getCLIPS(String slContent)
			throws AdapterTranslationException {

		ContentSLConfiguration contentConf;
		try {
			contentConf = SLParser.parse(slContent);
		} catch (ParseException e) {
			throw new AdapterTranslationException(
					"Could not translate from SL to CLIPS.", e);
		}
		List<SLConfiguration> results = contentConf.getExpressions();
		if (results.size() != 2) {
			throw new AdapterTranslationException("Unexpected structure of the content. Expected 2 Expressions.");
		}
		StringBuilder result = new StringBuilder();
		String ruleName = "accept-proposal" + uniqueId++;

		result.append("(defrule ");
		result.append(ruleName);
		result.append(" ");
		result.append(results.get(1).compile(SLCompileType.RULE_LHS));
		result.append(" => ");
		result
				.append("(assert (agent-acceptProposal-result (message %MSG%)(result \"");
		result.append(results.get(0).compile(SLCompileType.ACTION_AND_ASSERT));
		result.append("\")))");
		result.append("(undefrule ");
		result.append(ruleName);
		result.append("))");
		result
				.append("(assert (agent-message-rule-pairing (message %MSG%)(ruleName \"");
		result.append(ruleName);
		result.append("\")))");

		return result.toString();
	}
}
