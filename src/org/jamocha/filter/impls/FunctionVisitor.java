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

import org.jamocha.filter.impls.functions.DividedBy;
import org.jamocha.filter.impls.functions.Minus;
import org.jamocha.filter.impls.functions.Plus;
import org.jamocha.filter.impls.functions.Times;
import org.jamocha.filter.impls.functions.TypeConverter;
import org.jamocha.filter.impls.functions.UnaryMinus;
import org.jamocha.filter.impls.predicates.And;
import org.jamocha.filter.impls.predicates.Equals;
import org.jamocha.filter.impls.predicates.Greater;
import org.jamocha.filter.impls.predicates.GreaterOrEqual;
import org.jamocha.filter.impls.predicates.Less;
import org.jamocha.filter.impls.predicates.LessOrEqual;
import org.jamocha.visitor.Visitor;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public interface FunctionVisitor extends Visitor {

	// functions
	void visit(final DividedBy<?> function);

	void visit(final Minus<?> function);

	void visit(final Plus<?> function);

	void visit(final Times<?> function);

	void visit(final TypeConverter<?> function);

	void visit(final UnaryMinus<?> function);

	// predicates
	void visit(final And predicate);

	void visit(final Equals predicate);

	void visit(final Greater predicate);

	void visit(final GreaterOrEqual predicate);

	void visit(final Less predicate);

	void visit(final LessOrEqual predicate);
}
