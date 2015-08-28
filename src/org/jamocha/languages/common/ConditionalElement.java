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
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.jamocha.function.fwa.PredicateWithArguments;
import org.jamocha.function.fwa.SymbolLeaf;
import org.jamocha.languages.common.ScopeStack.Scope;
import org.jamocha.visitor.Visitable;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor
public abstract class ConditionalElement implements Visitable<ConditionalElementsVisitor> {

	@Getter
	final List<ConditionalElement> children;

	private static abstract class PositiveOrNegativeExistentialConditionalElement extends ConditionalElement {
		@Getter
		final Scope scope;

		public PositiveOrNegativeExistentialConditionalElement(final Scope scope,
				final List<ConditionalElement> children) {
			super(children);
			this.scope = scope;
		}
	}

	public static class ExistentialConditionalElement extends PositiveOrNegativeExistentialConditionalElement {
		public ExistentialConditionalElement(final Scope scope, final List<ConditionalElement> children) {
			super(scope, children);
		}

		@Override
		public <V extends ConditionalElementsVisitor> V accept(final V visitor) {
			visitor.visit(this);
			return visitor;
		}

		@Override
		public String toString() {
			return "ExistentialCE " + Objects.toString(children);
		}
	}

	public static class NegatedExistentialConditionalElement extends PositiveOrNegativeExistentialConditionalElement {
		public NegatedExistentialConditionalElement(final Scope scope, final List<ConditionalElement> children) {
			super(scope, children);
		}

		@Override
		public <V extends ConditionalElementsVisitor> V accept(final V visitor) {
			visitor.visit(this);
			return visitor;
		}

		@Override
		public String toString() {
			return "NegatedExistentialCE " + Objects.toString(children);
		}
	}

	public static class TestConditionalElement extends ConditionalElement {
		@Getter
		final PredicateWithArguments<SymbolLeaf> predicateWithArguments;

		public TestConditionalElement(final PredicateWithArguments<SymbolLeaf> predicateWithArguments) {
			super(new ArrayList<>());
			this.predicateWithArguments = predicateWithArguments;
		}

		@Override
		public <V extends ConditionalElementsVisitor> V accept(final V visitor) {
			visitor.visit(this);
			return visitor;
		}

		@Override
		public String toString() {
			return "TestCE (" + Objects.toString(predicateWithArguments) + ")";
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

		@Override
		public String toString() {
			return "OrCE " + Objects.toString(children);
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

		@Override
		public String toString() {
			return "AndCE " + Objects.toString(children);
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

		@Override
		public String toString() {
			return "NotCE " + Objects.toString(children);
		}
	}

	/**
	 * This class is inserted into a {@link RuleCondition} iff there are no variable bindings in the
	 * {@link RuleCondition}.
	 *
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 */
	public static class InitialFactConditionalElement extends ConditionalElement {
		@Getter
		final SingleFactVariable initialFactVariable;

		public InitialFactConditionalElement(final SingleFactVariable initialFactVariable) {
			super(new ArrayList<>(0));
			this.initialFactVariable = initialFactVariable;
		}

		@Override
		public <V extends ConditionalElementsVisitor> V accept(final V visitor) {
			visitor.visit(this);
			return visitor;
		}

		@Override
		public String toString() {
			return "InitialFactCE";
		}
	}

	/**
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 */
	public static class TemplatePatternConditionalElement extends ConditionalElement {
		@Getter
		final SingleFactVariable factVariable;

		public TemplatePatternConditionalElement(final SingleFactVariable factVariable) {
			super(Collections.emptyList());
			this.factVariable = factVariable;
		}

		@Override
		public <V extends ConditionalElementsVisitor> V accept(final V visitor) {
			visitor.visit(this);
			return visitor;
		}

		@Override
		public String toString() {
			return "TPCE [" + Objects.toString(factVariable) + "]";
		}
	}
}
