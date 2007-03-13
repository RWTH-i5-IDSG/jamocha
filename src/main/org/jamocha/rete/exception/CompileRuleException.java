/*
 * Copyright 2002-2006 Peter Lin
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
package org.jamocha.rete.exception;

/**
 * @author Peter Lin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CompileRuleException extends Exception {

	private static final long serialVersionUID = 1L;
	public static final String ADD_FAILURE = "Unable to add the rule, due to compilation.";
    public static final String INVALID_RULE = "The rule was not added because it is invalid";
    
	/**
	 * 
	 */
	public CompileRuleException() {
		super();
	}

	/**
	 * @param message
	 */
	public CompileRuleException(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public CompileRuleException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param cause
	 */
	public CompileRuleException(Throwable cause) {
		super(cause);
	}

}
