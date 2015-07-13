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

import org.jamocha.visitor.Visitor;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 *
 */
public interface ConditionalElementsVisitor extends Visitor {
	public void visit(final ConditionalElement.AndFunctionConditionalElement ce);

	public void visit(final ConditionalElement.ExistentialConditionalElement ce);

	public void visit(final ConditionalElement.InitialFactConditionalElement ce);

	public void visit(final ConditionalElement.NegatedExistentialConditionalElement ce);

	public void visit(final ConditionalElement.NotFunctionConditionalElement ce);

	public void visit(final ConditionalElement.OrFunctionConditionalElement ce);

	public void visit(final ConditionalElement.TestConditionalElement ce);

	public void visit(final ConditionalElement.TemplatePatternConditionalElement ce);
}
