/*
 * Copyright 2002-2013 The Jamocha Team
 * 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.jamocha.org/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.jamocha.dn.memory.javaimpl;

import lombok.RequiredArgsConstructor;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 * 
 */
@RequiredArgsConstructor
class Fact {
	final Object slotValues[];

	public Object getValue(final org.jamocha.dn.memory.SlotAddress slot) {
		return slotValues[((SlotAddress) slot).getIndex()];
	}

	public void setValue(final org.jamocha.dn.memory.SlotAddress slot,
			final Object value) {
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

}