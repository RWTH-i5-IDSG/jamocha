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

import lombok.*;
import org.jamocha.dn.SideEffectFunctionToNetwork;
import org.jamocha.dn.memory.Fact;
import org.jamocha.dn.memory.FactIdentifier;
import org.jamocha.dn.memory.SlotType;
import org.jamocha.dn.memory.Template;
import org.jamocha.function.Function;

import java.util.Arrays;

import static org.jamocha.util.ToArray.toArray;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor
public class Assert<L extends ExchangeableLeaf<L>> implements FunctionWithArguments<L> {

    @Value
    @ToString(exclude = {"hashPIR", "hashPII"})
    public static class TemplateContainer<L extends ExchangeableLeaf<L>> implements FunctionWithArguments<L> {
        final Template template;
        final FunctionWithArguments<L>[] args;
        @Getter(lazy = true, onMethod = @__(@Override))
        private final SlotType[] paramTypes = calculateParamTypes();
        @Getter(lazy = true, value = AccessLevel.PRIVATE)
        private final int hashPIR = initHashPIR(), hashPII = initHashPII();

        @SafeVarargs
        public TemplateContainer(final Template template, final FunctionWithArguments<L>... args) {
            this.template = template;
            this.args = args;
        }

        private SlotType[] calculateParamTypes() {
            return Assert.calculateParamTypes(this.args);
        }

        private int initHashPII() {
            final int[] hashPII = new int[this.args.length + 1];
            hashPII[0] = this.template.hashCode();
            for (int i = 0; i < this.args.length; i++) {
                final FunctionWithArguments<L> arg = this.args[i];
                hashPII[i + 1] = arg.hashPositionIsIrrelevant();
            }
            return FunctionWithArguments.hash(hashPII, FunctionWithArguments.POSITION_IS_IRRELEVANT);
        }

        private int initHashPIR() {
            final int[] hashPIR = new int[this.args.length + 1];
            hashPIR[0] = this.template.hashCode();
            for (int i = 0; i < this.args.length; i++) {
                final FunctionWithArguments<L> arg = this.args[i];
                hashPIR[i + 1] = arg.hashPositionIsRelevant();
            }
            return FunctionWithArguments.hash(hashPIR, FunctionWithArguments.POSITION_IS_RELEVANT);
        }

        @Override
        public <T extends FunctionWithArgumentsVisitor<L>> T accept(final T visitor) {
            visitor.visit(this);
            return visitor;
        }

        @Override
        public SlotType getReturnType() {
            // not really true
            return SlotType.FACTADDRESS;
        }

        @Override
        public int hashPositionIsIrrelevant() {
            return getHashPII();
        }

        @Override
        public int hashPositionIsRelevant() {
            return getHashPIR();
        }

        @Override
        public Function<?> lazyEvaluate(final Function<?>... params) {
            return GenericWithArgumentsComposite.staticLazyEvaluate((final Function<?>[] functions) -> this.template
                            .newFact(Arrays.stream(functions).map(f -> f.evaluate()).toArray()),
                    "assert::templateContainer",
                    this.args, params);
        }

        @Override
        public Object evaluate(final Object... params) {
            return GenericWithArgumentsComposite.staticEvaluate(this::lazyEvaluate, params);
        }

        public Fact toFact() {
            return (Fact) evaluate();
        }
    }

    @Getter
    final SideEffectFunctionToNetwork network;
    @Getter
    final TemplateContainer<L>[] args;
    @Getter(lazy = true, onMethod = @__(@Override))
    private final SlotType[] paramTypes = calculateParamTypes();
    @Getter(lazy = true, value = AccessLevel.PRIVATE)
    private final int hashPIR = initHashPIR(), hashPII = initHashPII();

    private SlotType[] calculateParamTypes() {
        return calculateParamTypes(this.args);
    }

    private static <L extends ExchangeableLeaf<L>> SlotType[] calculateParamTypes(
            final FunctionWithArguments<L>[] args) {
        return toArray(Arrays.stream(args).flatMap(fwa -> Arrays.stream(fwa.getParamTypes())), SlotType[]::new);
    }

    private int initHashPII() {
        final int[] hashPII = new int[this.args.length];
        for (int i = 0; i < this.args.length; i++) {
            final FunctionWithArguments<L> arg = this.args[i];
            hashPII[i] = arg.hashPositionIsIrrelevant();
        }
        return FunctionWithArguments.hash(hashPII, FunctionWithArguments.POSITION_IS_IRRELEVANT);
    }

    private int initHashPIR() {
        final int[] hashPIR = new int[this.args.length];
        for (int i = 0; i < this.args.length; i++) {
            final FunctionWithArguments<L> arg = this.args[i];
            hashPIR[i] = arg.hashPositionIsRelevant();
        }
        return FunctionWithArguments.hash(hashPIR, FunctionWithArguments.POSITION_IS_RELEVANT);
    }

    @Override
    public <T extends FunctionWithArgumentsVisitor<L>> T accept(final T visitor) {
        visitor.visit(this);
        return visitor;
    }

    @Override
    public SlotType getReturnType() {
        return SlotType.FACTADDRESS;
    }

    @Override
    public Function<FactIdentifier> lazyEvaluate(final Function<?>... params) {
        return GenericWithArgumentsComposite.staticLazyEvaluate(fs -> {
            final FactIdentifier[] assertFacts =
                    this.network.assertFacts(toArray(Arrays.stream(fs).map(f -> (Fact) f.evaluate()), Fact[]::new));
            return assertFacts[assertFacts.length - 1];
        }, "assert", this.args, params);
    }

    @Override
    public FactIdentifier evaluate(final Object... params) {
        return GenericWithArgumentsComposite.staticEvaluate(this::lazyEvaluate, params);
    }

    @Override
    public int hashPositionIsIrrelevant() {
        return getHashPII();
    }

    @Override
    public int hashPositionIsRelevant() {
        return getHashPIR();
    }
}
