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

import lombok.Getter;
import org.jamocha.function.fwa.ExchangeableLeaf;
import org.jamocha.languages.common.ConditionalElement.*;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public abstract class CETranslator<S extends ExchangeableLeaf<S>, T extends ExchangeableLeaf<T>>
        implements ConditionalElementsVisitor<S> {
    @Getter
    protected ConditionalElement<T> result;

    public abstract CETranslator<S, T> of();

    private List<ConditionalElement<T>> translateChildren(final List<ConditionalElement<S>> children) {
        return children.stream().map(c -> c.accept(of()).result).collect(toList());
    }

    @Override
    public void visit(final AndFunctionConditionalElement<S> ce) {
        this.result = new AndFunctionConditionalElement<>(translateChildren(ce.getChildren()));
    }

    @Override
    public void visit(final ExistentialConditionalElement<S> ce) {
        this.result = new ExistentialConditionalElement<>(ce.getScope(), translateChildren(ce.getChildren()));
    }

    @Override
    public void visit(final NegatedExistentialConditionalElement<S> ce) {
        this.result = new NegatedExistentialConditionalElement<>(ce.getScope(),
                new AndFunctionConditionalElement<>(translateChildren(ce.getChildren())));
    }

    @Override
    public void visit(final NotFunctionConditionalElement<S> ce) {
        this.result = new NotFunctionConditionalElement<>(translateChildren(ce.getChildren()));
    }

    @Override
    public void visit(final OrFunctionConditionalElement<S> ce) {
        this.result = new OrFunctionConditionalElement<>(translateChildren(ce.getChildren()));
    }

    @Override
    public void visit(final InitialFactConditionalElement<S> ce) {
        this.result = new InitialFactConditionalElement<>(ce.getInitialFactVariable());
    }

    @Override
    public void visit(final TemplatePatternConditionalElement<S> ce) {
        this.result = new TemplatePatternConditionalElement<>(ce.getFactVariable());
    }
}
