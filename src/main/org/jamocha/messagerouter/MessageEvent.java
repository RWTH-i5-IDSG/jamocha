/**
 * Copyright 2006 Alexander Wilden, Christoph Emonds, Sebastian Reinartz
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
package org.jamocha.messagerouter;

import org.jamocha.rete.AbstractEvent;

/**
 * The Class for MessageEvents.
 * 
 * @author Alexander Wilden, Christoph Emonds, Sebastian Reinartz
 * 
 */
public class MessageEvent extends AbstractEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final int COMMAND = 101;
	
	public static final int RESULT = 102;
	
	public static final int ENGINE = 103;
	
	public static final int ERROR = -1;

	/**
	 * The message that was send.
	 */
	private Object message;
	
	private int type;

	/**
	 * The constructor for a new MessageEvent. Uses CLIPS as standard-language.
	 * 
	 * @param message
	 *            The message that was send.
	 * @param channelId
	 *            The id of the sender of this message.
	 * @param receiver
	 *            The id of the receiver of this message.
	 */
	public MessageEvent(int type, Object message, String channelId) {
		super(channelId);
		this.type = type;
		this.message = message;
	}

	/**
	 * Returns the message of this event
	 * 
	 * @return The message
	 */
	public Object getMessage() {
		return message;
	}

	/**
	 * Returns the id of the sender of the message.
	 * 
	 * @return The sender-id
	 */
	public String getChannelId() {
		return (String) getSource();
	}

	public int getType() {
		return type;
	}
	
	public boolean isError() {
		return type < 0;
	}

	void setChannelId(String channelId) {
		this.source = channelId;
	}
}
