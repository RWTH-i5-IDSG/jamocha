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
import org.jamocha.adapter.sl.configurations.ContentSLConfiguration;
import org.jamocha.adapter.sl.configurations.SLCompileType;
import org.jamocha.adapter.sl.configurations.SLConfiguration;
import org.jamocha.parser.sl.ParseException;
import org.jamocha.parser.sl.SLParser;

/**
 * Translates SL code of a not-understood to CLIPS code. A not-understood
 * performative consists of an action or event and a proposition. The action
 * resp. event is something the sender didn't understand and the proposition
 * tries give a reason for why the action resp. event was not understood.
 * 
 * @author Daniel Grams, Georg Jennessen, Alexander Wilden
 * 
 */
class NotUnderstood extends SLPerformativeTranslator {

	/**
	 * Translates SL code of a not-understood to CLIPS code. A not-understood
	 * performative consists of an action or event and a proposition. The action
	 * resp. event is something the sender didn't understand and the proposition
	 * tries give a reason for why the action resp. event was not understood.
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

		StringBuilder result = new StringBuilder();
		result
				.append("(assert (agent-notUnderstood-result (message %MSG%)(action \"");
		result.append(results.get(0).compile(SLCompileType.ACTION_AND_ASSERT));
		result.append("\")(proposition \"");
		result.append(results.get(1).compile(SLCompileType.RULE_LHS));
		result.append("\")))");
		System.out.println(result);
		return result.toString();
	}

}
