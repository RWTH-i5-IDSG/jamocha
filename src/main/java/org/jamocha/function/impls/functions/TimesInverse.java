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
package org.jamocha.function.impls.functions;

import org.jamocha.dn.memory.SlotType;
import org.jamocha.function.Function;
import org.jamocha.function.FunctionDictionary;
import org.jamocha.function.impls.FunctionVisitor;

/**
 * Implements the multiplicative inverse (for all integers a holds <code>Equals(1, Times(a, TimesInverse(a)))</code>).
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @see Function
 * @see FunctionDictionary
 */
public abstract class TimesInverse<R> implements Function<R> {
    public static final String IN_CLIPS = "1/";

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
        FunctionDictionary.addImpl(new TimesInverse<Long>() {
            @Override
            public SlotType[] getParamTypes() {
                return new SlotType[]{SlotType.LONG};
            }

            @Override
            public SlotType getReturnType() {
                return SlotType.LONG;
            }

            @Override
            public Long evaluate(final Function<?>... params) {
                return 1L / (Long) params[0].evaluate();
            }
        });
        FunctionDictionary.addImpl(new TimesInverse<Double>() {
            @Override
            public SlotType[] getParamTypes() {
                return new SlotType[]{SlotType.DOUBLE};
            }

            @Override
            public SlotType getReturnType() {
                return SlotType.DOUBLE;
            }

            @Override
            public Double evaluate(final Function<?>... params) {
                return 1.0 / (Double) params[0].evaluate();
            }
        });
    }
}
