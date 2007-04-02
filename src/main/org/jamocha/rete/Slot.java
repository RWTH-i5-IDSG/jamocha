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

import org.jamocha.parser.IllegalConversionException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;

/**
 * @author Peter Lin
 * 
 * Slot is similar to CLIPS slots, though slightly different.
 * 
 */
public class Slot extends AbstractSlot {
    
	protected JamochaValue value = JamochaValue.NIL;
	
	public Slot() {
	}

	/**
	 * Create a new instance with a given name
	 * 
	 * @param name
	 */
	public Slot(String name) {
		this.setName(name);
	}

	/**
	 * For convenience you can create here a slot with a given value directly
	 */
	public Slot(String name, JamochaValue value) {
		this(name);
		this.value = value;
	}

	/**
	 * get the value of the slot
	 * 
	 * @return
	 */
	public JamochaValue getValue() {
		return this.value;
	}

	/**
	 * set the value of the slot
	 * 
	 * @param val
	 * @throws IllegalConversionException
	 */
	public void setValue(JamochaValue val) {
		if (inspectType(val)) {
			this.value = val;
		} else {
			try {
				this.value = val.implicitCast(this.getValueType());
			} catch (IllegalConversionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * method will check the type of the value and the type of the slot
	 * 
	 * @param value
	 *            value, which is checked, if it has the same type as the slot
	 * @return <code>true</code> if value has a compatible type, otherwise
	 *         <code>false</code>
	 */
	protected boolean inspectType(JamochaValue value) {
		if (getValueType().equals(JamochaType.UNDEFINED)
				|| value.getType().equals(JamochaType.BINDING)
				|| getValueType().equals(value.getType())) {
			return true;
		}
		return false;
	}

	/**
	 * A convienance method to clone slots
	 */
	public Object clone() {
		Slot newslot = new Slot();
		newslot.setId(this.getId());
		newslot.setName(this.getName());
		newslot.value = this.value;
		newslot.setValueType(this.getValueType());
		return newslot;
	}

	public String valueToString() {
		return this.value.toString();
	}
}
