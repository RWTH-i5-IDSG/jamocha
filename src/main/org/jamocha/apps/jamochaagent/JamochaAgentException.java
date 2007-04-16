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

import jade.lang.acl.ACLMessage;

/**
 * 
 * JessAgentException is the parent type of all exceptions thrown by public methods
 * in the JessAgent library.
 * 
 * @author Alexander Zimmermann <alexander@i4.informatik.rwth-aachen.de>
 * @version 1.0
 * @see Exception
 * @see ACLMessage
 *
 */

public class JamochaAgentException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int performative;
	
	/**
	 * Constructs a JessAgentException containing a descriptive message.
	 * @param msg An informational message.
	 */
	public JamochaAgentException(String msg) {
		super(msg);
		performative = ACLMessage.NOT_UNDERSTOOD;
	}
	
	/**
	 * Constructs a JessAgentException containing a descriptive message
	 * and a performative.
	 * @param msg An informational message.
	 * @param perf The performative for the exception
	 */
	public JamochaAgentException(String msg, int perf) {
		super(msg);
		performative = perf;
	}
	
	/**
	 * @return The performative of exception
	 */
	public int getPerformative() {
		return performative;
	}

}
