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

import com.google.common.collect.Sets;
import lombok.RequiredArgsConstructor;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.AssignmentGraph;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.NodeType;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.AssignmentGraphNode;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.binding.BindingNode;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.occurrence.ECOccurrenceNode;
import org.jamocha.dn.compiler.ecblocks.column.Column;
import org.jamocha.dn.compiler.ecblocks.lazycollections.extend.SetExtender;
import org.jamocha.dn.compiler.ecblocks.partition.BindingPartition;
import org.jamocha.dn.compiler.ecblocks.partition.OccurrencePartition;
import org.jamocha.dn.compiler.ecblocks.partition.Partition;
import org.jamocha.languages.common.SingleFactVariable;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;
import static org.jamocha.util.Lambdas.groupingIntoListOfLists;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor
public class IncompleteBlock implements BlockInterface {
    final BlockInterface block;
    final Set<ECOccurrence> unboundOccurrences;

    private IncompleteBlock extendByBindingEdges(final Column<ECOccurrenceNode, BindingNode> column,
            final List<AssignmentGraph.Edge<ECOccurrenceNode, BindingNode>> edges,
            final OccurrencePartition occurrencePartition, final Set<ECOccurrence> unboundOccurrences) {
        final Block.RowContainer rowContainer = this.block.getRowContainer().addColumn(edges);
        // FIXME do we shrink the column?
        final Set<Column<ECOccurrenceNode, BindingNode>> columns = SetExtender.with(this.block.getColumns(), column);
        final Set<SingleFactVariable> singleFactVariables = this.block.getFactVariablesUsed();
        final BindingPartition bindingPartition = this.getBindingPartition();
        return new IncompleteBlock(
                new Block(this.block.getGraph(), rowContainer, columns, singleFactVariables, bindingPartition,
                        occurrencePartition), unboundOccurrences);
    }

    private IncompleteBlock extendByEffectivelyType1Bindings(final Column<ECOccurrenceNode, BindingNode> column,
            final List<AssignmentGraph.Edge<ECOccurrenceNode, BindingNode>> type1Edges) {
        // when adding type 1 edges we know that we produce new unbound occurrences since only the binding node is
        // already contained in the active part of the block
        final Map<RowIdentifier, ECOccurrenceNode> newOccurrenceSubSet = type1Edges.stream().collect(
                toMap(edge -> this.block.getRowContainer().getRowIdentifier(edge.getTarget()),
                        AssignmentGraph.Edge::getSource, null, IdentityHashMap::new));
        final OccurrencePartition occurrencePartition =
                this.getOccurrencePartition().add(new OccurrencePartition.OccurrenceSubSet(newOccurrenceSubSet));
        final Sets.SetView<ECOccurrence> unboundOccurrences = Sets.union(this.unboundOccurrences,
                newOccurrenceSubSet.values().stream().map(ECOccurrenceNode::getOccurrence).collect(toSet()));
        return extendByBindingEdges(column, type1Edges, occurrencePartition, unboundOccurrences);
    }

    private IncompleteBlock extendByType3BindingEdges(final Column<ECOccurrenceNode, BindingNode> column,
            final List<AssignmentGraph.Edge<ECOccurrenceNode, BindingNode>> type3Edges) {
        // when adding type 3 edges we know that we do not produce new unbound occurrences since both endpoints are
        // already contained in the block
        final OccurrencePartition occurrencePartition = this.getOccurrencePartition();
        final Set<ECOccurrence> unboundOccurrences = this.unboundOccurrences;
        return extendByBindingEdges(column, type3Edges, occurrencePartition, unboundOccurrences);
    }

    private enum EdgeType {
        IRRELEVANT, TYPE1, TYPE2, TYPE3;
    }

    private static Map<EdgeType, List<AssignmentGraph.Edge<ECOccurrenceNode, BindingNode>>> getEdgeTypes(
            final Block.RowContainer rowContainer, final Column<ECOccurrenceNode, BindingNode> column,
            final Function<AssignmentGraph.Edge<ECOccurrenceNode, BindingNode>, AssignmentGraphNode<?>> oldNode,
            final Function<AssignmentGraph.Edge<ECOccurrenceNode, BindingNode>, AssignmentGraphNode<?>> newNode) {
        return column.getEdges().stream().filter(edge -> null != rowContainer.getRowIdentifier(oldNode.apply(edge)))
                .collect(groupingByConcurrent(edge -> {
                    // here we already know that oldRow is non-null
                    final RowIdentifier newRow = rowContainer.getRowIdentifier(newNode.apply(edge));
                    if (null == newRow) {
                        return EdgeType.TYPE1;
                    }
                    final RowIdentifier oldRow = rowContainer.getRowIdentifier(oldNode.apply(edge));
                    if (newRow == oldRow) {
                        return EdgeType.TYPE3;
                    }
                    return EdgeType.TYPE2;
                }));
    }

