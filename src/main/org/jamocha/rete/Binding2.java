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

/**
 * @author Peter Lin
 * 
 * Binding2 is used for bindings that are are numeric comparison like
 * >, <, >=, <=.
 */
public class Binding2 extends Binding {

	protected int operator = Constants.EQUAL;
	
	/**
	 * 
	 */
	public Binding2(int operator) {
		super();
		this.operator = operator;
	}

	public int getOperator() {
		return this.operator;
	}
}
