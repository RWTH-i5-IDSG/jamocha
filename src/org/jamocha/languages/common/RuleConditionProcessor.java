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

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.partitioningBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.jamocha.function.fwa.ConstantLeaf;
import org.jamocha.function.fwa.DefaultFunctionWithArgumentsLeafVisitor;
import org.jamocha.function.fwa.GlobalVariableLeaf;
import org.jamocha.function.fwa.SymbolLeaf;
import org.jamocha.languages.common.ConditionalElement.AndFunctionConditionalElement;
import org.jamocha.languages.common.ConditionalElement.ExistentialConditionalElement;
import org.jamocha.languages.common.ConditionalElement.InitialFactConditionalElement;
import org.jamocha.languages.common.ConditionalElement.NegatedExistentialConditionalElement;
import org.jamocha.languages.common.ConditionalElement.NotFunctionConditionalElement;
import org.jamocha.languages.common.ConditionalElement.OrFunctionConditionalElement;
import org.jamocha.languages.common.ConditionalElement.SharedConditionalElementWrapper;
import org.jamocha.languages.common.ConditionalElement.TemplatePatternConditionalElement;
import org.jamocha.languages.common.ConditionalElement.TestConditionalElement;
import org.jamocha.languages.common.ScopeStack.VariableSymbol;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 */
public class RuleConditionProcessor {

	private static ConditionalElement combine(final List<ConditionalElement> conditionalElements,
			final Function<List<ConditionalElement>, ConditionalElement> combiner) {
		if (conditionalElements.size() > 1) {
			return combiner.apply(conditionalElements);
		}
		return conditionalElements.get(0);
	}

	public static ConditionalElement combineViaAnd(final List<ConditionalElement> conditionalElements) {
		return combine(conditionalElements, AndFunctionConditionalElement::new);
	}

	public static ConditionalElement combineViaOr(final List<ConditionalElement> conditionalElements) {
		return combine(conditionalElements, OrFunctionConditionalElement::new);
	}

	public static void flatten(final RuleCondition condition) {
		flatten(condition.getConditionalElements());
	}

	public static void flatten(final List<ConditionalElement> conditionalElements) {
		// add surrounding (and ), if more than one CE
		final ConditionalElement ce = combineViaAnd(new ArrayList<ConditionalElement>(conditionalElements));
		conditionalElements.clear();
		conditionalElements.add(flatten(ce));
	}

	public static ConditionalElement flatten(final ConditionalElement conditionalElement) {
		ConditionalElement ce = conditionalElement;

		// move (not )s down to the lowest possible nodes
		ce = RuleConditionProcessor.moveNots(ce);

		// combine nested ands and ors
		RuleConditionProcessor.combineNested(ce);

		// split existential CEs into their atomic groups
		ce = ce.accept(new ExistentialSplitter()).ce;

		// replace NegatedExistentialConditionalElement by NotFunctionConditionalElement if no TPCE
		// is contained (this may occur if the previous step performed changes)
		ce = ce.accept(new NotExistsReplacer()).ce;

		// perform the initial transformation again in case the previous two steps performed changes

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

		private void expand(final ConditionalElement ce) {
			// recurse on children, partition to find the children that had an (or ) on top level
			// (will have more than one element)
			final Map<Boolean, List<List<ConditionalElement>>> partition =
					ce.getChildren().stream().map(el -> el.accept(new ExpandOrs()).getCes())
							.collect(partitioningBy(el -> el.size() == 1));
			final List<ConditionalElement> singletonLists =
					partition.get(Boolean.TRUE).stream().map(l -> l.get(0)).collect(toList());
			final List<List<ConditionalElement>> orLists = partition.get(Boolean.FALSE);
			final int numOrs = orLists.size();
			if (0 == numOrs) {
				// no (or )s, nothing to do
				this.ces = new ArrayList<>(Collections.singletonList(ce));
				return;
			}
			if (1 == numOrs) {
				if (singletonLists.isEmpty()) {
					// only one or, no other children
					// pull up the list
					this.ces = orLists.get(0);
					return;
				}
				// wrap the part without the (or )s into shared element wrapper
				final SharedConditionalElementWrapper shared =
						new SharedConditionalElementWrapper(combineViaAnd(singletonLists));
				// only one (or ), no need to share the elements of the (or )
				// combine shared part with each of the (or )-elements
				this.ces =
						orLists.get(0)
								.stream()
								.map(orPart -> new AndFunctionConditionalElement(new ArrayList<>(Arrays.asList(shared,
										orPart)))).collect(toList());
				return;
			}
			// gradually blow up the CEs
			// the elements of CEs will always be AndFunctionConditionalElements acting as a list
			// while the construction of CEs is incomplete, thus we start by adding the shared part
			if (singletonLists.isEmpty()) {
				// no (or )-free part available, wrap the first or-parts into shared wrappers
				this.ces =
						orLists.remove(0).stream().map(orPart -> new SharedConditionalElementWrapper(orPart))
								.collect(toList());
			} else {
				// wrap the part without the (or )s into shared element wrapper
				final SharedConditionalElementWrapper shared =
						new SharedConditionalElementWrapper(combineViaAnd(singletonLists));
				this.ces =
						new ArrayList<>(Collections.singletonList(new AndFunctionConditionalElement(Collections
								.singletonList(shared))));
			}
			// for every (or ) occurrence we need to duplicate the list of CEs and combine them with
			// the (or ) elements
			orLists.forEach(orList -> {
				final List<ConditionalElement> newCEs = new ArrayList<>(orList.size() * this.ces.size());
				orList.forEach(orPart -> {
					// wrap the next or part into a shared element wrapper
					final SharedConditionalElementWrapper sharedOrPart = new SharedConditionalElementWrapper(orPart);
					// copy the old part and add the shared part, add combination of them to newCEs
					this.ces.forEach(oldPart -> {
						final ArrayList<ConditionalElement> children = new ArrayList<>(oldPart.getChildren());
						children.add(sharedOrPart);
						newCEs.add(new AndFunctionConditionalElement(children));
					});
				});
				this.ces = newCEs;
			});
		}

		@Override
		public void visit(final AndFunctionConditionalElement ce) {
			expand(ce);
		}

		@Override
		public void visit(final ExistentialConditionalElement ce) {
			expand(ce);
			this.ces =
					this.ces.stream()
							.map(c -> new ExistentialConditionalElement(ce.scope, new ArrayList<>(Collections
									.singletonList(c)))).collect(toList());
		}

		@Override
		public void visit(final NegatedExistentialConditionalElement ce) {
			expand(ce);
			this.ces =
					new ArrayList<>(Collections.singletonList(new AndFunctionConditionalElement(this.ces
							.stream()
							.map(c -> new NegatedExistentialConditionalElement(ce.scope, new ArrayList<>(Collections
									.singletonList(c)))).collect(toList()))));
		}

		@Override
		public void defaultAction(final ConditionalElement ce) {
			this.ces = new ArrayList<>(Collections.singletonList(ce));
		}

		@Override
		public void visit(final OrFunctionConditionalElement ce) {
			this.ces =
					ce.getChildren().stream().flatMap(el -> el.accept(new ExpandOrs()).getCes().stream())
							.collect(toList());
		}
	}

