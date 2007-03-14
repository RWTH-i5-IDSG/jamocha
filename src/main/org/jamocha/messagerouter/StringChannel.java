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

/**
 * Interface for Channels parsing single Strings as input.
 * 
 * @author Alexander Wilden
 * @author Christoph Emonds
 * @author Sebastian Reinartz
 * 
 */
public interface StringChannel extends CommunicationChannel {

	/**
	 * Parses the given command and send the Expression to the
	 * <code>MessageRouter</code>.
	 * <p>
	 * This channel calls <code>executeCommand(command, false)</code> and
	 * therefore doesn't wait until results are available.
	 * 
	 * @param command
	 *            The <code>String</code> that should be parsed.
	 */
	public void executeCommand(String command);

	/**
	 * Parses the given command and send the Expression to the
	 * <code>MessageRouter</code>.
	 * 
	 * @param command
	 *            The <code>String</code> that should be parsed.
	 * @param blocked
	 *            Set this to <code>true</code> if this method should be
	 *            blocked until results are available. These results are still
	 *            not returned immediatly.
	 */
	public void executeCommand(String command, boolean blocked);
}
