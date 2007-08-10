/*
 * Copyright (C) 2006 Christoph Emonds, Sebastian Reinartz, Alexander Wilden
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
package org.jamocha.apps.jamochaagent;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.Date;
import java.util.Iterator;

import org.jamocha.messagerouter.StringChannel;
import org.jamocha.parser.ParserUtils;

public class MessageReceiver extends CyclicBehaviour {

	private static final long serialVersionUID = 1L;

	private JamochaAgent agent;

	private StringChannel channel;

	public MessageReceiver(JamochaAgent agent) {
		super();
		this.agent = agent;
		channel = agent.getEngine().getMessageRouter().openChannel(
				agent.getProperties().getProperty("agent.name", "Agent"));
	}

	public void action() {
		ACLMessage msg = agent.receive();
		if (msg != null) {
			String assertion = createAssertString(msg);
			channel.executeCommand(assertion + "(fire)");
		} else {
			block(100);
		}
	}

	@SuppressWarnings("unchecked")
	private String createAssertString(ACLMessage msg) {
		StringBuilder sb = new StringBuilder();
		sb.append("(assert (").append(JamochaAgent.TEMPLATE_AGENT_MESSAGE);
		sb.append("(sender \"").append(msg.getSender().getName()).append("\")");
		sb.append("(receivers ");
		Iterator<AID> itReceiver = msg.getAllReceiver();
		while (itReceiver.hasNext()) {
			sb.append('"').append(itReceiver.next().getName()).append('"')
					.append(' ');
		}
		sb.append(")");
		sb.append("(reply-to ");
		Iterator<AID> itReplyTo = msg.getAllReplyTo();
		while (itReplyTo.hasNext()) {
			sb.append('"').append(itReplyTo.next().getName()).append('"')
					.append(' ');
		}
		sb.append(")");
		sb.append("(performative \"")
				.append(
						ACLMessage.getPerformative(msg.getPerformative())
								.toLowerCase()).append("\")");
		if (msg.getContent() != null) {
			sb.append("(content \"").append(
					ParserUtils.escapeStringLiteral(msg.getContent())).append(
					"\")");
		} else {
			sb.append("(content \"").append("\")");
		}
		if (msg.getLanguage() != null) {
			sb.append("(language \"").append(msg.getLanguage()).append("\")");
		} else {
			sb.append("(language \"").append("\")");
		}
		if (msg.getEncoding() != null) {
			sb.append("(encoding \"").append(msg.getEncoding()).append("\")");
		} else {
			sb.append("(encoding \"").append("\")");
		}
		if (msg.getOntology() != null) {
			sb.append("(ontology \"").append(msg.getOntology()).append("\")");
		} else {
			sb.append("(ontology \"").append("\")");
		}
		if (msg.getProtocol() != null) {
			sb.append("(protocol \"").append(msg.getProtocol().toLowerCase())
					.append("\")");
		} else {
			sb.append("(protocol \"").append("\")");
		}
		if (msg.getConversationId() != null) {
			sb.append("(conversation-id \"").append(msg.getConversationId())
					.append("\")");
		} else {
			sb.append("(conversation-id \"").append("\")");
		}
		if (msg.getInReplyTo() != null) {
			sb.append("(in-reply-to \"").append(msg.getInReplyTo()).append(
					"\")");
		} else {
			sb.append("(in-reply-to \"").append("\")");
		}
		if (msg.getReplyWith() != null) {
			sb.append("(reply-with \"").append(msg.getReplyWith())
					.append("\")");
		} else {
			sb.append("(reply-with \"").append("\")");
		}
		Date replyBy = msg.getReplyByDate();
		if (replyBy == null) {
			sb.append("(reply-by 0)");
		} else {
			sb.append("(reply-by ").append(ParserUtils.dateToLong(replyBy))
					.append(")");
		}
		// not needed and therefore isn't defined in init.clp
		// if (msg.getContent() != null) {
		// sb.append("(user-properties \"\" ");
		// sb.append(")");
		// } else {
		// sb.append("(user-properties \"\")");
		// }
		sb.append("(timestamp ").append(System.currentTimeMillis()).append(")");
		sb.append("(incoming TRUE)");
		sb.append("))");
		return sb.toString();
	}

}
