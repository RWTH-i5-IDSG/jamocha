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
import java.util.Objects;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.jamocha.dn.memory.FactIdentifier;
import org.jamocha.dn.memory.MemoryFact;

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

	FactIdentifier factIdentifier;

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("f-").append(null == factIdentifier ? "?" : factIdentifier.getId()).append(": (")
				.append(template.getName());
		for (int i = 0; i < slotValues.length; ++i) {
			final Object value = slotValues[i];
			if (null == value)
				continue;
			sb.append(" (").append(template.getSlotName(i)).append(' ').append(value).append(')');
		}
		sb.append(')');
		return sb.toString();
	}

	@Override
	public void setFactIdentifier(final FactIdentifier factIdentifier) {
		if (null != this.factIdentifier) {
			throw new IllegalArgumentException("A Fact was reassigned a FactIdentifier!");
		}
		this.factIdentifier = factIdentifier;
	}

	@Override
	public FactIdentifier getFactIdentifier() {
		Objects.requireNonNull(factIdentifier, "A fact did not contain a FactIdentifier!");
		return factIdentifier;
	}

	/**
	 * Retrieves the value stored in the slot identified by {@link SlotAddress slot}.
	 * 
	 * @param slot
	 *            {@link SlotAddress slot address} identifying the slot the value is stored in
	 * @return the value stored in the slot identified by {@link SlotAddress slot}
	 */
	public Object getValue(final org.jamocha.dn.memory.SlotAddress slot) {
		if (null == slot)
			return factIdentifier;
		return this.slotValues[((SlotAddress) slot).getIndex()];
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
