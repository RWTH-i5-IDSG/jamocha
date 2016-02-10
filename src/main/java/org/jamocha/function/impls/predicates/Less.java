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
package org.jamocha.function.impls.predicates;

import org.jamocha.dn.memory.SlotType;
import org.jamocha.function.Function;
import org.jamocha.function.FunctionDictionary;
import org.jamocha.function.Predicate;
import org.jamocha.function.impls.FunctionVisitor;

/**
 * Implements the functionality of the binary less ({@code <}) operator.
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @see Predicate
 * @see FunctionDictionary
 */
public abstract class Less extends Predicate {
    public static final String IN_CLIPS = "<";

    @Override
    public String inClips() {
        return IN_CLIPS;
    }

    @Override
    public <V extends FunctionVisitor> V accept(final V visitor) {
        visitor.visit(this);
        return visitor;
    }

    static {
        FunctionDictionary.addImpl(new Less() {
            @Override
            public SlotType[] getParamTypes() {
                return new SlotType[]{SlotType.LONG, SlotType.LONG};
            }

            @Override
            public Boolean evaluate(final Function<?>... params) {
                return (Long) params[0].evaluate() < (Long) params[1].evaluate();
            }
        });
        FunctionDictionary.addImpl(new Less() {
            @Override
            public SlotType[] getParamTypes() {
                return new SlotType[]{SlotType.DOUBLE, SlotType.DOUBLE};
            }

            @Override
            public Boolean evaluate(final Function<?>... params) {
                return (Double) params[0].evaluate() < (Double) params[1].evaluate();
            }
        });
    }

}
