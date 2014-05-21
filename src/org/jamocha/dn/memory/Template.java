/*
 * Copyright 2002-2013 The Jamocha Team
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
package org.jamocha.dn.memory;

import java.util.Arrays;

import lombok.ToString;
import lombok.Value;

/**
 * A Template is an array of {@link SlotType slot types}. Facts always comply with some Template.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @see SlotType
 * @see Fact
 */
@ToString
public class Template {
	@Value
	public static class Slot {
		final SlotType slotType;
		final String name;
	}

	final Slot slots[];
	final String comment;

	/**
	 * Template holding exactly one {@link SlotType#STRING} type.
	 */
	final public static Template STRING = new Template(SlotType.STRING);
	/**
	 * Template holding exactly one {@link SlotType#BOOLEAN} type.
	 */
	final public static Template BOOLEAN = new Template(SlotType.BOOLEAN);
	/**
	 * Template holding exactly one {@link SlotType#DOUBLE} type.
	 */
	final public static Template DOUBLE = new Template(SlotType.DOUBLE);
	/**
	 * Template holding exactly one {@link SlotType#LONG} type.
	 */
	final public static Template LONG = new Template(SlotType.LONG);

	/**
	 * Constructs a template holding the given comment and {@link SlotType slot}.
	 * 
	 * @param comment
	 *            comment describing the template
	 * @param slots
	 *            {@link SlotType slot types} to hold
	 */
	public Template(final String comment, final Slot... slots) {
		this.comment = comment;
		this.slots = slots;
	}

	/**
	 * Constructs a template holding the given comment and {@link SlotType slot types} with empty
	 * slot names.
	 * 
	 * @param slots
	 *            {@link Slot slots} to hold
	 */
	public Template(final String comment, final SlotType... slotTypes) {
		this(comment, Arrays.stream(slotTypes).map(t -> new Slot(t, "")).toArray(Slot[]::new));
	}

	/**
	 * Constructs a template holding the given {@link Slot slots}.
	 * 
	 * @param slots
	 *            {@link Slot slots} to hold
	 */
	public Template(final Slot... slots) {
		this("", slots);
	}

	/**
	 * Constructs a template holding the given {@link SlotType slot types} with empty slot names.
	 * 
	 * @param slots
	 *            {@link SlotType slot types} to hold
	 */
	public Template(final SlotType... slotTypes) {
		this("", slotTypes);
	}

	/**
	 * Gets the {@link SlotType} corresponding to the position specified by the given index.
	 * 
	 * @param index
	 *            position in the template
	 * @return {@link SlotType} corresponding to the position specified by the given index
	 */
	public SlotType getSlotsType(final int index) {
		return this.slots[index].slotType;
	}

	/**
	 * Ease-of-use method to create facts with type-check for its arguments.
	 * 
	 * @param values
	 *            values to store in the fact instance to create
	 * @return newly created fact instance holding the values specified
	 */
	public Fact newFact(final Object... values) {
		for (int i = 0; i < this.slots.length; ++i) {
			assert getSlotsType(i).getJavaClass().isInstance(values[i]);
		}
		return new Fact(this, values);
	}

}
