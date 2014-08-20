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

import static java.util.stream.Collectors.partitioningBy;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

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
		private List<ConditionalElement> ces;

		@Override
		public void visit(final AndFunctionConditionalElement ce) {
			// recurse on children, partition to find the children that had an (or ) on top level
			// (will have more than one element)
			final Map<Boolean, List<List<ConditionalElement>>> partition =
					ce.getChildren().stream().map((el) -> el.accept(new ExpandOrs()).getCes())
							.collect(partitioningBy(el -> el.size() == 1));
			final List<List<ConditionalElement>> singletonLists = partition.get(Boolean.TRUE);
			final List<List<ConditionalElement>> orLists = partition.get(Boolean.FALSE);
			final int numOrs = orLists.size();
			if (0 == numOrs) {
				// no (or )s, nothing to do
				ces = Arrays.asList(ce);
				return;
			}
			// wrap the part without the (or )s into shared element wrapper
			final SharedConditionalElementWrapper shared =
					new SharedConditionalElementWrapper(combineViaAnd(singletonLists.stream()
							.map(l -> l.get(0)).collect(toList())));
			if (1 == numOrs) {
				// only one (or ), no need to share the elements of the (or )
				// combine shared part with each of the (or )-elements
				this.ces =
						orLists.get(0)
								.stream()
								.map(orPart -> new AndFunctionConditionalElement(Arrays.asList(
										shared, orPart))).collect(toList());
				return;
			}
			// gradually blow up the CEs
			// the elements of CEs will always be AndFunctionConditionalElements acting as a list
			// while the construction of CEs is incomplete, thus we start by adding the shared part
			this.ces.add(new AndFunctionConditionalElement(Arrays.asList(shared)));
			// for every (or ) occurrence we need to duplicate the list of CEs and combine them with
			// the (or ) elements
			orLists.forEach(orList -> {
				final List<ConditionalElement> newCEs = new ArrayList<>(orList.size() * ces.size());
				orList.forEach(orPart -> {
					// wrap the next or part into a shared element wrapper
					final SharedConditionalElementWrapper sharedOrPart =
							new SharedConditionalElementWrapper(orPart);
					// copy the old part and add the shared part, add combination of them to newCEs
					this.ces.forEach(oldPart -> {
						final ArrayList<ConditionalElement> children =
								new ArrayList<>(oldPart.getChildren());
						children.add(sharedOrPart);
						newCEs.add(new AndFunctionConditionalElement(children));
					});
				});
				this.ces = newCEs;
			});
		}

		@Override
		public void defaultAction(final ConditionalElement ce) {
			this.ces = Arrays.asList(ce);
		}

		@Override
		public void visit(final OrFunctionConditionalElement ce) {
			this.ces =
					ce.getChildren().stream().map(el -> el.accept(new ExpandOrs()).getCes())
							.collect(ArrayList::new, ArrayList::addAll, ArrayList::addAll);
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
