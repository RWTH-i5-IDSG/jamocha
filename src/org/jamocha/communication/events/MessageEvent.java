/*
 * Copyright 2002-2008 Peter Lin & The Jamocha Team
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

package org.jamocha.communication.events;

import org.jamocha.communication.events.AbstractEvent;

/**
 * The Class for MessageEvents.
 * 
 * @author Alexander Wilden
 * @author Christoph Emonds
 * @author Sebastian Reinartz
 * @author Josef Alexander Hahn
 * 
 */
public class MessageEvent extends AbstractEvent {

    public enum MessageEventType {
        ENGINE,
        ERROR,
        COMMAND,
        RESULT,
        PARSE_ERROR,
    }
    private static final long serialVersionUID = 1L;
    /**
     * The message that was send.
     */
    private Object message;
    /**
     * Type of this message.
     */
    private MessageEventType type = null;

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
    public MessageEvent(MessageEventType type, Object message, String channelId) {
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
    public MessageEventType getType() {
        return type;
    }

    /**
     * Returns <code>true</code> if the message clearly is an error and
     * <code>false</code> otherwise.
     * 
     * @return <code>true</code> if the message is an error.
     */
    public boolean isError() {
        return (type == MessageEventType.ERROR);
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
