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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.OptionalInt;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import lombok.Getter;

import org.apache.logging.log4j.Marker;
import org.jamocha.dn.memory.Fact;
import org.jamocha.dn.memory.MemoryFact;
import org.jamocha.dn.memory.SlotType;
import org.jamocha.filter.fwa.FunctionWithArguments;
import org.jamocha.logging.MarkerType;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 *
 */
public class Template implements org.jamocha.dn.memory.Template {

	/**
	 * Template holding exactly one {@link SlotType#STRING} type.
	 */
	final public static Template STRING = new Template("STRING",
			"Simple template holding exactly one string type.", new Slot(SlotType.STRING,
					"String slot"));
	/**
	 * Template holding exactly one {@link SlotType#BOOLEAN} type.
	 */
	final public static Template BOOLEAN = new Template("BOOLEAN",
			"Simple template holding exactly one boolean type.", new Slot(SlotType.BOOLEAN,
					"Boolean slot"));
	/**
	 * Template holding exactly one {@link SlotType#DOUBLE} type.
	 */
	final public static Template DOUBLE = new Template("DOUBLE",
			"Simple template holding exactly one double type.", new Slot(SlotType.DOUBLE,
					"Double slot"));
	/**
	 * Template holding exactly one {@link SlotType#LONG} type.
	 */
	final public static Template LONG = new Template("LONG",
			"Simple template holding exactly one long type.", new Slot(SlotType.LONG, "Long slot"));

	@Getter(onMethod = @__(@Override))
	final String name;
	@Getter(onMethod = @__(@Override))
	final String description;
	@Getter(onMethod = @__(@Override))
	final Collection<Slot> slots;
	final HashMap<String, SlotAddress> slotNames = new HashMap<>();
	final SlotType[] slotTypes;
	@Getter(onMethod = @__(@Override))
	final Marker instanceMarker;

	Template(final String name, final String description, final Slot... slots) {
		this.name = name;
		this.description = description;
		this.slots = Arrays.asList(slots);
		this.slotTypes = Arrays.stream(slots).map(s -> s.getSlotType()).toArray(SlotType[]::new);
		IntStream.range(0, slots.length).forEach(
				i -> this.slotNames.put(slots[i].getName(), new SlotAddress(i)));
		instanceMarker = MarkerType.createChild(Template.templateMarker, name);
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

	@Override
	public Object getValue(final MemoryFact fact, final org.jamocha.dn.memory.SlotAddress slot) {
		return ((org.jamocha.dn.memory.javaimpl.Fact) fact).getValue(slot);
	}

	@Override
	public Object getValue(final org.jamocha.dn.memory.Fact fact,
			final org.jamocha.dn.memory.SlotAddress slot) {
		return fact.getValue(((SlotAddress) slot).index);
	}

	@Override
	public void setValue(final org.jamocha.dn.memory.Fact fact,
			final org.jamocha.dn.memory.SlotAddress slot, final Object value) {
		assert getSlotType(slot).getJavaClass().isInstance(value);
		fact.setValue(((SlotAddress) slot).index, value);
	}

	@Override
	public Fact newFact(final Map<org.jamocha.dn.memory.SlotAddress, Object> values) {
		values.forEach((s, o) -> {
			assert this.slotTypes[((SlotAddress) s).index].getJavaClass().isInstance(o);
		});
		final Stream<Entry<org.jamocha.dn.memory.SlotAddress, Object>> stream =
				values.entrySet().stream();
		assert !stream
				.filter(e -> !this.slotTypes[((SlotAddress) e.getKey()).index].getJavaClass()
						.isInstance(e.getValue())).findAny().isPresent();
		final OptionalInt max = stream.mapToInt(e -> ((SlotAddress) e.getKey()).index).max();
		assert max.isPresent();
		final Object[] args = new Object[max.getAsInt()];
		stream.forEach(e -> args[((SlotAddress) e.getKey()).index] = e.getValue());
		return new Fact(this, args);
	}

	@Override
	public String toString(final Object[] slotValues) {
		final StringBuilder builder = new StringBuilder();
		builder.append('(').append(name);
		this.slotNames
				.entrySet()
				.stream()
				.sorted((a, b) -> Integer.compare(a.getValue().index, b.getValue().index))
				.forEach(
						e -> builder
								.append(" (")
								.append(e.getKey())
								.append(' ')
								.append(this.slotTypes[e.getValue().index].toString(slotValues[e
										.getValue().index])).append(')'));
		return builder.append(')').toString();

	}

	@Override
	public FunctionWithArguments[] applyDefaultsAndOrder(
			final Map<org.jamocha.dn.memory.SlotAddress, FunctionWithArguments> values) {
		// TBD defaults
		final FunctionWithArguments[] ret = new FunctionWithArguments[this.slotTypes.length];
		// as long as no default are implemented, check for complete specification of the values
		assert values.keySet().containsAll(this.slotNames.values());
		assert this.slotNames.values().containsAll(values.keySet());
		values.forEach((s, f) -> ret[((SlotAddress) s).index] = f);
		return ret;
	}
}
