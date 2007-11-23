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
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import org.jamocha.Jamocha;
import org.jamocha.apps.jamochaagent.userfunctions.AgentFunctions;
import org.jamocha.messagerouter.MessageEvent;
import org.jamocha.messagerouter.StringChannel;
import org.jamocha.parser.JamochaValue;
import org.jamocha.parser.ModeNotFoundException;
import org.jamocha.parser.ParserUtils;
import org.jamocha.rete.Rete;
import org.jamocha.rete.functions.Function;
import org.jamocha.rete.functions.FunctionGroup;

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

	private static final String INIT_FILE_NAME = "init.clp";

	public static final String TEMPLATE_AGENT_DESCRIPTION = "agent-identifier";

	public static final String TEMPLATE_AGENT_MESSAGE = "agent-message";

	private static final long serialVersionUID = 1L;

	private Rete engine;

	private final String STANDARD_PROP_FILE = "Agent.properties";

	private BasicProperties properties = null;

	private String[] arguments;

	private MessageSender sendingBehaviour;

	private Jamocha jamocha;

	StringChannel initChannel;

	/** Is called when agent is started. */
	@Override
	public void toolSetup() {
		// Initialize the Rule-engine
		// Load the properties and merge them with possible arguments
		initProperties();

		try {
			jamocha = new Jamocha(getProperties().getBooleanProperty(
					"jamocha.gui", false), getProperties().getBooleanProperty(
					"jamoche.shell", false), getProperties().getProperty(
					"jamocha.mode", ""), null);
			if (getProperties().getBooleanProperty("jamocha.gui", false)) {
				jamocha.getJamochaGui().setExitOnClose(false);
				jamocha.setGUITitle("JamochaAgent - " + getName());
			}
		} catch (ModeNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}
		engine = jamocha.getEngine();

		addBehaviour(new MessageReceiver(this));
		sendingBehaviour = new MessageSender(this);
		addBehaviour(sendingBehaviour);

		initChannel = engine.getMessageRouter().openChannel(
				getProperties().getProperty("agent.name", "Agent") + "init");

		initEngine();

		initAgentWithProperties();

		List<String> batchFiles = new LinkedList<String>();
		String batchString = getProperties().getProperty("jamocha.batch", "");
		if (!batchString.equals("")) {
			String[] batchArr = batchString.split(",");
			for (String batchFile : batchArr) {
				batchFiles.add(batchFile);
			}
		}
		jamocha.batchFiles(batchFiles);

		initChannel.executeCommand(ParserUtils.getStringLiteral(getProperties()
				.getProperty("jamocha.clips", "")), true);
		engine.getMessageRouter().closeChannel(initChannel);

		if (getProperties().getBooleanProperty("agent.debug", false)) {
			List<MessageEvent> events = new LinkedList<MessageEvent>();
			initChannel.fillEventList(events);
			for (MessageEvent event : events) {
				if (event.getMessage() instanceof Exception) {
					((Exception) event.getMessage()).printStackTrace();
				} else if (event.getMessage() instanceof Function) {
					System.out.println(((Function) event.getMessage())
							.getName());
				} else
					System.out.println(event.getMessage());
			}
		}
	}

	private void initEngine() {
		// register user function for sending messages
		FunctionGroup agentFuncs = new AgentFunctions(this);
		engine.getFunctionMemory().declareFunctionGroup(agentFuncs);

		StringBuilder buffer = new StringBuilder();

		// initializing the agent with the code from the file specified
		// in the "agent.init" property
		String initFolderName = getProperties().getProperty("agent.initFolder",
				"apps/jamochaagent/initial/");
		try {
			engine.setBinding("init-folder", JamochaValue
					.newString(initFolderName));
			readFile(buffer, initFolderName + INIT_FILE_NAME);
			String pathProtocols = getProperties().getProperty(
					"agent.protocols", "apps/jamochaagent/protocols/");
			readPath(buffer, pathProtocols);

			String pathPerformatives = getProperties().getProperty(
					"agent.performatives", "apps/jamochaagent/performatives/");

			readPath(buffer, pathPerformatives);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		// store agent as fact
		buffer.append("(assert (agent-is-local (agent (assert (");
		buffer.append(TEMPLATE_AGENT_DESCRIPTION).append("(name \"");
		buffer.append(getName()).append("\"))))))");

		try {
			initChannel.executeCommand(buffer.toString(), true);
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	private void initAgentWithProperties() {
		// register the agent with the DF
		String dfName = getProperties().getProperty("agent.df.name",
				"undefined");
		String dfAddress = getProperties().getProperty("agent.df.address",
				"undefined");

		if (!dfName.equals("undefined") && !dfAddress.equals("undefined")) {

			AID myDF = new AID(dfName, true);
			myDF.addAddresses(dfAddress);
			
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

	private void readFile(StringBuilder buffer, String fileName)
			throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		while (reader.ready()) {
			buffer.append(reader.readLine() + "\n");
		}
		reader.close();

	}

	private void readPath(StringBuilder buffer, String path) throws IOException {
		File dir = new File(path);
		if (dir.isDirectory()) {
			FileFilter filter = new FileFilter() {
				public boolean accept(File arg0) {
					return (arg0.isFile() && arg0.getName().endsWith(".clp"));
				}
			};
			File[] contents = dir.listFiles(filter);
			for (File file : contents) {
				readFile(buffer, file.getAbsolutePath());
			}
		}
	}
}
