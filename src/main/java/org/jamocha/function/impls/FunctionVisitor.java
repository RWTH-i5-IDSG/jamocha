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
package org.jamocha.function.impls;

import org.jamocha.function.impls.sideeffects.SetCompiler;
import org.jamocha.visitor.Visitor;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public interface FunctionVisitor extends Visitor {
    // functions
    void visit(final org.jamocha.function.impls.functions.Create$ function);

    void visit(final org.jamocha.function.impls.functions.DividedBy<?> function);

    void visit(final org.jamocha.function.impls.functions.Minus<?> function);

    void visit(final org.jamocha.function.impls.functions.Plus<?> function);

    void visit(final org.jamocha.function.impls.functions.Times<?> function);

    void visit(final org.jamocha.function.impls.functions.TimesInverse<?> function);

    void visit(final org.jamocha.function.impls.functions.TypeConverter<?> function);

    void visit(final org.jamocha.function.impls.functions.UnaryMinus<?> function);

    // predicates
    void visit(final org.jamocha.function.impls.predicates.And predicate);

    void visit(final org.jamocha.function.impls.predicates.DummyPredicate predicate);

    void visit(final org.jamocha.function.impls.predicates.Equals predicate);

    void visit(final org.jamocha.function.impls.predicates.Greater predicate);

    void visit(final org.jamocha.function.impls.predicates.GreaterOrEqual predicate);

    void visit(final org.jamocha.function.impls.predicates.Less predicate);

    void visit(final org.jamocha.function.impls.predicates.LessOrEqual predicate);

    void visit(final org.jamocha.function.impls.predicates.Not predicate);

    void visit(final org.jamocha.function.impls.predicates.Or predicate);

    // side effects
    void visit(final org.jamocha.function.impls.sideeffects.Facts function);

    void visit(final org.jamocha.function.impls.sideeffects.Execute function);

    void visit(final org.jamocha.function.impls.sideeffects.Exit function);

    void visit(final org.jamocha.function.impls.sideeffects.ListDefrules function);

    void visit(final org.jamocha.function.impls.sideeffects.ListDeftemplates function);

    void visit(final org.jamocha.function.impls.sideeffects.Ppdeftemplate function);

    void visit(final org.jamocha.function.impls.sideeffects.Printout function);

    void visit(final org.jamocha.function.impls.sideeffects.Clear function);

    void visit(final org.jamocha.function.impls.sideeffects.Reset function);

    void visit(final org.jamocha.function.impls.sideeffects.Run function);

    void visit(final org.jamocha.function.impls.sideeffects.Halt function);

    void visit(final org.jamocha.function.impls.sideeffects.ExportGv function);

    void visit(final org.jamocha.function.impls.sideeffects.Unwatch function);

    void visit(final org.jamocha.function.impls.sideeffects.Watch function);

    void visit(final org.jamocha.function.impls.sideeffects.SetStrategy function);

    void visit(final org.jamocha.function.impls.sideeffects.GetStrategy function);

    void visit(final org.jamocha.function.impls.sideeffects.Load function);

    void visit(final org.jamocha.function.impls.sideeffects.LoadAsterisk function);

    void visit(final org.jamocha.function.impls.sideeffects.LoadFacts function);

    void visit(final org.jamocha.function.impls.sideeffects.Save function);

    void visit(final org.jamocha.function.impls.sideeffects.SaveFacts function);

    void visit(final SetCompiler function);

    // waltz

    void visit(final org.jamocha.function.impls.waltz.Make3Junction function);
}
