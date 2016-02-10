/*
 * Copyright 2002-2016 The Jamocha Team
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.jamocha.org/
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the License.
 */
package org.jamocha.function.fwa;

import java.util.function.IntBinaryOperator;

import org.jamocha.dn.memory.SlotType;
import org.jamocha.function.Function;
import org.jamocha.visitor.Visitable;

/**
 * This class bundles a {@link Function} and its arguments. The Composite Pattern has been used. An argument of a
 * function can be either a constant or a path i.e. a slot of a {@link org.jamocha.dn.memory.Fact}.
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public interface FunctionWithArguments<L extends ExchangeableLeaf<L>>
        extends Visitable<FunctionWithArgumentsVisitor<L>> {

    /**
     * Getter for the list of the corresponding parameter types for the function.
     *
     * @return list of the corresponding parameter types for the function
     */
    SlotType[] getParamTypes();

    /**
     * Getter for the return type of the Function.
     *
     * @return return type of the function
     */
    SlotType getReturnType();

    /**
     * Returns the string representation of the corresponding function in CLIPS.
     *
     * @return name of the corresponding function in CLIPS
     */
    @Override
    String toString();

    /**
     * Stores the parameters and returns a function that can be evaluated without further parameters
     *
     * @param params
     *         parameters for the function call
     * @return function to call to get the actual result
     */
    Function<?> lazyEvaluate(final Function<?>... params);

    /**
     * Evaluates the function for the given parameters and returns the result
     *
     * @param params
     *         parameters for the function call
     * @return result of the function call
     */
    Object evaluate(final Object... params);

    int hashPositionIsIrrelevant();

    default int hashPositionIsRelevant() {
        return hashPositionIsIrrelevant();
    }

    IntBinaryOperator POSITION_IS_IRRELEVANT = (final int i, final int hash) -> hash;
    IntBinaryOperator POSITION_IS_RELEVANT = (final int i, final int hash) -> (i + 1) * hash;

    static int hash(final int[] hashes, final IntBinaryOperator positioner) {
        final int prime = 59;
        int result = 1;
        for (int i = 0; i < hashes.length; i++) {
            result ^= prime * positioner.applyAsInt(i, hashes[i]);
        }
        return result;
    }

    default int hash() {
        return hashPositionIsRelevant();
    }
}
