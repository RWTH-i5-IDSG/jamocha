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
package org.jamocha.rule;

import java.io.Serializable;

/**
 * @author Peter Lin
 * The purpose of the class is for conditions that have & or |. For
 * example (name "bob" | "mike" | "jordan"), (name ~"mike" & ~"bob")
 * In those cases, we don't want to create a Literal constraint, since
 * they are all for the same slot.
 */
public class MultiValue implements Serializable {

	protected Object value = null;
	protected boolean negated = false;
	
	/**
	 * 
	 */
	public MultiValue() {
		super();
	}
	
	public MultiValue(Object val) {
		setValue(val);
	}
	
	public MultiValue(Object val, boolean neg) {
		setValue(val);
		this.negated = neg;
	}

	public void setValue(Object val) {
		this.value = val;
	}
	
	public Object getValue() {
		return this.value;
	}
	
	public void setNegated(boolean neg) {
		this.negated = neg;
	}
	
	public boolean getNegated() {
		return this.negated;
	}
}
