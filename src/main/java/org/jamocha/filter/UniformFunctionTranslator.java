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
package org.jamocha.filter;

import static org.jamocha.util.ToArray.toArray;

import java.util.Arrays;

import org.jamocha.dn.memory.SlotType;
import org.jamocha.function.Function;
import org.jamocha.function.FunctionDictionary;
import org.jamocha.function.Predicate;
import org.jamocha.function.fwa.DefaultFunctionWithArgumentsVisitor;
import org.jamocha.function.fwa.ExchangeableLeaf;
import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.function.fwa.FunctionWithArgumentsComposite;
import org.jamocha.function.fwa.GenericWithArgumentsComposite;
import org.jamocha.function.fwa.PredicateWithArguments;
import org.jamocha.function.fwa.PredicateWithArgumentsComposite;
import org.jamocha.function.fwatransformer.FWADeepCopy;
import org.jamocha.function.impls.DefaultFunctionVisitor;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class UniformFunctionTranslator {
    public static <L extends ExchangeableLeaf<L>> PredicateWithArguments<L> translate(
            final PredicateWithArguments<L> predicateWithArguments) {
        final PredicateWithArguments<L> copy =
                (PredicateWithArguments<L>) predicateWithArguments.accept(new FWADeepCopy<>())
                        .getFunctionWithArguments();
        final PredicateWithArguments<L> transformed =
                (PredicateWithArguments<L>) copy.accept(new UpperLevelFWATranslator<>(copy)).result;
        return transformed;
    }

    interface SelectiveFunctionWithArgumentsVisitor<L extends ExchangeableLeaf<L>>
            extends DefaultFunctionWithArgumentsVisitor<L> {
        @Override
        default void defaultAction(final FunctionWithArguments<L> function) {
        }
    }

    interface SelectiveFunctionVisitor extends DefaultFunctionVisitor {
        @Override
        default <R> void defaultAction(final Function<R> function) {
        }
    }

    static <L extends ExchangeableLeaf<L>> boolean translateArgsClone(final FunctionWithArguments<L>[] argsClone) {
        boolean changed = false;
        for (int i = 0; i < argsClone.length; i++) {
            final FunctionWithArguments<L> input = argsClone[i];
            final FunctionWithArguments<L> result = input.accept(new UpperLevelFWATranslator<>(input)).result;
            if (input != result) changed = true;
            argsClone[i] = result;
        }
        return changed;
    }

    @FunctionalInterface
    private interface GwacCtor<R, F extends Function<? extends R>, L extends ExchangeableLeaf<L>, G extends
            GenericWithArgumentsComposite<R, F, L>> {
        G create(final F f, final FunctionWithArguments<L>[] args);
    }

    private static class UpperLevelFWATranslator<L extends ExchangeableLeaf<L>>
            implements SelectiveFunctionWithArgumentsVisitor<L> {
        FunctionWithArguments<L> result;

        UpperLevelFWATranslator(final FunctionWithArguments<L> defaultResult) {
            this.result = defaultResult;
        }

        @Override
        public void visit(final FunctionWithArgumentsComposite<L> functionWithArgumentsComposite) {
            this.<Object, Function<?>, FunctionWithArgumentsComposite<L>>handle(functionWithArgumentsComposite,
                    (final Function<?> f, final FunctionWithArguments<L>[] args) -> new
                            FunctionWithArgumentsComposite<L>(
                            f, args));
        }

        @Override
        public void visit(final PredicateWithArgumentsComposite<L> predicateWithArgumentsComposite) {
            this.<Boolean, Predicate, PredicateWithArgumentsComposite<L>>handle(predicateWithArgumentsComposite,
                    PredicateWithArgumentsComposite<L>::new);
        }

        public <R, F extends Function<? extends R>, G extends GenericWithArgumentsComposite<R, F, L>> void handle(
                final G gwac, final GwacCtor<R, F, L, G> ctor) {
            final FunctionWithArguments<L>[] argsClone = gwac.getArgs().clone();
            final F f = gwac.getFunction();
            final G newGwac;
            if (translateArgsClone(argsClone)) {
                // some arg was replaced, generate new gwac to hold the new arg list
                newGwac = ctor.create(f, argsClone);
            } else {
                newGwac = gwac;
            }
            final FunctionWithArguments<L> translated = f.accept(new UpperLevelFunctionTranslator<>(newGwac)).result;
            if (this.result != translated) {
                this.result = translated.accept(new UpperLevelFWATranslator<>(translated)).result;
            } else {
                this.result = translated;
            }
        }
    }

    private static class UpperLevelFunctionTranslator<L extends ExchangeableLeaf<L>>
            implements SelectiveFunctionVisitor {
        FunctionWithArguments<L> result;
        final GenericWithArgumentsComposite<?, ?, L> upperGwac;

        UpperLevelFunctionTranslator(final GenericWithArgumentsComposite<?, ?, L> upperGwac) {
            this.upperGwac = upperGwac;
            this.result = upperGwac;
        }

        // -(a,b) -> +(a,(-b))
        @SuppressWarnings("unchecked")
        @Override
        public void visit(final org.jamocha.function.impls.functions.Minus<?> function) {
            this.result = new FunctionWithArgumentsComposite<>(FunctionDictionary
                    .lookup(org.jamocha.function.impls.functions.Plus.IN_CLIPS, function.getParamTypes()),
                    (FunctionWithArguments<L>[]) new FunctionWithArguments[]{this.upperGwac.getArgs()[0],
                            new FunctionWithArgumentsComposite<>(FunctionDictionary
                                    .lookup(org.jamocha.function.impls.functions.UnaryMinus.IN_CLIPS,
                                            function.getParamTypes()[1]), this.upperGwac.getArgs()[1])});
        }

        // -(-(a)) -> a
        // -(+(a,b,c,...)) -> +(-a,-b,-c,...)
        @Override
        public void visit(final org.jamocha.function.impls.functions.UnaryMinus<?> function) {
            this.result = this.upperGwac.getArgs()[0]
                    .accept(new LowerLevelFWATranslator<L>(UnaryMinusTranslator<L>::new, this.upperGwac)).result;
        }

        // /(a,b) -> *(a,1/b)
        @SuppressWarnings("unchecked")
        @Override
        public void visit(final org.jamocha.function.impls.functions.DividedBy<?> function) {
            if (function.getReturnType() == SlotType.LONG) return;
            this.result = new FunctionWithArgumentsComposite<>(FunctionDictionary
                    .lookup(org.jamocha.function.impls.functions.Times.IN_CLIPS, function.getParamTypes()),
                    (FunctionWithArguments<L>[]) new FunctionWithArguments[]{this.upperGwac.getArgs()[0],
                            new FunctionWithArgumentsComposite<>(FunctionDictionary
                                    .lookup(org.jamocha.function.impls.functions.TimesInverse.IN_CLIPS,
                                            function.getParamTypes()[1]), this.upperGwac.getArgs()[1])});
        }

        // 1/(1/a) -> a
        @Override
        public void visit(final org.jamocha.function.impls.functions.TimesInverse<?> function) {
            if (function.getReturnType() == SlotType.LONG) return;
            this.result = this.upperGwac.getArgs()[0]
                    .accept(new LowerLevelFWATranslator<L>(TimesInverseTranslator<L>::new, this.upperGwac)).result;

        }

        private void argumentChanginLoopWithIndex(final LowerLevelFunctionWithPositionTranslatorCtor<L> ctor) {
            for (int i = 0; i < this.upperGwac.getArgs().length; i++) {
                final FunctionWithArguments<L> arg = this.upperGwac.getArgs()[i];
                final int j = i;
                this.result = arg.accept(new LowerLevelFWATranslator<>((u, l) -> {
                    return ctor.create(u, l, j);
                }, this.upperGwac)).result;
                if (this.upperGwac != this.result) return;
            }
        }

        // +(+(a,b),c) -> +(a,b,c)
        // +(a,+(b,c)) -> +(a,b,c)
        @Override
        public void visit(final org.jamocha.function.impls.functions.Plus<?> function) {
            argumentChanginLoopWithIndex(PlusTranslator<L>::new);
        }

        // *(*(a,b),c) -> *(a,b,c)
        // *(+(a,b),c) -> +(*(a,c),*(b,c))
        // *(-(a),b) -> -(*(a,b))
        @Override
        public void visit(final org.jamocha.function.impls.functions.Times<?> function) {
            argumentChanginLoopWithIndex(TimesTranslator<L>::new);
        }

        @SuppressWarnings("unchecked")
        private static <L extends ExchangeableLeaf<L>> FunctionWithArguments<L>[] swapTwoArguments(
                final FunctionWithArguments<L>[] args) {
            return (FunctionWithArguments<L>[]) new FunctionWithArguments[]{args[1], args[0]};
        }

        // >(a,b) -> <(b,a)
        @Override
        public void visit(final org.jamocha.function.impls.predicates.Greater predicate) {
            this.result = new PredicateWithArgumentsComposite<>(FunctionDictionary
                    .lookupPredicate(org.jamocha.function.impls.predicates.Less.IN_CLIPS, predicate.getParamTypes()),
                    swapTwoArguments(this.upperGwac.getArgs()));
        }

        // <=(a,b) -> !(<(b,a))
        @Override
        public void visit(final org.jamocha.function.impls.predicates.LessOrEqual predicate) {
            this.result = new PredicateWithArgumentsComposite<>(FunctionDictionary
                    .lookupPredicate(org.jamocha.function.impls.predicates.Not.IN_CLIPS, SlotType.BOOLEAN),
                    new PredicateWithArgumentsComposite<L>(FunctionDictionary
                            .lookupPredicate(org.jamocha.function.impls.predicates.Less.IN_CLIPS,
                                    predicate.getParamTypes()), swapTwoArguments(this.upperGwac.getArgs())));
        }

        // >=(a,b) -> !(<(a,b))
        @Override
        public void visit(final org.jamocha.function.impls.predicates.GreaterOrEqual predicate) {
            this.result = new PredicateWithArgumentsComposite<>(FunctionDictionary
                    .lookupPredicate(org.jamocha.function.impls.predicates.Not.IN_CLIPS, SlotType.BOOLEAN),
                    new PredicateWithArgumentsComposite<L>(FunctionDictionary
                            .lookupPredicate(org.jamocha.function.impls.predicates.Less.IN_CLIPS,
                                    predicate.getParamTypes()), this.upperGwac.getArgs()));
        }
    }

    @FunctionalInterface
    private interface LowerLevelFunctionTranslatorCtor<L extends ExchangeableLeaf<L>> {
        LowerLevelFunctionTranslator<L> create(final GenericWithArgumentsComposite<?, ?, L> upperGWAC,
                final GenericWithArgumentsComposite<?, ?, L> lowerGWAC);
    }

    private static class LowerLevelFWATranslator<L extends ExchangeableLeaf<L>>
            implements SelectiveFunctionWithArgumentsVisitor<L> {
        FunctionWithArguments<L> result;
        final LowerLevelFunctionTranslatorCtor<L> ctor;
        final GenericWithArgumentsComposite<?, ?, L> upperGwac;

        LowerLevelFWATranslator(final LowerLevelFunctionTranslatorCtor<L> ctor,
                final GenericWithArgumentsComposite<?, ?, L> upperGwac) {
            this.ctor = ctor;
            this.upperGwac = upperGwac;
            this.result = upperGwac;
        }

        @Override
        public void visit(final FunctionWithArgumentsComposite<L> functionWithArgumentsComposite) {
            handle(functionWithArgumentsComposite);
        }

        @Override
        public void visit(final PredicateWithArgumentsComposite<L> predicateWithArgumentsComposite) {
            handle(predicateWithArgumentsComposite);
        }

        public void handle(final GenericWithArgumentsComposite<?, ?, L> gwac) {
            this.result = gwac.getFunction().accept(this.ctor.create(this.upperGwac, gwac)).result;
        }
    }

    private abstract static class LowerLevelFunctionTranslator<L extends ExchangeableLeaf<L>>
            implements SelectiveFunctionVisitor {
        final GenericWithArgumentsComposite<?, ?, L> upperGwac;
        final GenericWithArgumentsComposite<?, ?, L> lowerGwac;
        FunctionWithArguments<L> result;

        LowerLevelFunctionTranslator(final GenericWithArgumentsComposite<?, ?, L> upperGwac,
                final GenericWithArgumentsComposite<?, ?, L> lowerGwac) {
            this.upperGwac = upperGwac;
            this.lowerGwac = lowerGwac;
            this.result = upperGwac;
        }

    }

    private static class UnaryMinusTranslator<L extends ExchangeableLeaf<L>> extends LowerLevelFunctionTranslator<L> {
        UnaryMinusTranslator(final GenericWithArgumentsComposite<?, ?, L> upperGWAC,
                final GenericWithArgumentsComposite<?, ?, L> lowerGWAC) {
            super(upperGWAC, lowerGWAC);
        }

        @Override
        public void visit(final org.jamocha.function.impls.functions.UnaryMinus<?> function) {
            this.result = this.lowerGwac.getArgs()[0];
        }

        @SuppressWarnings("unchecked")
        @Override
        public void visit(final org.jamocha.function.impls.functions.Plus<?> function) {
            result = new FunctionWithArgumentsComposite<L>(function, (FunctionWithArguments<L>[]) toArray(
                    Arrays.stream(lowerGwac.getArgs()).map((final FunctionWithArguments<L> fwa) -> {
                        return new FunctionWithArgumentsComposite<L>(upperGwac.getFunction(), fwa);
                    }), FunctionWithArguments[]::new));
        }
    }

    private static class TimesInverseTranslator<L extends ExchangeableLeaf<L>> extends LowerLevelFunctionTranslator<L> {
        TimesInverseTranslator(final GenericWithArgumentsComposite<?, ?, L> upperGWAC,
                final GenericWithArgumentsComposite<?, ?, L> lowerGWAC) {
            super(upperGWAC, lowerGWAC);
        }

        @Override
        public void visit(final org.jamocha.function.impls.functions.TimesInverse<?> function) {
            if (function.getReturnType() == SlotType.LONG) {
                return;
            }
            this.result = this.lowerGwac.getArgs()[0];
        }
    }

    @FunctionalInterface
    private interface LowerLevelFunctionWithPositionTranslatorCtor<L extends ExchangeableLeaf<L>> {
        LowerLevelFunctionTranslator<L> create(final GenericWithArgumentsComposite<?, ?, L> upperGWAC,
                final GenericWithArgumentsComposite<?, ?, L> lowerGWAC, final int position);
    }

    private static class LowerLevelFunctionWithPositionTranslator<L extends ExchangeableLeaf<L>>
            extends LowerLevelFunctionTranslator<L> {
        final int position;

        LowerLevelFunctionWithPositionTranslator(final GenericWithArgumentsComposite<?, ?, L> upperGWAC,
                final GenericWithArgumentsComposite<?, ?, L> lowerGWAC, final int position) {
            super(upperGWAC, lowerGWAC);
            this.position = position;
        }

        protected FunctionWithArguments<L> combineSameFunction(final org.jamocha.function.Function<?> function) {
            // `newArgs` are `upperArgs` with the `lowerArgs` embedded at `position` replacing one
            // arg with two or more
            final FunctionWithArguments<L>[] upperArgs = this.upperGwac.getArgs();
            final FunctionWithArguments<L>[] lowerArgs = this.lowerGwac.getArgs();
            final int length = upperArgs.length + lowerArgs.length - 1;
            final FunctionWithArguments<L>[] newArgs = Arrays.copyOf(upperArgs, length);
            System.arraycopy(lowerArgs, 0, newArgs, position, lowerArgs.length);
            System.arraycopy(upperArgs, 0, newArgs, 0, position);
            System.arraycopy(upperArgs, position + 1, newArgs, position + lowerArgs.length,
                    upperArgs.length - position - 1);
            final SlotType[] paramTypes = new SlotType[length];
            Arrays.fill(paramTypes, function.getParamTypes()[0]);
            return new FunctionWithArgumentsComposite<>(FunctionDictionary.lookup(function.inClips(), paramTypes),
                    newArgs);
        }

    }

    private static class PlusTranslator<L extends ExchangeableLeaf<L>>
            extends LowerLevelFunctionWithPositionTranslator<L> {
        PlusTranslator(final GenericWithArgumentsComposite<?, ?, L> upperGWAC,
                final GenericWithArgumentsComposite<?, ?, L> lowerGWAC, final int position) {
            super(upperGWAC, lowerGWAC, position);
        }

        @Override
        public void visit(final org.jamocha.function.impls.functions.Plus<?> function) {
            this.result = combineSameFunction(function);
        }
    }

    private static class TimesTranslator<L extends ExchangeableLeaf<L>>
            extends LowerLevelFunctionWithPositionTranslator<L> {
        TimesTranslator(final GenericWithArgumentsComposite<?, ?, L> upperGWAC,
                final GenericWithArgumentsComposite<?, ?, L> lowerGWAC, final int position) {
            super(upperGWAC, lowerGWAC, position);
        }

        @Override
        public void visit(final org.jamocha.function.impls.functions.Plus<?> function) {
            // *(+(a,b),c,d,...) -> +(*(a,c,d,...),*(b,c,d,...))
            final FunctionWithArguments<L>[] oldTimesArgs = this.upperGwac.getArgs();
            final FunctionWithArguments<L>[] oldPlusArgs = this.lowerGwac.getArgs();
            @SuppressWarnings("unchecked")
            final FunctionWithArguments<L>[] newPlusArgs =
                    (FunctionWithArguments<L>[]) new FunctionWithArguments[oldPlusArgs.length];
            for (int i = 0; i < oldPlusArgs.length; i++) {
                // for each element in the plus, create a times with the element and cAndMore
                final FunctionWithArguments<L>[] newTimesArgs = Arrays.copyOf(oldTimesArgs, oldTimesArgs.length);
                newTimesArgs[this.position] = oldPlusArgs[i];
                newPlusArgs[i] = new FunctionWithArgumentsComposite<>(this.upperGwac.getFunction(), newTimesArgs);
            }
            this.result = new FunctionWithArgumentsComposite<>(function, newPlusArgs);
        }

        @Override
        public void visit(final org.jamocha.function.impls.functions.Times<?> function) {
            // *(*(a,b),c) -> *(a,b,c)
            this.result = combineSameFunction(function);
        }

        @Override
        public void visit(final org.jamocha.function.impls.functions.UnaryMinus<?> function) {
            // *(-(a),b) -> -(*(a,b))
            // upperGwac - times
            // lowerGwac - unary minus
            // unwrap parameter i from its unary minus and replace it inplace
            final FunctionWithArguments<L>[] newArgs = this.upperGwac.getArgs().clone();
            newArgs[this.position] = this.lowerGwac.getArgs()[0];
            this.result = new FunctionWithArgumentsComposite<>(function,
                    new FunctionWithArgumentsComposite<L>(this.upperGwac.getFunction(), newArgs));
        }
    }
}
