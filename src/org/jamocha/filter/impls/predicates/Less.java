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
import org.jamocha.filter.FunctionDictionary;
import org.jamocha.filter.Predicate;

/**
 * Implements the functionality of the binary less ({@code <}) operator.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @see Predicate
 * @see FunctionDictionary
 */
public abstract class Less extends Predicate {
	static String inClips = "<";

	@Override
	public String inClips() {
		return inClips;
	}

	static {
		FunctionDictionary.addImpl(new Less() {
			@Override
			public SlotType[] getParamTypes() {
				return new SlotType[] { SlotType.LONG, SlotType.LONG };
			}

			@Override
			public Boolean evaluate(final Function<?>... params) {
				return (Long) params[0].evaluate() < (Long) params[1].evaluate();
			}
		});
		FunctionDictionary.addImpl(new Less() {
			@Override
			public SlotType[] getParamTypes() {
				return new SlotType[] { SlotType.DOUBLE, SlotType.DOUBLE };
			}

			@Override
			public Boolean evaluate(final Function<?>... params) {
				return (Double) params[0].evaluate() < (Double) params[1].evaluate();
			}
		});
	}

}
