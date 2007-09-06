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
package testcases;

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
public class JamochaTest extends TestCase {

	protected Rete engine;

	protected StringChannel channel;

	/**
	 * @param arg0
	 */
	public JamochaTest(String arg0) {
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
	protected List executeCommand(String command) {
		channel.executeCommand(command, true);
		List<MessageEvent> events = new LinkedList<MessageEvent>();
		channel.fillEventList(events);
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
		String result=null;
		channel.executeCommand(command, true);
		List<MessageEvent> events = new LinkedList<MessageEvent>();
		channel.fillEventList(events);
		for (MessageEvent event : events) {
			assertFalse(event.getMessage() instanceof Exception);
			result = (String)event.getMessage().toString();
		}
		return result;
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