	private static class CombineNested implements DefaultConditionalElementsVisitor {

		private void combineNestedAnds(final ConditionalElement ce) {
			final List<ConditionalElement> oldChildrenList = new ArrayList<ConditionalElement>(ce.getChildren());
			final List<ConditionalElement> childrenList = ce.getChildren();
			childrenList.clear();
			for (final ConditionalElement conditionalElement : oldChildrenList) {
				childrenList.addAll(conditionalElement.accept(new StripAnds()).getCes());
			}
		}

		@Override
		public void visit(final AndFunctionConditionalElement ce) {
			defaultAction(ce);
			combineNestedAnds(ce);
		}

		@Override
		public void visit(final OrFunctionConditionalElement ce) {
			defaultAction(ce);
			final List<ConditionalElement> oldChildrenList = new ArrayList<ConditionalElement>(ce.getChildren());
			final List<ConditionalElement> childrenList = ce.getChildren();
			childrenList.clear();
			for (final ConditionalElement conditionalElement : oldChildrenList) {
				childrenList.addAll(conditionalElement.accept(new StripOrs()).getCes());
			}
		}

		@Override
		public void visit(final ExistentialConditionalElement ce) {
			defaultAction(ce);
			combineNestedAnds(ce);
		}

		@Override
		public void visit(final NegatedExistentialConditionalElement ce) {
			defaultAction(ce);
			combineNestedAnds(ce);
		}

		@Override
		public void defaultAction(final ConditionalElement ce) {
			ce.getChildren().forEach(child -> child.accept(this));
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
					(final ConditionalElement x) -> x.accept(new NotFunctionConditionalElementSeep(nextNegated)).ce);
		}

		@Override
		public void visit(final NotFunctionConditionalElement ce) {
			this.ce = combineViaAnd(ce.getChildren()).accept(new NotFunctionConditionalElementSeep(!negated)).ce;
		}

		private static ConditionalElement applySkippingIfNegated(final ConditionalElement ce, final boolean negated,
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
			this.ce = negated ? new NegatedExistentialConditionalElement(ce.scope, ce.children) : ce;
			processChildren(this.ce, false);
		}

		@Override
		public void visit(final NegatedExistentialConditionalElement ce) {
			this.ce = negated ? new ExistentialConditionalElement(ce.scope, ce.children) : ce;
			processChildren(this.ce, false);
		}

