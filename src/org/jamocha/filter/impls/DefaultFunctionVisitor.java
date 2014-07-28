/*
 * Copyright 2002-2014 The Jamocha Team
 * 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.jamocha.org/
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.jamocha.filter.impls;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public interface DefaultFunctionVisitor extends FunctionVisitor {

	<R> void defaultAction(final org.jamocha.filter.Function<R> function);

	// functions
	@Override
	default void visit(final org.jamocha.filter.impls.functions.DividedBy<?> function) {
		defaultAction(function);
	}

	@Override
	default void visit(final org.jamocha.filter.impls.functions.Minus<?> function) {
		defaultAction(function);
	}

	@Override
	default void visit(final org.jamocha.filter.impls.functions.Plus<?> function) {
		defaultAction(function);
	}

	@Override
	default void visit(final org.jamocha.filter.impls.functions.Times<?> function) {
		defaultAction(function);
	}

	@Override
	default void visit(final org.jamocha.filter.impls.functions.TimesInverse<?> function) {
		defaultAction(function);
	}

	@Override
	default void visit(final org.jamocha.filter.impls.functions.TypeConverter<?> function) {
		defaultAction(function);
	}

	@Override
	default void visit(final org.jamocha.filter.impls.functions.UnaryMinus<?> function) {
		defaultAction(function);
	}

	// predicates
	@Override
	default void visit(final org.jamocha.filter.impls.predicates.And predicate) {
		defaultAction(predicate);
	}

	@Override
	default void visit(final org.jamocha.filter.impls.predicates.Equals predicate) {
		defaultAction(predicate);
	}

	@Override
	default void visit(final org.jamocha.filter.impls.predicates.Greater predicate) {
		defaultAction(predicate);
	}

	@Override
	default void visit(final org.jamocha.filter.impls.predicates.GreaterOrEqual predicate) {
		defaultAction(predicate);
	}

	@Override
	default void visit(final org.jamocha.filter.impls.predicates.Less predicate) {
		defaultAction(predicate);
	}

	@Override
	default void visit(final org.jamocha.filter.impls.predicates.LessOrEqual predicate) {
		defaultAction(predicate);
	}

	@Override
	default void visit(final org.jamocha.filter.impls.predicates.Not predicate) {
		defaultAction(predicate);
	}

	@Override
	default void visit(final org.jamocha.filter.impls.predicates.Or predicate) {
		defaultAction(predicate);
	}

	// side effects
	@Override
	default void visit(final org.jamocha.filter.impls.sideeffects.Facts function) {
		defaultAction(function);
	}

	@Override
	default void visit(final org.jamocha.filter.impls.sideeffects.Watch function) {
		defaultAction(function);
	}

	@Override
	default void visit(final org.jamocha.filter.impls.sideeffects.Unwatch function) {
		defaultAction(function);
	}
}
