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
package org.jamocha.engine.memory.javaimpl;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Fact {
	final Object slotValues[];

	public Object getValue(final SlotAddress slot) {
		return slotValues[slot.getIndex()];
	}

	public void setValue(final SlotAddress slot, final Object value) {
		slotValues[slot.getIndex()] = value;
	}
}
