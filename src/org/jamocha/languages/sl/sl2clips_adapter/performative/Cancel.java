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
import org.jamocha.languages.sl.sl2clips_adapter.configurations.ActionSLConfiguration;
import org.jamocha.languages.sl.sl2clips_adapter.configurations.ContentSLConfiguration;
import org.jamocha.languages.sl.sl2clips_adapter.configurations.FunctionCallOrFactSLConfiguration;
import org.jamocha.languages.sl.sl2clips_adapter.configurations.SLCompileType;
import org.jamocha.languages.sl.sl2clips_adapter.configurations.SLConfiguration;

/**
 * Translates SL code of a cancel to CLIPS code. A cancel informs the receiver
 * that an ongoing or outstanding action isn't necessary any more and should be
 * canceled. There are different ways to find out which action should be
 * canceled. On the one hand one could cancel any action started by a message
 * with the same conversation-id as this cancel speech act. On the other hand
 * one can check the message content against the content of the other messages
 * to find out the message whose action should be canceled.
 * <p>
 * As the second method requires a slow string comparison we prefer the first
 * one. Here we just assert a fact with all the information to cancel an action
 * in both ways.
 * <p>
 * As an action we here define a rule defined by another performative like
 * request-when. A generic cancel rule than checks for
 * agent-message-rule-pairings whose initializing message had the same
 * conversation-id as this cancel speech act. This rule than gets undefined.This
 * class walks through an SL code tree and translates it to CLIPS depending on
 * the given performative.
 * 
 * @author Alexander Wilden
 * 
 */
class Cancel extends SLPerformativeTranslator {

	/**
	 * Translates SL code of a cancel to CLIPS code. A cancel informs the
	 * receiver that an ongoing or outstanding action isn't necessary any more
	 * and should be canceled.
	 * <p>
	 * As an action we here define a rule defined by another performative like
	 * request-when. A generic cancel rule than checks for
	 * agent-message-rule-pairings whose initializing message had the same
	 * conversation-id as this cancel speech act. This rule than gets undefined.
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
		ActionSLConfiguration act = (ActionSLConfiguration) results.get(0);
		FunctionCallOrFactSLConfiguration funcCall = (FunctionCallOrFactSLConfiguration) act
				.getAction();
		String actString = "";
		String msg = "";
		if (funcCall.isSLMessage())
			msg = funcCall.compile(SLCompileType.ASSERT_MESSAGE_AS_TEMPLATE);
		else
			actString = funcCall.compile(SLCompileType.ACTION_AND_ASSERT);

		StringBuilder result = new StringBuilder();
		result.append("(assert (agent-cancel-result (message %MSG%)");
		result.append("(cancelAction \"").append(actString).append("\")");
		result.append("(cancelMessage ").append(msg).append(")");
		result.append("))");

		return result.toString();
	}
}