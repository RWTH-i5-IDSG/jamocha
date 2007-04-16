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

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.LinkedList;
import java.util.Queue;

public class MessageSender extends CyclicBehaviour {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JamochaAgent agent;

	private Queue<ACLMessage> messages;

	public MessageSender(JamochaAgent agent) {
		super();
		this.agent = agent;
		messages = new LinkedList<ACLMessage>();
	}

	public void enqueueMessage(ACLMessage msg) {
		messages.offer(msg);
	}

	public void action() {
		if (messages.isEmpty())
			block(100);
		else {
			ACLMessage msg = messages.poll();
			if (null != msg)
				agent.send(msg);
		}
	}

}
