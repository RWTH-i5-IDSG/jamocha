/*
 * Copyright 2002-2008 The Jamocha Team
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

import java.util.List;

import org.jamocha.communication.events.MessageEvent;

/**
 * Interface for all possible Communication Channels used in the
 * {@link MessageRouter}.
 * 
 * @author Alexander Wilden
 * @author Christoph Emonds
 * @author Sebastian Reinartz
 * 
 */
public interface CommunicationChannel {

	/**
	 * Fills the given List with all MessageEvents currently available.
	 * 
	 * @param eventList
	 *            The List that should be filled with available events.
	 */
	public void fillEventList(List<MessageEvent> eventList);

	/**
	 * Returns the ID of the Channel.
	 * 
	 * @return ID of the Channel.
	 */
	public String getChannelId();

	/**
	 * Returns the <code>InterestType</code> of the Channel.
	 * 
	 * @return <code>InterestType</code> of the Channel.
	 * @see InterestType
	 */
	public InterestType getInterest();
}