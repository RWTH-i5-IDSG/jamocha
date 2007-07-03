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
public class Propose {

	/**
	 * A private constructor to force access only in a static way.
	 * 
	 */
	private Propose() {
	}

	/**
	 * Translates SL code of a propose to CLIPS code. A propose contains an
	 * action and a proposition, which specifies the preconditions for 
	 * performing the action. The action will be performed by the sender, if 
	 * the receiver accepts the preconditions.
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
		StringBuffer result = new StringBuffer();
		List<SLConfiguration> results = contentConf.getExpressions();
		
		if(results.size() != 2)
			throw new AdapterTranslationException("Error");
		
	
		result.append("(assert (agent-propose-result (action");
		result.append(results.get(0).compile(SLCompileType.ASSERT));
		result.append(") propositions(");
		for(int i = 1; i < results.size(); i++){
			result.append(results.get(i).compile(SLCompileType.ASSERT));
		}
		result.append(")))");
		
		return result.toString();
		}
		
	}
