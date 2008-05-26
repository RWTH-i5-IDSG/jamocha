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

package org.jamocha.communication.agent;

import jade.lang.acl.ACLMessage;

public enum Performative {
	ACCEPT_PROPOSAL(ACLMessage.ACCEPT_PROPOSAL),
	AGREE(ACLMessage.AGREE),
	CANCEL(ACLMessage.CANCEL),
	CALL_FOR_PROPOSAL(ACLMessage.CFP),
	CONFIRM(ACLMessage.CONFIRM),
	DISCONFIRM(ACLMessage.DISCONFIRM),
	FAILURE(ACLMessage.FAILURE),
	INFORM(ACLMessage.INFORM),
	INFORM_IF(ACLMessage.INFORM_IF),
	INFORM_REF(ACLMessage.INFORM_REF),
	NOT_UNDERSTOOD(ACLMessage.NOT_UNDERSTOOD),
	PROPAGATE(ACLMessage.PROPAGATE),
	PROPOSE(ACLMessage.PROPOSE),
	PROXY(ACLMessage.PROXY), 
	QUERY_IF(ACLMessage.QUERY_IF),
	QUERY_REF(ACLMessage.QUERY_REF),
	REFUSE(ACLMessage.REFUSE),
	REJECT_PROPOSAL(ACLMessage.REJECT_PROPOSAL),
	REQUEST(ACLMessage.REQUEST),
	REQUEST_WHEN(ACLMessage.REQUEST_WHEN),
	REQUEST_WHENEVER(ACLMessage.REQUEST_WHENEVER),
	SUBSCRIBE(ACLMessage.SUBSCRIBE);
	
	private int messageCode;
	
	private Performative(int messageCode) {
		this.messageCode = messageCode;
	}

	public int getMessageCode() {
		return messageCode;
	}
}