/*
 * Copyright 2002-2015 The Jamocha Team
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
package org.jamocha.dn.compiler.ecblocks;

import com.google.common.collect.Sets;
import lombok.Getter;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.AssignmentGraph;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.AssignmentGraph.Edge;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.ExistentialSubgraphCompletelyContainedChecker;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.FunctionalExpressionBindingChecker;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.binding.*;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.occurrence.*;
import org.jamocha.filter.ECFilter;
import org.jamocha.languages.common.SingleFactVariable;

import java.util.*;

import static java.util.stream.Collectors.*;
import static org.jamocha.util.Lambdas.toHashSet;
import static org.jamocha.util.Lambdas.toIdentityHashSet;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Getter
public class Block {

	private class BlockRows {
		final AssignmentGraph assignmentGraph;
		final AssignmentGraph.UnrestrictedGraph.SubGraph subgraph;
		int ccCount = 0;

		public BlockRows(final AssignmentGraph assignmentGraph) {
			this.assignmentGraph = assignmentGraph;
			this.subgraph = assignmentGraph.getGraph().newSubGraph();
		}

		public BlockRows(final BlockRows other) {
			this.assignmentGraph = other.assignmentGraph;
			this.subgraph = other.assignmentGraph.getGraph().new SubGraph(other.subgraph);
			this.ccCount = other.ccCount;
		}

		public int getRowCount() {
			return this.ccCount;
		}
	}

	private class BlockColumn {
		final OccurrenceType occurrenceType;
		final BindingType bindingType;
		final Set<AssignmentGraph.Edge> edges;

		public BlockColumn(final Set<AssignmentGraph.Edge> edges) {
			assert !edges.isEmpty();
			this.edges = edges;
			final AssignmentGraph.Edge edge = edges.iterator().next();
			this.occurrenceType = edge.getSource().getNodeType();
			assert edges.stream().allMatch(e -> this.occurrenceType == e.getSource().getNodeType());
			this.bindingType = edge.getTarget().getNodeType();
			assert edges.stream().allMatch(e -> this.bindingType == e.getTarget().getNodeType());
		}

		public BlockColumn(final BlockColumn other) {
			this.occurrenceType = other.occurrenceType;
			this.bindingType = other.bindingType;
			this.edges = Sets.newHashSet(other.edges);
		}
	}

	//	public Set<AssignmentGraphNode> getNodesOfType(final BindingType type) {
	//		return columns.stream().filter(c -> type == c.bindingType)
	//				.flatMap(c -> c.edges.stream().map(e -> e.getBinding(graph))).collect(toSet());
	//	}
	//
	//	public Set<AssignmentGraphNode> getNodesOfType(final OccurrenceType type) {
	//		return columns.stream().filter(c -> type == c.occurrenceType)
	//				.flatMap(c -> c.edges.stream().map(e -> e.getOccurrence(graph))).collect(toSet());
	//	}

	final AssignmentGraph graph;
	final BlockRows rows;
	final Set<BlockColumn> columns;
	final Set<SingleFactVariable> factVariablesUsed;

	public Block(final AssignmentGraph graph) {
		this.graph = graph;
		this.rows = new BlockRows(graph);
		this.columns = new HashSet<>();
		this.factVariablesUsed = Sets.newIdentityHashSet();
	}

	public Block(final Block other) {
		this.graph = other.graph;
		this.rows = new BlockRows(other.rows);
		this.columns = other.columns.stream().map(BlockColumn::new).collect(toHashSet());
		this.factVariablesUsed = Sets.newIdentityHashSet();
		this.factVariablesUsed.addAll(other.factVariablesUsed);
	}

	@Override
	public String toString() {
		return "Block(" + this.getNumberOfColumns() + "x" + this.getNumberOfRows() + "): " +
				Objects.toString(this.columns);
	}

	public int getNumberOfRows() {
		return this.rows.getRowCount();
	}

	public int getNumberOfColumns() {
		return this.columns.size();
	}

	public boolean isConsistentBlock() {
		/*
		A non-empty subset $Z\subset E(G)$ of the edges of the assignment graph $G$ is called a \emph{block row} if
		the following conditions are fulfilled:
		\begin{itemize}
		\item
			If an edge adjacent to a filter is in $Z$, all edges in $G$ adjacent to that filter have to be in $Z$.
		\item
			The edge to a non-implicit occurrence is in $Z$ iff at least one edge from that occurrence to a binding is
			 in $Z$.
		\item
			The edge from a fact or slot binding to the corresponding template instance is in $Z$ iff at least one
			edge from an occurrence to that binding is in $Z$.
		\item
			If an edge from a binding to a template instance is in $Z$, all edges in $G$ adjacent to that template
			instance have to be in $Z$.
		\item
			If an edge from an occurrence to a functional expression binding is in $Z$, all edges originating from
			that binding have to be in $Z$.
		\item
			If an edge from a functional expression binding is in $Z$, at least one edge to that binding has to be in
			$Z$.
		\item
			An edge from an implicit occurrence to its corresponding binding is in $Z$ iff at least one other edge to
			that binding is in $Z$.
		\item
			If the edges $(o,b)$, $(o,b')$, and $(o',b)$ for occurrences $o$, $o'$ and bindings $b$, $b'$ are in $Z$,
			then $(o',b')$ has to be in $Z$.
		\item
			For all occurrences in functional expressions with adjacent edges in $Z$, it holds that there are paths in
			 $Z$ from these occurrences to slot, fact, or constant bindings.
		\item
			If an existential edge $(f,o)$ adjacent to a filter $f$ is in $Z$, the connected component originating
			from removing all existential edges adjacent to $f$ that contains $o$ has to be a subset of $Z$.
		\end{itemize}
		*/

		final AssignmentGraph.UnrestrictedGraph.SubGraph subgraph = this.rows.subgraph;
		final Set<BindingNode> bindingNodes = subgraph.bindingNodeSet();
		final Set<ECOccurrenceNode> occurrenceNodes = subgraph.occurrenceNodeSet();

		final Set<FilterOccurrenceNode> filterOccurrenceNodes = Sets.newIdentityHashSet();
		final Set<FunctionalExpressionOccurrenceNode> functionalExpressionOccurrenceNodes = Sets.newIdentityHashSet();
		final Set<ImplicitOccurrenceNode> implicitOccurrenceNodes = Sets.newIdentityHashSet();
		final Set<ConstantBindingNode> constantBindingNodes = Sets.newIdentityHashSet();
		final Set<SlotOrFactBindingNode> slotOrFactBindingNodes = Sets.newIdentityHashSet();
		final Set<FunctionalExpressionBindingNode> functionalExpressionBindingNodes = Sets.newIdentityHashSet();

		for (final BindingNode bindingNode : bindingNodes) {
			if (subgraph.incomingEdgesOf(bindingNode).isEmpty()) {
				throw new IllegalStateException("A binding node without incoming edges has been detected!");
			}
			switch (bindingNode.getNodeType()) {
				case FACT_BINDING:
				case SLOT_BINDING:
					slotOrFactBindingNodes.add((SlotOrFactBindingNode) bindingNode);
					break;
				case CONSTANT_EXPRESSION:
					constantBindingNodes.add((ConstantBindingNode) bindingNode);
					break;
				case FUNCTIONAL_EXPRESSION:
					functionalExpressionBindingNodes.add((FunctionalExpressionBindingNode) bindingNode);
					break;
			}
		}
		for (final ECOccurrenceNode occurrenceNode : occurrenceNodes) {
			if (subgraph.outgoingEdgesOf(occurrenceNode).isEmpty()) {
				throw new IllegalStateException("An occurrence node without outgoing edges has been detected!");
			}
			switch (occurrenceNode.getNodeType()) {
				case IMPLICIT_OCCURRENCE:
					implicitOccurrenceNodes.add((ImplicitOccurrenceNode) occurrenceNode);
					break;
				case FILTER_OCCURRENCE:
					filterOccurrenceNodes.add((FilterOccurrenceNode) occurrenceNode);
					break;
				case FUNCTIONAL_OCCURRENCE:
					functionalExpressionOccurrenceNodes.add((FunctionalExpressionOccurrenceNode) occurrenceNode);
					break;
			}
		}

		// now all occurrence nodes have outgoing edges
		// all bindings nodes have incoming edges
		{
			// If an edge adjacent to a filter is in $Z$, all edges in $G$ adjacent to that filter have to be in $Z$.
			final Map<ECFilter, Set<FilterOccurrenceNode>> filterToOccurrences =
					filterOccurrenceNodes.stream().collect(groupingBy(FilterOccurrenceNode::getFilter, toSet()));
			for (final Map.Entry<ECFilter, Set<FilterOccurrenceNode>> filterAndOccurrences : filterToOccurrences
					.entrySet()) {
				final ECFilter filter = filterAndOccurrences.getKey();
				final Set<FilterOccurrenceNode> occurrences = filterAndOccurrences.getValue();
				final Collection<FilterOccurrenceNode> arguments =
						this.graph.getFilterToOccurrenceNodes().get(filter).values();
				if (!occurrences.containsAll(arguments)) {
					throw new IllegalStateException("Not all arguments of a filter are bound!");
				}
			}
		}

		// The edge to a non-implicit occurrence is in $Z$ iff at least one edge from that occurrence to a binding is
		// in $Z$.
		// => we already checked that there is at least one outgoing edge for every occurrence

		{
			// The edge from a fact or slot binding to the corresponding template instance is in $Z$ iff at least one
			// edge from an occurrence to a binding of this template instance is in $Z$.
			// => graph will not contain unused bindings, but the set of fact variables used (stored in the block
			// class)
			// has to be identical to the set of fact variables used in the graph via edges
			final Set<SingleFactVariable> factVariables =
					slotOrFactBindingNodes.stream().map(SlotOrFactBindingNode::getGroupingFactVariable)
							.collect(toIdentityHashSet());
			if (!this.factVariablesUsed.equals(factVariables)) {
				throw new IllegalStateException("Template instance set of the block differs from that of the rows!");
			}
		}

		{
			// If an edge from an occurrence to a functional expression binding is in $Z$, all edges originating from
			// that binding have to be in $Z$.
			// If an edge from a functional expression binding is in $Z$, at least one edge to that binding has to be
			// in $Z$.
			final Map<FunctionalExpressionBindingNode, Set<FunctionalExpressionOccurrenceNode>> feToOccurrences =
					functionalExpressionOccurrenceNodes.stream()
							.collect(groupingBy(FunctionalExpressionOccurrenceNode::getGroupingBindingNode, toSet()));
			for (final Map.Entry<FunctionalExpressionBindingNode, Set<FunctionalExpressionOccurrenceNode>>
					feAndOccurrences : feToOccurrences
					.entrySet()) {
				final FunctionalExpressionBindingNode functionalExpressionBindingNode = feAndOccurrences.getKey();
				if (!functionalExpressionBindingNodes.contains(functionalExpressionBindingNode)) {
					throw new IllegalStateException("Arguments of an unused functional expression are bound!");
				}
				final Set<FunctionalExpressionOccurrenceNode> occurrences = feAndOccurrences.getValue();
				final Collection<FunctionalExpressionOccurrenceNode> arguments =
						this.graph.getFunctionalExpressionBindingToOccurrenceNodes()
								.get(functionalExpressionBindingNode).values();
				if (!occurrences.containsAll(arguments)) {
					throw new IllegalStateException("Not all arguments of a functional expression are bound!");
				}
			}
		}

		{
			// An edge from an implicit occurrence to its corresponding binding is in $Z$ iff at least one other edge
			// to that binding is in $Z$.
			for (final BindingNode binding : bindingNodes) {
				final ImplicitOccurrenceNode implicitOccurrenceNode =
						this.graph.getBindingNodeToImplicitOccurrence().get(binding);
				if (subgraph.inDegreeOf(binding) > 1 ^ !subgraph.containsEdge(implicitOccurrenceNode, binding)) {
					throw new IllegalStateException(
							"Edge to implicit occurrence is contained, but no other edge to that binding!");
				}
			}
		}


		{
			// If the edges $(o,b)$, $(o,b')$, and $(o',b)$ for occurrences $o$, $o'$ and bindings $b$, $b'$ are in
			// $Z$, then $(o',b')$ has to be in $Z$.
			// we will always fix (o,b) to be the edge to the binding from its corresponding occurrence
			// then look for other edges to that binding and inspect their occurrence nodes
			// then look for other edges from these occurrence nodes to other bindings
			// if there is such a chain of edges, there has to be an edge from the original occurrence to each of the
			// latter bindings
			for (final BindingNode binding : bindingNodes) {
				final Set<Edge> edgesToBinding = subgraph.incomingEdgesOf(binding);
				if (edgesToBinding.size() < 2) continue;
				final ImplicitOccurrenceNode correspondingOccurrenceNode =
						this.graph.getBindingNodeToImplicitOccurrence().get(binding);
				final Edge correspondingEdge = subgraph.getEdge(correspondingOccurrenceNode, binding);
				for (final Edge otherEdgeToBinding : edgesToBinding) {
					if (otherEdgeToBinding == correspondingEdge) {
						continue;
					}
					final ECOccurrenceNode otherOccurrence = otherEdgeToBinding.getSource();
					final Set<Edge> outgoingEdgesOfOtherOccurrence = subgraph.outgoingEdgesOf(otherOccurrence);
					if (outgoingEdgesOfOtherOccurrence.size() < 2) {
						continue;
					}
					for (final Edge outgoingEdgeOfOtherOccurrence : outgoingEdgesOfOtherOccurrence) {
						if (outgoingEdgeOfOtherOccurrence == otherEdgeToBinding) {
							continue;
						}
						final BindingNode otherBinding = outgoingEdgeOfOtherOccurrence.getTarget();
						if (!subgraph.containsEdge(correspondingOccurrenceNode, otherBinding)) {
							throw new IllegalStateException(
									"An edge is missing for a strongly connected equivalence class subset!");
						}
					}
				}
			}
		}

		final Set<BindingNode> directBindings = Sets.newIdentityHashSet();
		directBindings.addAll(constantBindingNodes);
		directBindings.addAll(slotOrFactBindingNodes);


		{
			// For all occurrences in functional expressions with adjacent edges in $Z$, it holds that there are paths
			// in $Z$ from these occurrences to slot, fact, or constant bindings.
			if (!FunctionalExpressionBindingChecker.check(graph, subgraph, functionalExpressionBindingNodes)) {
				throw new IllegalStateException(
						"No edge from a functional expression occurrence to a slot, fact, or constant binding!");
			}
		}

		{
			// If an existential edge $(f,o)$ adjacent to a filter $f$ is in $Z$, the connected component originating
			// from removing all existential edges adjacent to $f$ that contains $o$ has to be a subset of $Z$.
			final Map<ECFilter, ExistentialInfo> existentialFilters = filterOccurrenceNodes.stream()
					.filter(node -> node.getFunctionWithExistentialInfo().getExistentialInfo().isExistential())
					.collect(
							groupingBy(FilterOccurrenceNode::getFilter, collectingAndThen(
									mapping(node -> node.getFunctionWithExistentialInfo().getExistentialInfo(),
											toIdentityHashSet()), set -> set.iterator().next())));
			for (final Map.Entry<ECFilter, ExistentialInfo> filterWithExistentialInfo : existentialFilters.entrySet
					()) {
				final ExistentialInfo existentialInfo = filterWithExistentialInfo.getValue();
				final ECFilter filter = filterWithExistentialInfo.getKey();
				if (!ExistentialSubgraphCompletelyContainedChecker.check(graph, subgraph, filter, existentialInfo)) {
					throw new IllegalStateException("Existential subgraph not completely contained!");
				}
			}
		}

		// Two block rows are \emph{compatible} iff both block rows are disjoint and no edge in the one block row is
		// adjacent to an edge in the other block row.

		/*
		A non-empty subset $S\subset E(G)$ of the edges of the assignment graph $G$ is called a \emph{block column} if
		 the following conditions are fulfilled:
		\begin{itemize}
		\item
			All edges in $S$ are pairwise non-adjacent
		\item
			For the set of start or target nodes $V'$ of the edges in $S$ one of the following conditions holds:
			\begin{itemize}
			\item
				$V'$ contains filters only and they all apply the same predicate having the same parameters marked as
				(negated) existential
			\item
				$V'$ only contains implicit occurrences
			\item
				$V'$ only contains non-implicit occurrences representing the same position in the list of parameters
				of a filter or functional expression.
			\item
				$V'$ only contains bindings to the same constant.
			\item
				$V'$ only contains bindings to slots of the same name.
				The equality of the template is assured via the compatibility of block columns (see below).
			\item
				$V'$ only contains fact bindings.
			\item
				$V'$ only contains bindings to functional expressions and they all use the same function.
			\item
				$V'$ only contains template instances (facts) of the same template.
			\end{itemize}
		\item
			If all start nodes of the edges in $S$ are implicit occurrences, either all or none of the edges lead to
			the corresponding binding.
		\end{itemize}
		*/

		// Two different block columns $S$ and $S'$ are \emph{compatible} iff at most one pair of the sets of start and
		// target nodes of $S$ and the sets of start and target nodes of $S'$ are identical and all others are
		// disjoint.

		/*
		We also refer to the number of the elements of a block column as the height of the block column.
		A set of pairwise compatible block rows $\mathcal{Z}$ together with a set of pairwise compatible block columns
		 $\mathcal{S}$ is called a \emph{block} iff the set of all edges of the block rows is identical to the set of
		 all edges of the block columns and the amount of block rows corresponds to the height of the block columns.
		*/

		return true;
	}
}