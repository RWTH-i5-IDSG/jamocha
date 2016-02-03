/*
 * Copyright 2002-2016 The Jamocha Team
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.jamocha.org/
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under
 * the License.
 */

package org.jamocha.dn.compiler.ecblocks;

import lombok.Getter;
import org.apache.commons.lang3.tuple.Pair;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.AssignmentGraph;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.AssignmentGraph.Edge;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.binding.*;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.occurrence.*;
import org.jamocha.dn.compiler.ecblocks.column.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Getter
public class MaximalColumns {
	final HashMap<Edge<FilterOccurrenceNode, ConstantBindingNode>, Column<FilterOccurrenceNode, ConstantBindingNode>>
			filterToConstant = new HashMap<>();
	final HashMap<Edge<FilterOccurrenceNode, FunctionalExpressionBindingNode>, Column<FilterOccurrenceNode,
			FunctionalExpressionBindingNode>>
			filterToFunctionalExpression = new HashMap<>();
	final HashMap<Edge<FilterOccurrenceNode, SlotOrFactBindingNode>, Column<FilterOccurrenceNode,
			SlotOrFactBindingNode>>
			filterToTemplate = new HashMap<>();
	final HashMap<Edge<FunctionalExpressionOccurrenceNode, ConstantBindingNode>,
			Column<FunctionalExpressionOccurrenceNode, ConstantBindingNode>>
			functionalExpressionToConstant = new HashMap<>();
	final HashMap<Edge<FunctionalExpressionOccurrenceNode, FunctionalExpressionBindingNode>,
			Column<FunctionalExpressionOccurrenceNode, FunctionalExpressionBindingNode>>
			functionalExpressionToFunctionalExpression = new HashMap<>();
	final HashMap<Edge<FunctionalExpressionOccurrenceNode, SlotOrFactBindingNode>,
			Column<FunctionalExpressionOccurrenceNode, SlotOrFactBindingNode>>
			functionalExpressionToTemplate = new HashMap<>();
	final HashMap<Edge<ImplicitOccurrenceNode, ConstantBindingNode>, Column<ImplicitOccurrenceNode,
			ConstantBindingNode>>
			implicitToConstant = new HashMap<>();
	final HashMap<Edge<ImplicitOccurrenceNode, FunctionalExpressionBindingNode>, Column<ImplicitOccurrenceNode,
			FunctionalExpressionBindingNode>>
			implicitToFunctionalExpression = new HashMap<>();
	final HashMap<Edge<ImplicitOccurrenceNode, SlotOrFactBindingNode>, Column<ImplicitOccurrenceNode,
			SlotOrFactBindingNode>>
			implicitToTemplate = new HashMap<>();

	public MaximalColumns(final AssignmentGraph assignmentGraph) {
		final Set<Edge<ECOccurrenceNode, BindingNode>> edges = assignmentGraph.getGraph().edgeSet();

		final Map<OccurrenceType, Map<BindingType, Set<Edge<ECOccurrenceNode, BindingNode>>>> collect = edges.stream()
				.collect(groupingBy(e -> e.getSource().getNodeType(),
						groupingBy(e -> e.getTarget().getNodeType(), toSet())));
		for (final Map.Entry<OccurrenceType, Map<BindingType, Set<Edge<ECOccurrenceNode, BindingNode>>>> outerEntry :
				collect
				.entrySet()) {
			final OccurrenceType occurrenceType = outerEntry.getKey();
			for (final Map.Entry<BindingType, Set<Edge<ECOccurrenceNode, BindingNode>>> innerEntry : outerEntry
					.getValue().entrySet()) {
				final BindingType bindingType = innerEntry.getKey();
				storeColumn(occurrenceType, bindingType, innerEntry.getValue());
			}
		}
	}

	private static <O extends ECOccurrenceNode> Stream<Set<Edge<O, ConstantBindingNode>>> groupConstantBindings(
			final Set<Edge<O, ConstantBindingNode>> set) {
		return set.stream().collect(groupingBy(e -> e.getTarget().getConstant(), toSet())).values().stream();
	}

	private static <O extends ECOccurrenceNode> Stream<Set<Edge<O, SlotOrFactBindingNode>>> groupSlotOrFactBindings(
			final Set<Edge<O, SlotOrFactBindingNode>> set) {
		return set.stream().collect(groupingBy(e -> e.getTarget().getSchema(), toSet())).values().stream();
	}

	private static <O extends ECOccurrenceNode> Stream<Set<Edge<O, FunctionalExpressionBindingNode>>>
	groupFunctionalExpressionBindings(
			final Set<Edge<O, FunctionalExpressionBindingNode>> set) {
		return set.stream().collect(groupingBy(e -> e.getTarget().getFunction(), toSet())).values().stream();
	}

	private static <B extends BindingNode> Stream<Set<Edge<ImplicitOccurrenceNode, B>>> groupImplicitOccurrences(
			final Set<Edge<ImplicitOccurrenceNode, B>> set) {
		return set.stream()
				.collect(partitioningBy(e -> e.getSource().getCorrespondingBindingNode() == e.getTarget(), toSet()))
				.values().stream();
	}

	private static <B extends BindingNode> Stream<Set<Edge<FilterOccurrenceNode, B>>> groupFilterOccurrences(
			final Set<Edge<FilterOccurrenceNode, B>> set) {
		return set.stream().collect(
				groupingBy(e -> Pair.of(e.getSource().getFilter(), e.getSource().getParameterPosition()), toSet()))
				.values().stream();
	}

