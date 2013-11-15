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
package org.jamocha.dn.memory.javaimpl;

import java.util.Arrays;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

/**
 * Implementation of a Fact. Stores values as Objects.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 */
@RequiredArgsConstructor
@EqualsAndHashCode
class Fact {
	final Object slotValues[];

	/**
	 * Retrieves the value stored in the slot identified by {@link SlotAddress slot}.
	 * 
	 * @param slot
	 *            {@link SlotAddress slot address} identifying the slot the value is stored in
	 * @return the value stored in the slot identified by {@link SlotAddress slot}
	 */
	public Object getValue(final org.jamocha.dn.memory.SlotAddress slot) {
		return slotValues[((SlotAddress) slot).getIndex()];
	}

	/**
	 * Sets the {@link Object value} in the slot identified by {@link SlotAddress slot}.
	 * 
	 * @param slot
	 *            {@link SlotAddress slot address} identifying the slot the value is to be stored in
	 * @param value
	 *            the value to store in the slot identified by {@link SlotAddress slot}
	 */
	public void setValue(final org.jamocha.dn.memory.SlotAddress slot, final Object value) {
		slotValues[((SlotAddress) slot).getIndex()] = value;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Fact: ");
		if (slotValues.length > 0) {
			sb.append(slotValues[0].toString());
		}
		for (int i = 1; i < slotValues.length; i++) {
			sb.append(", " + slotValues[i].toString());
		}
		return sb.toString();
	}

	public static boolean equalContent(final Fact a, final Fact b) {
		if (a == b)
			return true;
		if (!Arrays.deepEquals(a.slotValues, b.slotValues))
			return false;
		return true;
	}

	public static boolean equalReference(final Fact a, final Fact b) {
		return a == b;
	}

}