    private static Map<EdgeType, List<AssignmentGraph.Edge<ECOccurrenceNode, BindingNode>>> getEdgeTypesFromBindingNode(
            final Block.RowContainer rowContainer, final Column<ECOccurrenceNode, BindingNode> column) {
        return getEdgeTypes(rowContainer, column, AssignmentGraph.Edge::getTarget, AssignmentGraph.Edge::getSource);
    }

    private static Map<EdgeType, List<AssignmentGraph.Edge<ECOccurrenceNode, BindingNode>>>
    getEdgeTypesFromOccurrenceNode(
            final Block.RowContainer rowContainer, final Column<ECOccurrenceNode, BindingNode> column) {
        return getEdgeTypes(rowContainer, column, AssignmentGraph.Edge::getSource, AssignmentGraph.Edge::getTarget);
    }

    public IncompleteBlock extendByBindings(final RandomWrapper random,
            final Column<ECOccurrenceNode, BindingNode> column) {
        // there are three types of edges to consider:
        // edge type 1: the block contains only the binding node
        // edge type 2: both endpoints are contained in the block, but they belong to different rows
        // edge type 3: both endpoints are contained in the same row

        // the set of edges of type 3 can (and has to) be considered on its own, since if property 3 holds for one
        // edge of a column, it has to hold for all of them

        // the set of edges of type 1 can be supplemented by subsets of the edges of type 2 such that the enlarged
        // set is still of type 1

        // type 3 edges can only overlap in binding node, not in occurrence node, otherwise they would not be type 3
        // before constructing the cartesian product, check:
        // all binding and occurrence nodes have to belong to the same partition in the block
        // the resulting set of sets contains sets that don't overlap. choose 1 and add to block

        // type 1 edges should be filtered such that all binding nodes are in the same partition in the block

        // model for maximal matching (no edge can be added, not necessarily max. cardinality)
        // rows as nodes
        // if a 'local' edge connects two rows, there is a 'model' edge connecting the corresponding nodes
        // type 1 edges connect an existing row and an additional row (where the same occurrence node is represented by
        // the same row-node)

        // randomly choose maximal matching

        final Block.RowContainer rowContainer = this.block.getRowContainer();
        final Map<EdgeType, List<AssignmentGraph.Edge<ECOccurrenceNode, BindingNode>>> edgeTypes =
                getEdgeTypesFromBindingNode(rowContainer, column);
        final BindingPartition bindingPartition = this.block.getBindingPartition();
        if (random.decide(50)) {
            // consider type 3 edges
            // group edges by binding partition and arbitrarily choose a set of edges
            final ArrayList<AssignmentGraph.Edge<ECOccurrenceNode, BindingNode>> chosenPartition =
                    chooseCompatibleEdges(random, bindingPartition, AssignmentGraph.Edge::getTarget,
                            edgeTypes.get(EdgeType.TYPE3).stream());
            // add to block to get a new incomplete block
            final IncompleteBlock newIncompleteBlock = extendByType3BindingEdges(column, chosenPartition);
            return newIncompleteBlock;
        }
        // consider type 1 and 2 edges
        // group edges by binding partition and arbitrarily choose a set of edges
        final ArrayList<AssignmentGraph.Edge<ECOccurrenceNode, BindingNode>> chosenPartition =
                chooseCompatibleEdges(random, bindingPartition, AssignmentGraph.Edge::getTarget,
                        Stream.concat(edgeTypes.get(EdgeType.TYPE1).stream(), edgeTypes.get(EdgeType.TYPE2).stream()));
        // determine random maximal matching (greedy)
        // shuffle edges to make the result random when just iterating over it
        random.shuffle(chosenPartition);
        // greedily get maximal matching
        final ArrayList<AssignmentGraph.Edge<ECOccurrenceNode, BindingNode>> maximalMatching =
                getGreedyMaximalMatching(rowContainer, chosenPartition);
        // add to block to get a new incomplete block
        final IncompleteBlock newIncompleteBlock = extendByEffectivelyType1Bindings(column, maximalMatching);
        return newIncompleteBlock;
    }

