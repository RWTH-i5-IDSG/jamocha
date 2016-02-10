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

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public interface DefaultFunctionVisitor extends FunctionVisitor {

    <R> void defaultAction(final org.jamocha.function.Function<R> function);

    // functions
    @Override
    default void visit(final org.jamocha.function.impls.functions.Create$ function) {
        defaultAction(function);
    }

    @Override
    default void visit(final org.jamocha.function.impls.functions.DividedBy<?> function) {
        defaultAction(function);
    }

    @Override
    default void visit(final org.jamocha.function.impls.functions.Minus<?> function) {
        defaultAction(function);
    }

    @Override
    default void visit(final org.jamocha.function.impls.functions.Plus<?> function) {
        defaultAction(function);
    }

    @Override
    default void visit(final org.jamocha.function.impls.functions.Times<?> function) {
        defaultAction(function);
    }

    @Override
    default void visit(final org.jamocha.function.impls.functions.TimesInverse<?> function) {
        defaultAction(function);
    }

    @Override
    default void visit(final org.jamocha.function.impls.functions.TypeConverter<?> function) {
        defaultAction(function);
    }

    @Override
    default void visit(final org.jamocha.function.impls.functions.UnaryMinus<?> function) {
        defaultAction(function);
    }

    // predicates
    @Override
    default void visit(final org.jamocha.function.impls.predicates.And predicate) {
        defaultAction(predicate);
    }

    @Override
    default void visit(final org.jamocha.function.impls.predicates.DummyPredicate predicate) {
        defaultAction(predicate);
    }

    @Override
    default void visit(final org.jamocha.function.impls.predicates.Equals predicate) {
        defaultAction(predicate);
    }

    @Override
    default void visit(final org.jamocha.function.impls.predicates.Greater predicate) {
        defaultAction(predicate);
    }

    @Override
    default void visit(final org.jamocha.function.impls.predicates.GreaterOrEqual predicate) {
        defaultAction(predicate);
    }

    @Override
    default void visit(final org.jamocha.function.impls.predicates.Less predicate) {
        defaultAction(predicate);
    }

    @Override
    default void visit(final org.jamocha.function.impls.predicates.LessOrEqual predicate) {
        defaultAction(predicate);
    }

    @Override
    default void visit(final org.jamocha.function.impls.predicates.Not predicate) {
        defaultAction(predicate);
    }

    @Override
    default void visit(final org.jamocha.function.impls.predicates.Or predicate) {
        defaultAction(predicate);
    }

    // side effects
    @Override
    default void visit(final org.jamocha.function.impls.sideeffects.Facts function) {
        defaultAction(function);
    }

    @Override
    default void visit(final org.jamocha.function.impls.sideeffects.Execute function) {
        defaultAction(function);
    }

    @Override
    default void visit(final org.jamocha.function.impls.sideeffects.Exit function) {
        defaultAction(function);
    }

    @Override
    default void visit(final org.jamocha.function.impls.sideeffects.ListDefrules function) {
        defaultAction(function);
    }

    @Override
    default void visit(final org.jamocha.function.impls.sideeffects.ListDeftemplates function) {
        defaultAction(function);
    }

    @Override
    default void visit(final org.jamocha.function.impls.sideeffects.Ppdeftemplate function) {
        defaultAction(function);
    }

    @Override
    default void visit(final org.jamocha.function.impls.sideeffects.Printout function) {
        defaultAction(function);
    }

    @Override
    default void visit(final org.jamocha.function.impls.sideeffects.Clear function) {
        defaultAction(function);
    }

    @Override
    default void visit(final org.jamocha.function.impls.sideeffects.Reset function) {
        defaultAction(function);
    }

    @Override
    default void visit(final org.jamocha.function.impls.sideeffects.Run function) {
        defaultAction(function);
    }

    @Override
    default void visit(final org.jamocha.function.impls.sideeffects.Halt function) {
        defaultAction(function);
    }

    @Override
    default void visit(final org.jamocha.function.impls.sideeffects.ExportGv function) {
        defaultAction(function);
    }

    @Override
    default void visit(final org.jamocha.function.impls.sideeffects.Unwatch function) {
        defaultAction(function);
    }

    @Override
    default void visit(final org.jamocha.function.impls.sideeffects.Watch function) {
        defaultAction(function);
    }

    @Override
    default void visit(final org.jamocha.function.impls.sideeffects.SetStrategy function) {
        defaultAction(function);
    }

    @Override
    default void visit(final org.jamocha.function.impls.sideeffects.GetStrategy function) {
        defaultAction(function);
    }

    @Override
    default void visit(final org.jamocha.function.impls.sideeffects.Load function) {
        defaultAction(function);
    }

    @Override
    default void visit(final org.jamocha.function.impls.sideeffects.LoadAsterisk function) {
        defaultAction(function);
    }

    @Override
    default void visit(final org.jamocha.function.impls.sideeffects.LoadFacts function) {
        defaultAction(function);
    }

    @Override
    default void visit(final org.jamocha.function.impls.sideeffects.Save function) {
        defaultAction(function);
    }

    @Override
    default void visit(final org.jamocha.function.impls.sideeffects.SaveFacts function) {
        defaultAction(function);
    }

    @Override
    default void visit(final SetCompiler function) {
        defaultAction(function);
    }

    // waltz
    @Override
    default void visit(final org.jamocha.function.impls.waltz.Make3Junction function) {
        defaultAction(function);
    }
}
