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
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.AssignmentGraphNode;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.binding.BindingNode;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.occurrence.ECOccurrenceNode;
import org.jamocha.dn.compiler.ecblocks.column.Column;
import org.jamocha.dn.compiler.ecblocks.partition.BindingPartition;
import org.jamocha.dn.compiler.ecblocks.partition.FilterPartition;
import org.jamocha.dn.compiler.ecblocks.partition.TemplateInstancePartition;
import org.jamocha.languages.common.SingleFactVariable;

import java.util.*;
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

    private IncompleteBlock extendByBindings(
            final List<AssignmentGraph.Edge<ECOccurrenceNode, BindingNode>> edgeCombination) {

    }

    IncompleteBlock extendByBindings(final RandomWrapper random, final Column<ECOccurrenceNode, BindingNode> column) {
        final Block.RowContainer rowContainer = this.block.getRowContainer();
        final Set<AssignmentGraphNode<?>> blockNodes = rowContainer.getLazyBlockNodeSet();

        final Set<AssignmentGraph.Edge<ECOccurrenceNode, BindingNode>> relevantEdges =
                column.getEdges().stream().filter(e -> blockNodes.contains(e.getTarget())).collect(toSet());
        final Map<Boolean, List<AssignmentGraph.Edge<ECOccurrenceNode, BindingNode>>> isOccurrenceNodeContained =
                relevantEdges.stream().collect(partitioningBy(e -> blockNodes.contains(e.getSource())));

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

        final List<AssignmentGraph.Edge<ECOccurrenceNode, BindingNode>> type1Edges =
                isOccurrenceNodeContained.get(false);
        final Map<Boolean, List<AssignmentGraph.Edge<ECOccurrenceNode, BindingNode>>> isOccurrenceNodeInSameRow =
                isOccurrenceNodeContained.get(true).stream().collect(
                        partitioningBy(e -> rowContainer.getRow(e.getSource()) == rowContainer.getRow(e.getTarget())));
        final List<AssignmentGraph.Edge<ECOccurrenceNode, BindingNode>> type2Edges =
                isOccurrenceNodeInSameRow.get(false);
        final List<AssignmentGraph.Edge<ECOccurrenceNode, BindingNode>> type3Edges =
                isOccurrenceNodeInSameRow.get(true);

        final BindingPartition bindingPartition = this.block.getBindingPartition();
        if (random.decide(50)) {
            // consider type 3 edges
            // group by binding partition of the block
            final ArrayList<ArrayList<AssignmentGraph.Edge<ECOccurrenceNode, BindingNode>>> partitionedType3Edges =
                    type3Edges.stream().collect(groupingIntoListOfLists(e -> bindingPartition.lookup(e.getTarget())));
            // choose arbitrary set of edges
            final ArrayList<AssignmentGraph.Edge<ECOccurrenceNode, BindingNode>> chosenPartition =
                    random.choose(partitionedType3Edges);
            // add to block to get a new incomplete block
            final IncompleteBlock newIncompleteBlock = extendByBindings(chosenPartition);
            return newIncompleteBlock;
        } else {
            // consider type 1 and 2 edges
            // group type 1 and 2 edges by binding partition of the bock
            final ArrayList<ArrayList<AssignmentGraph.Edge<ECOccurrenceNode, BindingNode>>> partitionedType1And2Edges =
                    Stream.concat(type1Edges.stream(), type2Edges.stream())
                            .collect(groupingIntoListOfLists(e -> bindingPartition.lookup(e.getTarget())));
            // choose arbitrary set of edges
            final ArrayList<AssignmentGraph.Edge<ECOccurrenceNode, BindingNode>> chosenPartition =
                    random.choose(partitionedType1And2Edges);
            // determine random maximal matching (greedy)
            // shuffle edges to make the result random when just iterating over it
            final ArrayList<AssignmentGraph.Edge<ECOccurrenceNode, BindingNode>> shuffledEdges =
                    random.shuffle(chosenPartition);
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
            // add to block to get a new incomplete block
            final IncompleteBlock newIncompleteBlock = extendByBindings(maximalMatching);
            return newIncompleteBlock;
        }
    }

    Set<IncompleteBlock> extendByOccurences(final Column<ECOccurrenceNode, BindingNode> column) {

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
    public TemplateInstancePartition getTemplateInstancePartition() {
        return this.block.getTemplateInstancePartition();
    }

    @Override
    public BindingPartition getBindingPartition() {
        return this.block.getBindingPartition();
    }

    @Override
    public FilterPartition getFilterPartition() {
        return this.block.getFilterPartition();
    }
}
