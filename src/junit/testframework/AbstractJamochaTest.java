/*
 * Copyright 2006 Sebastian Reinartz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.jamocha.org
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package testframework;

import java.util.LinkedList;
import java.util.List;

import org.jamocha.messagerouter.MessageEvent;
import org.jamocha.messagerouter.MessageRouter;
import org.jamocha.messagerouter.StringChannel;
import org.jamocha.rete.Rete;

import junit.framework.TestCase;

/**
 * @author Sebastian Reinartz
 * 
 */
public abstract class AbstractJamochaTest extends TestCase {

	private Rete engine;

	private StringChannel channel;

	/**
	 * @param arg0
	 */
	public AbstractJamochaTest(String arg0) {
		super(arg0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		engine = new Rete();
		MessageRouter router = engine.getMessageRouter();
		channel = router.openChannel("TestChannel");
	}

	/**
	 * executes a given CLIPS command an returns all message event
	 * 
	 * @param arg0
	 */
	protected List<MessageEvent> executeCommandReturnAll(String command) {
		List<MessageEvent> events = executeCommand(command);
		for (MessageEvent event : events) {
			assertFalse(event.getMessage() instanceof Exception);
		}
		return events;
	}

	/**
	 * executes a given CLIPS command an returns the last message event
	 * 
	 * @param arg0
	 */

	protected String executeCommandReturnLast(String command) {
		String result = null;
		List<MessageEvent> events = executeCommand(command);
		for (MessageEvent event : events) {
			assertFalse(event.getMessage() instanceof Exception);
			result = (String) event.getMessage().toString();
		}
		return result;
	}

	private List<MessageEvent> executeCommand(String command) {
		channel.executeCommand(command, true);
		List<MessageEvent> events = new LinkedList<MessageEvent>();
		channel.fillEventList(events);
		return events;
	}

	protected void executeTestEquals(String inputCommand, String expectedLastResult) {
		String result = this.executeCommandReturnLast(inputCommand);
		assertEquals(expectedLastResult, result);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void test() {

	}
}
