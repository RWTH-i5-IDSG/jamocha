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

import org.jamocha.parser.clips.CLIPSParser;
import org.jamocha.parser.clips.ParseException;
import org.jamocha.parser.clips.TokenMgrError;

class StreamChannelImpl extends AbstractCommunicationChannel implements
		StreamChannel {

	private Reader reader;

	private class StreamChannelThread extends Thread {

		private boolean stopped = false;

		@Override
		public void run() {
			while (!stopped) {
				Object command = null;
				try {
					while (!stopped && (command = parser.basicExpr()) != null) {
						router.enqueueCommand(command, getChannelId());
					}
				} catch (ParseException e) {
					router.postMessageEvent(new MessageEvent(
							MessageEvent.PARSE_ERROR, e, getChannelId()));
					parser.ReInit(reader);
				} catch (TokenMgrError e) {
					router.postMessageEvent(new MessageEvent(
							MessageEvent.PARSE_ERROR, e, getChannelId()));
					parser.ReInit(reader);
				}
			}
		}
		
		private void setStopped() {
			stopped = true;
		}

	}

	public void init(InputStream inputStream) {
		init(new InputStreamReader(inputStream));
	}

	public void init(Reader reader) {
		this.reader = reader;
		streamChannelThread.setStopped();
		streamChannelThread = new StreamChannelThread();
		parser.ReInit(reader);
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

	private CLIPSParser parser;

	private StreamChannelThread streamChannelThread;

	public StreamChannelImpl(String channelId, MessageRouter router,
			InterestType interest) {
		super(channelId, router, interest);
		this.parser = new CLIPSParser(router.getReteEngine(), (Reader) null);
		streamChannelThread = new StreamChannelThread();
	}

	public void executeCommand(String commandString) {
		Object command = null;
		try {
			while ((command = parser.basicExpr()) != null) {
				router.enqueueCommand(command, getChannelId());
			}
		} catch (ParseException e) {
			router.postMessageEvent(new MessageEvent(MessageEvent.PARSE_ERROR,
					e, getChannelId()));
		}
	}

}
