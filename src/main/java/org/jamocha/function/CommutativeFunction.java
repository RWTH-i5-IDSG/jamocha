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

import org.jamocha.function.fwa.ExchangeableLeaf;
import org.jamocha.function.fwa.FunctionWithArguments;

/**
 * Implement this interface to have it override the default implementation of {@link
 * Function#hash(FunctionWithArguments)} to make the hashing ignore the order of arguments.
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public interface CommutativeFunction<R> extends Function<R> {
    @Override
    default <L extends ExchangeableLeaf<L>> int hash(final FunctionWithArguments<L> fwa) {
        return fwa.hashPositionIsIrrelevant();
    }
}
