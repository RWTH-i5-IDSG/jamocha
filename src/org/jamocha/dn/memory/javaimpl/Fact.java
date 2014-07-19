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
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.jamocha.dn.memory.MemoryFact;
import org.jamocha.dn.memory.Template;

/**
 * Implementation of a Fact. Stores values as Objects.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 */
@RequiredArgsConstructor
@EqualsAndHashCode
class Fact implements MemoryFact {
	@Getter(onMethod = @__({ @Override }))
	final Template template;

	final Object slotValues[];

	/**
	 * Retrieves the value stored in the slot identified by {@link SlotAddress slot}.
	 * 
	 * @param slot
	 *            {@link SlotAddress slot address} identifying the slot the value is stored in
	 * @return the value stored in the slot identified by {@link SlotAddress slot}
	 */
	public Object getValue(final org.jamocha.dn.memory.SlotAddress slot) {
		return this.slotValues[((SlotAddress) slot).getIndex()];
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
		this.slotValues[((SlotAddress) slot).getIndex()] = value;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("Fact(");
		if (this.slotValues.length > 0) {
			sb.append(this.slotValues[0].toString());
		}
		for (int i = 1; i < this.slotValues.length; i++) {
			sb.append(", " + this.slotValues[i].toString());
		}
		sb.append(')');
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

	@Override
	public org.jamocha.dn.memory.Fact toMutableFact() {
		return new org.jamocha.dn.memory.Fact(this.template, this.slotValues);
	}
}
