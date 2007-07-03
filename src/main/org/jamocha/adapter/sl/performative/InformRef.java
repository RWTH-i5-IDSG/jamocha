/*
 * Copyright 2007 Alexander Wilden
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
import org.jamocha.adapter.sl.configurations.IdentifyingExpressionSLConfiguration;
import org.jamocha.adapter.sl.configurations.SLCompileType;
import org.jamocha.adapter.sl.configurations.SLConfiguration;
import org.jamocha.parser.sl.ParseException;
import org.jamocha.parser.sl.SLParser;

/**
 * This class walks through an SL code tree and translates it to CLIPS depending
 * on the given performative.
 * 
 * @author Daniel & Georg Grams-Jennessen
 * 
 */
public class InformRef {

	/**
	 * A private constructor to force access only in a static way.
	 * 
	 */
	private InformRef() {
	}

	/**
	 * Translates SL code of a request to CLIPS code. A request only contains
	 * one action.
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
		if (results.size() != 1) {
			// TODO: Add more Exceptions for different things extending
			// AdapterTranslationException that tell more about the nature of
			// the problem!
			throw new AdapterTranslationException("Error");
		}
		StringBuilder result = new StringBuilder();
		IdentifyingExpressionSLConfiguration conf = (IdentifyingExpressionSLConfiguration) results
				.get(0);
		String refOp = conf.getRefOp().compile(SLCompileType.RULE_LHS);
		String binding = conf.getTermOrIE().compile(SLCompileType.RULE_RESULT);
		result.append("(bind ?*informRef-temp* (create$))");
		result.append("(defrule inform-ref ");
		result.append(conf.getWff().compile(SLCompileType.RULE_LHS));
		result.append(" => ");
		result
				.append("(bind ?*informRef-temp* (insert-list$ ?*informRef-temp* 1 ");
		result.append(binding);
		result.append(")))");
		result.append("(fire)");
		result.append("(undefrule \"inform-ref\")");
		result.append("(assert (agent-informRef-result (message %MSG%)(refOp ");
		result.append(refOp);
		result.append(")(items ?*informRef-temp*)))");
		System.out.println(result);
		return result.toString();
	}

}
