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
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.NonFinal;

import org.jamocha.dn.memory.FactAddress;
import org.jamocha.dn.memory.MemoryHandler;
import org.jamocha.dn.memory.SlotAddress;
import org.jamocha.dn.memory.Template;

/**
 * Base class for java implementations of most handlers. Contains the template of the facts and a
 * list storing the facts handled. Provides the methods required by the MemoryHandler interface.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@AllArgsConstructor
@EqualsAndHashCode
public class MemoryHandlerBase implements MemoryHandler {
	@Getter
	final Template[] template;
	@NonNull
	@NonFinal
	List<Fact[]> facts;

	/**
	 * @see org.jamocha.dn.memory.MemoryHandler#getValue(FactAddress, SlotAddress, int)
	 */
	@Override
	public Object getValue(final FactAddress address, final SlotAddress slot, int row) {
		return this.facts.get(row)[((org.jamocha.dn.memory.javaimpl.FactAddress) address)
				.getIndex()].getValue(slot);
	}

	/**
	 * @see org.jamocha.dn.memory.MemoryHandler#size()
	 */
	@Override
	public int size() {
		return this.facts.size();
	}

	@Override
	public String toString() {
		return "MemoryHandlerBase(template=" + Arrays.deepToString(this.template) + ", facts="
				+ Arrays.deepToString(this.facts.toArray()) + ")";
	}
}
