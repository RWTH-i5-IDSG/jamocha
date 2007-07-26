/*
 * Copyright 2007 Fehmi Karanfil, Alexander Wilden
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
import org.jamocha.adapter.sl.configurations.ContentSLConfiguration;
import org.jamocha.adapter.sl.configurations.IdentifyingExpressionSLConfiguration;
import org.jamocha.adapter.sl.configurations.SLCompileType;
import org.jamocha.adapter.sl.configurations.SLConfiguration;
import org.jamocha.parser.sl.ParseException;
import org.jamocha.parser.sl.SLParser;

/**
 * Translates SL code of a query-ref to CLIPS code. A query-ref consists of a
 * referential expression for which is checked if there exist items that fulfill
 * it. If this is the case these items are listed in a result fact.
 * 
 * @author Fehmi Karanfil, Alexander Wilden
 * 
 */
class QueryRef extends SLPerformativeTranslator {

	/**
	 * Translates SL code of a query-ref to CLIPS code. A query-ref consists of
	 * a referential expression for which is checked if there exist items that
	 * fulfill it. If this is the case these items are listed in a result fact.
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
		String ruleName = "query-ref-" + uniqueId;
		String bindName = "?*query-ref-" + uniqueId + "*";

		IdentifyingExpressionSLConfiguration conf = (IdentifyingExpressionSLConfiguration) results
				.get(0);
		String refOp = conf.getRefOp().compile(SLCompileType.RULE_LHS);
		String binding = conf.getTermOrIE().compile(SLCompileType.RULE_RESULT);

		StringBuilder result = new StringBuilder();
		result.append("(bind ");
		result.append(bindName);
		result.append(" (create$))");

		result.append("(defrule ");
		result.append(ruleName);
		result.append(" ");
		result.append(conf.getWff().compile(SLCompileType.RULE_LHS));
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

		result.append("(assert (agent-queryRef-result (message %MSG%)(refOp ");
		result.append(refOp);
		result.append(")(items ");
		result.append(bindName);
		result.append(")))");
		return result.toString();
	}
}
