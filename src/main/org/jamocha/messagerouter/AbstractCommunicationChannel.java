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

import java.util.List;

abstract class AbstractCommunicationChannel implements CommunicationChannel {
	
	private String channelId;
		
	protected MessageRouter router;
	
	private InterestType interest;

	protected AbstractCommunicationChannel(String channelId, MessageRouter router, InterestType interest) {
		this.channelId = channelId;
		this.router = router;
		this.interest = interest;
	}

	public void fillEventList(List<MessageEvent> eventList) {
		router.fillMessageList(getChannelId(), eventList);
	}

	public String getChannelId() {
		return channelId;
	}

	public InterestType getInterest() {
		return interest;
	}
}
