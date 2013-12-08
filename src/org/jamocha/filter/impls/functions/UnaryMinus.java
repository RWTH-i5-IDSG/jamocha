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
package org.jamocha.filter.impls.functions;

import org.jamocha.dn.memory.SlotType;
import org.jamocha.filter.Function;
import org.jamocha.filter.FunctionDictionary;

/**
 * Implements the functionality of the unary minus ({@code -}) operator.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @see Function
 * @see FunctionDictionary
 */
public class UnaryMinus {

	static {
		FunctionDictionary.addImpl(new Function<Long>() {
			public SlotType[] getParamTypes() {
				return new SlotType[] { SlotType.LONG };
			}

			public String toString() {
				return "-";
			}

			public SlotType getReturnType() {
				return SlotType.LONG;
			}

			public Long evaluate(final Function<?>... params) {
				return -(Long) params[0].evaluate();
			}
		});
		FunctionDictionary.addImpl(new Function<Double>() {
			public SlotType[] getParamTypes() {
				return new SlotType[] { SlotType.DOUBLE };
			}

			public String toString() {
				return "-";
			}

			public SlotType getReturnType() {
				return SlotType.DOUBLE;
			}

			public Double evaluate(final Function<?>... params) {
				return -(Double) params[0].evaluate();
			}
		});
	}
}
