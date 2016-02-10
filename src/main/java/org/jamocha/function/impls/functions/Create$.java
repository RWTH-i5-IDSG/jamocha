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

import static org.jamocha.util.ToArray.toArray;

import java.util.Arrays;

import org.jamocha.dn.memory.SlotType;
import org.jamocha.function.Function;
import org.jamocha.function.FunctionDictionary;
import org.jamocha.function.impls.FunctionVisitor;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@SuppressWarnings("checkstyle:typename")
public abstract class Create$ implements Function<Object> {
    public static final String IN_CLIPS = "create$";

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
        for (final SlotType slotType : SlotType.values()) {
            if (slotType.isArrayType()) continue;
            FunctionDictionary.addImpl(new Create$() {
                final SlotType[] types = SlotType.nCopies(slotType, 2);

                @Override
                public SlotType[] getParamTypes() {
                    return types;
                }

                @Override
                public SlotType getReturnType() {
                    return SlotType.singleToArray(slotType);
                }

                @Override
                public Object evaluate(final Function<?>... params) {
                    final Object[] array = slotType.getArrayCtor().apply(2);
                    array[0] = params[0].evaluate();
                    array[1] = params[1].evaluate();
                    return array;
                }
            });
            FunctionDictionary.addGenerator(IN_CLIPS, slotType, (final SlotType[] paramTypes) -> new Create$() {
                @Override
                public SlotType[] getParamTypes() {
                    return paramTypes;
                }

                @Override
                public SlotType getReturnType() {
                    return SlotType.singleToArray(slotType);
                }

                @Override
                public Object evaluate(final Function<?>... params) {
                    return (toArray(Arrays.stream(params).map(f -> f.evaluate()), slotType.getArrayCtor()));
                }
            });
        }
    }
}
