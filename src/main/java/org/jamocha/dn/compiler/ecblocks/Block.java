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
 * the specific language governing permissions and limitations under the License.
 */
package org.jamocha.dn.compiler.ecblocks;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import lombok.Getter;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.AssignmentGraph;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.AssignmentGraph.Edge;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.ConnectedComponentCounter;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.ExistentialSubgraphCompletelyContainedChecker;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.FunctionalExpressionBindingChecker;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.AssignmentGraphNode;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.binding.*;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.occurrence.*;
import org.jamocha.dn.compiler.ecblocks.column.Column;
import org.jamocha.dn.compiler.ecblocks.exceptions.*;
import org.jamocha.filter.ECFilter;
import org.jamocha.languages.common.SingleFactVariable;
import org.paukov.combinatorics.Factory;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;
import static org.jamocha.util.Lambdas.toHashSet;
import static org.jamocha.util.Lambdas.toIdentityHashSet;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Getter
public class Block implements BlockInterface {

    @Getter
    protected class BlockRows {
        final AssignmentGraph assignmentGraph;
        //        final AssignmentGraph.UnrestrictedGraph.SubGraph subgraph;
        final Map<AssignmentGraphNode<?>, AssignmentGraph.UnrestrictedGraph.SubGraph> node2Row;
        final Set<AssignmentGraph.UnrestrictedGraph.SubGraph> rows;

        BlockRows(final AssignmentGraph assignmentGraph) {
            this.assignmentGraph = assignmentGraph;
            this.rows = new HashSet<>();
            this.node2Row = new HashMap<>();
        }

        BlockRows(final BlockRows other) {
            this.assignmentGraph = other.assignmentGraph;
            this.rows =
                    other.rows.stream().map(other.assignmentGraph.getGraph()::newSubGraph).collect(Collectors.toSet());
            this.node2Row = new HashMap<>(other.node2Row);
        }

        public void removeRow(final AssignmentGraphNode<?> representative) {
            removeRow(this.node2Row.get(representative));
        }

        public void removeRow(final AssignmentGraph.UnrestrictedGraph.SubGraph row) {
            for (final Edge<ECOccurrenceNode, BindingNode> edge : row.edgeSet()) {
                this.node2Row.remove(edge.getSource());
                this.node2Row.remove(edge.getTarget());
            }
            this.rows.remove(row);
        }

        public void addColumn(final Iterable<Edge<ECOccurrenceNode, BindingNode>> edges) {
            if (this.rows.isEmpty()) {
                for (final Edge<ECOccurrenceNode, BindingNode> edge : edges) {
                    final AssignmentGraph.UnrestrictedGraph.SubGraph subGraph =
                            this.assignmentGraph.getGraph().newSubGraph();
                    subGraph.addEdge(edge);
                    this.rows.add(subGraph);
                }
                return;
            }
            assert Iterables.size(edges) == this.rows.size();
            final Edge<ECOccurrenceNode, BindingNode> firstEdge = edges.iterator().next();
            final Function<Edge<ECOccurrenceNode, BindingNode>, AssignmentGraphNode<?>> getter =
                    this.node2Row.containsKey(firstEdge.getSource()) ? Edge::getSource : Edge::getTarget;
            edges.forEach(edge -> this.node2Row.get(getter.apply(edge)).addEdge(edge));
        }

        public int getRowCount() {
            return this.rows.size();
        }
    }

    final AssignmentGraph graph;
    final BlockRows rows;
    final Set<Column<ECOccurrenceNode, BindingNode>> columns;
    final Set<SingleFactVariable> factVariablesUsed;
    final TemplateInstancePartition templateInstancePartition;
    final FilterPartition filterPartition;

    public Block(final AssignmentGraph graph) {
        this.graph = graph;
        this.rows = new BlockRows(graph);
        this.columns = new HashSet<>();
        this.factVariablesUsed = Sets.newIdentityHashSet();
        this.templateInstancePartition = new TemplateInstancePartition();
        this.filterPartition = new FilterPartition();
    }

    public Block(final BlockInterface other) {
        this.graph = other.getGraph();
        this.rows = new BlockRows(other.getRows());
        this.columns = other.getColumns().stream().map(Column::copy).collect(toHashSet());
        this.factVariablesUsed = Sets.newIdentityHashSet();
        this.factVariablesUsed.addAll(other.getFactVariablesUsed());
        this.templateInstancePartition = new TemplateInstancePartition(other.getTemplateInstancePartition());
        this.filterPartition = new FilterPartition(other.getFilterPartition());
    }

