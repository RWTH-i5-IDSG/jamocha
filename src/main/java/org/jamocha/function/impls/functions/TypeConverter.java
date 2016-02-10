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
 * Type conversion functions.
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public abstract class TypeConverter<R> implements Function<R> {
    @Override
    public <V extends FunctionVisitor> V accept(final V visitor) {
        visitor.visit(this);
        return visitor;
    }

    static {
        FunctionDictionary.addImpl(new TypeConverter<Double>() {
            @Override
            public SlotType[] getParamTypes() {
                return new SlotType[]{SlotType.LONG};
            }

            @Override
            public String inClips() {
                return "LongToDouble";
            }

            @Override
            public SlotType getReturnType() {
                return SlotType.DOUBLE;
            }

            @Override
            public Double evaluate(final Function<?>... params) {
                return ((Long) params[0].evaluate()).doubleValue();
            }
        });
        FunctionDictionary.addImpl(new TypeConverter<Long>() {
            @Override
            public SlotType[] getParamTypes() {
                return new SlotType[]{SlotType.DOUBLE};
            }

            @Override
            public String inClips() {
                return "DoubleToLong";
            }

            @Override
            public SlotType getReturnType() {
                return SlotType.LONG;
            }

            @Override
            public Long evaluate(final Function<?>... params) {
                return ((Double) params[0].evaluate()).longValue();
            }
        });
        FunctionDictionary.addImpl(new TypeConverter<String>() {
            @Override
            public SlotType[] getParamTypes() {
                return new SlotType[]{SlotType.LONG};
            }

            @Override
            public String inClips() {
                return "LongToString";
            }

            @Override
            public SlotType getReturnType() {
                return SlotType.STRING;
            }

            @Override
            public String evaluate(final Function<?>... params) {
                return ((Long) params[0].evaluate()).toString();
            }
        });
        FunctionDictionary.addImpl(new TypeConverter<String>() {
            @Override
            public SlotType[] getParamTypes() {
                return new SlotType[]{SlotType.DOUBLE};
            }

            @Override
            public String inClips() {
                return "DoubleToString";
            }

            @Override
            public SlotType getReturnType() {
                return SlotType.STRING;
            }

            @Override
            public String evaluate(final Function<?>... params) {
                return ((Double) params[0].evaluate()).toString();
            }
        });
        FunctionDictionary.addImpl(new TypeConverter<String>() {
            @Override
            public SlotType[] getParamTypes() {
                return new SlotType[]{SlotType.BOOLEAN};
            }

            @Override
            public String inClips() {
                return "BooleanToString";
            }

            @Override
            public SlotType getReturnType() {
                return SlotType.STRING;
            }

            @Override
            public String evaluate(final Function<?>... params) {
                return ((Boolean) params[0].evaluate()).toString();
            }
        });
    }
}
