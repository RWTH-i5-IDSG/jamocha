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
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.tools.ToolAgent;
import jade.util.BasicProperties;
import jade.util.ExpandedProperties;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jamocha.Jamocha;
import org.jamocha.adapter.sl.CLIPS2SLFunction;
import org.jamocha.adapter.sl.SL2CLIPSFunction;
import org.jamocha.messagerouter.MessageEvent;
import org.jamocha.messagerouter.StringChannel;
import org.jamocha.parser.ModeNotFoundException;
import org.jamocha.rete.Deftemplate;
import org.jamocha.rete.Function;
import org.jamocha.rete.Rete;

/**
 * 
 * JamochaAgent is a JADE Agent that provides a Jamocha Rule Engine.
 * 
 * @author Christoph Emonds <ich@christoph-emonds.de>
 * @author Sebastion Reinartz <sebastian.reinartz@googlemail.com>
 * @author Alexander Wilden <october.rust@gmx.de>
 * @version 1.0
 * @see ToolAgent
 * 
 */
public class JamochaAgent extends ToolAgent {

	public static final String TEMPLATE_AGENT_DESCRIPTION = "agent-description";

	public static final String TEMPLATE_AGENT_MESSAGE = "agent-message";

	private static final long serialVersionUID = 1L;

	private Rete engine;

	private final String STANDARD_PROP_FILE = "Agent.properties";

	private BasicProperties properties = null;

	private Map<String, String> arguments = new HashMap<String, String>();

	private MessageSender sendingBehaviour;

	private Deftemplate messageTemplate;
	
	private Jamocha jamocha;

	/** Is called when agent is started. */
	@Override
	public void toolSetup() {
		// Initialize the Rule-engine
		engine = new Rete();
		// Get the agents arguments and puts them into a seperate HashMap
		initArguments();

		// dump the provided properties if debugging is on
		if (getProperties().getBooleanProperty("agent.debug", false)) {
			System.out.println("----- " + getLocalName() + "'s properties:");
			getProperties().list(System.out);
			System.out.println("----- end of properties -----");
		}

		addBehaviour(new MessageReceiver(this));
		sendingBehaviour = new MessageSender(this);
		addBehaviour(sendingBehaviour);
		try {
			jamocha = new Jamocha(engine, isSetArgument("gui"),
					isSetArgument("shell"), getArgument("parser", ""));
			if (isSetArgument("gui"))
				jamocha.getJamochaGui().setExitOnClose(false);
		} catch (ModeNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}

		initEngine();

		initAgentWithProperties();
	}

