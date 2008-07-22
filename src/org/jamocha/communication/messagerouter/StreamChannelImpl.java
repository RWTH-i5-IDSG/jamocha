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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.jamocha.communication.events.MessageEvent;
import org.jamocha.parser.Expression;
import org.jamocha.parser.ParseException;
import org.jamocha.parser.ParserFactory;

/**
 * Implementation of a <code>StreamChannel</code>.
 * 
 * @author Alexander Wilden
 * @author Christoph Emonds
 * @author Sebastian Reinartz
 * 
 */
class StreamChannelImpl extends AbstractCommunicationChannel implements
		StreamChannel {

	/**
	 * The <code>Reader</code> we read from.
	 */
	private Reader reader;

	/**
	 * Thread reading from the <code>reader</code>, parsing the content and
	 * sending the Expressions to the <code>MessageRouter</code>.
	 */
	private StreamChannelThread streamChannelThread;

	/**
	 * Flag indicating if the initialzation phase is done.
	 */
	private boolean initDone = false;

	public StreamChannelImpl(String channelId, MessageRouter router,
			InterestType interest) {
		super(channelId, router, interest);
		streamChannelThread = new StreamChannelThread();
	}

	public void init(InputStream inputStream) {
		init(new InputStreamReader(inputStream));
	}

	public void init(Reader reader) {
		this.reader = reader;
		streamChannelThread.setStopped();
		streamChannelThread = new StreamChannelThread();
		parser = ParserFactory.getParser(reader);
		streamChannelThread.start();
		initDone = true;
	}

	void close() {
		streamChannelThread.setStopped();
	}

	public boolean isAvailable() {
		return initDone;
	}

	private class StreamChannelThread extends Thread {

		private boolean stopped = false;

		@Override
		public void run() {
			while (!stopped) {
				Expression command = null;
				try {
					while (!stopped && parser != null
							&& (command = parser.nextExpression()) != null)
						router.enqueueCommand(command, getChannelId());
				} catch (ParseException e) {
					router.postMessageEvent(new MessageEvent(
							MessageEvent.MessageEventType.PARSE_ERROR, e,
							getChannelId()));
					restartParser(reader);
				} catch (NullPointerException e) {
					// If the Thread is stopped we catch the last NPE that
					// occurs when closing everything. Otherwise we just throw
					// it as before.
					if (!stopped)
						throw e;
				}
			}
		}

		private void setStopped() {
			stopped = true;
		}

	}

}
