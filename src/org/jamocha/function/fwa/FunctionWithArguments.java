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
package org.jamocha.function.fwa;

import java.util.function.IntBinaryOperator;

import org.jamocha.dn.memory.Fact;
import org.jamocha.dn.memory.SlotType;
import org.jamocha.function.Function;
import org.jamocha.visitor.Visitable;

/**
 * This class bundles a {@link Function} and its arguments. The Composite Pattern has been used. An
 * argument of a function can be either a constant or a path i.e. a slot of a {@link Fact}.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public interface FunctionWithArguments<L extends ExchangeableLeaf<L>> extends Visitable<FunctionWithArgumentsVisitor<L>> {

	/**
	 * Getter for the list of the corresponding parameter types for the function.
	 * 
	 * @return list of the corresponding parameter types for the function
	 */
	public SlotType[] getParamTypes();

	/**
	 * Getter for the return type of the Function.
	 * 
	 * @return return type of the function
	 */
	public SlotType getReturnType();

	/**
	 * Returns the string representation of the corresponding function in CLIPS.
	 * 
	 * @return name of the corresponding function in CLIPS
	 */
	@Override
	public String toString();

	/**
	 * Stores the parameters and returns a function that can be evaluated without further parameters
	 * 
	 * @param params
	 *            parameters for the function call
	 * @return function to call to get the actual result
	 */
	public Function<?> lazyEvaluate(final Function<?>... params);

	/**
	 * Evaluates the function for the given parameters and returns the result
	 * 
	 * @param params
	 *            parameters for the function call
	 * @return result of the function call
	 */
	public Object evaluate(final Object... params);

	public int hashPositionIsIrrelevant();

	public default int hashPositionIsRelevant() {
		return hashPositionIsIrrelevant();
	}

	static IntBinaryOperator positionIsIrrelevant = (final int i, final int hash) -> {
		return hash;
	};
	static IntBinaryOperator positionIsRelevant = (final int i, final int hash) -> {
		return (i + 1) * hash;
	};

	static int hash(final int[] hashes, final IntBinaryOperator positioner) {
		final int PRIME = 59;
		int result = 1;
		for (int i = 0; i < hashes.length; i++) {
			result = (result * PRIME) + positioner.applyAsInt(i, hashes[i]);
		}
		return result;
	}

	public default int hash() {
		return hashPositionIsRelevant();
	}
}
