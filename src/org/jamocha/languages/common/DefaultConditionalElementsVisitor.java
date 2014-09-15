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
package org.jamocha.languages.common;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 *
 */
public interface DefaultConditionalElementsVisitor extends ConditionalElementsVisitor {

	public void defaultAction(final ConditionalElement ce);

	@Override
	public default void visit(final ConditionalElement.AndFunctionConditionalElement ce) {
		defaultAction(ce);
	}

	@Override
	public default void visit(final ConditionalElement.ExistentialConditionalElement ce) {
		defaultAction(ce);
	}

	@Override
	public default void visit(final ConditionalElement.InitialFactConditionalElement ce) {
		defaultAction(ce);
	}

	@Override
	public default void visit(final ConditionalElement.NegatedExistentialConditionalElement ce) {
		defaultAction(ce);
	}

	@Override
	public default void visit(final ConditionalElement.NotFunctionConditionalElement ce) {
		defaultAction(ce);
	}

	@Override
	public default void visit(final ConditionalElement.OrFunctionConditionalElement ce) {
		defaultAction(ce);
	}

	@Override
	public default void visit(final ConditionalElement.TestConditionalElement ce) {
		defaultAction(ce);
	}

	@Override
	public default void visit(final ConditionalElement.TemplatePatternConditionalElement ce) {
		defaultAction(ce);
	}

	@Override
	public default void visit(final ConditionalElement.SharedConditionalElementWrapper ce) {
		ce.getCe().accept(this);
	}
}
