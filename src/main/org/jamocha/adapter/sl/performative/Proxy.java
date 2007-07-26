/*
 * Copyright 2007 Mustafa Karafil, Fehmi Karanfil, Alexander Wilden
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
import org.jamocha.adapter.sl.configurations.FunctionCallOrFactSLConfiguration;
import org.jamocha.adapter.sl.configurations.IdentifyingExpressionSLConfiguration;
import org.jamocha.adapter.sl.configurations.SLCompileType;
import org.jamocha.adapter.sl.configurations.SLConfiguration;
import org.jamocha.parser.sl.ParseException;
import org.jamocha.parser.sl.SLParser;

/**
 * This class walks through an SL code tree and translates it to CLIPS depending
 * on the given performative.
 * 
 * @author Mustafa Karafil, Fehmi Karanfil, Alexander Wilden
 * 
 */
class Proxy extends SLPerformativeTranslator {

	/**
	 * Translates SL code of a proxy to CLIPS code. A proxy only contains a
	 * tuple of referential expression.
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

		IdentifyingExpressionSLConfiguration conf = (IdentifyingExpressionSLConfiguration) results
				.get(0);
		String refOp = conf.getRefOp().compile(SLCompileType.RULE_LHS);
		String binding = conf.getTermOrIE().compile(SLCompileType.RULE_RESULT);
		
		StringBuilder result = new StringBuilder();
		result.append("(bind ?*proxy-temp* (create$))");
		result.append("(defrule proxy ");
		result.append(conf.getWff().compile(SLCompileType.RULE_LHS));
		result.append(" => ");
		result.append("(bind ?*proxy-temp* (insert-list$ ?*proxy-temp* 1 ");
		result.append(binding);
		result.append(")))");
		result.append("(fire)");
		result.append("(undefrule \"proxy\")");

		ActionSLConfiguration actConf = (ActionSLConfiguration) results.get(1);
		FunctionCallOrFactSLConfiguration functionConf = (FunctionCallOrFactSLConfiguration) actConf
				.getAction();
		String performative = functionConf.getName().compile(
				SLCompileType.ASSERT);
		String oldContent = functionConf.getSlot("content",
				SLCompileType.ASSERT).compile(SLCompileType.ASSERT);

		if (oldContent != null) {
			result
					.append("(assert (agent-proxy-result (message %MSG%)(performative \"");
			result.append(performative);
			result.append("\")(messageContent \"");
			result.append(oldContent);
			result.append("\")(refOp \"");
			result.append(refOp);
			result.append("\")(agents ?*proxy-temp*)))");
		}
		System.out.println(result);
		return result.toString();
	}

}
