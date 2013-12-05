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

package org.jamocha.filter.impls.predicates;

import org.jamocha.dn.memory.SlotType;
import org.jamocha.filter.Function;
import org.jamocha.filter.Predicate;
import org.jamocha.filter.FunctionDictionary;

/**
 * Implements the functionality of the binary equality {@code =} operator.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @see Predicate
 * @see FunctionDictionary
 * 
 */
public class Equals {
	static {
		FunctionDictionary.addImpl(new Predicate() {
			@Override
			public SlotType[] getParamTypes() {
				return new SlotType[] { SlotType.LONG, SlotType.LONG };
			}

			@Override
			public String toString() {
				return "=";
			}

			@Override
			public Boolean evaluate(final Function<?>... params) {
				return ((Long) params[0].evaluate()).equals((Long) params[1].evaluate());
			}
		});
		FunctionDictionary.addImpl(new Predicate() {
			@Override
			public SlotType[] getParamTypes() {
				return new SlotType[] { SlotType.DOUBLE, SlotType.DOUBLE };
			}

			@Override
			public String toString() {
				return "=";
			}

			@Override
			public Boolean evaluate(final Function<?>... params) {
				return ((Double) params[0].evaluate()).equals((Double) params[1].evaluate());
			}
		});
		FunctionDictionary.addImpl(new Predicate() {
			@Override
			public SlotType[] getParamTypes() {
				return new SlotType[] { SlotType.BOOLEAN, SlotType.BOOLEAN };
			}

			@Override
			public String toString() {
				return "=";
			}

			@Override
			public Boolean evaluate(final Function<?>... params) {
				return ((Boolean) params[0].evaluate()).equals((Boolean) params[1].evaluate());
			}
		});
		FunctionDictionary.addImpl(new Predicate() {
			@Override
			public SlotType[] getParamTypes() {
				return new SlotType[] { SlotType.STRING, SlotType.STRING };
			}

			@Override
			public String toString() {
				return "=";
			}

			@Override
			public Boolean evaluate(final Function<?>... params) {
				return ((String) params[0].evaluate()).equals((String) params[1].evaluate());
			}
		});

	}
}
