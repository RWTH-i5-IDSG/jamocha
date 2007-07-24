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
import org.jamocha.adapter.sl.configurations.ActionSLConfiguration;
import org.jamocha.adapter.sl.configurations.ContentSLConfiguration;
import org.jamocha.adapter.sl.configurations.FunctionCallOrFactSLConfiguration;
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
public class Cancel {

	/**
	 * A private constructor to force access only in a static way.
	 * 
	 */
	private Cancel() {
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
			throw new AdapterTranslationException("Unexpected structure of the content. Expected 1 Expression.");
		}
		ActionSLConfiguration actConf = (ActionSLConfiguration) results.get(0);
		FunctionCallOrFactSLConfiguration functionConf = (FunctionCallOrFactSLConfiguration) actConf
				.getAction();
		String performative = functionConf.getName().compile(
				SLCompileType.ASSERT);
		String oldContent = functionConf.getSlot("content",
				SLCompileType.ASSERT).compile(SLCompileType.ASSERT);
		FunctionCallOrFactSLConfiguration agentConf = (FunctionCallOrFactSLConfiguration) actConf
				.getAgent();
		String agent = agentConf.getSlot("name", SLCompileType.ASSERT).compile(
				SLCompileType.ASSERT);
		StringBuilder result = new StringBuilder();
		if (oldContent != null) {
			result.append("(assert (agent-cancel-result (message %MSG%)(initiator \"");
			result.append(agent);
			result.append("\")(performative \"");
			result.append(performative);
			result.append("\")(messageContent ");
			result.append(oldContent);
			result.append(")))");
		}
		System.out.println(result);
		return result.toString();
	}
}
