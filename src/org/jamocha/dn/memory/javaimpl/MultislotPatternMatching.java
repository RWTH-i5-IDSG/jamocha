/*
 * Copyright 2002-2015 The Jamocha Team
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

import org.jamocha.dn.memory.Fact;
import org.jamocha.dn.memory.FactIdentifier;
import org.jamocha.dn.memory.MemoryFact;
import org.jamocha.dn.memory.SlotAddress;
import org.jamocha.dn.memory.Template;

import lombok.AllArgsConstructor;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@AllArgsConstructor
public class MultislotPatternMatching implements MemoryFact {
	final int[] separators;
	final MemoryFact decorated;

	@Override
	public Object getValue(final SlotAddress slotAddress) {
		final MatchingElementAddress addr = (MatchingElementAddress) slotAddress;
		final Object[] values = (Object[]) decorated.getValue(addr.origin);
		final int index = addr.matchingIndex;
		final int from = 0 == index ? 0 : separators[index - 1];
		final int to = index < separators.length ? separators[index] : values.length;
		if (addr.single) {
			assert 1 == to - from;
			return values[from];
		}
		return Arrays.copyOfRange(values, from, to);
	}

	@Override
	public Template getTemplate() {
		return decorated.getTemplate();
	}

	@Override
	public FactIdentifier getFactIdentifier() {
		return decorated.getFactIdentifier();
	}

	@Override
	public Fact toMutableFact() {
		return decorated.toMutableFact();
	}
}