	private void initEngine() {
		// register user function for sending messages
		engine.getFunctionMemory().declareFunction(new SendMessageFunction(this));
		engine.getFunctionMemory().declareFunction(new SL2CLIPSFunction());
		engine.getFunctionMemory().declareFunction(new CLIPS2SLFunction());

		StringBuilder buffer = new StringBuilder();

		// initializing the agent with the code from the file specified
		// in the "agent.init" property
		String initFileName = getProperties().getProperty("agent.initfile",
				"init.clp");
		try {
			if (getProperties().getBooleanProperty("agent.debug", false)) {
				System.out.println("----- initializing the agent from :");
				System.out.println(initFileName);
			}
			BufferedReader reader = new BufferedReader(new FileReader(
					initFileName));

			while (reader.ready()) {
				buffer.append(reader.readLine());
			}

		} catch (FileNotFoundException e1) {
			if (getProperties().getBooleanProperty("agent.debug", false)) {
				System.out.println(e1.getMessage());
			}
		} catch (IOException e2) {
			if (getProperties().getBooleanProperty("agent.debug", false)) {
				System.out.println(e2.getMessage());
			}
		}

		// store agent name as fact
		buffer.append("(assert (" + TEMPLATE_AGENT_DESCRIPTION + "(name \""
				+ getName() + "\")(local TRUE)))");

		StringChannel initChannel = engine.getMessageRouter().openChannel(
				getProperties().getProperty("agent.name", "Agent") + "init");
		try {
			initChannel.executeCommand(buffer.toString(), true);
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		List<MessageEvent> events = new LinkedList<MessageEvent>();
		initChannel.fillEventList(events);
		for (MessageEvent event : events) {
			if (event.getMessage() instanceof Exception) {
				((Exception) event.getMessage()).printStackTrace();
			} else if (event.getMessage() instanceof Function) {
				System.out.println(((Function) event.getMessage()).getName());
			} else
				System.out.println(event.getMessage());

		}
		engine.getMessageRouter().closeChannel(initChannel);
	}

	private void initAgentWithProperties() {

		// register the agent with the DF
		String dfName = getProperties().getProperty("agent.df.name",
				"undefined");
		String dfAddress = getProperties().getProperty("agent.df.address",
				"undefined");
		System.out.println("df name: " + dfName + " Address: " + dfAddress);

		AID myDF = new AID(dfName, true);
		myDF.addAddresses(dfAddress);
		if (!dfName.equals("undefined") && !dfAddress.equals("undefined")) {

			DFAgentDescription dfd = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			sd.setName(getName());

			String dfType = getProperties().getProperty("agent.df.type",
					"undefined");
			String dfOwnership = getProperties().getProperty(
					"agent.df.ownership", "undefined");
			sd.setType(dfType);
			sd.setOwnership(dfOwnership);
			dfd.setName(getAID());
			dfd.addServices(sd);

			try {
				DFAgentDescription actualDfd = DFService.register(this, myDF,
						dfd);
				DFService.keepRegistered(this, myDF, actualDfd, null);
			} catch (FIPAException fe) {
				fe.printStackTrace();
			}
		}
	}

	/**
	 * Sets the given Arguments for the Agent.
	 * 
	 */
	private void initArguments() {
		Object[] args = getArguments();
		String argKey = null, currArg = null;
		if (args != null) {
			for (int i = 0; i < args.length; ++i) {
				currArg = args[i].toString();
				if (currArg.startsWith("-")) {
					argKey = currArg.subSequence(1, currArg.length())
							.toString();
					arguments.put(argKey, null);
					if (argKey != null)
						System.out.println();
					System.out.print("arg: " + argKey);
				} else if (argKey != null) {
					arguments.put(argKey, currArg);
					System.out.println(" = " + currArg);
					argKey = currArg = null;
				}

			}
			if (argKey != null)
				System.out.println();
		}
	}

	private String getArgument(String key, String defaultValue) {
		if (arguments.get(key) == null)
			return defaultValue;
		else
			return arguments.get(key);
	}

	private boolean isSetArgument(String key) {
		return (arguments.containsKey(key));
	}

	public MessageSender getMessageSender() {
		return sendingBehaviour;
	}

	public Deftemplate getAgentConversationTemplate() {
		return messageTemplate;
	}

	/**
	 * Gets the Jamocha engine.
	 * 
	 * @return The Rete Engine
	 */
	public Rete getEngine() {
		return engine;
	}

	/**
	 * Sets the agents properties.
	 * 
	 * @param aProperties
	 *            The properties to be used.
	 */
	public void setProperties(BasicProperties aProperties) {
		properties = aProperties;
	}

	/**
	 * Returns the agents properties.
	 * 
	 * @return the Properties
	 */
	public BasicProperties getProperties() {
		if (properties == null) {
			properties = new ExpandedProperties();
			String defaultPropName = getArgument("properties",
					STANDARD_PROP_FILE);
			InputStream propertyStream;
			try {
				propertyStream = new FileInputStream(defaultPropName);
				try {
					properties.load(propertyStream);
				} catch (IOException ioe) {
					System.err.println("Error reading:" + defaultPropName);
					System.exit(-1);
				}
			} catch (FileNotFoundException fnfe) {
				System.out.println("No properties found!");
			}
		}
		return properties;
	}

	/**
	 * Fetch the String[] which was given to setArguments.
	 * 
	 * @return The value given to setArguments - may be null;
	 */
	@Override
	public Object[] getArguments() {
		return super.getArguments();
	}

}
