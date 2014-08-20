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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.jamocha.languages.common.ConditionalElement.AndFunctionConditionalElement;
import org.jamocha.languages.common.ConditionalElement.ExistentialConditionalElement;
import org.jamocha.languages.common.ConditionalElement.InitialFactConditionalElement;
import org.jamocha.languages.common.ConditionalElement.NegatedExistentialConditionalElement;
import org.jamocha.languages.common.ConditionalElement.NotFunctionConditionalElement;
import org.jamocha.languages.common.ConditionalElement.OrFunctionConditionalElement;
import org.jamocha.languages.common.ConditionalElement.SharedConditionalElementWrapper;
import org.jamocha.languages.common.ConditionalElement.TemplatePatternConditionalElement;
import org.jamocha.languages.common.ConditionalElement.TestConditionalElement;

/**
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 *
 */
public class RuleConditionProcessor {

	private static ConditionalElement combine(final List<ConditionalElement> conditionalElements,
			final Function<List<ConditionalElement>, ConditionalElement> combiner) {
		if (conditionalElements.size() > 1) {
			return combiner.apply(conditionalElements);
		}
		return conditionalElements.get(0);
	}

	public static ConditionalElement combineViaAnd(
			final List<ConditionalElement> conditionalElements) {
		return combine(conditionalElements, AndFunctionConditionalElement::new);
	}

	public static ConditionalElement combineViaOr(final List<ConditionalElement> conditionalElements) {
		return combine(conditionalElements, OrFunctionConditionalElement::new);
	}

	public static void flatten(final List<ConditionalElement> conditionalElements) {
		// add surrounding (and ), if more than one CE
		final ConditionalElement ce =
				combineViaAnd(new ArrayList<ConditionalElement>(conditionalElements));
		conditionalElements.clear();
		conditionalElements.add(flatten(ce));
	}

	public static ConditionalElement flatten(final ConditionalElement conditionalElement) {
		ConditionalElement ce = conditionalElement;

		// move (not )s down to the lowest possible nodes
		ce = RuleConditionProcessor.moveNots(ce);

		// combine nested ands and ors
		RuleConditionProcessor.combineNested(ce);

		// expand ors
		ce = RuleConditionProcessor.expandOrs(ce);

		return ce;
	}

	public static ConditionalElement moveNots(final ConditionalElement ce) {
		return ce.accept(new NotFunctionConditionalElementSeep()).getCe();
	}

	public static void combineNested(final ConditionalElement ce) {
		ce.accept(new CombineNested());
	}

	public static ConditionalElement expandOrs(final ConditionalElement ce) {
		return combineViaOr(ce.accept(new ExpandOrs()).ces);
	}

	private static class ExpandOrs implements DefaultConditionalElementsVisitor {

		@Getter
		private List<ConditionalElement> ces = new ArrayList<ConditionalElement>();

		@Override
		public void visit(final AndFunctionConditionalElement ce) {
			final List<List<ConditionalElement>> tmpList =
					ce.children.stream().map((el) -> el.accept(new ExpandOrs()).getCes())
							.collect(Collectors.toList());
			final int nez = (int) tmpList.stream().filter(el -> el.size() != 1).count();
			switch (nez) {
			case 0:
				ces.add(ce);
				break;
			default:
				final SharedConditionalElementWrapper sce =
						new SharedConditionalElementWrapper(new AndFunctionConditionalElement(
								new ArrayList<ConditionalElement>(ce.children.size() - nez)));
				final List<ConditionalElement> children =
						new ArrayList<ConditionalElement>(nez + 1);
				children.add(sce);
				ces.add(new AndFunctionConditionalElement(children));
				tmpList.forEach(el -> {
					if (el.size() == 1) {
						sce.getChildren().add(el.get(0));
					} else {
						final List<ConditionalElement> newCes =
								new ArrayList<>(el.size() * ces.size());
						el.forEach(subel -> ces.forEach(l -> {
							final List<ConditionalElement> andChildren = new ArrayList<>();
							newCes.add(new AndFunctionConditionalElement(andChildren));
							andChildren.addAll(l.getChildren());
							ConditionalElement tmp = subel;
							if (nez != 1) {
								tmp = new SharedConditionalElementWrapper(subel);
								andChildren.add(tmp);
							} else {
								andChildren.addAll(subel.accept(new StripAnds()).getCes());
							}
						}));
						ces = newCes;
					}
				});
				if (sce.getChildren().size() == 1)
					sce.replaceConditionalElement(sce.getChildren().get(0));
			}
		}

		@Override
		public void defaultAction(final ConditionalElement ce) {
			ces.add(ce);
		}

