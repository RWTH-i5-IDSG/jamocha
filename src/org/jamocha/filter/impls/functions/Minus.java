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
import org.jamocha.filter.TODODatenkrakeFunktionen;

/**
 * Implements the functionality of the binary minus ({@code -}) operator.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @author Kai Schwarz <kai.schwarz@rwth-aachen.de>
 * @see Function
 * @see TODODatenkrakeFunktionen
 */
public class Minus {
	
	static {
		TODODatenkrakeFunktionen.addImpl(new Function() {
			public SlotType[] getParamTypes() {
				return new SlotType[] { SlotType.LONG, SlotType.LONG };
			}

			public String toString() {
				return "-";
			}

			public SlotType getReturnType() {
				return SlotType.LONG;
			}

			public Long evaluate(final Object... params) {
				return (Long) params[0] - (Long) params[1];
			}

			@Override
			public boolean equalsInFunction(Function function) {
				// FIXME implement equalsInFunction
				return false;
			}
		});
		TODODatenkrakeFunktionen.addImpl(new Function() {
			public SlotType[] getParamTypes() {
				return new SlotType[] { SlotType.DOUBLE, SlotType.DOUBLE };
			}

			public String toString() {
				return "-";
			}

			public SlotType getReturnType() {
				return SlotType.DOUBLE;
			}

			public Double evaluate(final Object... params) {
				return (Double) params[0] - (Double) params[1];
			}

			@Override
			public boolean equalsInFunction(Function function) {
				// FIXME implement equalsInFunction
				return false;
			}
		});
	}
}
