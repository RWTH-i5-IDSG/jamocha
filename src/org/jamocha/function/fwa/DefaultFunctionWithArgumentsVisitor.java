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
package org.jamocha.function.fwa;

import org.jamocha.function.fwa.PathLeaf.ParameterLeaf;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public interface DefaultFunctionWithArgumentsVisitor extends FunctionWithArgumentsVisitor {

	void defaultAction(final FunctionWithArguments function);

	@Override
	public default void visit(final FunctionWithArgumentsComposite fwa) {
		defaultAction(fwa);
	}

	@Override
	public default void visit(final PredicateWithArgumentsComposite fwa) {
		defaultAction(fwa);
	}

	@Override
	public default void visit(final ConstantLeaf fwa) {
		defaultAction(fwa);
	}

	@Override
	public default void visit(final ParameterLeaf fwa) {
		defaultAction(fwa);
	}

	@Override
	public default void visit(final PathLeaf fwa) {
		defaultAction(fwa);
	}

	@Override
	public default void visit(final Assert fwa) {
		defaultAction(fwa);
	}

	@Override
	public default void visit(final Assert.TemplateContainer fwa) {
		defaultAction(fwa);
	}

	@Override
	public default void visit(final Retract fwa) {
		defaultAction(fwa);
	}

	@Override
	public default void visit(final Modify fwa) {
		defaultAction(fwa);
	}

	@Override
	public default void visit(final Modify.SlotAndValue fwa) {
		defaultAction(fwa);
	}

	@Override
	public default void visit(final SymbolLeaf fwa) {
		defaultAction(fwa);
	}
}
