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

import static org.jamocha.util.ToArray.toArray;

import java.util.Arrays;
import java.util.Objects;

import lombok.Getter;
import lombok.NonNull;

import org.jamocha.dn.SideEffectFunctionToNetwork;
import org.jamocha.dn.memory.FactIdentifier;
import org.jamocha.dn.memory.SlotType;
import org.jamocha.function.Function;
import org.jamocha.function.impls.FunctionVisitor;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class Retract<L extends ExchangeableLeaf<L>> extends GenericWithArgumentsComposite<Object, Function<?>, L> {
    @Getter
    @NonNull
    final SideEffectFunctionToNetwork network;

    @SafeVarargs
    public Retract(final SideEffectFunctionToNetwork network, final FunctionWithArguments<L>... args) {
        super(new Function<Object>() {
            @Getter(lazy = true, onMethod = @__(@Override))
            private final SlotType[] paramTypes = calculateParamTypes();

            private SlotType[] calculateParamTypes() {
                return GenericWithArgumentsComposite.calculateParamTypes(args);
            }

            @Override
            public <V extends FunctionVisitor> V accept(final V visitor) {
                throw new UnsupportedOperationException("You can not visit the internal retract function!");
            }

            @Override
            public SlotType getReturnType() {
                return SlotType.NIL;
            }

            @Override
            public String inClips() {
                return "retract";
            }

            @Override
            public Object evaluate(final Function<?>... params) {
                network.retractFacts(
                        toArray(Arrays.stream(params).map(Retract::toFactIdentifier), FactIdentifier[]::new));
                return null;
            }

        }, args);
        this.network = Objects.requireNonNull(network);
    }

    static {
        assert FactIdentifier.class == SlotType.FACTADDRESS.getJavaClass();
    }

    static FactIdentifier toFactIdentifier(final Function<?> param) {
        final Object value = param.evaluate();
        if (value instanceof Long) {
            return new FactIdentifier(((Long) value).intValue());
        }
        assert param instanceof LazyObject || param.getReturnType() == SlotType.FACTADDRESS;
        return ((FactIdentifier) value);
    }

    @Override
    public <T extends FunctionWithArgumentsVisitor<L>> T accept(final T visitor) {
        visitor.visit(this);
        return visitor;
    }
}
