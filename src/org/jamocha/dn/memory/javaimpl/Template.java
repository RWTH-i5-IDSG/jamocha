/*
 * Copyright 2002-2014 The Jamocha Team
 * 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.jamocha.org/
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.jamocha.dn.memory.javaimpl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.IntStream;

import org.jamocha.dn.memory.Fact;
import org.jamocha.dn.memory.SlotType;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 *
 */
public class Template implements org.jamocha.dn.memory.Template {

	/**
	 * Template holding exactly one {@link SlotType#STRING} type.
	 */
	final public static Template STRING = new Template(
			"Simple template holding exactly one string type.", new Slot(SlotType.STRING,
					"String slot"));
	/**
	 * Template holding exactly one {@link SlotType#BOOLEAN} type.
	 */
	final public static Template BOOLEAN = new Template(
			"Simple template holding exactly one boolean type.", new Slot(SlotType.BOOLEAN,
					"Boolean slot"));
	/**
	 * Template holding exactly one {@link SlotType#DOUBLE} type.
	 */
	final public static Template DOUBLE = new Template(
			"Simple template holding exactly one double type.", new Slot(SlotType.DOUBLE,
					"Double slot"));
	/**
	 * Template holding exactly one {@link SlotType#LONG} type.
	 */
	final public static Template LONG = new Template(
			"Simple template holding exactly one long type.", new Slot(SlotType.LONG, "Long slot"));

	final String description;
	final HashMap<String, SlotAddress> slotNames = new HashMap<>();
	final SlotType[] slotTypes;

	public Template(final String description, final Slot... slots) {
		this.description = description;
		this.slotTypes = Arrays.stream(slots).map(s -> s.getSlotType()).toArray(SlotType[]::new);
		IntStream.range(0, slots.length).forEach(
				i -> this.slotNames.put(slots[i].getName(), new SlotAddress(i)));
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public SlotType getSlotType(final org.jamocha.dn.memory.SlotAddress slotAddress) {
		return getSlotType(((SlotAddress) slotAddress).index);
	}

	public SlotType getSlotType(final int index) {
		return this.slotTypes[index];
	}

	@Override
	public SlotAddress getSlotAddress(final String name) {
		return this.slotNames.get(name);
	}

	@Override
	public Fact newFact(final Object... values) {
		for (int i = 0; i < this.slotTypes.length; ++i) {
			assert this.slotTypes[i].getJavaClass().isInstance(values[i]);
		}
		return new Fact(this, values);
	}
}