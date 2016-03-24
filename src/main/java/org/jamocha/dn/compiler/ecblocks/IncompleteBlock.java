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
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.AssignmentGraph.Edge;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.NodeType;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.AssignmentGraphNode;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.binding.BindingNode;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.binding.BindingType;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.binding.SlotOrFactBindingNode;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.occurrence.ECOccurrenceNode;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.occurrence.OccurrenceType;
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

    public Block masterMind(final RandomWrapper random) {
        return null;
    }

    private interface Configuration<T extends NodeType, N extends AssignmentGraphNode<T>> {
        N getOldNode(final Edge<ECOccurrenceNode, BindingNode> edge);

        AssignmentGraphNode<?> getNewNode(final Edge<ECOccurrenceNode, BindingNode> edge);

        Partition<N, ?, ?> getOldNodePartition(final BlockInterface block);

        Set<SingleFactVariable> getFactVariables(final Set<SingleFactVariable> previousFactVariables,
                final ArrayList<Edge<ECOccurrenceNode, BindingNode>> edges);

        BindingPartition determineBindingPartition(final Block.RowContainer rowContainer,
                BindingPartition bindingPartition, ArrayList<Edge<ECOccurrenceNode, BindingNode>> type1Edges);

        OccurrencePartition determineOccurrencePartition(final Block.RowContainer rowContainer,
                OccurrencePartition occurrencePartition, ArrayList<Edge<ECOccurrenceNode, BindingNode>> type1Edges);

        Set<ECOccurrence> determineUnboundOccurrences(Set<ECOccurrence> previouslyUnboundOccurrences,
                ArrayList<Edge<ECOccurrenceNode, BindingNode>> chosenEdges);

        Configuration<BindingType, BindingNode> BINDING = new Configuration<BindingType, BindingNode>() {
            @Override
            public BindingNode getOldNode(final Edge<ECOccurrenceNode, BindingNode> edge) {
                return edge.getTarget();
            }

            @Override
            public AssignmentGraphNode<?> getNewNode(final Edge<ECOccurrenceNode, BindingNode> edge) {
                return edge.getSource();
            }

            @Override
            public Partition<BindingNode, ?, ?> getOldNodePartition(final BlockInterface block) {
                return block.getBindingPartition();
            }

            @Override
            public Set<SingleFactVariable> getFactVariables(final Set<SingleFactVariable> previousFactVariables,
                    final ArrayList<Edge<ECOccurrenceNode, BindingNode>> edges) {
                return previousFactVariables;
            }

            @Override
            public BindingPartition determineBindingPartition(final Block.RowContainer rowContainer,
                    final BindingPartition bindingPartition,
                    final ArrayList<Edge<ECOccurrenceNode, BindingNode>> type1Edges) {
                return bindingPartition;
            }

            @Override
            public OccurrencePartition determineOccurrencePartition(final Block.RowContainer rowContainer,
                    final OccurrencePartition occurrencePartition,
                    final ArrayList<Edge<ECOccurrenceNode, BindingNode>> type1Edges) {
                // when adding type 1 edges we know that we produce new unbound bindings since only the
                // occurrence node is already contained in the active part of the block
                final IdentityHashMap<RowIdentifier, ECOccurrenceNode> newOccurrenceSubSet = type1Edges.stream()
                        .collect(toMap(edge -> rowContainer.getRowIdentifier(getNewNode(edge)), Edge::getSource, null,
                                IdentityHashMap::new));
                return occurrencePartition.add(new OccurrencePartition.OccurrenceSubSet(newOccurrenceSubSet));
            }

            @Override
            public Set<ECOccurrence> determineUnboundOccurrences(final Set<ECOccurrence> previouslyUnboundOccurrences,
                    final ArrayList<Edge<ECOccurrenceNode, BindingNode>> chosenEdges) {
                return Sets.union(previouslyUnboundOccurrences,
                        chosenEdges.stream().map(Edge::getSource).map(ECOccurrenceNode::getOccurrence)
                                .collect(toSet()));
            }
        };
        Configuration<OccurrenceType, ECOccurrenceNode> OCCURRENCE =
                new Configuration<OccurrenceType, ECOccurrenceNode>() {
                    @Override
                    public ECOccurrenceNode getOldNode(final Edge<ECOccurrenceNode, BindingNode> edge) {
                        return edge.getSource();
                    }

                    @Override
                    public AssignmentGraphNode<?> getNewNode(final Edge<ECOccurrenceNode, BindingNode> edge) {
                        return edge.getTarget();
                    }

                    @Override
                    public Partition<ECOccurrenceNode, ?, ?> getOldNodePartition(final BlockInterface block) {
                        return block.getOccurrencePartition();
                    }

                    @Override
                    public Set<SingleFactVariable> getFactVariables(final Set<SingleFactVariable> previousFactVariables,
                            final ArrayList<Edge<ECOccurrenceNode, BindingNode>> edges) {
                        if (BindingType.SLOT_OR_FACT_BINDING != edges.get(0).getTarget().getNodeType()) {
                            return previousFactVariables;
                        }
                        return Sets.union(previousFactVariables, edges.stream().unordered().parallel()
                                .map(edge -> ((SlotOrFactBindingNode) edge.getTarget()).getGroupingFactVariable())
                                .collect(toSet()));
                    }

                    @Override
                    public BindingPartition determineBindingPartition(final Block.RowContainer rowContainer,
                            final BindingPartition bindingPartition,
                            final ArrayList<Edge<ECOccurrenceNode, BindingNode>> type1Edges) {
                        // when adding type 1 edges we know that we produce new unbound bindings since only the
                        // occurrence node is already contained in the active part of the block
                        final IdentityHashMap<RowIdentifier, BindingNode> newBindingSubSet = type1Edges.stream()
                                .collect(toMap(edge -> rowContainer.getRowIdentifier(getNewNode(edge)), Edge::getTarget,
                                        null, IdentityHashMap::new));
                        return bindingPartition.add(new BindingPartition.BindingSubSet(newBindingSubSet));
                    }

                    @Override
                    public OccurrencePartition determineOccurrencePartition(final Block.RowContainer rowContainer,
                            final OccurrencePartition occurrencePartition,
                            final ArrayList<Edge<ECOccurrenceNode, BindingNode>> type1Edges) {
                        return occurrencePartition;
                    }

                    @Override
                    public Set<ECOccurrence> determineUnboundOccurrences(
                            final Set<ECOccurrence> previouslyUnboundOccurrences,
                            final ArrayList<Edge<ECOccurrenceNode, BindingNode>> chosenEdges) {
                        return Sets.difference(previouslyUnboundOccurrences,
                                chosenEdges.stream().map(Edge::getSource).map(ECOccurrenceNode::getOccurrence)
                                        .collect(toSet()));
                    }
                };
    }

    public Block binding(final RandomWrapper random, final Column<ECOccurrenceNode, BindingNode> column) {
        return extend(random, column, Configuration.BINDING);
    }

    public Block occurrence(final RandomWrapper random, final Column<ECOccurrenceNode, BindingNode> column) {
        return extend(random, column, Configuration.OCCURRENCE);
    }

    private <T extends NodeType, N extends AssignmentGraphNode<T>> Block extend(final RandomWrapper random,
            final Column<ECOccurrenceNode, BindingNode> column, final Configuration<T, N> config) {
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
        // partition edges by edge type
        final Map<EdgeType, List<Edge<ECOccurrenceNode, BindingNode>>> edgesByType =
                getEdgesByType(rowContainer, column, config);
        // decide whether to consider type 1&2 or type 3 edges
        final EdgeType edgeType = random.decide(50) ? EdgeType.TYPE1AND2 : EdgeType.TYPE3;
        final List<Edge<ECOccurrenceNode, BindingNode>> edges = edgesByType.get(edgeType);
        // group edges by old-node-partition
        final ArrayList<ArrayList<Edge<ECOccurrenceNode, BindingNode>>> partitionedEdges =
                partitionEdges(config.getOldNodePartition(this.block), config::getOldNode, edges.stream());
        for (int i = 0; i < 100; ++i) {
            // choose arbitrary set of edges
            final ArrayList<Edge<ECOccurrenceNode, BindingNode>> chosenPartition = random.choose(partitionedEdges);
            // get compatible set of edges to add to the block
            final ArrayList<Edge<ECOccurrenceNode, BindingNode>> chosenEdges =
                    edgeType.chooseEdges(random, rowContainer, chosenPartition);

            final Block.RowContainer newRowContainer = this.block.getRowContainer().addColumn(chosenEdges);
            // FIXME do we shrink the column?
            final Set<Column<ECOccurrenceNode, BindingNode>> newColumns =
                    SetExtender.with(this.block.getColumns(), column);
            final Set<SingleFactVariable> newFactVariablesUsed =
                    edgeType.getFactVariables(config, this.block.getFactVariablesUsed(), chosenEdges);

            final BindingPartition newBindingPartition =
                    edgeType.getBindingPartition(config, rowContainer, this.block.getBindingPartition(), chosenEdges);
            final OccurrencePartition newOccurrencePartition =
                    edgeType.getOccurrencePartition(config, rowContainer, this.block.getOccurrencePartition(),
                            chosenEdges);
            final Set<ECOccurrence> newUnboundOccurrences =
                    edgeType.getUnboundOccurrences(config, this.unboundOccurrences, chosenEdges);
            final IncompleteBlock newIncompleteBlock = new IncompleteBlock(
                    new Block(this.block.getGraph(), newRowContainer, newColumns, newFactVariablesUsed,
                            newBindingPartition, newOccurrencePartition), newUnboundOccurrences);
            final Block newBlock = newIncompleteBlock.masterMind(random);
            if (null != newBlock) {
                return newBlock;
            }
        }
        return null;
    }

    private enum EdgeType {
        TYPE1AND2 {
            @Override
            ArrayList<Edge<ECOccurrenceNode, BindingNode>> chooseEdges(final RandomWrapper random,
                    final Block.RowContainer rowContainer,
                    final ArrayList<Edge<ECOccurrenceNode, BindingNode>> chosenPartition) {
                return chosenPartition;
            }

            @Override
            Set<SingleFactVariable> getFactVariables(final Configuration<?, ?> configuration,
                    final Set<SingleFactVariable> previousFactVariables,
                    final ArrayList<Edge<ECOccurrenceNode, BindingNode>> edges) {
                return configuration.getFactVariables(previousFactVariables, edges);
            }

            @Override
            BindingPartition getBindingPartition(final Configuration<?, ?> config,
                    final Block.RowContainer rowContainer, final BindingPartition bindingPartition,
                    final ArrayList<Edge<ECOccurrenceNode, BindingNode>> chosenEdges) {
                return config.determineBindingPartition(rowContainer, bindingPartition, chosenEdges);
            }

            @Override
            OccurrencePartition getOccurrencePartition(final Configuration<?, ?> config,
                    final Block.RowContainer rowContainer, final OccurrencePartition occurrencePartition,
                    final ArrayList<Edge<ECOccurrenceNode, BindingNode>> chosenEdges) {
                return config.determineOccurrencePartition(rowContainer, occurrencePartition, chosenEdges);
            }

            @Override
            Set<ECOccurrence> getUnboundOccurrences(final Configuration<?, ?> config,
                    final Set<ECOccurrence> previouslyUnboundOccurrences,
                    final ArrayList<Edge<ECOccurrenceNode, BindingNode>> chosenEdges) {
                return config.determineUnboundOccurrences(previouslyUnboundOccurrences, chosenEdges);
            }
        }, TYPE3 {
            @Override
            ArrayList<Edge<ECOccurrenceNode, BindingNode>> chooseEdges(final RandomWrapper random,
                    final Block.RowContainer rowContainer,
                    final ArrayList<Edge<ECOccurrenceNode, BindingNode>> chosenPartition) {
                return getGreedyMaximalMatching(rowContainer, random.shuffle(chosenPartition));
            }

            @Override
            Set<SingleFactVariable> getFactVariables(final Configuration<?, ?> configuration,
                    final Set<SingleFactVariable> previousFactVariables,
                    final ArrayList<Edge<ECOccurrenceNode, BindingNode>> edges) {
                return previousFactVariables;
            }

            @Override
            BindingPartition getBindingPartition(final Configuration<?, ?> config,
                    final Block.RowContainer rowContainer, final BindingPartition bindingPartition,
                    final ArrayList<Edge<ECOccurrenceNode, BindingNode>> chosenEdges) {
                return bindingPartition;
            }

            @Override
            OccurrencePartition getOccurrencePartition(final Configuration<?, ?> config,
                    final Block.RowContainer rowContainer, final OccurrencePartition occurrencePartition,
                    final ArrayList<Edge<ECOccurrenceNode, BindingNode>> chosenEdges) {
                return occurrencePartition;
            }

            @Override
            Set<ECOccurrence> getUnboundOccurrences(final Configuration<?, ?> config,
                    final Set<ECOccurrence> previouslyUnboundOccurrences,
                    final ArrayList<Edge<ECOccurrenceNode, BindingNode>> chosenEdges) {
                return previouslyUnboundOccurrences;
            }
        };

        abstract ArrayList<Edge<ECOccurrenceNode, BindingNode>> chooseEdges(final RandomWrapper random,
                final Block.RowContainer rowContainer,
                final ArrayList<Edge<ECOccurrenceNode, BindingNode>> chosenPartition);

        abstract Set<SingleFactVariable> getFactVariables(final Configuration<?, ?> configuration,
                final Set<SingleFactVariable> previousFactVariables,
                final ArrayList<Edge<ECOccurrenceNode, BindingNode>> edges);

        abstract BindingPartition getBindingPartition(final Configuration<?, ?> config,
                final Block.RowContainer rowContainer, final BindingPartition bindingPartition,
                final ArrayList<Edge<ECOccurrenceNode, BindingNode>> chosenEdges);

        abstract OccurrencePartition getOccurrencePartition(final Configuration<?, ?> config,
                final Block.RowContainer rowContainer, final OccurrencePartition occurrencePartition,
                final ArrayList<Edge<ECOccurrenceNode, BindingNode>> chosenEdges);

        abstract Set<ECOccurrence> getUnboundOccurrences(final Configuration<?, ?> config,
                final Set<ECOccurrence> previouslyUnboundOccurrences,
                final ArrayList<Edge<ECOccurrenceNode, BindingNode>> chosenEdges);
    }

    private static Map<EdgeType, List<Edge<ECOccurrenceNode, BindingNode>>> getEdgesByType(
            final Block.RowContainer rowContainer, final Column<ECOccurrenceNode, BindingNode> column,
            final Configuration<?, ?> config) {
        return column.getEdges().stream().unordered().parallel()
                .filter(edge -> null != rowContainer.getRowIdentifier(config.getOldNode(edge)))
                .collect(groupingByConcurrent(edge -> {
                    // here we already know that oldRow is non-null
                    final RowIdentifier newRow = rowContainer.getRowIdentifier(config.getNewNode(edge));
                    if (null == newRow) {
                        return EdgeType.TYPE1AND2;
                    }
                    final RowIdentifier oldRow = rowContainer.getRowIdentifier(config.getOldNode(edge));
                    if (newRow == oldRow) {
                        return EdgeType.TYPE3;
                    }
                    return EdgeType.TYPE1AND2;
                }));
    }

    private static <T extends NodeType, N extends AssignmentGraphNode<T>> ArrayList<ArrayList<Edge<ECOccurrenceNode,
            BindingNode>>> partitionEdges(
            final Partition<N, ?, ?> partition,
            final Function<Edge<ECOccurrenceNode, BindingNode>, N> getPartitionedEndpoint,
            final Stream<Edge<ECOccurrenceNode, BindingNode>> edges) {
        // group by old-node-partition of the block
        return edges.collect(groupingIntoListOfLists(e -> partition.lookup(getPartitionedEndpoint.apply(e))));
    }

    private static ArrayList<Edge<ECOccurrenceNode, BindingNode>> getGreedyMaximalMatching(
            final Block.RowContainer rowContainer, final ArrayList<Edge<ECOccurrenceNode, BindingNode>> shuffledEdges) {
        // helper lookup map from node to row
        final Map<AssignmentGraphNode<?>, RowIdentifier> node2Row = new IdentityHashMap<>();
        // helper set of rows already in the matching
        final Set<RowIdentifier> rowsInMatching = Sets.newIdentityHashSet();
        // result container
        final ArrayList<Edge<ECOccurrenceNode, BindingNode>> maximalMatching = new ArrayList<>();
        for (final Edge<ECOccurrenceNode, BindingNode> edge : shuffledEdges) {
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
