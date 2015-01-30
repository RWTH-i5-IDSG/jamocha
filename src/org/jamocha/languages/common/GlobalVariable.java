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

import lombok.Getter;
import lombok.Setter;

import org.jamocha.dn.memory.SlotType;
import org.jamocha.languages.common.ScopeStack.Symbol;

/**
 * Class representing a global variable.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Getter
public class GlobalVariable {
	@Setter
	Object value;
	final Symbol symbol;
	final Object defaultValue;
	final SlotType type;

	public GlobalVariable(final Symbol symbol, final Object value, final SlotType type) {
		this.symbol = symbol;
		this.value = value;
		this.defaultValue = value;
		this.type = type;
	}

	public void reset() {
		this.value = this.defaultValue;
	}
}
