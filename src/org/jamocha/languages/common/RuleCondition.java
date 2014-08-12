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
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.jamocha.languages.common.ConditionalElement.AndFunctionConditionalElement;
import org.jamocha.languages.common.ConditionalElement.ExistentialConditionalElement;
import org.jamocha.languages.common.ConditionalElement.InitialFactConditionalElement;
import org.jamocha.languages.common.ConditionalElement.NegatedExistentialConditionalElement;
import org.jamocha.languages.common.ConditionalElement.NotFunctionConditionalElement;
import org.jamocha.languages.common.ConditionalElement.OrFunctionConditionalElement;
import org.jamocha.languages.common.ConditionalElement.TestConditionalElement;
import org.jamocha.languages.common.ScopeStack.Symbol;
import org.jamocha.languages.common.SingleFactVariable.SingleSlotVariable;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 */
public class RuleCondition {
	@Getter
	final List<SingleFactVariable> singleFactVariables = new ArrayList<>();
	@Getter
	final List<SingleSlotVariable> singleSlotVariables = new ArrayList<>();
	@Getter
	final List<ConditionalElement> conditionalElements = new ArrayList<>();

	public void addSingleVariable(final SingleFactVariable singleVariable) {
		this.singleFactVariables.add(singleVariable);
	}

	public void addSingleVariable(final SingleSlotVariable singleVariable) {
		this.singleSlotVariables.add(singleVariable);
	}

	public Stream<Symbol> getSymbols() {
		return Stream.concat(singleFactVariables.stream().map(SingleFactVariable::getSymbol),
				singleSlotVariables.stream().map(SingleSlotVariable::getSymbol));
	}

	public void addConditionalElement(final ConditionalElement conditionalElement) {
		this.conditionalElements.add(conditionalElement);
	}

	public void addConditionalElements(final Collection<ConditionalElement> conditionalElement) {
		this.conditionalElements.addAll(conditionalElement);
	}

	public void flatten() {
		// add surrounding (and ), if more than one CE
		if (this.conditionalElements.size() > 1) {
			final List<ConditionalElement> tmplist = new ArrayList<ConditionalElement>();
			tmplist.addAll(this.conditionalElements);
			ConditionalElement ce = new ConditionalElement.AndFunctionConditionalElement(tmplist);
			this.conditionalElements.clear();
			this.conditionalElements.add(ce);
		}

		// move (not )s down to the lowest possible nodes
		NotFunctionConditionalElementSeep seep = new NotFunctionConditionalElementSeep();
		this.conditionalElements.get(0).accept(seep);
		this.conditionalElements.remove(0);
		this.conditionalElements.add(seep.getCe());

		// combine nested ands and ors
		final CombineNested cn = new CombineNested();
		this.conditionalElements.get(0).accept(cn);
	}

	private class CombineNested implements ConditionalElementsVisitor {

		@Override
		public void visit(AndFunctionConditionalElement ce) {
			ce.getChildren().forEach((child) -> {
				child.accept(this);
			});
			List<ConditionalElement> oldChildrenList =
					new ArrayList<ConditionalElement>(ce.getChildren());
			List<ConditionalElement> childrenList = ce.getChildren();
			childrenList.clear();
			for (ConditionalElement conditionalElement : oldChildrenList) {
				final StripAnds sa = new StripAnds();
				conditionalElement.accept(sa);
				childrenList.addAll(sa.getCes());
			}
		}

		@Override
		public void visit(OrFunctionConditionalElement ce) {
			ce.getChildren().forEach((child) -> {
				child.accept(this);
			});
			List<ConditionalElement> oldChildrenList =
					new ArrayList<ConditionalElement>(ce.getChildren());
			List<ConditionalElement> childrenList = ce.getChildren();
			childrenList.clear();
			for (ConditionalElement conditionalElement : oldChildrenList) {
				final StripOrs so = new StripOrs();
				conditionalElement.accept(so);
				childrenList.addAll(so.getCes());
			}
		}

		private class StripAnds implements ConditionalElementsVisitor {

			@Getter
			private List<ConditionalElement> ces;

			private List<ConditionalElement> wrapInList(final ConditionalElement ce) {
				final List<ConditionalElement> ceList = new ArrayList<ConditionalElement>(1);
				ceList.add(ce);
				return ceList;
			}

			@Override
			public void visit(AndFunctionConditionalElement ce) {
				ces = ce.getChildren();
			}

			@Override
			public void visit(ExistentialConditionalElement ce) {
				this.ces = wrapInList(ce);
			}

