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

import java.util.ArrayList;
import java.util.Arrays;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import org.jamocha.dn.SideEffectFunctionToNetwork;
import org.jamocha.dn.memory.Fact;
import org.jamocha.dn.memory.FactIdentifier;
import org.jamocha.dn.memory.SlotAddress;
import org.jamocha.dn.memory.SlotType;
import org.jamocha.dn.memory.Template;
import org.jamocha.function.Function;
import org.jamocha.languages.common.errors.NoSlotForThatNameError;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor
public class Modify<L extends ExchangeableLeaf<L>> implements FunctionWithArguments<L> {
    @Value
    public static class SlotAndValue<L extends ExchangeableLeaf<L>> implements FunctionWithArguments<L> {
        final String slotName;
        final FunctionWithArguments<L> value;
        @Getter(lazy = true, value = AccessLevel.PRIVATE)
        private final int hashPIR = initHashPIR(), hashPII = initHashPII();

        private int initHashPII() {
            final int[] hashPII = new int[2];
            hashPII[0] = this.slotName.hashCode();
            hashPII[1] = this.value.hashPositionIsIrrelevant();
            return FunctionWithArguments.hash(hashPII, FunctionWithArguments.POSITION_IS_IRRELEVANT);
        }

        private int initHashPIR() {
            final int[] hashPIR = new int[2];
            hashPIR[0] = this.slotName.hashCode();
            hashPIR[1] = this.value.hashPositionIsRelevant();
            return FunctionWithArguments.hash(hashPIR, FunctionWithArguments.POSITION_IS_RELEVANT);
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
        public <V extends FunctionWithArgumentsVisitor<L>> V accept(final V visitor) {
            visitor.visit(this);
            return visitor;
        }

        @Override
        public SlotType[] getParamTypes() {
            return this.value.getParamTypes();
        }

        @Override
        public SlotType getReturnType() {
            return this.value.getReturnType();
        }

        @Override
        public Function<?> lazyEvaluate(final Function<?>... params) {
            return this.value.lazyEvaluate(params);
        }

        @Override
        public Object evaluate(final Object... params) {
            return this.value.evaluate(params);
        }
    }

    @Getter
    @NonNull
    final SideEffectFunctionToNetwork network;
    @Getter
    @NonNull
    final FunctionWithArguments<L> targetFact;
    @Getter
    final SlotAndValue<L>[] args;
    @Getter(lazy = true, onMethod = @__(@Override))
    private final SlotType[] paramTypes = calculateParamTypes();
    @Getter(lazy = true, value = AccessLevel.PRIVATE)
    private final int hashPIR = initHashPIR(), hashPII = initHashPII();

    private SlotType[] calculateParamTypes() {
        return calculateParamTypes(this.args);
    }

    private static <L extends ExchangeableLeaf<L>> SlotType[] calculateParamTypes(final SlotAndValue<L>[] args) {
        final ArrayList<SlotType> types =
                Arrays.stream(args).map(FunctionWithArguments::getReturnType).map(Arrays::asList)
                        .collect(ArrayList::new, ArrayList::addAll, ArrayList::addAll);
        return toArray(types, SlotType[]::new);
    }

    private int initHashPII() {
        final int[] hashPII = new int[this.args.length + 1];
        hashPII[0] = this.targetFact.hashPositionIsIrrelevant();
        for (int i = 0; i < this.args.length; i++) {
            final SlotAndValue<L> arg = this.args[i];
            hashPII[i + 1] = arg.hashPositionIsIrrelevant();
        }
        return FunctionWithArguments.hash(hashPII, FunctionWithArguments.POSITION_IS_IRRELEVANT);
    }

    private int initHashPIR() {
        final int[] hashPIR = new int[this.args.length + 1];
        hashPIR[0] = this.targetFact.hashPositionIsRelevant();
        for (int i = 0; i < this.args.length; i++) {
            final SlotAndValue<L> arg = this.args[i];
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
        return SlotType.FACTADDRESS;
    }

    @Override
    public Function<FactIdentifier> lazyEvaluate(final Function<?>... params) {
        @SuppressWarnings("unchecked")
        final FunctionWithArguments<L>[] array = new FunctionWithArguments[this.args.length + 1];
        array[0] = this.targetFact;
        System.arraycopy(this.args, 0, array, 1, this.args.length);
        return new GenericWithArgumentsComposite.LazyObject<>(GenericWithArgumentsComposite.staticLazyEvaluate(fs -> {
            final FactIdentifier factIdentifier = Retract.toFactIdentifier(fs[0]);
            final Fact fact = this.network.getMemoryFact(factIdentifier).toMutableFact();
            this.network.retractFacts(factIdentifier);
            final Template template = fact.getTemplate();
            for (int i = 0; i < this.args.length; ++i) {
                final String slotName = this.args[i].getSlotName();
                final SlotAddress slotAddress = template.getSlotAddress(slotName);
                if (null == slotAddress) {
                    throw new NoSlotForThatNameError(slotName);
                }
                template.setValue(fact, slotAddress, fs[i + 1].evaluate());
            }
            return this.network.assertFacts(fact)[0];
        }, "assert", array, params).evaluate());
    }

    @Override
    public Object evaluate(final Object... params) {
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
