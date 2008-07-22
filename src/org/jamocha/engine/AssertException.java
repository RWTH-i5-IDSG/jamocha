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

package org.jamocha.engine;

import org.jamocha.parser.EvaluationException;

/**
 * @author Peter Lin
 * 
 * AssertException should be thrown if a node encounters issues matching a fact.
 * Normally, this should not occur. If it does, it generally means there's a bug
 * in the core RETE nodes.
 */
public class AssertException extends EvaluationException {

	private static final long serialVersionUID = 1L;

	/**
	 * @param message
	 */
	public AssertException(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public AssertException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param cause
	 */
	public AssertException(Throwable cause) {
		super(cause);
	}

}
