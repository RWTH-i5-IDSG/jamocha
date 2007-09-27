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

import java.io.Serializable;

import org.jamocha.Constants;

/**
 * @author Peter Lin
 * 
 * CompositeIndex is used by ObjectTypeNodes to hash AlphaNodes and stick them
 * in a HashTable. This should improve the performance over the proof-of-concept
 * implementation using Strings.
 */
public class CompositeIndex implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String name = null;

	// by default, we set it to Equals
	private int operator = Constants.EQUAL;

	private Object value = null;

	private int hashCode;

	/**
	 * 
	 */
	public CompositeIndex(String name, int operator, Object value) {
		super();
		this.name = name;
		this.operator = operator;
		this.value = value;
		this.calculateHash();
	}

	protected void calculateHash() {
		this.hashCode = name.hashCode() + this.operator + this.value.hashCode();
	}

	public boolean equals(Object val) {
		if (this == val) {
			return true;
		}
		if (val == null || getClass() != val.getClass()) {
			return false;
		}

		CompositeIndex ci = (CompositeIndex) val;
		return ci.name.equals(this.name) && ci.operator == this.operator
				&& ci.value.equals(this.value);
	}

	public int hashCode() {
		return this.hashCode;
	}

	public String toPPString() {
		return this.name + ":" + this.operator + ":"
				+ String.valueOf(this.value);
	}
}
