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
package org.jamocha.rete;

import java.util.EventObject;

/**
 * @author Peter Lin
 * 
 */
public abstract class AbstractEvent extends EventObject {

	public static final int ADD_RULE_EVENT = 0;

	public static final int REMOVE_RULE_EVENT = 1;

	public static final int PARSE_ERROR = 2;

	public static final int INVALID_RULE = 3;

	public static final int RULE_EXISTS = 4;

	public static final int TEMPLATE_NOTFOUND = 5;

	public static final int CLIPSPARSER_ERROR = 6;

	public static final int CLIPSPARSER_WARNING = 7;
	
	public static final int CLIPSPARSER_REINIT = 8;

	public static final int FUNCTION_NOT_FOUND = 9;
	
	public static final int FUNCTION_INVALID = 10;
	
	public static final int ADD_NODE_ERROR = 11;
	
	/**
	 * @param source
	 */
	public AbstractEvent(Object source) {
		super(source);
	}

}
