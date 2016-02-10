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
package org.jamocha.function.impls.predicates;

import org.jamocha.dn.memory.SlotType;
import org.jamocha.function.Function;
import org.jamocha.function.Predicate;
import org.jamocha.function.fwa.DefaultFunctionWithArgumentsVisitor;
import org.jamocha.function.fwa.ExchangeableLeaf;
import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.function.fwa.PredicateWithArgumentsComposite;
import org.jamocha.function.impls.FunctionVisitor;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public final class DummyPredicate extends Predicate {

    public static final String IN_CLIPS = "TRUE";
    public static final DummyPredicate INSTANCE = new DummyPredicate();

    public static class IsDummy<L extends ExchangeableLeaf<L>> implements DefaultFunctionWithArgumentsVisitor<L> {
        boolean dummy = false;

        @Override
        public void defaultAction(final FunctionWithArguments<L> function) {
        }

        @Override
        public void visit(final PredicateWithArgumentsComposite<L> predicateWithArgumentsComposite) {
            this.dummy = predicateWithArgumentsComposite.getFunction() == INSTANCE;
        }
    }

    public static <L extends ExchangeableLeaf<L>> boolean isDummy(final FunctionWithArguments<L> fwa) {
        return fwa.accept(new IsDummy<>()).dummy;
    }

    private DummyPredicate() {
    }

    @Override
    public String inClips() {
        return IN_CLIPS;
    }

    @Override
    public <V extends FunctionVisitor> V accept(final V visitor) {
        visitor.visit(this);
        return visitor;
    }

    @Override
    public SlotType[] getParamTypes() {
        return SlotType.EMPTY;
    }

    @Override
    public Boolean evaluate(final Function<?>... params) {
        return Boolean.TRUE;
    }
}
