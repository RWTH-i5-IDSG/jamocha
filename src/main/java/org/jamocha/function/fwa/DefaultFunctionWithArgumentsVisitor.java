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

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public interface DefaultFunctionWithArgumentsVisitor<L extends ExchangeableLeaf<L>>
        extends FunctionWithArgumentsVisitor<L> {

    void defaultAction(final FunctionWithArguments<L> function);

    @Override
    default void visit(final FunctionWithArgumentsComposite<L> fwa) {
        defaultAction(fwa);
    }

    @Override
    default void visit(final PredicateWithArgumentsComposite<L> fwa) {
        defaultAction(fwa);
    }

    @Override
    default void visit(final ConstantLeaf<L> fwa) {
        defaultAction(fwa);
    }

    @Override
    default void visit(final GlobalVariableLeaf<L> fwa) {
        defaultAction(fwa);
    }

    @Override
    default void visit(final L fwa) {
        defaultAction(fwa);
    }

    @Override
    default void visit(final Bind<L> fwa) {
        defaultAction(fwa);
    }

    @Override
    default void visit(final Assert<L> fwa) {
        defaultAction(fwa);
    }

    @Override
    default void visit(final Assert.TemplateContainer<L> fwa) {
        defaultAction(fwa);
    }

    @Override
    default void visit(final Retract<L> fwa) {
        defaultAction(fwa);
    }

    @Override
    default void visit(final Modify<L> fwa) {
        defaultAction(fwa);
    }

    @Override
    default void visit(final Modify.SlotAndValue<L> fwa) {
        defaultAction(fwa);
    }
}
