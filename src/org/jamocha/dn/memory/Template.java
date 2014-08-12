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

import java.util.Collection;
import java.util.Map;

import lombok.Value;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jamocha.function.fwa.FunctionWithArguments;

/**
 * A Template consists of slots which in turn have a {@link SlotType slot type} and a name. Facts
 * always comply with some Template.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @see SlotType
 * @see Fact
 */
public interface Template {

	public final static Marker templateMarker = MarkerManager.getMarker("TEMPLATE");

	@Value
	public static class Slot {
		final SlotType slotType;
		final String name;
	}

	/**
	 * Returns the name of the template.
	 * 
	 * @return the name of the template
	 */
	public String getName();

	/**
	 * Returns the description of the template.
	 * 
	 * @return the description of the template
	 */
	public String getDescription();

	/**
	 * Returns the slots in the order given at construction time, possibly deviating from the
	 * internal ordering.
	 * 
	 * @return the slots in the order given at construction time, possibly deviating from the
	 *         internal ordering
	 */
	public Collection<Slot> getSlots();

	/**
	 * Gets the {@link SlotType} corresponding to the position specified by the given index.
	 * 
	 * @param index
	 *            position in the template
	 * @return {@link SlotType} corresponding to the position specified by the given index
	 */
	public SlotType getSlotType(final SlotAddress slotAddress);
	
	/**
	 * Gets the name corresponding to the position specified by the given index.
	 * 
	 * @param index
	 *            position in the template
	 * @return name corresponding to the position specified by the given index
	 */
	public String getSlotName(final SlotAddress slotAddress);

	/**
	 * Returns the {@link SlotAddress} of the first slot matching the name given or null if no slot
	 * name matched.
	 * 
	 * @param name
	 *            string to match against the slot names
	 * @return the {@link SlotAddress} of the first slot matching the name given or null if no slot
	 *         name matched.
	 */
	public SlotAddress getSlotAddress(final String name);

	/**
	 * Ease-of-use method to create facts with type-check for its arguments.
	 * 
	 * @param values
	 *            values to store in the fact instance to create
	 * @return newly created fact instance holding the values specified
	 */
	public Fact newFact(final Object... values);

	/**
	 * Ease-of-use method to create facts with type-check for its arguments.
	 * 
	 * @param values
	 *            values to store in the fact instance to create
	 * @return newly created fact instance holding the values specified
	 */
	public Fact newFact(final Map<SlotAddress, Object> valuesMap);

	/**
	 * 
	 * @param fact
	 * @param slot
	 * @param value
	 */
	public void setValue(final Fact fact, final SlotAddress slot, final Object value);

	/**
	 * 
	 * @param fact
	 * @param slot
	 * @return
	 */
	public Object getValue(final Fact fact, final SlotAddress slot);

	/**
	 * 
	 * @param fact
	 * @param slot
	 * @return
	 */
	public Object getValue(final MemoryFact fact, final SlotAddress slot);

	/**
	 * Return a list of FunctionWithArguments which - after evaluation - can be used to construct an
	 * instance of this template. This includes inserting default values where appropriate and
	 * giving the return list the correct order.
	 * 
	 * @param values
	 *            given values for the corresponding slots
	 * @return list that can be used to construct a template after evaluation
	 */
	public FunctionWithArguments[] applyDefaultsAndOrder(
			final Map<SlotAddress, FunctionWithArguments> values);

	/**
	 * Returns a marker uniquely identifying the template instance as a child of
	 * {@link Template#templateMarker}.
	 * 
	 * @return a marker uniquely identifying the template instance as a child of
	 *         {@link Template#templateMarker}
	 */
	public Marker getInstanceMarker();
}
