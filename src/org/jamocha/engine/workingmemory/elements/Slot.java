/*
 * Copyright 2002-2008 Peter Lin & The Jamocha Team
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

package org.jamocha.engine.workingmemory.elements;

import org.jamocha.formatter.Formatter;
import org.jamocha.parser.IllegalConversionException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.engine.ConstraintViolationException;

/**
 * @author Peter Lin
 * 
 * Slot is similar to CLIPS slots, though slightly different.
 * 
 */
public class Slot extends AbstractSlot {

	static final long serialVersionUID = 0xDeadBeafCafeBabeL;

	protected JamochaValue value = JamochaValue.NIL;

	protected boolean silent = false;

	public Slot() {
	}

	public Slot(final boolean silent) {
		this.silent = silent;
	}

	public boolean isSilent() {
		return silent;
	}

	/**
	 * Create a new instance with a given name
	 * 
	 * @param name
	 */
	public Slot(final String name) {
		setName(name);
	}

	public Slot(final boolean silent, final String name) {
		this(name);
		this.silent = silent;
	}

	/**
	 * For convenience you can create here a slot with a given value directly
	 */
	public Slot(final String name, final JamochaValue value) {
		this(name);
		this.value = value;
	}

	public Slot(final boolean silent, final String name,
			final JamochaValue value) {
		this(name, value);
		this.silent = silent;
	}

	/**
	 * get the value of the slot
	 * 
	 * @return
	 */
	public JamochaValue getValue() {
		return value;
	}

	/**
	 * set the value of the slot
	 * 
	 * @param val
	 * @throws IllegalConversionException
	 */
	public void setValue(final JamochaValue val)
			throws ConstraintViolationException {
		if (inspectType(val))
			value = val;
		else
			try {
				value = val.implicitCast(getValueType());
			} catch (final IllegalConversionException e) {
				throw new ConstraintViolationException("Could not cast value "
						+ val + " to type " + getValueType() + ".");
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
	protected boolean inspectType(final JamochaValue value) {
		if (getValueType().equals(JamochaType.UNDEFINED)
				|| value.getType().equals(JamochaType.BINDING)
				|| getValueType().equals(value.getType()))
			return true;
		return false;
	}

	/**
	 * A convienance method to clone slots
	 */
	@Override
	public Object clone() {
		final Slot newslot = new Slot(silent);
		newslot.setId(getId());
		newslot.setName(getName());
		newslot.value = value;
		newslot.setValueType(getValueType());
		return newslot;
	}

	public String valueToString() {
		return value.toString();
	}

	@Override
	public String toString() {
		String result = super.toString();
		result += valueToString();
		return result;
	}

	public String format(final Formatter visitor) {
		return visitor.visit(this);
	}

	@Override
	public boolean mergableTo(final AbstractSlot other) {
		// 1. ask superclass:
		boolean result = super.mergableTo(other);
		// 2. slot instance?
		if (result)
			result = other instanceof Slot;
		// 3. same value:
		if (result)
			result = getValue().equals(((Slot) other).getValue());
		return result;
	}
}
