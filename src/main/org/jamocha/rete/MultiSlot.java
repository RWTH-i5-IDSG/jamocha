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

import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;

/**
 * @author Peter Lin
 * 
 * MultiSlot always returns Constants.ARRAY_TYPE. It is the class for array
 * types.
 */
public class MultiSlot extends Slot {

	protected Object[] value = {};

	public MultiSlot() {
		this("");
	}

	public MultiSlot(String name) {
		this(name, null);
	}

	public MultiSlot(String name, Object[] value) {
		super();
		super.setValueType(JamochaType.LIST);
		super.setName(name);
		this.value = value;
	}

	/**
	 * In some cases, a deftemplate can be define with a default value.
	 * 
	 * @param value
	 */
	public void setDefaultValue(Object[] value) {
		this.value = value;
	}

	/**
	 * We override the base implementation and do nothing, since a multislot is
	 * an object array. That means it is an array type
	 */
	public void setValueType(JamochaType type) {
	}

	public String valueToString() {
		return this.value.toString();
	}

	/**
	 * method returns a clone and set id, name and value.
	 */
	public Object clone() {
		MultiSlot newms = new MultiSlot();
		newms.setId(this.getId());
		newms.setName(this.getName());
		newms.value = this.value;
		return newms;
	}
}
