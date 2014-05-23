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

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import org.jamocha.dn.memory.SlotType;
import org.jamocha.dn.memory.Template;

/**
 * Implementation of the {@link org.jamocha.dn.memory.SlotAddress} interface.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @see org.jamocha.dn.memory.SlotAddress
 */
@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
@ToString
public class SlotAddress implements org.jamocha.dn.memory.SlotAddress {
	/**
	 * Index of the slot in the storing {@link Fact fact}.
	 * 
	 * -- GETTER --
	 * 
	 * Returns the index of the slot in the storing {@link Fact fact}.
	 * 
	 * @return the index of the slot in the storing {@link Fact fact}
	 */
	final int index;

	@Override
	public SlotType getSlotType(final Template template) {
		return template.getSlotType(this);
	}

}
