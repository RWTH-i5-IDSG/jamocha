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

import java.io.Serializable;

import org.jamocha.rete.Rete;
/**
 * @author Peter Lin
 *
 */
public interface ContentHandler extends Serializable {
	/**
	 * classes implementing the method should provide a list of
	 * the message types the class supports. The function will call
	 * Message.getJMSType() and try to find a handler for it. If
	 * no handler is registered, the jms client will simply ignore
	 * the message.
	 * @return
	 */
	String[] getMessageTypes();
	/**
	 * Classes implementing the method need to provide concrete
	 * logic for extracting the contents of the message and
	 * process it properly.
	 * @param msg
	 * @param engine
	 */
	void processMessage(javax.jms.Message msg, Rete engine, MessageClient client);

}
