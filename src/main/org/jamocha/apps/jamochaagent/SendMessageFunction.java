/**
 * Copyright (C) 2006 Christoph Emonds, Sebastian Reinartz, Alexander Wilden
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://sumatraagent.sourceforge.net/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jamocha.apps.jamochaagent;

import jade.core.AID;
import jade.lang.acl.ACLMessage;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.functions.FunctionDescription;

public class SendMessageFunction implements Function {
	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "LoadFacts will create a new instance of CLIPSParser and load the facts in the data file. TODO This function needs to be changed to be independed of CLIPSParser and use the ParserFactory instead.";
		}

		public int getParameterCount() {
			return 1;
		}

		public String getParameterDescription(int parameter) {
			return "Directory whose files and folders will be listed.";
		}

		public String getParameterName(int parameter) {
			return "dir";
		}

		public JamochaType[] getParameterTypes(int parameter) {
			return JamochaType.STRINGS;
		}

		public JamochaType[] getReturnType() {
			return JamochaType.NONE;
		}

		public boolean isParameterCountFixed() {
			return true;
		}

		public boolean isParameterOptional(int parameter) {
			return false;
		}
	}

	private static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	private JamochaAgent agent;
	
	private static final String NAME = "agent-send-message";

	private static final int RECEIVERS_PARAM_POS = 0;

	private static final int REPLY_TO_PARAM_POS = 1;

	private static final int PERFORMATIVE_PARAM_POS = 2;

	private static final int CONTENT_PARAM_POS = 3;

	private static final int LANGUAGE_PARAM_POS = 4;

	private static final int ENCODING_PARAM_POS = 5;

	private static final int ONTOLOGY_PARAM_POS = 6;

	private static final int PROTOCOL_PARAM_POS = 7;

	private static final int CONVERSATION_PARAM_POS = 8;

	private static final int IN_REPLY_TO_PARAM_POS = 9;

	private static final int REPLY_WITH_PARAM_POS = 10;

	private static final int REPLY_BY_PARAM_POS = 11;

	private static final int PARAM_COUNT = 12;

	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	public String getName() {
		return NAME;
	}

	/**
	 * the following slot order:
	 * <ol>
	 * <li>receivers (List/Array)</li>
	 * <li>reply-to (List/Array)</li>
	 * <li>performative (int)</li>
	 * <li>content (String)</li>
	 * <li>language (String)</li>
	 * <li>encoding (String)</li>
	 * <li>ontology (String)</li>
	 * <li>protocol (String)</li>
	 * <li>conversation-id (String)</li>
	 * <li>in-reply-to (String)</li>
	 * <li>reply-with (String)</li>
	 * <li>reply-by (Datetime)</li>
	 * </ol>
	 */

	/*
	 * <pre> (agent-send-message "DA@35-232.mops.rwth-aachen.de:1099/JADE"
	 * "keiner" 1 "all" "fipa" "encoding" "ontolo" "proto" "muelll" "inrepto"
	 * "repwith" "repby") </pre>
	 */

	public SendMessageFunction(JamochaAgent agent) {
		this.agent = agent;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		ACLMessage message = fillAclMessage(engine, params);
		agent.getMessageSender().enqueueMessage(message);
		return JamochaValue.TRUE;
	}

	private ACLMessage fillAclMessage(Rete engine, Parameter[] params)
			throws EvaluationException {
		if (params.length < PARAM_COUNT) {
			throw new EvaluationException(
					"Not enough parameters for sending a message.");
		}
		ACLMessage result = new ACLMessage(ACLMessage
				.getInteger(params[PERFORMATIVE_PARAM_POS].getValue(engine)
						.getStringValue()));
		// receivers are given in a LIST
		JamochaValue receivers = params[RECEIVERS_PARAM_POS].getValue(engine)
				.implicitCast(JamochaType.LIST);
		for (int i = 0; i < receivers.getListCount(); ++i) {
			result.addReceiver(new AID(receivers.getListValue(i)
					.getStringValue(), true));
		}

		// replyTo are given in a LIST
		JamochaValue replyTo = params[REPLY_TO_PARAM_POS].getValue(engine)
				.implicitCast(JamochaType.LIST);
		for (int i = 0; i < replyTo.getListCount(); ++i) {
			result.addReplyTo(new AID(replyTo.getListValue(i).getStringValue(),
					true));
		}
		result
				.setContent(params[CONTENT_PARAM_POS].getValue(engine)
						.toString());
		result.setLanguage(params[LANGUAGE_PARAM_POS].getValue(engine)
				.getStringValue());
		result.setEncoding(params[ENCODING_PARAM_POS].getValue(engine)
				.getStringValue());
		result.setOntology(params[ONTOLOGY_PARAM_POS].getValue(engine)
				.getStringValue());
		result.setProtocol(params[PROTOCOL_PARAM_POS].getValue(engine)
				.getStringValue());
		result.setConversationId(params[CONVERSATION_PARAM_POS]
				.getValue(engine).getStringValue());
		result.setInReplyTo(params[IN_REPLY_TO_PARAM_POS].getValue(engine)
				.getStringValue());
		result.setReplyWith(params[REPLY_WITH_PARAM_POS].getValue(engine)
				.getStringValue());
		result.setReplyByDate(params[REPLY_BY_PARAM_POS].getValue(engine)
				.getDateValue().getTime());
		return result;
	}

}
