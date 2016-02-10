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

/**
 * This abstract class is the base class for all {@link Function functions} that are predicates. The class therefore
 * specializes the return type of {@link Function#evaluate(Object...)} to {@link Boolean} and implements the {@link
 * Function#getReturnType()}, as all {@link Predicate predicates} return a boolean.
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @see Function
 * @see SlotType
 */
public abstract class Predicate implements Function<Boolean> {

    @Override
    public final SlotType getReturnType() {
        return SlotType.BOOLEAN;
    }

    @Override
    public abstract Boolean evaluate(final Function<?>... params);

}