		private void visitLeaf(final ConditionalElement ce) {
			this.ce = negated ? new NotFunctionConditionalElement(new ArrayList<>(Collections.singletonList(ce))) : ce;
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
			this.ces = new ArrayList<>(Collections.singletonList(ce));
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
			this.ces = new ArrayList<>(Collections.singletonList(ce));
		}

		@Override
		public void visit(final OrFunctionConditionalElement ce) {
			this.ces = ce.getChildren();
		}
	}

	private static class ExistentialSplitter implements DefaultConditionalElementsVisitor {
		private ConditionalElement ce;

		private static class ShallowSymbolCollector implements DefaultConditionalElementsVisitor,
				DefaultFunctionWithArgumentsLeafVisitor<SymbolLeaf> {
			final Set<VariableSymbol> symbols = new HashSet<>();

			@Override
			public void visit(final TestConditionalElement ce) {
				ce.getPredicateWithArguments().accept(this);
			}

			@Override
			public void defaultAction(final ConditionalElement ce) {
				ce.children.forEach(c -> c.accept(this));
			}

			@Override
			public void visit(final ConstantLeaf<SymbolLeaf> constantLeaf) {
			}

			@Override
			public void visit(final GlobalVariableLeaf<SymbolLeaf> globalVariableLeaf) {
			}

			@Override
			public void visit(final SymbolLeaf leaf) {
				symbols.add(leaf.getSymbol());
			}
		}

		@Override
		public void visit(final NegatedExistentialConditionalElement ce) {
			this.ce =
					combineViaOr(determinePartitions(ce).stream()
							.map(group -> new NegatedExistentialConditionalElement(ce.scope, group)).collect(toList()));
		}

		@Override
		public void visit(final ExistentialConditionalElement ce) {
			this.ce =
					combineViaAnd(determinePartitions(ce).stream()
							.map(group -> new ExistentialConditionalElement(ce.scope, group)).collect(toList()));
		}

		private Collection<List<ConditionalElement>> determinePartitions(final ConditionalElement ce) {
			final Map<SingleFactVariable, Set<SingleFactVariable>> occurringWith = new HashMap<>();
			final Map<ConditionalElement, SingleFactVariable> childToRepresentative = new HashMap<>();
			for (final ConditionalElement child : ce.getChildren()) {
				final Set<VariableSymbol> childSymbols = child.accept(new ShallowSymbolCollector()).symbols;
				for (final VariableSymbol childSymbol : childSymbols) {
					final Set<SingleFactVariable> factVariables =
							childSymbol.equal.equalSlotVariables.stream().map(ssv -> ssv.getFactVariable())
									.collect(toSet());
					factVariables.addAll(childSymbol.equal.factVariables);
					final Set<SingleFactVariable> combinedFVs =
							factVariables.stream()
									.flatMap(fv -> occurringWith.getOrDefault(fv, Collections.emptySet()).stream())
									.collect(toSet());
					combinedFVs.forEach(fv -> occurringWith.put(fv, combinedFVs));
					if (!factVariables.isEmpty()) {
						childToRepresentative.put(child, factVariables.iterator().next());
					}
				}
			}
			final Map<Set<SingleFactVariable>, List<ConditionalElement>> partition =
					ce.getChildren()
							.stream()
							.collect(
									groupingBy(c -> Optional.ofNullable(childToRepresentative.get(c))
											.map(occurringWith::get).orElse(Collections.emptySet())));
			final List<ConditionalElement> empty = partition.get(Collections.emptySet());
			if (empty.isEmpty())
				return partition.values();
			final List<List<ConditionalElement>> groups =
					partition.keySet().stream().filter(k -> k != Collections.<SingleFactVariable> emptySet())
							.map(partition::get).collect(toList());
			empty.forEach(c -> groups.add(new ArrayList<>(Collections.singletonList(c))));
			return groups;
		}

		@Override
		public void defaultAction(final ConditionalElement ce) {
			this.ce = ce;
			ce.children.replaceAll(c -> c.accept(new ExistentialSplitter()).ce);
		}
	}

	private static class NotExistsReplacer implements DefaultConditionalElementsVisitor {
		private ConditionalElement ce;

		private static class TPCEFinder implements DefaultConditionalElementsVisitor {
			boolean tpceContained = false;

			@Override
			public void defaultAction(final ConditionalElement ce) {
				for (final ConditionalElement child : ce.getChildren()) {
					child.accept(this);
					if (tpceContained)
						return;
				}
			}

			@Override
			public void visit(final TemplatePatternConditionalElement ce) {
				this.tpceContained = true;
			}
		}

		@Override
		public void visit(final NegatedExistentialConditionalElement ce) {
			defaultAction(ce);
			if (!ce.accept(new TPCEFinder()).tpceContained) {
				this.ce = new NotFunctionConditionalElement(ce.getChildren());
			}
		}

		@Override
		public void defaultAction(final ConditionalElement ce) {
			this.ce = ce;
			ce.getChildren().replaceAll(c -> c.accept(new NotExistsReplacer()).ce);
		}
	}
}
