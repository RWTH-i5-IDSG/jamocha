/*
 * Copyright (C) 2007 Alexander Wilden
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
package org.jamocha.apps.jamochaagent.userfunctions;

//import java.util.Date;

import jade.core.AID;
import jade.lang.acl.ACLMessage;

import org.jamocha.apps.jamochaagent.JamochaAgent;
import org.jamocha.messagerouter.StringChannel;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.functions.AbstractFunction;
import org.jamocha.rete.functions.FunctionDescription;
import org.jamocha.rete.wme.Fact;

import java.util.*;

/**
 * @author Daniel Schmitz
 * 
 * 
 */
public class AgentSearchFunction extends AbstractFunction {
	
	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "This function search the DF for other agents"; //TODO
		}

		public int getParameterCount() {
			return 3; //TODO
		}

		public String getParameterDescription(int parameter) {
			return "DF (as String or array of DFs), name of own agent, SD (as String or array of Strings)"; //TODO
		}

		public String getParameterName(int parameter) {
			switch (parameter){
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
			return JamochaType.FACTS; 
		}

		public boolean isParameterCountFixed() {
			return true;
		}

		public boolean isParameterOptional(int parameter) {
			return false;
		}

		public String getExample() {
			return "(bind ?ad '(df-agent-description :services (set (service-description :name profiling)))" +
					"(search-constraints :minax-depth 2)') \n(agent-search ?df ?aid ?ad)" +
					"\n Gives back something like that\"Found and stored as factid f-31\"";
		}

		public boolean isResultAutoGeneratable() {
			// TODO Auto-generated method stub
			return false;
		}
	}

	private static final long serialVersionUID = 1L;

	public static final FunctionDescription DESCRIPTION = new Description();

	public static final String NAME = "agent-search"; //TODO

	private JamochaAgent agent;

	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	public String getName() {
		return NAME;
	}

	public AgentSearchFunction(JamochaAgent agent) {
		this.agent = agent;
	}
	
	private StringChannel channel;
	
	public String output ="t"; //output when receiving a message

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
	throws EvaluationException {

	channel = agent.getEngine().getMessageRouter().openChannel(
				agent.getProperties().getProperty("agent.name", "Agent"));
	if (params.length != 3) {
		if (params.length == 1) {
			if (params[0].getValue(engine).is(JamochaType.FACT)) {
				Fact answer = params[0].getValue(engine).getFactValue();
				if (checkanswer(answer, engine)) {
					engine.writeMessage("true", output);
					return JamochaValue.TRUE;	
				}
				else {
					engine.writeMessage("false", output);
					return JamochaValue.FALSE;
				}
			}
			else {
				channel.executeCommand("(printout "+ output +" false)");
				return JamochaValue.FALSE;
			}
		}
		else 
			throw new EvaluationException(
			"Search needs, and only needs DF(s) the own AID and SD(s) or DF-AD(s) you want to yearch for");
	}
	//get sender
	String sender;
	JamochaValue aid = params[1].getValue(engine);
	if (aid.is(JamochaType.STRING)){
		sender = aid.getStringValue();
		sender = sender.substring(1, sender.length()-1);
	}
	else {
		throw new EvaluationException(
		"AID must to be a String (Name)");
	}
	
	//For each DF send each SD
	JamochaValue df = params[0].getValue(engine);
	if (df.is(JamochaType.STRING)) { 
		//Only one DF
		String df2 = df.getStringValue();
		df2 = df2.substring(1, df2.length()-1);
		JamochaValue sd = params[2].getValue(engine);
		
		if (checkServiceAndSend (sd, df2, sender, engine)) {
			//engine.writeMessage("DEBUG: sending message.....ok");
			return JamochaValue.newString("");
		}
		else {
			throw new EvaluationException(
			"Error during sending messages");
		}
	}	
	else if (df.is(JamochaType.LIST)) {
		for (int i = 0; i < df.getListCount(); i++){
			
			String df2 = df.getStringValue();
			df2 = df2.substring(1, df2.length()-1);
			JamochaValue sd = params[3].getValue(engine);
			if (checkServiceAndSend (sd, df2, sender, engine))
				return JamochaValue.newString("");
			else {
				throw new EvaluationException(
				"Error during sending messages");
			}
		}
	}
	else {
		throw new EvaluationException(
		"DF need to be a string or an array of strings");
	}
	//Kann doch eignetlich nicht erreicht werden...
	return JamochaValue.FALSE; //return JamochaValue.TRUE;		
	}

	private boolean checkServiceAndSend (JamochaValue sd, String df2, String sender, Rete engine)
	throws EvaluationException {
	
	if (sd.is(JamochaType.STRING)){
		//Only one df and one service, send it!
		String service = sd.getStringValue();
		service = service.substring(1, service.length()-1); // Delete the '' around the JamochaString
	
			ACLMessage message = makeMessage (engine, df2, sender, service);
			agent.send(message);
			String rule;
			rule ="(defrule suche_message ?message <- (agent-message (conversation-id "+message.getConversationId()+") ) => (agent-search ?message))";
			String delrule = "(undefrule suche_message)";
			channel = agent.getEngine().getMessageRouter().openChannel(
					agent.getProperties().getProperty("agent.name", "Agent"));
	         
			channel.executeCommand(rule+"(fire)"+delrule);
			return true;
			
		}
		else if (sd.is(JamochaType.LIST)){
			//Service List but only one df
			
			String service = "";
			String helper = "";
			ACLMessage message;
	
			for (int i = 0; i < sd.getListCount(); i++){
				if (sd.getListValue(i).is(JamochaType.STRING)) {
					service = sd.getListValue(i).getStringValue();
					service = service.substring(1, service.length()-1); //delete the '' around strings
					helper += "\n"+service;
					
				}
				else{
					engine.writeMessage("Services need to be a string or an array of services which are strings");
				}
			}
			if (helper != "") {
				message = makeMessage (engine, df2, sender, helper);
				agent.send(message);
				String rule ="(defrule suche_message ?message <- (agent-message (conversation-id "+message.getConversationId()+") ) => (agent-search ?message))";
				String delrule = "(undefrule suche_message)";
				//TODO
				channel = agent.getEngine().getMessageRouter().openChannel(
						agent.getProperties().getProperty("agent.name", "Agent"));
		        
		         channel.executeCommand(rule); 
		        /*try {
		        	engine.fire();
		        }
		        catch (ExecuteException e) {
		        	engine.writeMessage(e.toString());
		        }*/
		        channel.executeCommand("(fire)");
		        channel.executeCommand(delrule);
		         
				return true;
			}
			else{
				throw new EvaluationException(
				"Services error");
			}
		}
		else {
			throw new EvaluationException(
			"Services need to be a string or an array of services");
		}
	}

	private ACLMessage makeMessage(Rete engine, String df, String sender, String sd)
	throws EvaluationException {
	//This function generates the register message for one df and one set of sd
	//TODO
		String content;
		if (sd.contains("df-agent-description")) {
		
		content ="((action " +
		"(agent-identifier :name "+df+") " +
		"(search "
			+sd+ ")))";
		
		content = content.replace("\n", " ");
		}
		else {
			content ="((action " +
			"(agent-identifier :name " +df+ ") " +
			"(search "+
			"(df-agent-description " +
				":services " +
				"(set "+sd+ "))"+
			"(search-constraints :minax-depth 2)"+
			")))";
			content = content.replace("\n", " ");
		}
		ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
		if (sender.equals("")) sender = agent.getName();
		message.setSender(new AID(sender, true));
		message.addReceiver(new AID(df, true));	
		message.addReplyTo(new AID(sender, true));
		message.setContent(content);
		message.setLanguage("fipa-sl0");
		message.setOntology("fipa-agent-management");
		message.setProtocol("fipa-request");
		Date dt = new Date();
		message.setConversationId(""+ dt.getTime()); //Dirty Hack :(
		return message;
	}

	private boolean checkanswer (Fact answer, Rete engine)
	throws EvaluationException {
			
	//BEGIN INFORM CHECK
		if (answer.getSlotValue("performative").toString().contains("inform")) {
		
			String newcontent = answer.getSlotValue("content").toString();
			
			if ( (newcontent.contains("sequence (")) ){ 
				//df has found something matching our request
				
				String returnsequence = newcontent.substring(newcontent.indexOf("sequence")+9, newcontent.length()-2);
				Integer first = newcontent.indexOf("name", newcontent.indexOf("sequence"))+5;
				Integer second = newcontent.indexOf("services", newcontent.indexOf("sequence"))-2;
				
				String returnname = newcontent.substring(first, second);
				String rule = "(bind ?factid (assert (agent-request-result (result \""+returnname+"\")(message \""+returnsequence+"\"))))";
	
				channel = agent.getEngine().getMessageRouter().openChannel(
						agent.getProperties().getProperty("agent.name", "Agent"));
				
				channel.executeCommand(rule);
				channel.executeCommand("(printout "+ output +" \"Found and stored as factid \" ?factid)");
				//engine.writeMessage("Found and stored as factid ?factid", output);
				return true;
			}
			else {
				engine.writeMessage("Nothing found", output);
				//channel.executeCommand("(printout " +output +" \"Nothing found\")");
				return false;
			}
		}
		else {
			//message is not an inform
			String newcontent = answer.getSlotValue("content").toString();
			if (newcontent.contains("missing-parameter service-description")){
				engine.writeMessage("Sorry, missing-parameter service-description / wrong service-description.", output);
				//channel.executeCommand ("(printout "+ output +" Sorry, missing-parameter service-description / wrong service-description.)");
				return false;
			}
			if (newcontent.contains("unrecognised-value content")){
				engine.writeMessage("Sorry, unrecognised-value content, please ceck your input.", output);
				//channel.executeCommand ("(printout "+ output +" Sorry, unrecognised-value content, please ceck your input.)");
				return false;
			}
			else {
				engine.writeMessage("I got no inform message but an error code: "+answer.getSlotValue("performative").toString()+". Please use the sniffer to identify the problem!", output);
				//channel.executeCommand ("(printout "+ output +" \"I got no inform message but an error code: "+answer.getSlotValue("performative").toString()+". Please use the sniffer to identify the problem!\")");
				return false;
			}
		}
		//END INFORM CHECK
	}
}