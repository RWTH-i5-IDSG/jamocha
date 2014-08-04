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

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.visitor.Visitable;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor
public abstract class ConditionalElement implements Visitable<ConditionalElementsVisitor> {

	@Getter
	final List<ConditionalElement> children;

	public static class ExistentialConditionalElement extends ConditionalElement {
		@Getter
		final List<SingleFactVariable> variables;

		public ExistentialConditionalElement(final List<ConditionalElement> children,
				final List<SingleFactVariable> variables) {
			super(children);
			this.variables = variables;
		}

		@Override
		public <V extends ConditionalElementsVisitor> V accept(final V visitor) {
			visitor.visit(this);
			return visitor;
		}
	}

	public static class NegatedExistentialConditionalElement extends ConditionalElement {
		@Getter
		final List<SingleFactVariable> variables;

		public NegatedExistentialConditionalElement(final List<ConditionalElement> children,
				final List<SingleFactVariable> variables) {
			super(children);
			this.variables = variables;
		}

		@Override
		public <V extends ConditionalElementsVisitor> V accept(final V visitor) {
			visitor.visit(this);
			return visitor;
		}
	}

	public static class TestConditionalElement extends ConditionalElement {
		@Getter
		final FunctionWithArguments fwa;

		public TestConditionalElement(final FunctionWithArguments fwa) {
			super(new ArrayList<>());
			this.fwa = fwa;
		}

		@Override
		public <V extends ConditionalElementsVisitor> V accept(final V visitor) {
			visitor.visit(this);
			return visitor;
		}
	}

	public static class OrFunctionConditionalElement extends ConditionalElement {
		public OrFunctionConditionalElement(final List<ConditionalElement> children) {
			super(children);
		}

		@Override
		public <V extends ConditionalElementsVisitor> V accept(final V visitor) {
			visitor.visit(this);
			return visitor;
		}
	}

	public static class AndFunctionConditionalElement extends ConditionalElement {
		public AndFunctionConditionalElement(final List<ConditionalElement> children) {
			super(children);
		}

		@Override
		public <V extends ConditionalElementsVisitor> V accept(final V visitor) {
			visitor.visit(this);
			return visitor;
		}
	}

	public static class NotFunctionConditionalElement extends ConditionalElement {
		public NotFunctionConditionalElement(final List<ConditionalElement> children) {
			super(children);
		}

		@Override
		public <V extends ConditionalElementsVisitor> V accept(final V visitor) {
			visitor.visit(this);
			return visitor;
		}
	}

	/**
	 * This class is inserted into a {@link RuleCondition} iff there are no variable bindings in the
	 * {@link RuleCondition}.
	 * 
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 */
	public static class InitialFactConditionalElement extends ConditionalElement {
		public InitialFactConditionalElement() {
			super(new ArrayList<>(0));
		}

		@Override
		public <V extends ConditionalElementsVisitor> V accept(final V visitor) {
			visitor.visit(this);
			return visitor;
		}
	}
}
