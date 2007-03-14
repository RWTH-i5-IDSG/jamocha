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

import java.util.List;

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
	 * Unique ID of this channel.
	 */
	private String channelId;

	/**
	 * The <code>MessageRouter</code> this channel was opened in.
	 */
	protected MessageRouter router;

	/**
	 * The <code>InterestType</code> of this channel.
	 */
	private InterestType interest;

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
}
