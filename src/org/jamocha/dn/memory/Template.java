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

import lombok.Value;

/**
 * A Template consists of slots which in turn have a {@link SlotType slot type} and a name. Facts
 * always comply with some Template.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @see SlotType
 * @see Fact
 */
public interface Template {
	@Value
	public static class Slot {
		final SlotType slotType;
		final String name;
	}

	/**
	 * Returns the description of the template.
	 * 
	 * @return the description of the template
	 */
	public String getDescription();

	/**
	 * Gets the {@link SlotType} corresponding to the position specified by the given index.
	 * 
	 * @param index
	 *            position in the template
	 * @return {@link SlotType} corresponding to the position specified by the given index
	 */
	public SlotType getSlotType(final SlotAddress slotAddress);

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
}
