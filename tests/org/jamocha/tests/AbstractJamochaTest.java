/*
 * Copyright 2007 Sebastian Reinartz
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
package org.jamocha.tests;

import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

import org.jamocha.communication.events.MessageEvent;
import org.jamocha.communication.messagerouter.MessageRouter;
import org.jamocha.communication.messagerouter.StringChannel;
import org.jamocha.engine.Engine;

/**
 * The class provides basic methods to be used for junit tests. The setup method
 * creates a jamocha engine tha can be used. The executeTest... functions can be
 * used by extended classes to execute CLIPS-statements and compare returning
 * with expected results.
 * 
 * @author Sebastian Reinartz
 */
public abstract class AbstractJamochaTest extends TestCase {

	protected Engine engine;

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
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		engine = new Engine();
		MessageRouter router = engine.getMessageRouter();
		channel = router.openChannel("TestChannel");
	}

	/**
	 * executes a given CLIPS command an returns all message event
	 * 
	 * @param arg0
	 */
	@SuppressWarnings("unused")
	private List<MessageEvent> executeCommandReturnAll(String command) {
		List<MessageEvent> events = executeCommand(command);
		for (MessageEvent event : events)
			assertFalse(event.getMessage() instanceof Exception);
		return events;
	}

	/**
	 * executes a given CLIPS command an returns the last message event
	 * 
	 * @param arg0
	 */

	protected String executeCommandReturnLast(String command, String errorString) {
		String result = null;
		List<MessageEvent> events = executeCommand(command);
		for (MessageEvent event : events) {
			assertFalse(errorString, event.getMessage() instanceof Exception);
			result = event.getMessage().toString();
		}
		return result;
	}

	protected List<MessageEvent> executeCommand(String command) {
		channel.executeCommand(command, true);
		List<MessageEvent> events = new LinkedList<MessageEvent>();
		channel.fillEventList(events);
		return events;
	}

	/**
	 * The method executes the input CLIPS-command,checks return statements for
	 * jamocha exceptions and compares last result with given expectedLastResult
	 * 
	 * @param CLIPS
	 *            command
	 * @param expectedLastResult
	 * @param possible
	 *            error string
	 * @return
	 */
	protected void executeTestEquals(String inputCommand,
			String expectedLastResult, String errorString) {
		String result = executeCommandReturnLast(inputCommand, errorString);
		assertEquals(errorString, expectedLastResult, result);
	}

	/**
	 * The method executes the input CLIPS-command,checks return statements for
	 * jamocha exceptions and compares last result with given expectedLastResult
	 * 
	 * @param CLIPS
	 *            command
	 * @param expectedLastResult
	 * @return
	 */
	protected void executeTestEquals(String inputCommand,
			String expectedLastResult) {
		this.executeTestEquals(inputCommand, expectedLastResult, "");
	}

	/**
	 * The method executes the input CLIPS-command and checks return statements
	 * for jamocha exceptions
	 * 
	 * @param CLIPS
	 *            command
	 * @param possible
	 *            error string
	 * @return
	 */
	protected void executeTestException(String inputCommand, String errorString) {
		executeCommandReturnLast(inputCommand, errorString);
	}

	/**
	 * The method executes the input CLIPS-command and checks return statements
	 * for jamocha exceptions
	 * 
	 * @param CLIPS
	 *            command
	 * @return
	 */
	protected void executeTestException(String inputCommand) {
		this.executeTestException(inputCommand, "");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public abstract void test();
}
