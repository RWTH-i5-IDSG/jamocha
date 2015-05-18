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

import org.jamocha.filter.ECFilterList.ECSharedListWrapper;
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

	public static class ExistentialConditionalElement extends ConditionalElement {
		final Scope scope;

		public ExistentialConditionalElement(final Scope scope, final List<ConditionalElement> children) {
			super(children);
			this.scope = scope;
		}

		@Override
		public <V extends ConditionalElementsVisitor> V accept(final V visitor) {
			visitor.visit(this);
			return visitor;
		}
	}

	public static class NegatedExistentialConditionalElement extends ConditionalElement {
		final Scope scope;

		public NegatedExistentialConditionalElement(final Scope scope, final List<ConditionalElement> children) {
			super(children);
			this.scope = scope;
		}

		@Override
		public <V extends ConditionalElementsVisitor> V accept(final V visitor) {
			visitor.visit(this);
			return visitor;
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
	}

	/**
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 */
	public static class TemplatePatternConditionalElement extends ConditionalElement {
		@Getter
		final SingleFactVariable factVariable;

		public TemplatePatternConditionalElement(final SingleFactVariable factVariable) {
			super(new ArrayList<>(0));
			this.factVariable = factVariable;
		}

		@Override
		public <V extends ConditionalElementsVisitor> V accept(final V visitor) {
			visitor.visit(this);
			return visitor;
		}
	}

	/**
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
	 */
	@Getter
	public static class SharedConditionalElementWrapper extends ConditionalElement {

		private ConditionalElement ce;
		private final ECSharedListWrapper wrapper = new ECSharedListWrapper();

		public SharedConditionalElementWrapper(final ConditionalElement ce) {
			super(null);
			this.ce = ce;
		}

		public void replaceConditionalElement(final ConditionalElement ce) {
			this.ce = ce;
		}

		@Override
		public List<ConditionalElement> getChildren() {
			return this.ce.getChildren();
		}

		@Override
		public <V extends ConditionalElementsVisitor> V accept(final V visitor) {
			visitor.visit(this);
			return visitor;
		}
	}
}
