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
package org.jamocha.function.fwatransformer;

import java.util.function.IntFunction;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.jamocha.function.fwa.Assert;
import org.jamocha.function.fwa.Bind;
import org.jamocha.function.fwa.ConstantLeaf;
import org.jamocha.function.fwa.ExchangeableLeaf;
import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.function.fwa.FunctionWithArgumentsComposite;
import org.jamocha.function.fwa.FunctionWithArgumentsVisitor;
import org.jamocha.function.fwa.GlobalVariableLeaf;
import org.jamocha.function.fwa.Modify;
import org.jamocha.function.fwa.PredicateWithArgumentsComposite;
import org.jamocha.function.fwa.Retract;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor
@Getter
public abstract class FWATranslator<S extends ExchangeableLeaf<S>, T extends ExchangeableLeaf<T>>
        implements FunctionWithArgumentsVisitor<S> {
    protected FunctionWithArguments<T> functionWithArguments;

    public abstract FWATranslator<S, T> of();

    @Override
    public void visit(final FunctionWithArgumentsComposite<S> functionWithArgumentsComposite) {
        this.functionWithArguments = new FunctionWithArgumentsComposite<>(functionWithArgumentsComposite.getFunction(),
                translateArgs(functionWithArgumentsComposite.getArgs()));
    }

    @Override
    public void visit(final PredicateWithArgumentsComposite<S> predicateWithArgumentsComposite) {
        this.functionWithArguments =
                new PredicateWithArgumentsComposite<>(predicateWithArgumentsComposite.getFunction(),
                        translateArgs(predicateWithArgumentsComposite.getArgs()));
    }

    @SuppressWarnings("unchecked")
    @Override
    public void visit(final ConstantLeaf<S> constantLeaf) {
        this.functionWithArguments = (ConstantLeaf<T>) (ConstantLeaf<?>) constantLeaf;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void visit(final GlobalVariableLeaf<S> globalVariableLeaf) {
        this.functionWithArguments = (GlobalVariableLeaf<T>) (GlobalVariableLeaf<?>) globalVariableLeaf;
    }

    @Override
    public void visit(final Bind<S> fwa) {
        this.functionWithArguments = new Bind<>(translateArgs(fwa.getArgs()));
    }

    @SuppressWarnings("unchecked")
    @Override
    public void visit(final Assert<S> fwa) {
        this.functionWithArguments = new Assert<>(fwa.getNetwork(),
                (Assert.TemplateContainer<T>[]) translateArgs(fwa.getArgs(), Assert.TemplateContainer[]::new));
    }

    @SuppressWarnings("unchecked")
    @Override
    public void visit(final Modify<S> fwa) {
        this.functionWithArguments = new Modify<>(fwa.getNetwork(), translateArg(fwa.getTargetFact()),
                (Modify.SlotAndValue<T>[]) translateArgs(fwa.getArgs(), Modify.SlotAndValue[]::new));
    }

    @Override
    public void visit(final Retract<S> fwa) {
        this.functionWithArguments = new Retract<>(fwa.getNetwork(), translateArgs(fwa.getArgs()));
    }

    @Override
    public void visit(final Assert.TemplateContainer<S> fwa) {
        this.functionWithArguments = new Assert.TemplateContainer<>(fwa.getTemplate(), translateArgs(fwa.getArgs()));
    }

    @Override
    public void visit(final Modify.SlotAndValue<S> fwa) {
        this.functionWithArguments = new Modify.SlotAndValue<>(fwa.getSlotName(), translateArg(fwa.getValue()));
    }

    @SuppressWarnings("unchecked")
    private FunctionWithArguments<T>[] translateArgs(final FunctionWithArguments<S>[] originalArgs) {
        return (FunctionWithArguments<T>[]) translateArgs(originalArgs, FunctionWithArguments[]::new);
    }

    private FunctionWithArguments<T> translateArg(final FunctionWithArguments<S> originalArg) {
        return originalArg.accept(of()).getFunctionWithArguments();
    }

    @SuppressWarnings("unchecked")
    private <A extends FunctionWithArguments<?>> A[] translateArgs(final A[] originalArgs,
            final IntFunction<A[]> array) {
        final int numArgs = originalArgs.length;
        final A[] translatedArgs = array.apply(numArgs);
        for (int i = 0; i < numArgs; ++i) {
            final A originalArg = originalArgs[i];
            translatedArgs[i] = (A) translateArg((FunctionWithArguments<S>) originalArg);
        }
        return translatedArgs;
    }
}
