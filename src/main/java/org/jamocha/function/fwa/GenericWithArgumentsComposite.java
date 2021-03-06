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
import org.jamocha.dn.memory.SlotType;
import org.jamocha.function.Function;
import org.jamocha.function.FunctionDictionary;
import org.jamocha.function.Predicate;
import org.jamocha.function.impls.FunctionVisitor;
import org.jamocha.function.impls.predicates.Not;
import org.jamocha.languages.common.errors.VariableNotDeclaredError;
import org.jamocha.util.ToArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static java.util.stream.Collectors.*;
import static org.jamocha.util.ToArray.toArray;

/**
 * This class is the composite of the {@link FunctionWithArguments} hierarchy. It stores a {@link Function function} and
 * its parameters as an array of {@link FunctionWithArguments}. This way it can recursively represent any combination of
 * {@link Function functions} and their arguments. On evaluation, the given parameters are split into chunks and passed
 * to the corresponding arguments. The returning values are passed to the stored function evaluating the result.
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @see Function
 * @see FunctionWithArguments
 */
@EqualsAndHashCode
@Getter
public abstract class GenericWithArgumentsComposite<R, F extends Function<? extends R>, L extends ExchangeableLeaf<L>>
        implements FunctionWithArguments<L> {

    final F function;
    final FunctionWithArguments<L>[] args;
    @Getter(lazy = true, onMethod = @__(@Override))
    private final SlotType[] paramTypes = calculateParamTypes();
    @Getter(lazy = true, value = AccessLevel.PRIVATE)
    private final int hashPIR = initHashPIR(), hashPII = initHashPII();

    @SafeVarargs
    public GenericWithArgumentsComposite(final F function, final FunctionWithArguments<L>... args) {
        super();
        this.function = function;
        this.args = args;
    }

    private int initHashPII() {
        final int[] hashPII = new int[args.length + 1];
        hashPII[0] = function.hashCode();
        for (int i = 0; i < args.length; i++) {
            final FunctionWithArguments<L> arg = args[i];
            hashPII[i + 1] = arg.hashPositionIsIrrelevant();
        }
        return FunctionWithArguments.hash(hashPII, FunctionWithArguments.POSITION_IS_IRRELEVANT);
    }

    private int initHashPIR() {
        final int[] hashPIR = new int[args.length + 1];
        hashPIR[0] = function.hashCode();
        for (int i = 0; i < args.length; i++) {
            final FunctionWithArguments<L> arg = args[i];
            hashPIR[i + 1] = arg.hashPositionIsRelevant();
        }
        return FunctionWithArguments.hash(hashPIR, FunctionWithArguments.POSITION_IS_RELEVANT);
    }

    static <L extends ExchangeableLeaf<L>> SlotType[] calculateParamTypes(final FunctionWithArguments<L>[] args) {
        final ArrayList<SlotType> types = new ArrayList<>();
        for (final FunctionWithArguments<L> fwa : args) {
            for (final SlotType type : fwa.getParamTypes()) {
                types.add(type);
            }
        }
        return toArray(types, SlotType[]::new);
    }

    private SlotType[] calculateParamTypes() {
        return calculateParamTypes(args);
    }

    @Override
    public SlotType getReturnType() {
        return this.function.getReturnType();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append('\'');
        sb.append(this.function.inClips());
        sb.append('\'');
        sb.append('(');
        sb.append(Arrays.stream(this.args).map(FunctionWithArguments::toString).collect(joining(", ")));
        sb.append(')');
        return sb.toString();
    }

    @RequiredArgsConstructor
    @ToString
    public static class LazyObject<T> implements Function<T> {
        final T value;

        @Override
        public SlotType[] getParamTypes() {
            return SlotType.EMPTY;
        }

        @Override
        public SlotType getReturnType() {
            throw new UnsupportedOperationException("Type checking can not be done during lazy evaluation!");
        }

        @Override
        public String inClips() {
            return "LazyObject";
        }

        @Override
        public T evaluate(final Function<?>... params) {
            return this.value;
        }

        @Override
        public <V extends FunctionVisitor> V accept(final V visitor) {
            throw new UnsupportedOperationException(
                    "You can not visit objects generated by FunctionWithArguments#lazyEvaluate!");
        }
    }

    @Override
    public Function<R> lazyEvaluate(final Function<?>... params) {
        return staticLazyEvaluate(((final Function<?>[] args) -> function.evaluate(args)), function.inClips(), args,
                params);
    }

    static <R, L extends ExchangeableLeaf<L>> Function<R> staticLazyEvaluate(
            final java.util.function.Function<Function<?>[], R> function, final String inClips,
            final FunctionWithArguments<L>[] args, final Function<?>[] params) {
        return new Function<R>() {
            @Override
            public SlotType[] getParamTypes() {
                return SlotType.EMPTY;
            }

            @Override
            public SlotType getReturnType() {
                throw new UnsupportedOperationException("Type checking can not be done during lazy evaluation!");
            }

            @Override
            public String inClips() {
                return "LazyFunction[" + inClips + "]";
            }

            @Override
            public R evaluate(final Function<?>... innerParams) {
                final Function<?>[] evaluatableArgs = new Function<?>[args.length];
                int k = 0;
                for (int i = 0; i < args.length; i++) {
                    final FunctionWithArguments<L> fwa = args[i];
                    final SlotType[] types = fwa.getParamTypes();
                    evaluatableArgs[i] = fwa.lazyEvaluate(Arrays.copyOfRange(params, k, k + types.length));
                    k += types.length;
                }
                return function.apply(evaluatableArgs);
            }

            @Override
            public <V extends FunctionVisitor> V accept(final V visitor) {
                throw new UnsupportedOperationException(
                        "You can not visit objects generated by FunctionWithArguments#lazyEvaluate!");
            }
        };
    }

    @Override
    public R evaluate(final Object... params) {
        return staticEvaluate(this::lazyEvaluate, params);
    }

    static <R, F extends Function<? extends R>> R staticEvaluate(
            final java.util.function.Function<Function<?>[], F> function, final Object[] params) {
        return function.apply(toArray(Arrays.stream(params).map(LazyObject<Object>::new), LazyObject[]::new))
                .evaluate();
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
    public int hash() {
        return this.function.hash(this);
    }

    @SafeVarargs
    public static <L extends ExchangeableLeaf<L>> FunctionWithArguments<L> newInstance(
            final SideEffectFunctionToNetwork sideEffectFunctionToNetwork, final boolean sideEffectsAllowed,
            final String inClips, final FunctionWithArguments<L>... arguments) {
        final SlotType[] argTypes = getArgumentTypes(arguments);
        final Function<?> function = sideEffectsAllowed ? FunctionDictionary
                .lookupWithSideEffects(sideEffectFunctionToNetwork, inClips, argTypes)
                : FunctionDictionary.lookup(inClips, argTypes);
        return SlotType.BOOLEAN == function.getReturnType() ? new PredicateWithArgumentsComposite<L>(
                (Predicate) function, arguments) : new FunctionWithArgumentsComposite<L>(function, arguments);
    }

    public static <L extends ExchangeableLeaf<L>> PredicateWithArguments<L> newPredicateInstance(final String inClips,
            final Collection<FunctionWithArguments<L>> arguments) {
        return newPredicateInstance(inClips,
                ToArray.<FunctionWithArguments<L>>toArray(arguments, FunctionWithArguments[]::new));
    }

    public static <L extends ExchangeableLeaf<L>> PredicateWithArguments<L> newPredicateInstance(
            final boolean isPositive, final String inClips, final Collection<FunctionWithArguments<L>> arguments) {
        return newPredicateInstance(isPositive, inClips, arguments);
    }

    @SafeVarargs
    public static <L extends ExchangeableLeaf<L>> PredicateWithArguments<L> newPredicateInstance(final String inClips,
            final FunctionWithArguments<L>... arguments) {
        return newPredicateInstance(true, inClips, arguments);
    }

    @SafeVarargs
    public static <L extends ExchangeableLeaf<L>> PredicateWithArguments<L> newPredicateInstance(
            final boolean isPositive, final String inClips, final FunctionWithArguments<L>... arguments) {
        final SlotType[] argTypes = getArgumentTypes(arguments);
        final Predicate predicate = FunctionDictionary.lookupPredicate(inClips, argTypes);
        final PredicateWithArgumentsComposite<L> pwac = new PredicateWithArgumentsComposite<L>(predicate, arguments);
        return isPositive ? pwac : new PredicateWithArgumentsComposite<L>(
                FunctionDictionary.lookupPredicate(Not.IN_CLIPS, SlotType.BOOLEAN), pwac);
    }

    @SafeVarargs
    @SuppressWarnings("unchecked")
    private static <L extends ExchangeableLeaf<L>> SlotType[] getArgumentTypes(
            final FunctionWithArguments<L>... arguments) throws VariableNotDeclaredError {
        final SlotType[] argTypes =
                toArray(Arrays.stream(arguments).map(FunctionWithArguments::getReturnType), SlotType[]::new);
        for (int i = 0; i < argTypes.length; i++) {
            final SlotType type = argTypes[i];
            if (null == type) {
                final FunctionWithArguments<SymbolLeaf> fwa = (FunctionWithArguments<SymbolLeaf>) arguments[i];
                if (!(fwa instanceof SymbolLeaf)) {
                    throw new IllegalStateException("null-type leaves only allowed for symbol leaves!");
                }
                throw new VariableNotDeclaredError(((SymbolLeaf) fwa).getSymbol().getImage());
            }
        }
        return argTypes;
    }
}
