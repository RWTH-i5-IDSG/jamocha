/*
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
 * @author Alexander Wilden
 * @author Christoph Emonds
 * @author Sebastian Reinartz
 * 
 */
public class MessageEvent extends AbstractEvent {

	private static final long serialVersionUID = 1L;

	/**
	 * Message type for an incoming command (e.g. via the Shell).
	 */
	public static final int COMMAND = 101;

	/**
	 * Message type for a result coming from the engine.
	 */
	public static final int RESULT = 102;

	/**
	 * Message type for some message (e.g. error message) from the engine.
	 */
	public static final int ENGINE = 103;

	/**
	 * Message type for an error.
	 */
	public static final int ERROR = -1;

	/**
	 * The message that was send.
	 */
	private Object message;

	/**
	 * Type of this message.
	 */
	private int type;

	/**
	 * Constructor for a new <code>MessageEvent</code>. Sets all important
	 * attributes.
	 * 
	 * @param type
	 *            Type of this message.
	 * @param message
	 *            Content of this message.
	 * @param channelId
	 *            The ID of the channel this message was caused by. Also if this
	 *            is an engine message some channel message triggered this event
	 *            and therefore its ID is filled in (and not some ID of the
	 *            engine).
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
	 * Returns the ID of the channel that caused this message.
	 * 
	 * @return The channel ID.
	 */
	public String getChannelId() {
		return (String) getSource();
	}

	/**
	 * Returns the type of this message.
	 * 
	 * @return The type of this message.
	 */
	public int getType() {
		return type;
	}

	/**
	 * Returns <code>true</code> if the message clearly is an error and
	 * <code>false</code> otherwise.
	 * 
	 * @return <code>true</code> if the message is an error.
	 */
	public boolean isError() {
		return (type < 0);
	}

	/**
	 * Sets the ID of the channel. Only the <code>MessageRouter</code> may
	 * change / set it.
	 * 
	 * @param channelId
	 *            The new channel ID.
	 */
	void setChannelId(String channelId) {
		this.source = channelId;
	}
}
