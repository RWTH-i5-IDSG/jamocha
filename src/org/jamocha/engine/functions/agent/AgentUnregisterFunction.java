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

import java.util.Date;

import org.jamocha.communication.agent.JamochaAgent;
import org.jamocha.communication.messagerouter.StringChannel;
import org.jamocha.engine.Engine;
import org.jamocha.engine.Parameter;
import org.jamocha.engine.functions.AbstractFunction;
import org.jamocha.engine.functions.FunctionDescription;
import org.jamocha.engine.workingmemory.elements.Fact;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;

/**
 * @author Daniel Schmitz
 * 
 * 
 */
public class AgentUnregisterFunction extends AbstractFunction {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "This function deregisters agent-services to one or multiple dfs";
		}

		public int getParameterCount() {
			return 3; // DF, AID, SD
		}

		public String getParameterDescription(int parameter) {
			return "DF (as String or array of DFs), AID of own agent, SD (as String or array of Strings)";
		}

		public String getParameterName(int parameter) {
			switch (parameter) {
			case 1:
				return "DF";
			case 2:
				return "AID";
			case 3:
				return "SD";
			default:
				return "DF + AID + SD";
			}
		}

		public JamochaType[] getParameterTypes(int parameter) {
			return JamochaType.STRINGS;
		}

		public JamochaType[] getReturnType() {
			return JamochaType.BOOLEANS; // or arrays
		}

		public boolean isParameterCountFixed() {
			return true;
		}

		public boolean isParameterOptional(int parameter) {
			return false;
		}

		public String getExample() { // TODO
			return "(bind ?df 'df@Daniel-PC:1099/JADE')"
					+ "(bind ?aid 'JA@Daniel-PC:1099/JADE')"
					+ "(bind ?sd1 '(service-description :name \"profiling\" :type \"user-profiling\" :ontologies (set meeting-scheduler))')"
					+ "(bind ?sd2 '(service-description :name \"profiling2\" :type \"user-profiling2\" :ontologies (set meeting-scheduler2))')"
					+ "(bind ?sd (create$ ?sd1 ?sd2))"
					+ "(agent-register ?df ?aid ?sd)";
		}

		public boolean isResultAutoGeneratable() { //
			// TODO Auto-generated method stub
			return false;
		}

		public Object getExpectedResult() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	private static final long serialVersionUID = 1L;

	public static final FunctionDescription DESCRIPTION = new Description();

	public static final String NAME = "agent-unregister"; // TODO

	private final JamochaAgent agent;

	@Override
	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	@Override
	public String getName() {
		return NAME;
	}

	public AgentUnregisterFunction(JamochaAgent agent) {
		this.agent = agent;
	}

	private StringChannel channel;

	public String output = "t"; // output when receiving a message

	/*
	 * Probleme: '' um Strings
	 */
	@Override
	public JamochaValue executeFunction(Engine engine, Parameter[] params)
			throws EvaluationException {

		channel = agent.getEngine().getMessageRouter().openChannel(
				agent.getProperties().getProperty("agent.name", "Agent"));
		if (params.length != 3)
			if (params.length == 1) {
				// engine.writeMessage("Ein Parameter");
				if (params[0].getValue(engine).is(JamochaType.FACT)) {
					Fact answer = params[0].getValue(engine).getFactValue(engine);
					if (checkanswer(answer, engine)) {
						/*
						 * if (output.contains("JamochaGui"))
						 * channel.executeCommand("(printout "+ output +"
						 * \"true\" crlf)");
						 */
						engine.writeMessage("true", output);
						return JamochaValue.TRUE;
					} else {
						/*
						 * if (output.contains("JamochaGui"))
						 * channel.executeCommand("(printout "+ output +" false
						 * crlf)");
						 */
						engine.writeMessage("false", output);
						return JamochaValue.FALSE;
					}
				} else {
					/*
					 * if (output.contains("JamochaGui"))
					 * channel.executeCommand("(printout "+ output +" false)");
					 */
					engine.writeMessage("false", output);
					return JamochaValue.FALSE;
				}
			} else
				throw new EvaluationException(
						"Unregister needs, and only needs DF(s) AID and SD(s) of the agent to be registered");
		// get sender
		String sender;
		JamochaValue aid = params[1].getValue(engine);
		if (aid.is(JamochaType.STRING)) {
			sender = aid.getStringValue();
			sender = sender.substring(1, sender.length() - 1);
		} else
			throw new EvaluationException("AID must to be a String (Name)");

		// For each DF send each SD
		JamochaValue df = params[0].getValue(engine);
		if (df.is(JamochaType.STRING)) {
			// Only one DF
			String df2 = df.getStringValue();
			df2 = df2.substring(1, df2.length() - 1);
			JamochaValue sd = params[2].getValue(engine);

			if (checkServiceAndSend(sd, df2, sender, engine))
				// engine.writeMessage("DEBUG: sending message.....ok");
				// return JamochaValue.TRUE;
				return JamochaValue.newString("");
			else
				throw new EvaluationException("Error during sending messages");
		} else if (df.is(JamochaType.LIST))
			for (int i = 0; i < df.getListCount(); i++) {

				String df2 = df.getStringValue();
				df2 = df2.substring(1, df2.length() - 1);
				JamochaValue sd = params[3].getValue(engine);
				if (checkServiceAndSend(sd, df2, sender, engine))
					// return JamochaValue.TRUE;
					return JamochaValue.newString("");
				else
					throw new EvaluationException(
							"Error during sending messages");
			}
		else
			throw new EvaluationException(
					"DF need to be a string or an array of strings");
		// Kann doch eignetlich nicht erreicht werden...
		return JamochaValue.FALSE; // return JamochaValue.TRUE;
	}

	private boolean checkServiceAndSend(JamochaValue sd, String df2,
			String sender, Engine engine) throws EvaluationException {

		if (sd.is(JamochaType.STRING)) {
			// Only one df and one service, send it!
			String service = sd.getStringValue();
			service = service.substring(1, service.length() - 1); // Delete
			// the ''
			// around
			// the
			// JamochaString

			ACLMessage message = makeMessage(engine, df2, sender, service);
			agent.send(message);
			String rule;
			rule = "(defrule suche_message ?message <- (agent-message (conversation-id "
					+ message.getConversationId()
					+ ") ) => (agent-unregister ?message))";
			String delrule = "(undefrule suche_message)";
			channel = agent.getEngine().getMessageRouter().openChannel(
					agent.getProperties().getProperty("agent.name", "Agent"));

			/*
			 * try{ Thread.sleep(1000); } catch(InterruptedException e){
			 * engine.writeMessage("Sleep Interrupted"); }
			 */

			channel.executeCommand(rule + "(fire)" + delrule);
			return true;

		} else if (sd.is(JamochaType.LIST)) {
			// Service List but only one df

			String service = "";
			String helper = "";
			ACLMessage message;

			for (int i = 0; i < sd.getListCount(); i++)
				if (sd.getListValue(i).is(JamochaType.STRING)) {
					service = sd.getListValue(i).getStringValue();
					service = service.substring(1, service.length() - 1); // delete
					// the
					// ''
					// around
					// strings
					helper += "\n" + service;

				} else
					engine
							.writeMessage("Services need to be a string or an array of services which are strings");
			if (helper != "") {
				message = makeMessage(engine, df2, sender, helper);
				agent.send(message);
				// Hier passiert der Fehler!
				String rule = "(defrule suche_message ?message <- (agent-message (conversation-id "
						+ message.getConversationId()
						+ ") ) => (agent-unregister ?message))";
				String delrule = "(undefrule suche_message)";
				channel = agent.getEngine().getMessageRouter().openChannel(
						agent.getProperties()
								.getProperty("agent.name", "Agent"));

				/*
				 * try{ Thread.sleep(1000); } catch(InterruptedException e){
				 * engine.writeMessage("Sleep Interrupted"); }
				 */

				channel.executeCommand(rule);
				/*
				 * try { engine.fire(); } catch (ExecuteException e) {
				 * engine.writeMessage(e.toString()); }
				 */
				channel.executeCommand("(fire)");
				channel.executeCommand(delrule);

				return true;
			} else
				throw new EvaluationException("Services error");
		} else
			throw new EvaluationException(
					"Services need to be a string or an array of services");
	}

	private ACLMessage makeMessage(Engine engine, String df, String sender,
			String sd) throws EvaluationException {
		// This function generates the register message for one df and one set
		// of sd
		// TODO
		String content;
		if (sd.contains("df-agent-description")) {
			content = "((action " + "(agent-identifier :name " + df + ") "
					+ "(register " + sd + ")))";
			content = content.replace("\n", " ");
		} else {
			content = "((action " + "(agent-identifier :name " + df + ") "
					+ "(deregister " + "(df-agent-description " + ":name "
					+ "(agent-identifier " + ":name " + sender + ") "
					+ ":protocols (set fipa-request applications-protocol) "
					+ ":languages (set fipa-sl0) " + ":services (set " + sd
					+ "))))))";
			content = content.replace("\n", " ");
		}
		ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
		if (sender.equals(""))
			sender = agent.getName();
		message.setSender(new AID(sender, true));
		message.addReceiver(new AID(df, true));
		message.addReplyTo(new AID(sender, true));
		message.setContent(content);
		message.setLanguage("fipa-sl0");
		message.setOntology("fipa-agent-management");
		message.setProtocol("fipa-request");
		Date dt = new Date();
		message.setConversationId("" + dt.getTime()); // Dirty Hack :(
		return message;
	}

	private boolean checkanswer(Fact answer, Engine engine)
			throws EvaluationException {

		// BEGIN INFORM CHECK
		if (answer.getSlotValue("performative").toString().contains("inform")) {
			// engine.writeMessage("Is Inform \n"); DEBUG

			String newcontent = answer.getSlotValue("content").toString();

			if (newcontent.contains("done"))
				// everything ok
				return true;
			else {
				// channel.executeCommand("(printout "+ output +" \"Got wrong
				// content at the inform message but the right
				// ConversationID\")");
				engine
						.writeMessage(
								"Got wrong content at the inform message but the right ConversationID",
								output);
				return false;
			}
		} else {
			// message is not an inform
			// already-registered
			String newcontent = answer.getSlotValue("content").toString();
			if (newcontent.contains("not-registered")) {
				engine.writeMessage(
						"Sorry, that agent is not registered with the df!",
						output);
				// channel.executeCommand ("(printout "+ output +" \"Sorry, that
				// agent is already registered with the df!\")");
				return false;
			}
			if (newcontent.contains("missing-parameter service-description")) {
				engine
						.writeMessage(
								"Sorry, missing-parameter service-description / wrong service-description.",
								output);
				// channel.executeCommand ("(printout "+ output +" Sorry,
				// missing-parameter service-description / wrong
				// service-description.)");
				return false;
			}
			if (newcontent.contains("unrecognised-value content")) {
				engine
						.writeMessage(
								"Sorry, unrecognised-value content, please ceck your input.",
								output);
				// channel.executeCommand ("(printout "+ output +" Sorry,
				// unrecognised-value content, please ceck your input.)");
				return false;
			} else {
				engine
						.writeMessage(
								"I got no inform message but an error code: "
										+ answer.getSlotValue("performative")
												.toString()
										+ ". Please use the sniffer to identify the problem!",
								output);
				// channel.executeCommand ("(printout "+ output +" \"I got no
				// inform message but an error code:
				// "+answer.getSlotValue("performative").toString()+". Please
				// use the sniffer to identify the problem!\")");
				return false;
			}
		}
		// END INFORM CHECK
	}
}