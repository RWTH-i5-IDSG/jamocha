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

package org.jamocha.communication.messagerouter;

import org.jamocha.communication.events.MessageEvent;

import java.io.Reader;
import java.util.List;

import org.jamocha.parser.ModeNotFoundException;
import org.jamocha.parser.Parser;
import org.jamocha.parser.ParserFactory;

/**
 * Abstract implementation of the {@link CommunicationChannel} Interface. All
 * methods are filled with a standard behavior.
 * 
 * @author Alexander Wilden
 * @author Christoph Emonds
 * @author Sebastian Reinartz
 * 
 */
abstract class AbstractCommunicationChannel implements CommunicationChannel {

	/**
	 * The <code>Parser</code> used to parse the input we get from the
	 * <code>reader</code>.
	 */
	protected Parser parser;

	/**
	 * Name of the <code>Parser</code> we use. We need it when we restart the
	 * <code>Parser</code>.
	 */
	protected String parserName;

	/**
	 * Unique ID of this channel.
	 */
	private final String channelId;

	/**
	 * The <code>MessageRouter</code> this channel was opened in.
	 */
	protected MessageRouter router;

	/**
	 * The <code>InterestType</code> of this channel.
	 */
	private final InterestType interest;

	/**
	 * Constructor which directly accepts all the possible attributes for a
	 * Channel.
	 * 
	 * @param channelId
	 *            ID of this Channel.
	 * @param router
	 *            Instance of the <code>MessageRouter</code>.
	 * @param interest
	 *            <code>InterestType</code> of this Channel.
	 */
	protected AbstractCommunicationChannel(String channelId,
			MessageRouter router, InterestType interest) {
		this.channelId = channelId;
		this.router = router;
		this.interest = interest;
	}

	/**
	 * Fills the given List with all MessageEvents currently available.
	 * 
	 * @param eventList
	 *            The List that should be filled with available events.
	 */
	public void fillEventList(List<MessageEvent> eventList) {
		router.fillMessageList(getChannelId(), eventList);
	}

	/**
	 * Returns the ID of the Channel.
	 * 
	 * @return ID of the Channel.
	 */
	public String getChannelId() {
		return channelId;
	}

	/**
	 * Returns the <code>InterestType</code> of the Channel.
	 * 
	 * @return <code>InterestType</code> of the Channel.
	 * @see InterestType
	 */
	public InterestType getInterest() {
		return interest;
	}

	protected void restartParser(Reader reader) {
		try {
			parser = ParserFactory.getParser(parserName, reader);
		} catch (ModeNotFoundException e1) {
			// we ignore this Exception here, because if the Parser
			// didn't exist init() would already have thrown an
			// Exception.
		}
	}
}
