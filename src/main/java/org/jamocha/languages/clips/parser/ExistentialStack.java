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
package org.jamocha.languages.clips.parser;

import java.util.Stack;

import lombok.Value;

import org.jamocha.languages.clips.parser.SFPToCETranslator.ExistentialState;
import org.jamocha.languages.clips.parser.SFPToCETranslator.SFPConditionalElementVisitor;
import org.jamocha.languages.common.RuleCondition;
import org.jamocha.languages.common.ScopeStack.Scope;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class ExistentialStack extends RuleCondition {

    @Value
    class ExistentialMarkerElement {
        final SFPConditionalElementVisitor target;
        final ExistentialState state;
    }

    boolean templateCEContained = false;
    final Stack<ExistentialMarkerElement> stack = new Stack<>();

    public ExistentialStack(final Scope scope) {
        super(scope);
    }

    /**
     * To be called when a template conditional element is detected. This information is important in two situations: If
     * the calling visitor is in an assumed negated existential context (@{code not} means "not exists" and "!" in
     * CLIPS), the existence of a template CE inside confirms this assumption. Otherwise, this information is used in
     * {@link org.jamocha.languages.clips.parser.SFPToCETranslator.SFPStartVisitor#visit(
     * org.jamocha.languages.clips.parser.generated.SFPDefruleConstruct, Object)} to determine the situations where it
     * needs to insert an {@link org.jamocha.languages.common.ConditionalElement.InitialFactConditionalElement}, because
     * no non-existential variable bindings are contained.
     */
    void mark() {
        if (stack.isEmpty()) {
            this.templateCEContained = true;
            return;
        }
        stack.peek().target.containsTemplateCE = true;
    }

    void push(final SFPConditionalElementVisitor conditionalElementVisitor, final ExistentialState state) {
        assert state != ExistentialState.NORMAL;
        stack.push(new ExistentialMarkerElement(conditionalElementVisitor, state));
    }

    void pop() {
        stack.pop();
    }

    public static class ScopedExistentialStack implements AutoCloseable {
        final ExistentialStack existentialStack;
        final SFPConditionalElementVisitor target;

        public ScopedExistentialStack(final ExistentialStack existentialStack,
                final SFPConditionalElementVisitor target, final ExistentialState state) {
            this.existentialStack = existentialStack;
            this.target = target;
            this.existentialStack.push(target, state);
        }

        @Override
        public void close() {
            assert this.target == this.existentialStack.stack.peek().target;
            this.existentialStack.pop();
        }
    }
}
