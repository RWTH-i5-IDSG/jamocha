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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.jamocha.parser.Expression;
import org.jamocha.parser.ParseException;
import org.jamocha.parser.Parser;
import org.jamocha.parser.ParserFactory;
import org.jamocha.parser.ParserNotFoundException;
import org.jamocha.parser.clips.TokenMgrError;

class StreamChannelImpl extends AbstractCommunicationChannel implements
		StreamChannel {

	private Reader reader;

	private Parser parser;

	private String parserName;

	private StreamChannelThread streamChannelThread;

	private class StreamChannelThread extends Thread {

		private boolean stopped = false;

		@Override
		public void run() {
			while (!stopped) {
				Expression command = null;
				try {
					while (!stopped
							&& (command = parser.nextExpression()) != null) {
						router.enqueueCommand(command, getChannelId());
					}
				} catch (ParseException e) {
					router.postMessageEvent(new MessageEvent(
							MessageEvent.PARSE_ERROR, e, getChannelId()));
					try {
						parser = ParserFactory.getParser(parserName, reader);
					} catch (ParserNotFoundException e1) {
						// we ignore this Exception here, because if the Parser
						// didn't exist init() would already have thrown an
						// Exception.
					}
				} catch (TokenMgrError e) {
					router.postMessageEvent(new MessageEvent(
							MessageEvent.PARSE_ERROR, e, getChannelId()));
					try {
						parser = ParserFactory.getParser(parserName, reader);
					} catch (ParserNotFoundException e1) {
						// we ignore this Exception here, because if the Parser
						// didn't exist init() would already have thrown an
						// Exception.
					}
				}
			}
		}

		private void setStopped() {
			stopped = true;
		}

	}

	public void init(InputStream inputStream, String parserName)
			throws ParserNotFoundException {
		init(new InputStreamReader(inputStream), parserName);
	}

	public void init(Reader reader, String parserName)
			throws ParserNotFoundException {
		this.reader = reader;
		this.parserName = parserName;
		streamChannelThread.setStopped();
		streamChannelThread = new StreamChannelThread();
		parser = ParserFactory.getParser(parserName, reader);
		streamChannelThread.start();
	}

	void close() {
		streamChannelThread.setStopped();
		try {
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean isAvailable() {
		return false;
	}

	public StreamChannelImpl(String channelId, MessageRouter router,
			InterestType interest) {
		super(channelId, router, interest);
		// parser = new CLIPSParser((Reader) null);
		streamChannelThread = new StreamChannelThread();
	}

	public void executeCommand(String commandString) {
		Expression command = null;
		try {
			while ((command = parser.nextExpression()) != null) {
				router.enqueueCommand(command, getChannelId());
			}
		} catch (ParseException e) {
			router.postMessageEvent(new MessageEvent(MessageEvent.PARSE_ERROR,
					e, getChannelId()));
		}
	}

}
