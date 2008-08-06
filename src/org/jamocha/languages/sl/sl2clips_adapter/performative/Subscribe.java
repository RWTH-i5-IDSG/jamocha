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
package org.jamocha.languages.sl.sl2clips_adapter.performative;

import java.util.List;

import org.jamocha.languages.sl.parser.ParseException;
import org.jamocha.languages.sl.parser.SLParser;
import org.jamocha.languages.sl.sl2clips_adapter.AdapterTranslationException;
import org.jamocha.languages.sl.sl2clips_adapter.configurations.ContentSLConfiguration;
import org.jamocha.languages.sl.sl2clips_adapter.configurations.IdentifyingExpressionSLConfiguration;
import org.jamocha.languages.sl.sl2clips_adapter.configurations.SLCompileType;
import org.jamocha.languages.sl.sl2clips_adapter.configurations.SLConfiguration;

/**
 * Translates SL code of a subscribe to CLIPS code. Subscribe is a presistent
 * version of the query-ref performative. An inform should be send whenever some
 * object changes as long as it isn't canceled.
 * 
 * @author Alexander Wilden
 * 
 */
class Subscribe extends SLPerformativeTranslator {

	/**
	 * Translates SL code of a subscribe to CLIPS code. Subscribe is a
	 * presistent version of the query-ref performative. An inform should be
	 * send whenever some object changes as long as it isn't canceled.
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
		checkContentItemCount(results, 2);

		int uniqueId = getUniqueId();
		String ruleName = "subscribe-" + uniqueId;

		IdentifyingExpressionSLConfiguration conf = (IdentifyingExpressionSLConfiguration) results
				.get(0);
		String refOp = conf.getRefOp().compile(SLCompileType.RULE_LHS);
		String binding = conf.getTermOrIE().compile(
				SLCompileType.ACTION_AND_ASSERT);

		StringBuilder result = new StringBuilder();
		result.append("(defrule ");
		result.append(ruleName);
		result.append(" ");
		result.append(conf.getWff().compile(SLCompileType.RULE_LHS));
		result.append(" => ");
		result.append("(assert (agent-subcribe-result (message %MSG%)(refOp ");
		result.append(refOp).append(")(item ");
		result.append(binding);
		result.append("))))");

		result
				.append("(assert (agent-message-rule-pairing (message %MSG%)(rule-name \"");
		result.append(ruleName);
		result.append("\")))");
		return result.toString();
	}
}