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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.jamocha.Jamocha;
import org.jamocha.adapter.sl.CLIPS2SLFunction;
import org.jamocha.adapter.sl.SL2CLIPSFunction;
import org.jamocha.messagerouter.MessageEvent;
import org.jamocha.messagerouter.StringChannel;
import org.jamocha.parser.ModeNotFoundException;
import org.jamocha.rete.Function;
import org.jamocha.rete.FunctionGroup;
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

	private String[] arguments;

	private MessageSender sendingBehaviour;

	private Jamocha jamocha;

	/** Is called when agent is started. */
	@Override
	public void toolSetup() {
		// Initialize the Rule-engine
		engine = new Rete();
		// Load the properties and merge them with possible arguments
		initProperties();

		addBehaviour(new MessageReceiver(this));
		sendingBehaviour = new MessageSender(this);
		addBehaviour(sendingBehaviour);
		try {
			jamocha = new Jamocha(engine, getProperties().getBooleanProperty(
					"jamocha.gui", false), getProperties().getBooleanProperty(
					"jamoche.shell", false), getProperties().getProperty(
					"jamocha.mode", ""));
			if (getProperties().getBooleanProperty("jamocha.gui", false))
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
		FunctionGroup agentFuncs = new AgentFunctions();
		engine.getFunctionMemory().declareFunctionGroup(agentFuncs);
		
		Function sendMessageFunction = new SendMessageFunction(this);
		engine.getFunctionMemory().declareFunction(sendMessageFunction);
		agentFuncs.addFunction(sendMessageFunction);
		
		Function sl2ClipsFunction = new SL2CLIPSFunction();
		engine.getFunctionMemory().declareFunction(sl2ClipsFunction);
		agentFuncs.addFunction(sl2ClipsFunction);
		

		Function clips2SlFunction = new CLIPS2SLFunction();
		engine.getFunctionMemory().declareFunction(clips2SlFunction);
		agentFuncs.addFunction(clips2SlFunction);

		StringBuilder buffer = new StringBuilder();

		// initializing the agent with the code from the file specified
		// in the "agent.init" property
		String initFileName = getProperties().getProperty("agent.initfile",
				"init.clp");

		initFileName = this.getClass().getPackage().getName().replace('.', '/')
				+ "/" + initFileName;
		readFromPackage(buffer, initFileName);
		String path = getProperties().getProperty("agent.protocols.package",
				this.getClass().getPackage().getName() + ".protocols").replace(
				'.', '/');
		String[] protocols = listPackage(path);
		for (String str : protocols) {
			readFromPackage(buffer, path + "/" + str);
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

	private void initProperties() {
		properties = new ExpandedProperties();
		String defaultPropName = STANDARD_PROP_FILE;
		try {
			defaultPropName = this.getClass().getPackage().getName().replace(
					'.', '/')
					+ "/" + defaultPropName;
			// System.out.println(defaultPropName);
		} catch (Exception any) {
			// ignore - likely class not in package.
		}
		InputStream propertyStream = this.getClass().getClassLoader()
				.getResourceAsStream(defaultPropName);
		if (propertyStream != null) {
			try {
				properties.load(propertyStream);
			} catch (IOException ioe) {
				System.err.println("Error reading:" + defaultPropName);
				System.exit(-1);
			}
		}

		Object[] arguments = super.getArguments();
		if (arguments != null) {
			String[] stringArgs = new String[arguments.length];

			for (int i = 0; i < arguments.length; ++i) {
				stringArgs[i] = arguments[i].toString();
				System.out.print("arg[" + i + "]: " + (String) arguments[i]
						+ "\n");
			}
			setArguments(stringArgs);
		}

		// dump the provided properties
		if (getProperties().getBooleanProperty("agent.debug", false)) {
			System.out.println("----- " + getLocalName() + "'s properties:");
			getProperties().list(System.out);
			System.out.println("----- end of properties -----");
		}
	}

	public MessageSender getMessageSender() {
		return sendingBehaviour;
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
		return properties;
	}

	/**
	 * Called by Jade to set the agents arguments so we use it to initialize our
	 * properties. It calls getProperties to get the starting properties, copies
	 * them into a new instance of ExpandedProperties, calls its parseArgs
	 * method of handle the presented arguments, and then replaces our starting
	 * properties with this result. This ensures that empty setting in the
	 * arguments will overlay those in starting. arguments.
	 * 
	 * @param args
	 *            The arguments passed to the agent.
	 */
	public void setArguments(String[] args) {
		arguments = args;
		ExpandedProperties newProperties = new ExpandedProperties();
		newProperties.copyProperties(getProperties());
		newProperties.parseArgs(args);
		setProperties(newProperties);
	}

	/**
	 * Fetch the String[] which was given to setArguments.
	 * 
	 * @return The value given to setArguments - may be null;
	 */
	public Object[] getArguments() {
		return arguments;
	}

	private String[] listPackage(String path) {
		ClassLoader cld = this.getClass().getClassLoader();
		URL resource = cld.getResource(path);
		File directory = new File(resource.getFile());
		return directory.list();
	}

	private void readFromPackage(StringBuilder buffer, String dataFile) {
		InputStream initStream = this.getClass().getClassLoader()
				.getResourceAsStream(dataFile);
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				initStream));
		if (getProperties().getBooleanProperty("agent.debug", false)) {
			System.out.println("----- reading file :" + dataFile);
		}
		try {
			while (reader.ready()) {
				buffer.append(reader.readLine() + "\n");
			}
			reader.close();

		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

}
