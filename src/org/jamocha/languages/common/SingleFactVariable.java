/*
 * Copyright 2002-2014 The Jamocha Team
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
package org.jamocha.languages.common;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

import org.jamocha.dn.memory.SlotAddress;
import org.jamocha.dn.memory.SlotType;
import org.jamocha.dn.memory.Template;
import org.jamocha.function.fwa.SymbolLeaf;
import org.jamocha.languages.common.ScopeStack.Symbol;

/**
 * Gathers relevant information about a variable.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class SingleFactVariable {
	@NonNull
	final Symbol symbol;
	@NonNull
	final Template template;

	public SingleSlotVariable newSingleSlotVariable(final Symbol symbol, final SlotAddress slot,
			final boolean negated) {
		final SingleSlotVariable instance = new SingleSlotVariable(symbol, slot, negated);
		symbol.addSlotVariable(instance);
		return instance;
	}

	@Getter
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	@EqualsAndHashCode
	public class SingleSlotVariable {
		@NonNull
		final Symbol symbol;
		@NonNull
		final SlotAddress slot;
		// occurrence of symbol was negated by ~
		final boolean negated;

		public SlotType getType() {
			return template.getSlotType(slot);
		}

		public SingleFactVariable getFactVariable() {
			return SingleFactVariable.this;
		}

		public SymbolLeaf toSymbolLeaf() {
			return new SymbolLeaf(getSymbol());
		}
	}
}
