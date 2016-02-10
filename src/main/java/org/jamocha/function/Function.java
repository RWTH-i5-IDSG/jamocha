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
package org.jamocha.function;

import org.jamocha.dn.memory.SlotType;
import org.jamocha.function.fwa.ExchangeableLeaf;
import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.function.impls.FunctionVisitor;
import org.jamocha.visitor.Visitable;

/**
 * Interface for a function representing a part of a rule condition that performs an operation on data.
 *
 * @param <R>
 *         Return type of call to evaluate
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @see SlotType
 * @see Predicate
 * @see FunctionWithArguments
 */
public interface Function<R> extends Visitable<FunctionVisitor> {

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
    String inClips();

    /**
     * Evaluates the function for the given parameters and returns the result
     *
     * @param params
     *         parameters for the function call
     * @return result of the function call
     */
    R evaluate(final Function<?>... params);

    default <L extends ExchangeableLeaf<L>> int hash(final FunctionWithArguments<L> fwa) {
        return fwa.hashPositionIsRelevant();
    }
}
