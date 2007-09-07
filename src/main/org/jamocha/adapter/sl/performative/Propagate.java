/*
 * Copyright 2007 Alexander Wilden
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.jamocha.org/
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
import org.jamocha.adapter.sl.configurations.ActionSLConfiguration;
import org.jamocha.adapter.sl.configurations.ContentSLConfiguration;
import org.jamocha.adapter.sl.configurations.IdentifyingExpressionSLConfiguration;
import org.jamocha.adapter.sl.configurations.SLCompileType;
import org.jamocha.adapter.sl.configurations.SLConfiguration;
import org.jamocha.parser.sl.ParseException;
import org.jamocha.parser.sl.SLParser;

/**
 * This class walks through an SL code tree and translates it to CLIPS depending
 * on the given performative.
 * 
 * @author Alexander Wilden
 * 
 */
class Propagate extends SLPerformativeTranslator {

	/**
	 * Translates SL code of a propagate to CLIPS code. A propagate contains a
	 * referential expression denoting the agents the message should be send to,
	 * an ACL-communicative act that should be send out and a Proposition giving
	 * additional conditions allowing the message to be send.
	 * <p>
	 * In difference to proxy propagate additionally treads the embedded message
	 * as if it was incoming and acts according to its content.
	 * 
	 * @param slContent
	 *            The SL content we have to translate.
	 * @return CLIPS commands that represent the given SL code.
	 * @throws AdapterTranslationException
	 *             if the SLParser throws an Exception or anything else abnormal
	 *             happens.
	 */
	public String getCLIPS(String slContent) throws AdapterTranslationException {
		ContentSLConfiguration contentConf;
		try {
			contentConf = SLParser.parse(slContent);
		} catch (ParseException e) {
			throw new AdapterTranslationException(
					"Could not translate from SL to CLIPS.", e);
		}
		List<SLConfiguration> results = contentConf.getExpressions();
		checkContentItemCount(results, 3);

		int uniqueId = getUniqueId();
		String ruleName = "propagate-" + uniqueId;
		String bindName = "?*propagate-" + uniqueId + "*";

		IdentifyingExpressionSLConfiguration conf = (IdentifyingExpressionSLConfiguration) results
				.get(0);
		String refOp = conf.getRefOp().compile(SLCompileType.RULE_LHS);
		String binding = conf.getTermOrIE().compile(SLCompileType.RULE_RESULT);

		ActionSLConfiguration actConf = (ActionSLConfiguration) results.get(1);
		// In difference to proxy we handle this message as incoming and later
		// will work on this as template for the message to forward.
		String proxyMessage = actConf.compile(SLCompileType.ASSERT_MESSAGE);

		StringBuilder result = new StringBuilder();
		// binding an empty list
		result.append("(bind ");
		result.append(bindName);
		result.append(" (create$ ))");

		// creating the rule to get the addressees and check the proposition
		result.append("(defrule ");
		result.append(ruleName);
		result.append(" ");
		result.append(conf.getWff().compile(SLCompileType.RULE_LHS));
		result.append(results.get(2).compile(SLCompileType.RULE_LHS));
		result.append(" => ");
		result.append("(bind ");
		result.append(bindName);
		result.append(" (insert-list$ ");
		result.append(bindName);
		result.append(" 1 ");
		result.append(binding);
		result.append(")))");

		result.append("(fire)");

		result.append("(undefrule \"");
		result.append(ruleName);
		result.append("\")");

		// so the result will have target agents if agents could be found that
		// matched the wff AND the second proposition allowed the message to be
		// send at all.
		result.append("(assert (agent-propagate-result (message %MSG%)");
		result.append("(propagateMessage ");
		result.append(proxyMessage);
		result.append(")(refOp \"");
		result.append(refOp);
		result.append("\")(agents ");
		result.append(bindName);
		result.append(")))");

		return result.toString();
	}

}
