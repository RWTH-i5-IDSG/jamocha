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

import java.io.Reader;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

import org.jamocha.parser.Expression;
import org.jamocha.parser.ParseException;
import org.jamocha.parser.ParserFactory;
import org.jamocha.parser.ParserNotFoundException;
import org.jamocha.parser.clips.TokenMgrError;

/**
 * Implementation of a <code>StringChannel</code>.
 * 
 * @author Alexander Wilden
 * @author Christoph Emonds
 * @author Sebastian Reinartz
 * 
 */
class StringChannelImpl extends AbstractCommunicationChannel implements
		StringChannel {

	/**
	 * If blocked we collect all received message in this list to see when our
	 * command is fully processed in the engine.
	 */
	private List<MessageEvent> alreadyReceived = new LinkedList<MessageEvent>();

	public StringChannelImpl(String channelId, MessageRouter router,
			InterestType interest, String parserName)
			throws ParserNotFoundException {
		super(channelId, router, interest);
		this.parserName = parserName;
		parser = ParserFactory.getParser(parserName, (Reader) null);
	}

	public void executeCommand(String commandString) {
		executeCommand(commandString, false);
	}

	public void executeCommand(String commandString, boolean blocked) {
		StringReader reader = new StringReader(commandString);
		List<MessageEvent> commandMessages = blocked ? new LinkedList<MessageEvent>()
				: null;
		restartParser(reader);
		Expression command = null;
		try {
			alreadyReceived.clear();
			while ((command = parser.nextExpression()) != null) {
				router.enqueueCommand(command, getChannelId());
				if (blocked) {
					try {
						while (blocked) {
							router.fillMessageList(getChannelId(),
									commandMessages);
							int count = commandMessages.size();
							if (count > 0) {
								MessageEvent lastMessage = commandMessages
										.get(count - 1);
								if (lastMessage.getType() == MessageEvent.RESULT
										|| lastMessage.getType() == MessageEvent.ERROR) {
									alreadyReceived.addAll(commandMessages);
									commandMessages.clear();
									blocked = false;
								}
							}
							if (blocked) {
								Thread.sleep(10);
							}
						}
					} catch (InterruptedException e) {
						/* TODO for now we just ignore this case */
					}
					blocked = true;
				}
			}
		} catch (ParseException e) {
			router.postMessageEvent(new MessageEvent(MessageEvent.PARSE_ERROR,
					e, getChannelId()));
			restartParser(reader);
		} catch (TokenMgrError e) {
			router.postMessageEvent(new MessageEvent(MessageEvent.PARSE_ERROR,
					e, getChannelId()));
			restartParser(reader);
		}
	}

	@Override
	public void fillEventList(List<MessageEvent> eventList) {
		eventList.addAll(alreadyReceived);
		super.fillEventList(eventList);
	}

}