		@Override
		public void visit(final OrFunctionConditionalElement ce) {
			ce.children.forEach(el -> ces.addAll(el.accept(new ExpandOrs()).getCes()));
		}
	}

	private static class CombineNested implements DefaultConditionalElementsVisitor {

		@Override
		public void visit(final AndFunctionConditionalElement ce) {
			ce.getChildren().forEach((child) -> {
				child.accept(this);
			});
			final List<ConditionalElement> oldChildrenList =
					new ArrayList<ConditionalElement>(ce.getChildren());
			final List<ConditionalElement> childrenList = ce.getChildren();
			childrenList.clear();
			for (final ConditionalElement conditionalElement : oldChildrenList) {
				childrenList.addAll(conditionalElement.accept(new StripAnds()).getCes());
			}
		}

		@Override
		public void visit(final OrFunctionConditionalElement ce) {
			ce.getChildren().forEach((child) -> {
				child.accept(this);
			});
			final List<ConditionalElement> oldChildrenList =
					new ArrayList<ConditionalElement>(ce.getChildren());
			final List<ConditionalElement> childrenList = ce.getChildren();
			childrenList.clear();
			for (final ConditionalElement conditionalElement : oldChildrenList) {
				childrenList.addAll(conditionalElement.accept(new StripOrs()).getCes());
			}
		}

		@Override
		public void defaultAction(final ConditionalElement ce) {
		}
	}

	@RequiredArgsConstructor
	private static class NotFunctionConditionalElementSeep implements ConditionalElementsVisitor {

		final private boolean negated;
		@Getter
		private ConditionalElement ce = null;

		public NotFunctionConditionalElementSeep() {
			negated = false;
		}

		private void processChildren(final ConditionalElement ce, final boolean nextNegated) {
			ce.getChildren().replaceAll(
					(final ConditionalElement x) -> x.accept(new NotFunctionConditionalElementSeep(
							nextNegated)).ce);
		}

		@Override
		public void visit(final NotFunctionConditionalElement ce) {
			this.ce =
					combineViaAnd(ce.getChildren()).accept(
							new NotFunctionConditionalElementSeep(!negated)).ce;
		}

		private static ConditionalElement applySkippingIfNegated(final ConditionalElement ce,
				final boolean negated,
				final Function<List<ConditionalElement>, ConditionalElement> ctor) {
			return negated ? ctor.apply(ce.getChildren()) : ce;
		}

		@Override
		public void visit(final OrFunctionConditionalElement ce) {
			this.ce = applySkippingIfNegated(ce, negated, AndFunctionConditionalElement::new);
			processChildren(this.ce, negated);
		}

		@Override
		public void visit(final AndFunctionConditionalElement ce) {
			this.ce = applySkippingIfNegated(ce, negated, OrFunctionConditionalElement::new);
			processChildren(this.ce, negated);
		}

		@Override
		public void visit(final ExistentialConditionalElement ce) {
			this.ce =
					applySkippingIfNegated(ce, negated, NegatedExistentialConditionalElement::new);
			processChildren(this.ce, !negated);
		}

		@Override
		public void visit(final NegatedExistentialConditionalElement ce) {
			this.ce = applySkippingIfNegated(ce, negated, ExistentialConditionalElement::new);
			processChildren(this.ce, !negated);
		}

		private void visitLeaf(final ConditionalElement ce) {
			this.ce =
					negated ? new NotFunctionConditionalElement(Collections.singletonList(ce)) : ce;
		}

		@Override
		public void visit(final InitialFactConditionalElement ce) {
			visitLeaf(ce);
		}

		@Override
		public void visit(final TestConditionalElement ce) {
			visitLeaf(ce);
		}

		@Override
		public void visit(final TemplatePatternConditionalElement ce) {
			visitLeaf(ce);
		}
	}

	private static class StripAnds implements DefaultConditionalElementsVisitor {
		@Getter
		private List<ConditionalElement> ces;

		@Override
		public void defaultAction(final ConditionalElement ce) {
			this.ces = Arrays.asList(ce);
		}

		@Override
		public void visit(final AndFunctionConditionalElement ce) {
			this.ces = ce.getChildren();
		}
	}

	private static class StripOrs implements DefaultConditionalElementsVisitor {
		@Getter
		private List<ConditionalElement> ces;

		@Override
		public void defaultAction(final ConditionalElement ce) {
			this.ces = Arrays.asList(ce);
		}

		@Override
		public void visit(final OrFunctionConditionalElement ce) {
			this.ces = ce.getChildren();
		}
	}
}
