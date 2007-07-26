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
 * Translates SL code of an inform-ref to CLIPS code. An inform-ref has a
 * referential operator that (possibly) contains items referenced by a certain
 * proposition.
 * 
 * @author Daniel Grams, Georg Jennessen, Alexander Wilden
 * 
 */
class InformRef extends SLPerformativeTranslator {

	/**
	 * Translates SL code of an inform-ref to CLIPS code. An inform-ref has a
	 * referential operator that (possibly) contains items referenced by a
	 * certain proposition.
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

		StringBuilder result = new StringBuilder();
		result
				.append("(assert (agent-informRef-result (message %MSG%)(proposition \"");
		result.append(results.get(0).compile(SLCompileType.RULE_LHS));
		result.append("\")))");
		return result.toString();
	}

}