    private <T extends NodeType, N extends AssignmentGraphNode<T>> ArrayList<AssignmentGraph.Edge<ECOccurrenceNode,
            BindingNode>> chooseCompatibleEdges(
            final RandomWrapper random, final Partition<N, ?, ?> bindingPartition,
            final Function<AssignmentGraph.Edge<ECOccurrenceNode, BindingNode>, N> getPartitionedEndpoint,
            final Stream<AssignmentGraph.Edge<ECOccurrenceNode, BindingNode>> edges) {
        // group by binding partition of the block
        final ArrayList<ArrayList<AssignmentGraph.Edge<ECOccurrenceNode, BindingNode>>> partitionedEdges =
                edges.collect(groupingIntoListOfLists(e -> bindingPartition.lookup(getPartitionedEndpoint.apply(e))));
        // choose arbitrary set of edges
        return random.choose(partitionedEdges);
    }

    private ArrayList<AssignmentGraph.Edge<ECOccurrenceNode, BindingNode>> getGreedyMaximalMatching(
            final Block.RowContainer rowContainer,
            final ArrayList<AssignmentGraph.Edge<ECOccurrenceNode, BindingNode>> shuffledEdges) {
        // helper lookup map from node to row
        final Map<AssignmentGraphNode<?>, RowIdentifier> node2Row = new IdentityHashMap<>();
        // helper set of rows already in the matching
        final Set<RowIdentifier> rowsInMatching = Sets.newIdentityHashSet();
        // result container
        final ArrayList<AssignmentGraph.Edge<ECOccurrenceNode, BindingNode>> maximalMatching = new ArrayList<>();
        for (final AssignmentGraph.Edge<ECOccurrenceNode, BindingNode> edge : shuffledEdges) {
            final ECOccurrenceNode source = edge.getSource();
            // determine and cache block row of source node or (if not part of the block) create a new row for it
            final RowIdentifier sourceRow = node2Row.computeIfAbsent(source,
                    x -> Optional.ofNullable(rowContainer.getRowIdentifier(source)).orElseGet(RowIdentifier::new));
            if (rowsInMatching.contains(sourceRow)) continue;
            final BindingNode target = edge.getTarget();
            // same as above for target node
            final RowIdentifier targetRow = node2Row.computeIfAbsent(target,
                    x -> Optional.ofNullable(rowContainer.getRowIdentifier(target)).orElseGet(RowIdentifier::new));
            if (rowsInMatching.contains(targetRow)) continue;
            rowsInMatching.add(sourceRow);
            rowsInMatching.add(targetRow);
            maximalMatching.add(edge);
        }
        return maximalMatching;
    }

    public IncompleteBlock extendByOccurrences(final RandomWrapper random,
            final Column<ECOccurrenceNode, BindingNode> column) {
        final Block.RowContainer rowContainer = this.block.getRowContainer();
        final Map<EdgeType, List<AssignmentGraph.Edge<ECOccurrenceNode, BindingNode>>> edgeTypes =
                getEdgeTypesFromOccurrenceNode(rowContainer, column);
        final OccurrencePartition occurrencePartition = this.block.getOccurrencePartition();
        if (random.decide(50)) {
            // consider type 3 edges
            // group by binding partition of the block
            final ArrayList<AssignmentGraph.Edge<ECOccurrenceNode, BindingNode>> chosenPartition =
                    chooseCompatibleEdges(random, occurrencePartition, AssignmentGraph.Edge::getSource,
                            edgeTypes.get(EdgeType.TYPE3).stream());
            // add to block to get a new incomplete block
            final IncompleteBlock newIncompleteBlock = extendByType3OccurrenceEdges(column, chosenPartition);
            return newIncompleteBlock;
        }
        // consider type 1 and 2 edges
        // group by binding partition of the block
        final ArrayList<AssignmentGraph.Edge<ECOccurrenceNode, BindingNode>> chosenPartition =
                chooseCompatibleEdges(random, occurrencePartition, AssignmentGraph.Edge::getSource,
                        Stream.concat(edgeTypes.get(EdgeType.TYPE1).stream(), edgeTypes.get(EdgeType.TYPE2).stream()));
        // determine random maximal matching (greedy)
        // shuffle edges to make the result random when just iterating over it
        random.shuffle(chosenPartition);
        // greedily get maximal matching
        final ArrayList<AssignmentGraph.Edge<ECOccurrenceNode, BindingNode>> maximalMatching =
                getGreedyMaximalMatching(rowContainer, chosenPartition);
        // add to block to get a new incomplete block
        final IncompleteBlock newIncompleteBlock = extendByEffectivelyType1Occurrences(column, maximalMatching);
        return newIncompleteBlock;
    }

