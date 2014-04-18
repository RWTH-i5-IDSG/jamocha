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

import org.jamocha.visitor.Visitor;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public interface FunctionVisitor extends Visitor {

	// functions
	void visit(final org.jamocha.filter.impls.functions.DividedBy<?> function);

	void visit(final org.jamocha.filter.impls.functions.Minus<?> function);

	void visit(final org.jamocha.filter.impls.functions.Plus<?> function);

	void visit(final org.jamocha.filter.impls.functions.Times<?> function);

	void visit(final org.jamocha.filter.impls.functions.TimesInverse<?> function);

	void visit(final org.jamocha.filter.impls.functions.TypeConverter<?> function);

	void visit(final org.jamocha.filter.impls.functions.UnaryMinus<?> function);

	// predicates
	void visit(final org.jamocha.filter.impls.predicates.And predicate);

	void visit(final org.jamocha.filter.impls.predicates.Equals predicate);

	void visit(final org.jamocha.filter.impls.predicates.Greater predicate);

	void visit(final org.jamocha.filter.impls.predicates.GreaterOrEqual predicate);

	void visit(final org.jamocha.filter.impls.predicates.Less predicate);

	void visit(final org.jamocha.filter.impls.predicates.LessOrEqual predicate);

	void visit(final org.jamocha.filter.impls.predicates.Not predicate);
}
