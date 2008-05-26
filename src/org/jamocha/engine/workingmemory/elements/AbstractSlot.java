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

import java.io.Serializable;

import org.jamocha.formatter.Formattable;
import org.jamocha.parser.JamochaType;

/**
 * @author Peter Lin
 * 
 * AbstractSlot contains common attributes of Slot, multislot and binding. Slot
 * classes need to implement the clone method for cloning the slots. This is
 * necessary because slots are used to parse CLIPS and for the RETE nodes. <br>
 * 
 */
public abstract class AbstractSlot implements Serializable, Cloneable,
		Formattable {

	/**
	 * The name of the slot
	 */
	protected String name;

	/**
	 * the id of the slot
	 */
	protected int id;

	/**
	 * The type of the value
	 */
	protected JamochaType type = JamochaType.UNDEFINED;

	/**
	 * depth is a place holder for ordered facts, which is a list of symbols.
	 * For the first version, ordered facts are not implemented. it is also used
	 * in the case a condition has multiple equal/not equal as in (attr2 "me" |
	 * "you" | ~"her" | ~"she")
	 */

	/**
	 * Get the name of the slot
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the name of the slot
	 * 
	 * @param text
	 */
	public void setName(final String text) {
		name = text;
	}

	public JamochaType getValueType() {
		return type;
	}

	public void setValueType(final JamochaType type) {
		this.type = type;
	}

	/**
	 * the id is the column id, this is the sequence java introspection returns
	 * the fields for the object
	 * 
	 * @return
	 */
	public int getId() {
		return id;
	}

	/**
	 * Set the column id for the slot. the id is the position of the slot in the
	 * deftemplate
	 * 
	 * @param id
	 */
	public void setId(final int id) {
		this.id = id;
	}

	/**
	 * A convienance method to clone slots. subclasses must implement this
	 * method.
	 */
	@Override
	public abstract Object clone();

	@Override
	public String toString() {
		return "Slot " + getName() + " Type: " + getValueType();
	}

	public boolean mergableTo(final AbstractSlot other) {
		return getValueType().equals(other.getValueType())
				&& getName().equals(other.getName());
	}
}