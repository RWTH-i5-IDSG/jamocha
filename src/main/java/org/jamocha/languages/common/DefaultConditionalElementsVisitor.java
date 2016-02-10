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
package org.jamocha.languages.common;

import org.jamocha.function.fwa.ExchangeableLeaf;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public interface DefaultConditionalElementsVisitor<L extends ExchangeableLeaf<L>>
        extends ConditionalElementsVisitor<L> {

    void defaultAction(final ConditionalElement<L> ce);

    @Override
    default void visit(final ConditionalElement.AndFunctionConditionalElement<L> ce) {
        defaultAction(ce);
    }

    @Override
    default void visit(final ConditionalElement.ExistentialConditionalElement<L> ce) {
        defaultAction(ce);
    }

    @Override
    default void visit(final ConditionalElement.InitialFactConditionalElement<L> ce) {
        defaultAction(ce);
    }

    @Override
    default void visit(final ConditionalElement.NegatedExistentialConditionalElement<L> ce) {
        defaultAction(ce);
    }

    @Override
    default void visit(final ConditionalElement.NotFunctionConditionalElement<L> ce) {
        defaultAction(ce);
    }

    @Override
    default void visit(final ConditionalElement.OrFunctionConditionalElement<L> ce) {
        defaultAction(ce);
    }

    @Override
    default void visit(final ConditionalElement.TestConditionalElement<L> ce) {
        defaultAction(ce);
    }

    @Override
    default void visit(final ConditionalElement.TemplatePatternConditionalElement<L> ce) {
        defaultAction(ce);
    }
}
