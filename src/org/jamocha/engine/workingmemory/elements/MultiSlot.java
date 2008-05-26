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
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;

/**
 * @author Peter Lin
 * 
 * MultiSlot always returns Constants.ARRAY_TYPE. It is the class for array
 * types.
 */
public class MultiSlot extends Slot {

	static final long serialVersionUID = 0xDeadBeafCafeBabeL;

	public MultiSlot() {
		this("");
	}

	public MultiSlot(final String name) {
		super();
		super.setValueType(JamochaType.LIST);
		value = JamochaValue.newList();
		super.setName(name);
	}

	public MultiSlot(final boolean silent) {
		this();
		this.silent = silent;
	}

	public MultiSlot(final boolean silent, final String name) {
		this(name);
		this.silent = silent;
	}

	/**
	 * We override the base implementation and do nothing, since a multislot is
	 * an object array. That means it is an array type
	 */
	@Override
	public void setValueType(final JamochaType type) {
	}

	@Override
	public String valueToString() {
		return value.toString();
	}

	/**
	 * method returns a clone and set id, name and value.
	 */
	@Override
	public Object clone() {
		final MultiSlot newms = new MultiSlot(silent);
		newms.setId(getId());
		newms.setName(getName());
		newms.value = value;
		return newms;
	}

	@Override
	public String format(final Formatter visitor) {
		return visitor.visit(this);
	}
}
