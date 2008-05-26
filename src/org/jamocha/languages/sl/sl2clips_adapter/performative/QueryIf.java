/*
 * Copyright 2007 Mustafa Karafil, Alexander Wilden
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
import org.jamocha.languages.sl.sl2clips_adapter.configurations.SLCompileType;
import org.jamocha.languages.sl.sl2clips_adapter.configurations.SLConfiguration;

/**
 * Translates SL code of a query-if to CLIPS code. A query-if contains just one
 * proposition that should be either validated or negated.
 * 
 * @author Mustafa Karafil, Alexander Wilden
 * 
 */
class QueryIf extends SLPerformativeTranslator {

	/**
	 * Translates SL code of a query-if to CLIPS code. A query-if contains just
	 * one proposition that should be either validated or negated.
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
		checkContentItemCount(results, 1);

		int uniqueId = getUniqueId();
		String ruleName = "query-if-" + uniqueId;
		String bindName = "?*query-if-" + uniqueId + "*";

		StringBuilder result = new StringBuilder();
		result.append("(bind ");
		result.append(bindName);
		result.append(" FALSE)");

		result.append("(defrule ");
		result.append(ruleName);
		result.append(" ");
		result.append(results.get(0).compile(SLCompileType.RULE_LHS));
		result.append(" => ");
		result.append("(bind ");
		result.append(bindName);
		result.append(" TRUE)");
		result.append(")");

		result.append("(fire)");

		result.append("(undefrule \"");
		result.append(ruleName);
		result.append("\")");

		result.append("(assert (agent-queryIf-result (message %MSG%)(result ");
		result.append(bindName);
		result.append(")))");

		return result.toString();
	}

}