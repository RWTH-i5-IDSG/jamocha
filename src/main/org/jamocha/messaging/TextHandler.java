/*
 * Copyright 2002-2006 Peter Lin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://ruleml-dev.sourceforge.net/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.jamocha.messaging;

import javax.jms.Message;

import org.jamocha.logging.DefaultLogger;
import org.jamocha.rete.Rete;

/**
 * @author Peter Lin
 * 
 * TODO - this is a stub handler for handling text messages
 */
public class TextHandler implements ContentHandler {

    protected DefaultLogger log = new DefaultLogger(TextHandler.class);

    /**
     * TODO - the class needs to declare the type of content it
     * should handle
     */
	protected String[] TYPES = null;
	protected Message LAST = null;
	/**
	 * 
	 */
	public TextHandler() {
		super();
	}

	public String[] getMessageTypes() {
		return null;
	}

	/**
	 * TODO - the method needs to extract the message and pass the
	 * text message to the engine properly
	 */
	public void processMessage(Message msg, Rete engine, MessageClient client) {

	}

}
