/*
 * Copyright 2006 Alexander Wilden, Christoph Emonds, Sebastian Reinartz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://jamocha.sourceforge.net/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.jamocha.rete;

import java.util.ArrayList;
import java.util.List;

import org.jamocha.messagerouter.MessageEvent;
import org.jamocha.messagerouter.MessageRouter;
import org.jamocha.messagerouter.StreamChannel;
import org.jamocha.parser.JamochaValue;
import org.jamocha.parser.ParserNotFoundException;

/**
 * This class represents a simple command line shell on System.in / System.out.
 * 
 * @author Alexander Wilden
 * 
 */
public class Shell {

	public static final String CHANNELNAME = "Shell";

	private MessageRouter router;

	private StreamChannel channel;

	public Shell(Rete engine) throws ParserNotFoundException {
		router = engine.getMessageRouter();
		channel = router.openChannel(CHANNELNAME, System.in,"clips");
	}

	public void run() {
		List<MessageEvent> msgEvents = new ArrayList<MessageEvent>();
		boolean printPrompt = false;
		System.out.print(Constants.SHELL_PROMPT);

		while (true) {
			channel.fillEventList(msgEvents);
			if (!msgEvents.isEmpty()) {
				for (MessageEvent event : msgEvents) {
					if (event.getType() == MessageEvent.PARSE_ERROR
							|| event.getType() == MessageEvent.ERROR
							|| event.getType() == MessageEvent.RESULT) {
						printPrompt = true;
					}
					if (event.getType() == MessageEvent.ERROR) {
						// System.out.println(exceptionToString(
						// (Exception) event.getMessage()).trim());
						String msg = ((Exception) event.getMessage())
								.getMessage();
						if (msg != null) {
							System.out.println(msg.trim());
						}
					}
					if (event.getType() != MessageEvent.COMMAND) {
						if (!event.getMessage().toString().equals("")
								&& !event.getMessage().equals(JamochaValue.NIL)) {
							System.out.println(event.getMessage().toString()
									.trim());
						}
					}
				}
				msgEvents.clear();
				if (printPrompt) {
					System.out.print(Constants.SHELL_PROMPT);
				}
				printPrompt = false;
			} else {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// ignore an interruption
				}
			}
		}
	}

}
