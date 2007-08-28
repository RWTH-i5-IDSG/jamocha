/**
 * Copyright 2006 Alexander Wilden
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

import java.util.ArrayList;
import java.util.List;

import org.jamocha.messagerouter.MessageEvent;
import org.jamocha.messagerouter.MessageRouter;
import org.jamocha.messagerouter.StringChannel;
import org.jamocha.rete.Rete;

public class MessageRouterTest {

	/**
	 * a simple main-method for testing purposes
	 * 
	 * @param args
	 *            The commandline arguments
	 */
	public static void main(String[] args) {
		// starting the MessageRouter
		System.out.println("Started");
		Rete engine = new Rete();
		MessageRouter router = engine.getMessageRouter();
		StringChannel stringChannel = router.openChannel("TestChannel");
		long start;
		start = System.currentTimeMillis();
		// try {
		stringChannel
				.executeCommand("(deftemplate wurst(slot name)(slot size))");
		List<MessageEvent> messages = new ArrayList<MessageEvent>();
		do {
			messages.clear();
			Thread.yield();
			stringChannel.fillEventList(messages);
			for (MessageEvent message : messages) {
				if (message.isError()) {
					System.err.println(message.getMessage());
				} else {
					System.out.println(message.getMessage());
				}
			}
		} while (!messages.isEmpty());

		// making some simple requests
		System.out.println(Runtime.getRuntime().maxMemory());
		for (int i = 1; i <= 100000; ++i) {
			start = System.currentTimeMillis();
			stringChannel.executeCommand("(assert (wurst(name salami" + i
					+ ")(size 20)))");
			stringChannel.executeCommand("(+ 5 " + i + ")");
			System.out.println("-- elapsed time: "
					+ (System.currentTimeMillis() - start) + "ms");
			if (i % 10000 == 0)
				System.out.println(i
						+ ": "
						+ (Runtime.getRuntime().totalMemory() - Runtime
								.getRuntime().freeMemory()));

		}
		stringChannel.executeCommand("(facts)");
		do {
			messages.clear();
			Thread.yield();
			stringChannel.fillEventList(messages);
			for (MessageEvent message : messages) {
				if (message.isError()) {
					System.err.println(message.getMessage());
				} else {
					System.out.println(message.getMessage());
				}
			}
		} while (!messages.isEmpty());
		System.out.println("-- elapsed time: "
				+ (System.currentTimeMillis() - start) + "ms");
	}

}
