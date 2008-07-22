/*
 * Copyright 2002-2008 The Jamocha Team
 * 
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
 * 
 */

package org.jamocha.engine.functions.agent;

import jade.core.AID;
import jade.lang.acl.ACLMessage;

import java.util.Calendar;

import org.jamocha.communication.agent.JamochaAgent;
import org.jamocha.engine.Engine;
import org.jamocha.engine.Parameter;
import org.jamocha.engine.functions.AbstractFunction;
import org.jamocha.engine.functions.FunctionDescription;
import org.jamocha.engine.workingmemory.elements.Fact;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.IllegalParameterException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;

/**
 * @author Christoph Emonds, Sebastian Reinartz, Alexander Wilden
 * 
 * This Function takes a simple agent-message fact and sends it out via the
 * JamochaAgent. If no sender is given the local agent is taken as sender.
 * Always returns true if no error occured.
 */
public class AgentSendMessage extends AbstractFunction {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "This Function takes a simple agent-message fact and sends it out via the JamochaAgent. If no sender is given the local agent is taken as sender. Always returns true if no error occured.";
		}

		public int getParameterCount() {
			return 1;
		}

		public String getParameterDescription(int parameter) {
			return "An agent-message fact that should be send out as acl message.";
		}

		public String getParameterName(int parameter) {
			return "aclMessage";
		}

		public JamochaType[] getParameterTypes(int parameter) {
			return JamochaType.FACTS;
		}

		public JamochaType[] getReturnType() {
			return JamochaType.BOOLEANS;
		}

		public boolean isParameterCountFixed() {
			return true;
		}

		public boolean isParameterOptional(int parameter) {
			return false;
		}

		public String getExample() {
			// TODO Auto-generated method stub
			return null;
		}

		public boolean isResultAutoGeneratable() {
			return false;
		}

		public Object getExpectedResult() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	private static final long serialVersionUID = 1L;

	public static final FunctionDescription DESCRIPTION = new Description();

	public static final String NAME = "agent-send-message";

	private final JamochaAgent agent;

	@Override
	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	@Override
	public String getName() {
		return NAME;
	}

	public AgentSendMessage(JamochaAgent agent) {
		this.agent = agent;
	}

	private static final String AGENT_IDENTIFIER = "agent-identifier";

	private static final String AGENT_MESSAGE = "agent-message";

	@Override
	public JamochaValue executeFunction(Engine engine, Parameter[] params)
			throws EvaluationException {
		if (params.length == 1) {
			JamochaValue val = params[0].getValue(engine);
			Fact messageFact;
			if (val.is(JamochaType.FACT))
				messageFact = val.getFactValue();
			else if (val.is(JamochaType.FACT_ID) || val.is(JamochaType.LONG))
				messageFact = engine.getFactById(val.getLongValue());
			else
				throw new EvaluationException(
						"The only parameter of (send-message) must be of type FACT or FACT_ID.");
			if (!messageFact.getTemplate().getName().equals(AGENT_MESSAGE))
				throw new EvaluationException(
						"(send-message) expects a fact of type agent-message as parameter.");
			ACLMessage message = fillAclMessage(engine, messageFact);
			agent.getMessageSender().enqueueMessage(message);
			return JamochaValue.TRUE;

		}
		throw new IllegalParameterException(1);
	}

	private ACLMessage fillAclMessage(Engine engine, Fact messageFact)
			throws EvaluationException {

		ACLMessage result = new ACLMessage(ACLMessage.getInteger(slotToString(
				messageFact, "performative")));
		String sender = identifierToString(messageFact.getSlotValue("sender"),
				engine);
		if (sender.equals(""))
			sender = agent.getName();
		result.setSender(new AID(sender, true));

		// receivers are given in a LIST
		JamochaValue receivers = messageFact.getSlotValue("receiver");
		String recv;
		for (int i = 0; i < receivers.getListCount(); ++i) {
			recv = identifierToString(receivers.getListValue(i), engine);
			if (!recv.equals(""))
				result.addReceiver(new AID(recv, true));
		}

		// replyTo are given in a LIST
		JamochaValue replyTo = messageFact.getSlotValue("reply-to");
		String repl;
		for (int i = 0; i < replyTo.getListCount(); ++i) {
			repl = identifierToString(replyTo.getListValue(i), engine);
			if (!repl.equals(""))
				result.addReplyTo(new AID(repl, true));
		}
		result.setContent(slotToString(messageFact, "content"));
		result.setLanguage(slotToString(messageFact, "language"));
		result.setEncoding(slotToString(messageFact, "encoding"));
		result.setOntology(slotToString(messageFact, "ontology"));
		result.setProtocol(slotToString(messageFact, "protocol"));
		result.setConversationId(slotToString(messageFact, "conversation-id"));
		result.setInReplyTo(slotToString(messageFact, "in-reply-to"));
		result.setReplyWith(slotToString(messageFact, "reply-with"));
		JamochaValue repBy = messageFact.getSlotValue("reply-by");
		if (repBy.is(JamochaType.LONG)) {
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(repBy.getLongValue());
			result.setReplyByDate(cal.getTime());
		}
		return result;
	}

	private String slotToString(Fact messageFact, String slotName)
			throws EvaluationException {
		return messageFact.getSlotValue(slotName).getStringValue();
	}

	private String identifierToString(JamochaValue idVal, Engine engine)
			throws EvaluationException {
		String res = "";
		if (!idVal.is(JamochaType.NIL)) {
			Fact idFact = null;
			if (idVal.is(JamochaType.FACT))
				idFact = idVal.getFactValue();
			else if (idVal.is(JamochaType.FACT_ID)
					|| idVal.is(JamochaType.LONG))
				idFact = engine.getFactById(idVal.getLongValue());
			if (idFact != null
					&& idFact.getTemplate().getName().equals(AGENT_IDENTIFIER))
				res = idFact.getSlotValue("name").getStringValue();
		}
		return res;
	}
}