    private IncompleteBlock extendByOccurrenceEdges(final Column<ECOccurrenceNode, BindingNode> column,
            final List<AssignmentGraph.Edge<ECOccurrenceNode, BindingNode>> edges,
            final BindingPartition bindingPartition, final Set<ECOccurrence> unboundOccurrences) {
        final Block.RowContainer rowContainer = this.block.getRowContainer().addColumn(edges);
        // FIXME do we shrink the column?
        final Set<Column<ECOccurrenceNode, BindingNode>> columns = SetExtender.with(this.block.getColumns(), column);
        final Set<SingleFactVariable> singleFactVariables = this.block.getFactVariablesUsed();
        final OccurrencePartition occurrencePartition = this.getOccurrencePartition();
        return new IncompleteBlock(
                new Block(this.block.getGraph(), rowContainer, columns, singleFactVariables, bindingPartition,
                        occurrencePartition), unboundOccurrences);
    }

    private IncompleteBlock extendByEffectivelyType1Occurrences(final Column<ECOccurrenceNode, BindingNode> column,
            final List<AssignmentGraph.Edge<ECOccurrenceNode, BindingNode>> type1Edges) {
        // when adding type 1 edges we know that we produce new unbound occurrences since only the binding node is
        // already contained in the active part of the block
        final Map<RowIdentifier, BindingNode> newBindingSubSet = type1Edges.stream().collect(
                toMap(edge -> this.block.getRowContainer().getRowIdentifier(edge.getTarget()),
                        AssignmentGraph.Edge::getTarget, null, IdentityHashMap::new));
        final BindingPartition bindingPartition =
                this.getBindingPartition().add(new BindingPartition.BindingSubSet(newBindingSubSet));
        final Sets.SetView<ECOccurrence> unboundOccurrences = Sets.difference(this.unboundOccurrences,
                type1Edges.stream().map(AssignmentGraph.Edge::getSource).map(ECOccurrenceNode::getOccurrence)
                        .collect(toSet()));
        return extendByOccurrenceEdges(column, type1Edges, bindingPartition, unboundOccurrences);
    }

    private IncompleteBlock extendByType3OccurrenceEdges(final Column<ECOccurrenceNode, BindingNode> column,
            final List<AssignmentGraph.Edge<ECOccurrenceNode, BindingNode>> type3Edges) {
        // when adding type 3 edges we know that we do not produce new unbound occurrences since both endpoints are
        // already contained in the block
        final BindingPartition bindingPartition = this.getBindingPartition();
        final Set<ECOccurrence> unboundOccurrences = this.unboundOccurrences;
        return extendByOccurrenceEdges(column, type3Edges, bindingPartition, unboundOccurrences);
    }

    Block finish() {
        if (this.unboundOccurrences.size() > 0) throw new Error();
        return new Block(this);
    }

    @Override
    public int getNumberOfRows() {
        return this.block.getNumberOfRows();
    }

    @Override
    public int getNumberOfColumns() {
        return this.block.getNumberOfColumns();
    }

    @Override
    public AssignmentGraph getGraph() {
        return this.block.getGraph();
    }

    @Override
    public Block.RowContainer getRowContainer() {
        return this.block.getRowContainer();
    }

    @Override
    public Set<Column<ECOccurrenceNode, BindingNode>> getColumns() {
        return this.block.getColumns();
    }

    @Override
    public Set<SingleFactVariable> getFactVariablesUsed() {
        return this.block.getFactVariablesUsed();
    }

    @Override
    public BindingPartition getBindingPartition() {
        return this.block.getBindingPartition();
    }

    @Override
    public OccurrencePartition getOccurrencePartition() {
        return this.block.getOccurrencePartition();
    }
}