			@Override
			public void visit(InitialFactConditionalElement ce) {
				this.ces = wrapInList(ce);
			}

			@Override
			public void visit(NegatedExistentialConditionalElement ce) {
				this.ces = wrapInList(ce);
			}

			@Override
			public void visit(NotFunctionConditionalElement ce) {
				this.ces = wrapInList(ce);
			}

			@Override
			public void visit(OrFunctionConditionalElement ce) {
				this.ces = wrapInList(ce);
			}

			@Override
			public void visit(TestConditionalElement ce) {
				this.ces = wrapInList(ce);
			}

		}

		private class StripOrs implements ConditionalElementsVisitor {

			@Getter
			private List<ConditionalElement> ces;

			private List<ConditionalElement> wrapInList(final ConditionalElement ce) {
				final List<ConditionalElement> ceList = new ArrayList<ConditionalElement>(1);
				ceList.add(ce);
				return ceList;
			}

			@Override
			public void visit(AndFunctionConditionalElement ce) {
				this.ces = wrapInList(ce);
			}

			@Override
			public void visit(ExistentialConditionalElement ce) {
				this.ces = wrapInList(ce);
			}

			@Override
			public void visit(InitialFactConditionalElement ce) {
				this.ces = wrapInList(ce);
			}

			@Override
			public void visit(NegatedExistentialConditionalElement ce) {
				this.ces = wrapInList(ce);
			}

			@Override
			public void visit(NotFunctionConditionalElement ce) {
				this.ces = wrapInList(ce);
			}

			@Override
			public void visit(OrFunctionConditionalElement ce) {
				this.ces = ce.getChildren();
			}

			@Override
			public void visit(TestConditionalElement ce) {
				this.ces = wrapInList(ce);
			}

		}

		@Override
		public void visit(ExistentialConditionalElement ce) {
		}

		@Override
		public void visit(InitialFactConditionalElement ce) {
		}

		@Override
		public void visit(NegatedExistentialConditionalElement ce) {
		}

		@Override
		public void visit(NotFunctionConditionalElement ce) {
		}

		@Override
		public void visit(TestConditionalElement ce) {
		}

	}

	@RequiredArgsConstructor
	private class NotFunctionConditionalElementSeep implements ConditionalElementsVisitor {

		final private boolean negated;
		@Getter
		private ConditionalElement ce = null;

		public NotFunctionConditionalElementSeep() {
			negated = false;
		}

		private void processChilds(ConditionalElement ce, boolean nextNegated) {
			ce.getChildren().replaceAll(
					(ConditionalElement x) -> {
						NotFunctionConditionalElementSeep seep =
								new NotFunctionConditionalElementSeep(nextNegated);
						x.accept(seep);
						return seep.ce;
					});
		}

		private void visitLeaf(ConditionalElement ce) {
			if (negated) {
				List<ConditionalElement> list = new ArrayList<>();
				list.add(ce);
				this.ce = new NotFunctionConditionalElement(list);
			} else {
				this.ce = ce;
			}
		}

		@Override
		public void visit(final AndFunctionConditionalElement ce) {
			if (negated) {
				this.ce = new OrFunctionConditionalElement(ce.getChildren());
			} else {
				this.ce = ce;
			}
			processChilds(this.ce, negated);
		}

		@Override
		public void visit(final ExistentialConditionalElement ce) {
			throw new Error(
					"Found existential conditional element inside not function conditional element.");
		}

		@Override
		public void visit(final InitialFactConditionalElement ce) {
			visitLeaf(ce);
		}

		@Override
		public void visit(final NegatedExistentialConditionalElement ce) {
			throw new Error(
					"Found negated existential conditional element inside not function conditional element.");
		}

		@Override
		public void visit(final NotFunctionConditionalElement ce) {
			if (ce.getChildren().size() > 1) {
				this.ce = new AndFunctionConditionalElement(ce.getChildren());
			} else {
				this.ce = ce.getChildren().get(0);
			}
			NotFunctionConditionalElementSeep seep =
					new NotFunctionConditionalElementSeep(!negated);
			this.ce.accept(seep);
			this.ce = seep.ce;
		}

		@Override
		public void visit(final OrFunctionConditionalElement ce) {
			if (negated) {
				this.ce = new AndFunctionConditionalElement(ce.getChildren());
			} else {
				this.ce = ce;
			}
			processChilds(this.ce, negated);
		}

		@Override
		public void visit(final TestConditionalElement ce) {
			visitLeaf(ce);
		}

	}
}