    public IncompleteBlock beginExtension() {
        return new IncompleteBlock(this);
    }

    @Override
    public String toString() {
        return "Block(" + this.getNumberOfColumns() + "x" + this.getNumberOfRows() + "): " + Objects
                .toString(this.columns);
    }

    @Override
    public int getNumberOfRows() {
        return this.rows.getRowCount();
    }

    @Override
    public int getNumberOfColumns() {
        return this.columns.size();
    }

    @SuppressWarnings("checkstyle:methodlength")
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
            The edge from a fact or slot binding to the corresponding template INSTANCE is in $Z$ iff at least one
            edge from an occurrence to that binding is in $Z$.
        \item
            If an edge from a binding to a template INSTANCE is in $Z$, all edges in $G$ adjacent to that template
            INSTANCE have to be in $Z$.
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

        final AssignmentGraph.UnrestrictedGraph.SubGraph subgraph = this.rows.getSubgraph();
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
                throw new IllegalNodeDegreeException("A binding node without incoming edges has been detected!");
            }
            switch (bindingNode.getNodeType()) {
            case SLOT_OR_FACT_BINDING:
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
                throw new IllegalNodeDegreeException("An occurrence node without outgoing edges has been detected!");
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
                    throw new FaultyArgumentBindingException("Not all arguments of a filter are bound!");
                }
            }
        }

        // The edge to a non-implicit occurrence is in $Z$ iff at least one edge from that occurrence to a binding is
        // in $Z$.
        // => we already checked that there is at least one outgoing edge for every occurrence

        {
            // The edge from a fact or slot binding to the corresponding template INSTANCE is in $Z$ iff at least one
            // edge from an occurrence to a binding of this template INSTANCE is in $Z$.
            // => graph will not contain unused bindings, but the set of fact variables used (stored in the block
            // class)
            // has to be identical to the set of fact variables used in the graph via edges
            final Set<SingleFactVariable> factVariables =
                    slotOrFactBindingNodes.stream().map(SlotOrFactBindingNode::getGroupingFactVariable)
                            .collect(toIdentityHashSet());
            if (!this.factVariablesUsed.equals(factVariables)) {
                throw new InconsistentTemplateInstanceSetException(
                        "Template instance set of the block differs from that of the rows!");
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
                    throw new FaultyArgumentBindingException(
                            "Arguments of an unused functional expression are " + "bound!");
                }
                final Set<FunctionalExpressionOccurrenceNode> occurrences = feAndOccurrences.getValue();
                final Collection<FunctionalExpressionOccurrenceNode> arguments =
                        this.graph.getFunctionalExpressionBindingToOccurrenceNodes()
                                .get(functionalExpressionBindingNode).values();
                if (!occurrences.containsAll(arguments)) {
                    throw new FaultyArgumentBindingException(
                            "Not all arguments of a functional expression are " + "bound!");
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
                    throw new IllegalUseOfImplicitOccurrenceException(
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
                final Set<Edge<ECOccurrenceNode, BindingNode>> edgesToBinding = subgraph.incomingEdgesOf(binding);
                if (edgesToBinding.size() < 2) continue;
                final ImplicitOccurrenceNode correspondingOccurrenceNode =
                        this.graph.getBindingNodeToImplicitOccurrence().get(binding);
                final Edge<ECOccurrenceNode, BindingNode> correspondingEdge =
                        subgraph.getEdge(correspondingOccurrenceNode, binding);
                for (final Edge<ECOccurrenceNode, BindingNode> otherEdgeToBinding : edgesToBinding) {
                    if (otherEdgeToBinding == correspondingEdge) {
                        continue;
                    }
                    final ECOccurrenceNode otherOccurrence = otherEdgeToBinding.getSource();
                    final Set<Edge<ECOccurrenceNode, BindingNode>> outgoingEdgesOfOtherOccurrence =
                            subgraph.outgoingEdgesOf(otherOccurrence);
                    if (outgoingEdgesOfOtherOccurrence.size() < 2) {
                        continue;
                    }
                    for (final Edge<ECOccurrenceNode, BindingNode> outgoingEdgeOfOtherOccurrence :
                            outgoingEdgesOfOtherOccurrence) {
                        if (outgoingEdgeOfOtherOccurrence == otherEdgeToBinding) {
                            continue;
                        }
                        final BindingNode otherBinding = outgoingEdgeOfOtherOccurrence.getTarget();
                        if (!subgraph.containsEdge(correspondingOccurrenceNode, otherBinding)) {
                            throw new NoStronglyConnectedSubsetException(
                                    "An edge is missing for a strongly connected equivalence class subset!");
                        }
                    }
                }
            }
        }

        {
            // For all occurrences in functional expressions with adjacent edges in $Z$, it holds that there are paths
            // in $Z$ from these occurrences to slot, fact, or constant bindings.
            if (!FunctionalExpressionBindingChecker.check(this.graph, subgraph, functionalExpressionBindingNodes)) {
                throw new NoPathToDirectBindingException(
                        "No edge from a functional expression occurrence to a slot, fact, or constant binding!");
            }
        }

        {
            // If an existential edge $(f,o)$ adjacent to a filter $f$ is in $Z$, the connected component originating
            // from removing all existential edges adjacent to $f$ that contains $o$ has to be a subset of $Z$.
            final Map<ECFilter, ExistentialInfo> existentialFilters = filterOccurrenceNodes.stream()
                    .filter(node -> node.getFunctionWithExistentialInfo().getExistentialInfo().isExistential()).collect(
                            groupingBy(FilterOccurrenceNode::getFilter, collectingAndThen(
                                    mapping(node -> node.getFunctionWithExistentialInfo().getExistentialInfo(),
                                            toIdentityHashSet()), set -> set.iterator().next())));
            for (final Map.Entry<ECFilter, ExistentialInfo> filterWithExistentialInfo : existentialFilters.entrySet()) {
                final ExistentialInfo existentialInfo = filterWithExistentialInfo.getValue();
                final ECFilter filter = filterWithExistentialInfo.getKey();
                if (!ExistentialSubgraphCompletelyContainedChecker
                        .check(this.graph, subgraph, filter, existentialInfo)) {
                    throw new ExistentialSubgraphNotContainedException(
                            "Existential subgraph not completely contained!");
                }
            }
        }

        // Two block rows are \emph{compatible} iff both block rows are disjoint and no edge in the one block row is
        // adjacent to an edge in the other block row.
        if (this.rows.getCcCount() != ConnectedComponentCounter
                .countConnectedComponents(this.graph, this.rows.getSubgraph())) {
            throw new IllegalStateException(
                    "The cached number of connected components in the row-subgraph of a block is incorrect!");
        }

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

        for (final Column<ECOccurrenceNode, BindingNode> column : this.columns) {
            // All edges in $S$ are pairwise non-adjacent
            final int edgeCount = column.getEdges().size();
            if (0 == edgeCount) {
                throw new InconsistentBlockColumnException("A block column is empty!");
            }
            final Set<ECOccurrenceNode> sourceNodes = column.getSourceNodeSet();
            if (edgeCount != sourceNodes.size()) {
                throw new InconsistentBlockColumnException("Edges in block column overlap in source node!");
            }
            final OccurrenceType columnOccurrenceType = column.getOccurrenceType();
            if (sourceNodes.stream().map(ECOccurrenceNode::getNodeType)
                    .anyMatch(type -> type != columnOccurrenceType)) {
                throw new InconsistentBlockColumnException(
                        "Not all of the source nodes of a block column are of the correct type!");
            }
            final Set<BindingNode> targetNodes = column.getTargetNodeSet();
            if (edgeCount != targetNodes.size()) {
                throw new InconsistentBlockColumnException("Edges in block column overlap in target node!");
            }
            final BindingType columnBindingType = column.getBindingType();
            if (targetNodes.stream().map(BindingNode::getNodeType).anyMatch(type -> type != columnBindingType)) {
                throw new InconsistentBlockColumnException(
                        "Not all of the target nodes of a block column are of the correct type!");
            }
            switch (columnOccurrenceType) {
            case IMPLICIT_OCCURRENCE:
                // $V'$ only contains implicit occurrences
                // => check
                // If all start nodes of the edges in $S$ are implicit occurrences, either all or none of the
                // edges lead to the corresponding binding.
                if (1L != column.getEdges().stream()
                        .map(edge -> ((ImplicitOccurrenceNode) edge.getSource()).getCorrespondingBindingNode() == edge
                                .getTarget()).distinct().count()) {
                    throw new InconsistentBlockColumnException(
                            "In a column with implicit occurrences, some of the edges lead to their "
                                    + "corresponding binding and others don't!");
                }
                break;
            case FILTER_OCCURRENCE: {
                // $V'$ contains filters only and they all apply the same predicate having the same parameters
                // marked as (negated) existential
                final Set<FilterOccurrenceNode> occurrences =
                        sourceNodes.stream().map(node -> (FilterOccurrenceNode) node).collect(toIdentityHashSet());
                if (1L != occurrences.stream().map(FilterOccurrenceNode::getFunctionWithExistentialInfo).distinct()
                        .count()) {
                    throw new InconsistentBlockColumnException("Predicates in a block column differ!");
                }
                // $V'$ only contains non-implicit occurrences representing the same position in the list of
                // parameters of a filter or functional expression.
                if (1L != occurrences.stream().mapToInt(FilterOccurrenceNode::getParameterPosition).distinct()
                        .count()) {
                    throw new InconsistentBlockColumnException(
                            "Filter occurrences in a block column represent different parameter positions of a "
                                    + "filter!");
                }
            }
            break;
            case FUNCTIONAL_OCCURRENCE: {
                // $V'$ only contains non-implicit occurrences representing the same position in the list of
                // parameters of a filter or functional expression.
                if (1L != sourceNodes.stream()
                        .mapToInt(node -> ((FunctionalExpressionOccurrenceNode) node).getParameterPosition()).distinct()
                        .count()) {
                    throw new InconsistentBlockColumnException(
                            "Filter occurrences in a block column represent different parameter positions of a "
                                    + "filter!");
                }
            }
            break;
            }
            switch (columnBindingType) {
            case SLOT_OR_FACT_BINDING: {
                // $V'$ only contains fact bindings.
                // => check
                // $V'$ only contains template instances (facts) of the same template.
                // or
                // $V'$ only contains bindings to slots of the same name. The equality of the template is assured
                // via the compatibility of block columns (see below).
                if (1L != targetNodes.stream().map(node -> ((SlotOrFactBindingNode) node).getSchema()).distinct()
                        .count()) {
                    throw new InconsistentBlockColumnException("Slot of fact bindings in a block column differ!");
                }
            }
            break;
            case CONSTANT_EXPRESSION:
                // $V'$ only contains bindings to the same constant.
                if (1L != targetNodes.stream().map(node -> ((ConstantBindingNode) node).getConstant()).distinct()
                        .count()) {
                    throw new InconsistentBlockColumnException("Constants in a block column differ!");
                }
                break;
            case FUNCTIONAL_EXPRESSION: {
                // $V'$ only contains bindings to functional expressions and they all use the same function.
                if (1L != targetNodes.stream().map(node -> ((FunctionalExpressionBindingNode) node).getFunction())
                        .distinct().count()) {
                    throw new InconsistentBlockColumnException("Functions in a block column differ!");
                }
            }
            break;
            }
        }

        // Two different block columns $S$ and $S'$ are \emph{compatible} iff at most one pair of the sets of start and
        // target nodes of $S$ and the sets of start and target nodes of $S'$ are identical and all others are
        // disjoint.
        {
            final ICombinatoricsVector<Column<ECOccurrenceNode, BindingNode>> columnVector =
                    Factory.createVector(this.columns);
            final Generator<Column<ECOccurrenceNode, BindingNode>> generator =
                    Factory.createSimpleCombinationGenerator(columnVector, 2);
            for (final ICombinatoricsVector<Column<ECOccurrenceNode, BindingNode>> combination : generator) {
                final Column<ECOccurrenceNode, BindingNode> c0 = combination.getValue(0);
                final Column<ECOccurrenceNode, BindingNode> c1 = combination.getValue(1);
                final BindingType bt0 = c0.getBindingType();
                final BindingType bt1 = c1.getBindingType();
                final Set<BindingNode> b0 = c0.getEdges().stream().map(Edge::getTarget).collect(toIdentityHashSet());
                final Set<BindingNode> b1 = c1.getEdges().stream().map(Edge::getTarget).collect(toIdentityHashSet());
                if (!b0.equals(b1)) {
                    if (!Collections.disjoint(b0, b1)) {
                        throw new IncompatibleColumnsException("There are incompatible columns in the block!");
                    }
                    if (bt0 == BindingType.SLOT_OR_FACT_BINDING && bt1 == BindingType.SLOT_OR_FACT_BINDING) {
                        final Set<SingleFactVariable> t0 =
                                b0.stream().map(node -> ((SlotOrFactBindingNode) node).getGroupingFactVariable())
                                        .collect(toIdentityHashSet());
                        final Set<SingleFactVariable> t1 =
                                b1.stream().map(node -> ((SlotOrFactBindingNode) node).getGroupingFactVariable())
                                        .collect(toIdentityHashSet());
                        if (!t0.equals(t1) && !Collections.disjoint(t0, t1)) {
                            throw new IncompatibleColumnsException("There are incompatible columns in the block!");
                        }
                    }
                }
                final OccurrenceType ot0 = c0.getOccurrenceType();
                final OccurrenceType ot1 = c1.getOccurrenceType();
                final Set<ECOccurrenceNode> o0 =
                        c0.getEdges().stream().map(Edge::getSource).collect(toIdentityHashSet());
                final Set<ECOccurrenceNode> o1 =
                        c1.getEdges().stream().map(Edge::getSource).collect(toIdentityHashSet());
                if (!o0.equals(o1)) {
                    if (!Collections.disjoint(o0, o1)) {
                        throw new IncompatibleColumnsException("There are incompatible columns in the block!");
                    }
                    if (ot0 == OccurrenceType.FILTER_OCCURRENCE && ot1 == OccurrenceType.FILTER_OCCURRENCE) {
                        final Set<ECFilter> f0 = o0.stream().map(node -> ((FilterOccurrenceNode) node).getFilter())
                                .collect(toIdentityHashSet());
                        final Set<ECFilter> f1 = o1.stream().map(node -> ((FilterOccurrenceNode) node).getFilter())
                                .collect(toIdentityHashSet());
                        if (!f0.equals(f1) && !Collections.disjoint(f0, f1)) {
                            throw new IncompatibleColumnsException("There are incompatible columns in the block!");
                        }
                    }
                    if (ot0 == OccurrenceType.FUNCTIONAL_OCCURRENCE && ot1 == OccurrenceType.FUNCTIONAL_OCCURRENCE) {
                        final Set<FunctionalExpressionBindingNode> fe0 = o0.stream()
                                .map(node -> ((FunctionalExpressionOccurrenceNode) node).getGroupingBindingNode())
                                .collect(toIdentityHashSet());
                        final Set<FunctionalExpressionBindingNode> fe1 = o1.stream()
                                .map(node -> ((FunctionalExpressionOccurrenceNode) node).getGroupingBindingNode())
                                .collect(toIdentityHashSet());
                        if (!fe0.equals(fe1) && !Collections.disjoint(fe0, fe1)) {
                            throw new IncompatibleColumnsException("There are incompatible columns in the block!");
                        }
                    }
                }
                if (bt0 == BindingType.FUNCTIONAL_EXPRESSION && ot1 == OccurrenceType.FUNCTIONAL_OCCURRENCE) {
                    final Set<FunctionalExpressionBindingNode> gb1 = o1.stream()
                            .map(node -> ((FunctionalExpressionOccurrenceNode) node).getGroupingBindingNode())
                            .collect(toIdentityHashSet());
                    if (!b0.equals(gb1) && !Collections.disjoint(b0, gb1)) {
                        throw new IncompatibleColumnsException("There are incompatible columns in the block!");
                    }
                }
                if (bt1 == BindingType.FUNCTIONAL_EXPRESSION && ot0 == OccurrenceType.FUNCTIONAL_OCCURRENCE) {
                    final Set<FunctionalExpressionBindingNode> gb0 = o0.stream()
                            .map(node -> ((FunctionalExpressionOccurrenceNode) node).getGroupingBindingNode())
                            .collect(toIdentityHashSet());
                    if (!b1.equals(gb0) && !Collections.disjoint(b1, gb0)) {
                        throw new IncompatibleColumnsException("There are incompatible columns in the block!");
                    }
                }
            }
        }

        /*
        We also refer to the number of the elements of a block column as the height of the block column.
        A set of pairwise compatible block rows $\mathcal{Z}$ together with a set of pairwise compatible block columns
         $\mathcal{S}$ is called a \emph{block} iff the set of all edges of the block rows is identical to the set of
         all edges of the block columns and the amount of block rows corresponds to the height of the block columns.
        */

        {
            if (1L != this.columns.stream().mapToInt(col -> col.getEdges().size()).distinct().count()) {
                throw new IncompatibleColumnsException("Not all of the columns are of the same height!");
            }
            final int columnHeight = this.columns.iterator().next().getEdges().size();
            if (this.rows.ccCount != columnHeight) {
                throw new ColumnToRowIncompatibilityException("The column height is not equal to the row count!");
            }
            if (!this.rows.getSubgraph().edgeSet().equals(this.columns.stream().flatMap(col -> col.getEdges().stream())
                    .collect(toIdentityHashSet()))) {
                throw new ColumnToRowIncompatibilityException(
                        "The set of edges in the columns is not identical to the set of edges in the rows!");
            }
        }
        return true;
    }
}
