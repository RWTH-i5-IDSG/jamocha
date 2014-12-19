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

import static org.jamocha.util.ToArray.toArray;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.StreamSupport;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import org.jamocha.dn.memory.FactAddress;
import org.jamocha.dn.memory.FactIdentifier;
import org.jamocha.dn.memory.MemoryFactToFactIdentifier;
import org.jamocha.dn.memory.MemoryHandler;
import org.jamocha.dn.memory.SlotAddress;
import org.jamocha.dn.memory.Template;

/**
 * Base class for java implementations of most handlers. Contains the template of the facts and a
 * list storing the facts handled. Provides the methods required by the MemoryHandler interface.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@EqualsAndHashCode
@AllArgsConstructor
public class MemoryHandlerBase implements MemoryHandler {

	@Getter
	final MemoryFactToFactIdentifier memoryFactToFactIdentifier;
	@Getter
	final Template[] template;
	JamochaArray<Row> validRows;

	/**
	 * @see org.jamocha.dn.memory.MemoryHandler#getValue(FactAddress, SlotAddress, int)
	 */
	@Override
	public Object getValue(final FactAddress address, final SlotAddress slot, final int row) {
		return validRows.get(row).getFactTuple()[((org.jamocha.dn.memory.javaimpl.FactAddress) address).getIndex()]
				.getValue(memoryFactToFactIdentifier, slot);
	}

	/**
	 * @see org.jamocha.dn.memory.MemoryHandler#size()
	 */
	@Override
	public int size() {
		return (int) StreamSupport.stream(validRows.spliterator(), false).filter(Objects::nonNull).count();
	}

	@Override
	public String toString() {
		return "MemoryHandlerBase(template=" + Arrays.deepToString(this.template) + ", facts="
				+ Arrays.deepToString(this.validRows.toArray()) + ")";
	}

	@Override
	public FactIdentifier[] getFactIdentifiers(final int row) {
		return toArray(
				Arrays.stream(validRows.get(row).getFactTuple()).map(memoryFactToFactIdentifier::getFactIdentifier),
				FactIdentifier[]::new);
	}
}
