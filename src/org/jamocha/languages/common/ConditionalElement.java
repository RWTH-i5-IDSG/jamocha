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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jamocha.function.fwa.ExchangeableLeaf;
import org.jamocha.function.fwa.PredicateWithArguments;
import org.jamocha.languages.common.ScopeStack.Scope;
import org.jamocha.visitor.Visitable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor
public abstract class ConditionalElement<L extends ExchangeableLeaf<L>>
		implements Visitable<ConditionalElementsVisitor<L>> {

	@Getter
	final List<ConditionalElement<L>> children;

	private static abstract class PositiveOrNegativeExistentialConditionalElement<L extends ExchangeableLeaf<L>>
			extends ConditionalElement<L> {
		@Getter
		final Scope scope;

		public PositiveOrNegativeExistentialConditionalElement(final Scope scope,
				final AndFunctionConditionalElement<L> child) {
			this(scope, Lists.newArrayList(ImmutableList.of(child)));
		}

		private PositiveOrNegativeExistentialConditionalElement(final Scope scope,
				final List<ConditionalElement<L>> children) {
			super(children);
			this.scope = scope;
		}

		abstract public PositiveOrNegativeExistentialConditionalElement<L> negate();
	}

	public static class ExistentialConditionalElement<L extends ExchangeableLeaf<L>>
			extends PositiveOrNegativeExistentialConditionalElement<L> {
		public ExistentialConditionalElement(final Scope scope, final AndFunctionConditionalElement<L> child) {
			super(scope, child);
		}

		public ExistentialConditionalElement(final Scope scope, final List<ConditionalElement<L>> children) {
			super(scope, children);
		}

		@Override
		public <V extends ConditionalElementsVisitor<L>> V accept(final V visitor) {
			visitor.visit(this);
			return visitor;
		}

		@Override
		public String toString() {
			return "ExistentialCE " + Objects.toString(children);
		}

		@Override
		public NegatedExistentialConditionalElement<L> negate() {
			return new NegatedExistentialConditionalElement<>(scope, children);
		}
	}

	public static class NegatedExistentialConditionalElement<L extends ExchangeableLeaf<L>>
			extends PositiveOrNegativeExistentialConditionalElement<L> {
		public NegatedExistentialConditionalElement(final Scope scope, final AndFunctionConditionalElement<L> child) {
			super(scope, child.getChildren().size() == 1 &&
					child.getChildren().get(0) instanceof AndFunctionConditionalElement ?
					(AndFunctionConditionalElement<L>) child.getChildren().get(0) : child);
		}

		private NegatedExistentialConditionalElement(final Scope scope, final List<ConditionalElement<L>> children) {
			super(scope, children);
		}

		@Override
		public <V extends ConditionalElementsVisitor<L>> V accept(final V visitor) {
			visitor.visit(this);
			return visitor;
		}

		@Override
		public String toString() {
			return "NegatedExistentialCE " + Objects.toString(children);
		}

		@Override
		public ExistentialConditionalElement<L> negate() {
			return new ExistentialConditionalElement<>(scope, children);
		}
	}

	public static class TestConditionalElement<L extends ExchangeableLeaf<L>> extends ConditionalElement<L> {
		@Getter
		final PredicateWithArguments<L> predicateWithArguments;

		public TestConditionalElement(final PredicateWithArguments<L> predicateWithArguments) {
			super(new ArrayList<>());
			this.predicateWithArguments = predicateWithArguments;
		}

		@Override
		public <V extends ConditionalElementsVisitor<L>> V accept(final V visitor) {
			visitor.visit(this);
			return visitor;
		}

		@Override
		public String toString() {
			return "TestCE (" + Objects.toString(predicateWithArguments) + ")";
		}
	}

	public static class OrFunctionConditionalElement<L extends ExchangeableLeaf<L>> extends ConditionalElement<L> {
		public OrFunctionConditionalElement(final List<ConditionalElement<L>> children) {
			super(children);
		}

		@Override
		public <V extends ConditionalElementsVisitor<L>> V accept(final V visitor) {
			visitor.visit(this);
			return visitor;
		}

		@Override
		public String toString() {
			return "OrCE " + Objects.toString(children);
		}
	}

	public static class AndFunctionConditionalElement<L extends ExchangeableLeaf<L>> extends ConditionalElement<L> {
		public AndFunctionConditionalElement(final List<ConditionalElement<L>> children) {
			super(children);
		}

		@Override
		public <V extends ConditionalElementsVisitor<L>> V accept(final V visitor) {
			visitor.visit(this);
			return visitor;
		}

		@Override
		public String toString() {
			return "AndCE " + Objects.toString(children);
		}
	}

	public static class NotFunctionConditionalElement<L extends ExchangeableLeaf<L>> extends ConditionalElement<L> {
		public NotFunctionConditionalElement(final List<ConditionalElement<L>> children) {
			super(children);
		}

		@Override
		public <V extends ConditionalElementsVisitor<L>> V accept(final V visitor) {
			visitor.visit(this);
			return visitor;
		}

		@Override
		public String toString() {
			return "NotCE " + Objects.toString(children);
		}
	}

	/**
	 * This class is inserted into a {@link RuleCondition} iff there are no variable bindings in the {@link
	 * RuleCondition}.
	 *
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 */
	public static class InitialFactConditionalElement<L extends ExchangeableLeaf<L>> extends ConditionalElement<L> {
		@Getter
		final SingleFactVariable initialFactVariable;

		public InitialFactConditionalElement(final SingleFactVariable initialFactVariable) {
			super(new ArrayList<>(0));
			this.initialFactVariable = initialFactVariable;
		}

		@Override
		public <V extends ConditionalElementsVisitor<L>> V accept(final V visitor) {
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
	public static class TemplatePatternConditionalElement<L extends ExchangeableLeaf<L>> extends
			ConditionalElement<L> {
		@Getter
		final SingleFactVariable factVariable;

		public TemplatePatternConditionalElement(final SingleFactVariable factVariable) {
			super(Collections.emptyList());
			this.factVariable = factVariable;
		}

		@Override
		public <V extends ConditionalElementsVisitor<L>> V accept(final V visitor) {
			visitor.visit(this);
			return visitor;
		}

		@Override
		public String toString() {
			return "TPCE [" + Objects.toString(factVariable) + "]";
		}
	}
}
