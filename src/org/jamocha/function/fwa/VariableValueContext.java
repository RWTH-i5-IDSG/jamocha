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
package org.jamocha.function.fwa;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jamocha.dn.memory.MemoryHandlerTerminal.AssertOrRetract;
import org.jamocha.dn.nodes.SlotInFactAddress;
import org.jamocha.languages.common.ScopeStack.Symbol;

import lombok.RequiredArgsConstructor;

/**
 * This class stores the values of local variables.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor
public class VariableValueContext {
	final Map<Symbol, Object> variableValues = new HashMap<>();
	final Map<Symbol, SlotInFactAddress> initializers = new HashMap<>();

	/**
	 * Returns the value of the given variable.
	 * 
	 * @param key
	 *            variable to get the value for
	 * @return the value of the given variable
	 */
	public Object get(final Symbol key) {
		return variableValues.get(key);
	}

	/**
	 * Sets the value of the given variable to be the given value.
	 * 
	 * @param key
	 *            variable to set the value for
	 * @param value
	 *            value to set
	 */
	public void put(final Symbol key, final Object value) {
		variableValues.put(key, value);
	}

	/**
	 * Registers an initializer for a left hand side variable.
	 * 
	 * @param key
	 *            symbol of the variable
	 * @param slotInFactAddress
	 *            address used to initialize the variable
	 */
	public void addInitializer(final Symbol key, final SlotInFactAddress slotInFactAddress) {
		initializers.put(key, slotInFactAddress);
	}

	/**
	 * Initializes the variables of the left hand side using the initializers registered via
	 * {@link #addInitializer(Symbol, SlotInFactAddress)}.
	 * 
	 * @param token
	 *            token containing the fact tuple
	 */
	public void initialize(final AssertOrRetract<?> token) {
		variableValues.clear();
		for (final Entry<Symbol, SlotInFactAddress> entry : initializers.entrySet()) {
			final SlotInFactAddress address = entry.getValue();
			final Symbol key = entry.getKey();
			variableValues.put(key, token.getValue(address.getFactAddress(), address.getSlotAddress()));
		}
	}
}
