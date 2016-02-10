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
package test.jamocha.util.builder.fwa;

import org.jamocha.dn.memory.SlotType;
import org.jamocha.function.Function;
import org.jamocha.function.fwa.ConstantLeaf;
import org.jamocha.function.fwa.ExchangeableLeaf;
import org.jamocha.function.fwa.FunctionWithArguments;

import java.util.LinkedList;
import java.util.List;

/**
 * Derived classes have to use themselves or one of their super classes as T.
 *
 * @param <L>
 *         the leaf type
 * @param <R>
 *         return type of function
 * @param <F>
 *         function type
 * @param <T>
 *         current subclass of generic builder
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public abstract class GenericBuilder<L extends ExchangeableLeaf<L>, R, F extends Function<? extends R>, T extends
        GenericBuilder<L, R, F, T>> {

    protected final F function;
    final List<FunctionWithArguments<L>> args = new LinkedList<>();

    protected GenericBuilder(final F function) {
        this.function = function;
    }

    @SuppressWarnings("unchecked")
    public T addConstant(final Object value, final SlotType type) {
        final SlotType[] paramTypes = this.function.getParamTypes();
        if (paramTypes.length == this.args.size()) {
            throw new IllegalArgumentException("All arguments already set!");
        }
        if (paramTypes[this.args.size()] != type) {
            throw new IllegalArgumentException("Wrong argument type!");
        }
        this.args.add(new ConstantLeaf<>(value, type));
        return (T) this;
    }

    public T addLong(final long value) {
        return addConstant(value, SlotType.LONG);
    }

    public T addDouble(final double value) {
        return addConstant(value, SlotType.DOUBLE);
    }

    public T addString(final String value) {
        return addConstant(value, SlotType.STRING);
    }

    public T addBoolean(final boolean value) {
        return addConstant(value, SlotType.BOOLEAN);
    }

    @SuppressWarnings("unchecked")
    public T addFunction(final FunctionWithArguments<L> function) {
        final SlotType[] paramTypes = this.function.getParamTypes();
        if (paramTypes.length == this.args.size()) {
            throw new IllegalArgumentException("All arguments already set!");
        }
        if (paramTypes[this.args.size()] != function.getReturnType()) {
            throw new IllegalArgumentException("Wrong argument type!");
        }
        this.args.add(function);
        return (T) this;
    }

    public abstract FunctionWithArguments<L> build();
}