	private static <B extends BindingNode> Stream<Set<Edge<FunctionalExpressionOccurrenceNode, B>>>
	groupFunctionalExpressionOccurrences(
			final Set<Edge<FunctionalExpressionOccurrenceNode, B>> set) {
		return set.stream().collect(
				groupingBy(e -> Pair.of(e.getSource().getFunction(), e.getSource().getParameterPosition()), toSet()))
				.values().stream();
	}

	private static <O extends ECOccurrenceNode, B extends BindingNode, C extends Column<O, B>> void disperse(
			final Set<Edge<O, B>> edges, final Function<Set<Edge<O, B>>, C> ctor, final HashMap<Edge<O, B>, C>
			target) {
		final C column = ctor.apply(edges);
		edges.forEach(edge -> target.put(edge, column));
	}

	@SuppressWarnings("unchecked")
	private void storeColumn(final OccurrenceType occurrenceType, final BindingType bindingType,
			final Set<Edge<ECOccurrenceNode, BindingNode>> edges) {
		switch (occurrenceType) {
			case IMPLICIT_OCCURRENCE:
				switch (bindingType) {
					case CONSTANT_EXPRESSION:
						Stream.of((Set<Edge<ImplicitOccurrenceNode, ConstantBindingNode>>) (Set<?>) edges)
								.flatMap(MaximalColumns::groupImplicitOccurrences)
								.flatMap(MaximalColumns::groupConstantBindings)
								.forEach(set -> disperse(set, ImplicitToConstantColumn::new, this.implicitToConstant));
						break;
					case FUNCTIONAL_EXPRESSION:
						Stream.of((Set<Edge<ImplicitOccurrenceNode, FunctionalExpressionBindingNode>>) (Set<?>) edges)
								.flatMap(MaximalColumns::groupImplicitOccurrences)
								.flatMap(MaximalColumns::groupFunctionalExpressionBindings).forEach(
								set -> disperse(set, ImplicitToFunctionalExpressionColumn::new,
										this.implicitToFunctionalExpression));
						break;
					case SLOT_OR_FACT_BINDING:
						Stream.of((Set<Edge<ImplicitOccurrenceNode, SlotOrFactBindingNode>>) (Set<?>) edges)
								.flatMap(MaximalColumns::groupImplicitOccurrences)
								.flatMap(MaximalColumns::groupSlotOrFactBindings)
								.forEach(set -> disperse(set, ImplicitToTemplateColumn::new, this.implicitToTemplate));
						break;
				}
				break;
			case FILTER_OCCURRENCE:
				switch (bindingType) {
					case CONSTANT_EXPRESSION:
						Stream.of((Set<Edge<FilterOccurrenceNode, ConstantBindingNode>>) (Set<?>) edges)
								.flatMap(MaximalColumns::groupFilterOccurrences)
								.flatMap(MaximalColumns::groupConstantBindings)
								.forEach(set -> disperse(set, FilterToConstantColumn::new, this.filterToConstant));
						break;
					case FUNCTIONAL_EXPRESSION:
						Stream.of((Set<Edge<FilterOccurrenceNode, FunctionalExpressionBindingNode>>) (Set<?>) edges)
								.flatMap(MaximalColumns::groupFilterOccurrences)
								.flatMap(MaximalColumns::groupFunctionalExpressionBindings).forEach(
								set -> disperse(set, FilterToFunctionalExpressionColumn::new,
										this.filterToFunctionalExpression));
						break;
					case SLOT_OR_FACT_BINDING:
						Stream.of((Set<Edge<FilterOccurrenceNode, SlotOrFactBindingNode>>) (Set<?>) edges)
								.flatMap(MaximalColumns::groupFilterOccurrences)
								.flatMap(MaximalColumns::groupSlotOrFactBindings)
								.forEach(set -> disperse(set, FilterToTemplateColumn::new, this.filterToTemplate));
						break;
				}
				break;
			case FUNCTIONAL_OCCURRENCE:
				switch (bindingType) {
					case CONSTANT_EXPRESSION:
						Stream.of((Set<Edge<FunctionalExpressionOccurrenceNode, ConstantBindingNode>>) (Set<?>) edges)
								.flatMap(MaximalColumns::groupFunctionalExpressionOccurrences)
								.flatMap(MaximalColumns::groupConstantBindings).forEach(
								set -> disperse(set, FunctionalExpressionToConstantColumn::new,
										this.functionalExpressionToConstant));
						break;
					case FUNCTIONAL_EXPRESSION:
						Stream.of(
								(Set<Edge<FunctionalExpressionOccurrenceNode, FunctionalExpressionBindingNode>>)
										(Set<?>) edges)
								.flatMap(MaximalColumns::groupFunctionalExpressionOccurrences)
								.flatMap(MaximalColumns::groupFunctionalExpressionBindings).forEach(
								set -> disperse(set, FunctionalExpressionToFunctionalExpressionColumn::new,
										this.functionalExpressionToFunctionalExpression));
						break;
					case SLOT_OR_FACT_BINDING:
						Stream.of((Set<Edge<FunctionalExpressionOccurrenceNode, SlotOrFactBindingNode>>) (Set<?>)
								edges)
								.flatMap(MaximalColumns::groupFunctionalExpressionOccurrences)
								.flatMap(MaximalColumns::groupSlotOrFactBindings).forEach(
								set -> disperse(set, FunctionalExpressionToTemplateColumn::new,
										this.functionalExpressionToTemplate));
						break;
				}
				break;
		}
	}
}
