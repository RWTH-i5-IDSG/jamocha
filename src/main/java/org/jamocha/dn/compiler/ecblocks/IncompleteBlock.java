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

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import lombok.RequiredArgsConstructor;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.AssignmentGraph;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.AssignmentGraph.Edge;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.NodeType;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.AssignmentGraphNode;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.binding.BindingNode;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.binding.BindingType;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.binding.FunctionalExpressionBindingNode;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.binding.SlotOrFactBindingNode;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.occurrence.*;
import org.jamocha.dn.compiler.ecblocks.column.Column;
import org.jamocha.dn.compiler.ecblocks.lazycollections.extend.SetExtender;
import org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.ImmutableMinimalSet;
import org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.indexed.IndexedIdentityHashSet;
import org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.indexed.IndexedImmutableSet;
import org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.normal.SimpleMinimalIdentityHashMap;
import org.jamocha.dn.compiler.ecblocks.partition.BindingPartition;
import org.jamocha.dn.compiler.ecblocks.partition.OccurrencePartition;
import org.jamocha.dn.compiler.ecblocks.partition.Partition;
import org.jamocha.filter.ECFilter;
import org.jamocha.languages.common.SingleFactVariable;
import org.jamocha.util.Lambdas;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;
import static org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.indexed.IndexedIdentityHashSet
        .toIndexedIdentityHashSet;
import static org.jamocha.util.Lambdas.groupingIntoListOfLists;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor
public class IncompleteBlock implements BlockInterface {
    public static final int EDGE_TYPE_THRESHOLD = 50;
    public static final int NUM_EXTENSION_TRIES = 100;
    public static final int NUM_BINDING_TRIES = 100;

    final BlockInterface block;
    final IndexedIdentityHashSet<ECOccurrenceNode> unboundOccurrences;

    // TODO while extending a block starting from a binding, don't consider FE Occurrence Nodes
    // and of the implicit occurrence nodes, only consider the 'corresponding' one

    public Block masterMind(final RandomWrapper random, final MaximalColumns maximalColumns) {
        if (0 == this.unboundOccurrences.size()) return new Block(this);
        for (int i = 0; i < NUM_BINDING_TRIES; ++i) {
            final ECOccurrenceNode occurrenceToBind = random.choose(this.unboundOccurrences);
            final List<Edge<ECOccurrenceNode, BindingNode>> bindingOpportunities;
            switch (occurrenceToBind.getNodeType()) {
            case IMPLICIT_OCCURRENCE:
                // for implicit occurrences, only consider the corresponding binding nodes since they have to be
                // considered anyways
                bindingOpportunities = ImmutableList.of(this.block.getGraph().getGraph().getEdge(occurrenceToBind,
                        ((ImplicitOccurrenceNode) occurrenceToBind).getCorrespondingBindingNode()));
                break;
            case FILTER_OCCURRENCE:
            case FUNCTIONAL_OCCURRENCE:
                bindingOpportunities =
                        random.shuffledCopy(this.block.getGraph().getGraph().outgoingEdgesOf(occurrenceToBind));
                break;
            default:
                throw new UnsupportedOperationException();
            }
            for (final Edge<ECOccurrenceNode, BindingNode> edge : bindingOpportunities) {
                final Column<ECOccurrenceNode, BindingNode> column = maximalColumns.getColumn(edge);
                final Block extended = occurrence(random, maximalColumns, column);
                if (null != extended) {
                    return extended;
                }
            }
        }
        return null;
    }

    private interface Configuration<OT extends NodeType, ON extends AssignmentGraphNode<OT>, NT extends NodeType, NN
            extends AssignmentGraphNode<NT>> {
        ON getOldNode(final Edge<ECOccurrenceNode, BindingNode> edge);

        NN getNewNode(final Edge<ECOccurrenceNode, BindingNode> edge);

        Partition<ON, ?, ?> getOldNodePartition(final BlockInterface block);

        Partition<NN, ?, ?> getNewNodePartition(final BlockInterface block);

        Set<SingleFactVariable> getFactVariables(final Set<SingleFactVariable> previousFactVariables,
                final ArrayList<Edge<ECOccurrenceNode, BindingNode>> edges);

        BindingPartition determineBindingPartition(final Block.RowContainer rowContainer,
                final BindingPartition bindingPartition,
                final ArrayList<Edge<ECOccurrenceNode, BindingNode>> type1Edges);

        OccurrencePartition determineOccurrencePartition(final Block.RowContainer rowContainer,
                final OccurrencePartition occurrencePartition,
                final ArrayList<Edge<ECOccurrenceNode, BindingNode>> type1Edges);

        IndexedIdentityHashSet<ECOccurrenceNode> determineUnboundOccurrences(final BlockInterface block,
                final IndexedIdentityHashSet<ECOccurrenceNode> previouslyUnboundOccurrences,
                final ArrayList<Edge<ECOccurrenceNode, BindingNode>> chosenEdges);

        IndexedIdentityHashSet<ECOccurrenceNode> EMPTY_ARRAY = new IndexedIdentityHashSet<>();

        Configuration<BindingType, BindingNode, OccurrenceType, ECOccurrenceNode> BINDING =
                new Configuration<BindingType, BindingNode, OccurrenceType, ECOccurrenceNode>() {
                    @Override
                    public BindingNode getOldNode(final Edge<ECOccurrenceNode, BindingNode> edge) {
                        return edge.getTarget();
                    }

                    @Override
                    public ECOccurrenceNode getNewNode(final Edge<ECOccurrenceNode, BindingNode> edge) {
                        return edge.getSource();
                    }

                    @Override
                    public Partition<BindingNode, ?, ?> getOldNodePartition(final BlockInterface block) {
                        return block.getBindingPartition();
                    }

                    @Override
                    public Partition<ECOccurrenceNode, ?, ?> getNewNodePartition(final BlockInterface block) {
                        return block.getOccurrencePartition();
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
                        final SimpleMinimalIdentityHashMap<RowIdentifier, ECOccurrenceNode> newOccurrenceSubSet =
                                type1Edges.stream().collect(
                                        toMap(edge -> rowContainer.getRowIdentifier(getNewNode(edge)), Edge::getSource,
                                                null, SimpleMinimalIdentityHashMap::new));
                        return occurrencePartition.add(new OccurrencePartition.OccurrenceSubSet(newOccurrenceSubSet));
                    }

                    @Override
                    public IndexedIdentityHashSet<ECOccurrenceNode> determineUnboundOccurrences(
                            final BlockInterface block,
                            final IndexedIdentityHashSet<ECOccurrenceNode> previouslyUnboundOccurrences,
                            final ArrayList<Edge<ECOccurrenceNode, BindingNode>> chosenEdges) {
                        assert !chosenEdges.isEmpty();
                        final IndexedIdentityHashSet<ECOccurrenceNode> boundOccurrences =
                                chosenEdges.stream().map(Edge::getSource).collect(toIndexedIdentityHashSet());
                        if (boundOccurrences.stream().anyMatch(previouslyUnboundOccurrences::contains)) {
                            assert boundOccurrences.stream().allMatch(previouslyUnboundOccurrences::contains);
                            // case 1: the newly bound occurrences were part of previouslyUnboundOccurrences
                            // this means at least one of their siblings is already bound and all other siblings are
                            // part of previouslyUnboundOccurrences
                            // all we need to do is remove the newly bound occurrences
                            return previouslyUnboundOccurrences.stream()
                                    .filter(Lambdas.negate(boundOccurrences::contains))
                                    .collect(toIndexedIdentityHashSet());
                        }
                        // case 2: the newly bound occurrences were not part of previouslyUnboundOccurrences
                        // this means we have to add all their siblings (but not themselves) to
                        // previouslyUnboundOccurrences
                        // and don't have to remove anything

                        final IndexedIdentityHashSet<ECOccurrenceNode> unboundOccurrences =
                                new IndexedIdentityHashSet<>(previouslyUnboundOccurrences);
                        final ECOccurrenceNode someSourceNode = chosenEdges.iterator().next().getSource();
                        switch (someSourceNode.getNodeType()) {
                        case IMPLICIT_OCCURRENCE:
                            break;
                        case FILTER_OCCURRENCE:
                            final IdentityHashMap<ECFilter, TreeMap<Integer, FilterOccurrenceNode>>
                                    filterToOccurrenceNodes = block.getGraph().getFilterToOccurrenceNodes();
                            chosenEdges.stream().flatMap(
                                    e -> filterToOccurrenceNodes.get(((FilterOccurrenceNode) e.getSource()).getFilter())
                                            .values().stream()).forEach(unboundOccurrences::add);
                            break;
                        case FUNCTIONAL_OCCURRENCE:
                            final IdentityHashMap<FunctionalExpressionBindingNode, TreeMap<Integer,
                                    FunctionalExpressionOccurrenceNode>>
                                    functionalExpressionBindingToOccurrenceNodes =
                                    block.getGraph().getFunctionalExpressionBindingToOccurrenceNodes();
                            chosenEdges.stream().flatMap(e -> functionalExpressionBindingToOccurrenceNodes
                                    .get(((FunctionalExpressionOccurrenceNode) e.getSource()).getGroupingBindingNode())
                                    .values().stream()).forEach(unboundOccurrences::add);
                            break;
                        default:
                            throw new UnsupportedOperationException();
                        }
                        unboundOccurrences.removeAll(boundOccurrences);
                        return unboundOccurrences;
                    }
                };
        Configuration<OccurrenceType, ECOccurrenceNode, BindingType, BindingNode> OCCURRENCE =
                new Configuration<OccurrenceType, ECOccurrenceNode, BindingType, BindingNode>() {
                    @Override
                    public ECOccurrenceNode getOldNode(final Edge<ECOccurrenceNode, BindingNode> edge) {
                        return edge.getSource();
                    }

                    @Override
                    public BindingNode getNewNode(final Edge<ECOccurrenceNode, BindingNode> edge) {
                        return edge.getTarget();
                    }

                    @Override
                    public Partition<ECOccurrenceNode, ?, ?> getOldNodePartition(final BlockInterface block) {
                        return block.getOccurrencePartition();
                    }

                    @Override
                    public Partition<BindingNode, ?, ?> getNewNodePartition(final BlockInterface block) {
                        return block.getBindingPartition();
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
                        final SimpleMinimalIdentityHashMap<RowIdentifier, BindingNode> newBindingSubSet =
                                type1Edges.stream().collect(
                                        toMap(edge -> rowContainer.getRowIdentifier(getNewNode(edge)), Edge::getTarget,
                                                null, SimpleMinimalIdentityHashMap::new));
                        return bindingPartition.add(new BindingPartition.BindingSubSet(newBindingSubSet));
                    }

                    @Override
                    public OccurrencePartition determineOccurrencePartition(final Block.RowContainer rowContainer,
                            final OccurrencePartition occurrencePartition,
                            final ArrayList<Edge<ECOccurrenceNode, BindingNode>> type1Edges) {
                        return occurrencePartition;
                    }

                    @Override
                    public IndexedIdentityHashSet<ECOccurrenceNode> determineUnboundOccurrences(
                            final BlockInterface block,
                            final IndexedIdentityHashSet<ECOccurrenceNode> previouslyUnboundOccurrences,
                            final ArrayList<Edge<ECOccurrenceNode, BindingNode>> chosenEdges) {
                        assert !chosenEdges.isEmpty();

                        final IndexedIdentityHashSet<ECOccurrenceNode> unboundOccurrences =
                                new IndexedIdentityHashSet<>(previouslyUnboundOccurrences);

                        switch (chosenEdges.iterator().next().getTarget().getNodeType()) {
                        case SLOT_OR_FACT_BINDING:
                        case CONSTANT_EXPRESSION:
                            break;
                        case FUNCTIONAL_EXPRESSION:
                            final AssignmentGraph graph = block.getGraph();
                            // all FE Occurrences are unbound yet, since the occurrences can only be part of the
                            // block if the corresponding binding is part of the block, which wasn't the case,
                            // otherwise this method would not be called
                            final IdentityHashMap<FunctionalExpressionBindingNode, TreeMap<Integer,
                                    FunctionalExpressionOccurrenceNode>>
                                    feBindingToOccN = graph.getFunctionalExpressionBindingToOccurrenceNodes();
                            chosenEdges.stream().flatMap(
                                    edge -> feBindingToOccN.get(((FunctionalExpressionBindingNode) edge.getTarget()))
                                            .values().stream()).forEach(unboundOccurrences::add);
                            break;
                        default:
                            throw new UnsupportedOperationException();
                        }
                        chosenEdges.stream().map(Edge::getSource).forEach(unboundOccurrences::remove);
                        return unboundOccurrences;
                    }
                };
    }

    public Block binding(final RandomWrapper random, final MaximalColumns maximalColumns,
            final Column<ECOccurrenceNode, BindingNode> column) {
        return extend(random, maximalColumns, column, Configuration.BINDING);
    }

    public Block occurrence(final RandomWrapper random, final MaximalColumns maximalColumns,
            final Column<ECOccurrenceNode, BindingNode> column) {
        return extend(random, maximalColumns, column, Configuration.OCCURRENCE);
    }

    private <OT extends NodeType, ON extends AssignmentGraphNode<OT>, NT extends NodeType, NN extends
            AssignmentGraphNode<NT>> Block extend(
            final RandomWrapper random, final MaximalColumns maximalColumns,
            final Column<ECOccurrenceNode, BindingNode> column, final Configuration<OT, ON, NT, NN> config) {
        // there are three types of edges to consider:
        // edge type 1: the block contains only one end node
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

        // type 1 edges should be filtered such that all contained nodes are in the same partition in the block

        // model for maximal matching (no edge can be added, not necessarily max. cardinality)
        // rows as nodes
        // if a 'local' edge connects two rows, there is a 'model' edge connecting the corresponding nodes
        // type 1 edges connect an existing row and an additional row (where the same occurrence node is represented by
        // the same row-node)

        // randomly choose maximal matching

        final Block.RowContainer oldRowContainer = this.block.getRowContainer();

        // partition edges by edge type
        final Map<EdgeType, List<Edge<ECOccurrenceNode, BindingNode>>> edgesByType =
                getEdgesByType(oldRowContainer, column, config);

        // decide whether to consider type 1&2 or type 3 edges
        final EdgeType edgeType;
        if (!edgesByType.containsKey(EdgeType.TYPE1AND2)) {
            edgeType = EdgeType.TYPE3;
        } else if (!edgesByType.containsKey(EdgeType.TYPE3)) {
            edgeType = EdgeType.TYPE1AND2;
        } else {
            edgeType = random.decide(EDGE_TYPE_THRESHOLD) ? EdgeType.TYPE1AND2 : EdgeType.TYPE3;
        }
        final List<Edge<ECOccurrenceNode, BindingNode>> edges =
                edgesByType.getOrDefault(edgeType, Collections.emptyList());
        if (edges.isEmpty()) {
            return null;
        }
        // group edges by old-node-partition
        final ArrayList<ArrayList<Edge<ECOccurrenceNode, BindingNode>>> partitionedEdges =
                partitionEdges(config.getOldNodePartition(this.block), config::getOldNode, edges.stream());
        // throw away all edge groups that would produce a block with row count 1
        partitionedEdges.removeIf(list -> list.size() <= 1);

        for (int i = 0; i < NUM_EXTENSION_TRIES; ++i) {
            // choose arbitrary set of edges
            final ArrayList<Edge<ECOccurrenceNode, BindingNode>> chosenPartition = random.choose(partitionedEdges);

            // get compatible set of edges to add to the block
            // FIXME: make sure that the block will contain more than one row after extension
            final ArrayList<Edge<ECOccurrenceNode, BindingNode>> chosenEdges =
                    getGreedyMaximalMatching(edgeType, oldRowContainer, random.shuffle(chosenPartition),
                            config::getOldNode, config::getNewNode, config.getNewNodePartition(this.block));
            // edgeType.chooseEdges(this.block, config, random, oldRowContainer, chosenPartition);
            if (chosenEdges.size() <= 1) {
                continue;
            }

            final Block.RowContainer newRowContainer = oldRowContainer.addColumn(chosenEdges, config::getOldNode);

            // determine rows not contained in the block after adding the new column, may be empty, especially in the
            // case where the block was empty beforehand
            final BiMap<RowIdentifier, AssignmentGraph.UnrestrictedGraph.SubGraph> oldRowToGraphMap =
                    oldRowContainer.row2Identifier.inverse();
            final Sets.SetView<RowIdentifier> removedRows =
                    Sets.difference(oldRowToGraphMap.keySet(), newRowContainer.row2Identifier.inverse().keySet());

            // FIXME do we shrink the column?
            final ImmutableMinimalSet<Column<ECOccurrenceNode, BindingNode>> newColumns =
                    SetExtender.with(this.block.getColumns(), column);

            // reduce fact variables used to relevant rows before extending them by columns
            final Set<SingleFactVariable> oldFactVariablesUsed = this.block.getFactVariablesUsed();
            final Set<SingleFactVariable> adjustedFactVariablesUsed = removedRows.isEmpty() ? oldFactVariablesUsed
                    : Sets.difference(oldFactVariablesUsed, removedRows.stream().map(oldRowToGraphMap::get)
                            .map(AssignmentGraph.UnrestrictedGraph.SubGraph::bindingNodeSet)
                            .flatMap(IndexedImmutableSet::stream)
                            .filter(b -> b.getNodeType() == BindingType.SLOT_OR_FACT_BINDING)
                            .map(b -> ((SlotOrFactBindingNode) b).getGroupingFactVariable()).collect(toSet()));
            final Set<SingleFactVariable> newFactVariablesUsed =
                    edgeType.getFactVariables(config, adjustedFactVariablesUsed, chosenEdges);

            // reduce binding partition to relevant rows before extending them by columns
            final BindingPartition oldBindingPartition = this.block.getBindingPartition();
            final BindingPartition adjustedBindingPartition = oldBindingPartition.remove(removedRows);
            final BindingPartition newBindingPartition =
                    edgeType.getBindingPartition(config, newRowContainer, adjustedBindingPartition, chosenEdges);

            // reduce occurrence partition to relevant rows before extending them by columns
            final OccurrencePartition oldOccurrencePartition = this.block.getOccurrencePartition();
            final OccurrencePartition adjustedOccurrencePartition = oldOccurrencePartition.remove(removedRows);
            final OccurrencePartition newOccurrencePartition =
                    edgeType.getOccurrencePartition(config, newRowContainer, adjustedOccurrencePartition, chosenEdges);

            // reduce unbound occurrences to relevant rows before extending them by columns
            final IndexedIdentityHashSet<ECOccurrenceNode> oldUnboundOccurrences = this.unboundOccurrences;
            final IndexedIdentityHashSet<ECOccurrenceNode> adjustedUnboundOccurrences;
            if (removedRows.isEmpty()) {
                adjustedUnboundOccurrences = oldUnboundOccurrences;
            } else {
                final Set<ECOccurrenceNode> removedOccurrences = removedRows.stream().map(oldRowToGraphMap::get)
                        .map(AssignmentGraph.UnrestrictedGraph.SubGraph::occurrenceNodeSet)
                        .flatMap(IndexedImmutableSet::stream).collect(toSet());
                adjustedUnboundOccurrences = oldUnboundOccurrences.stream().filter(removedOccurrences::contains)
                        .collect(toIndexedIdentityHashSet());
            }
            final IndexedIdentityHashSet<ECOccurrenceNode> newUnboundOccurrences =
                    edgeType.getUnboundOccurrences(this, config, adjustedUnboundOccurrences, chosenEdges);

            final IncompleteBlock newIncompleteBlock = new IncompleteBlock(
                    new Block(this.block.getGraph(), newRowContainer, newColumns, newFactVariablesUsed,
                            newBindingPartition, newOccurrencePartition), newUnboundOccurrences);
            final Block newBlock = newIncompleteBlock.masterMind(random, maximalColumns);
            if (null != newBlock) {
                return newBlock;
            }
        }
        return null;
    }

    private enum EdgeType {
        TYPE1AND2 {
            @Override
            <OT extends NodeType, ON extends AssignmentGraphNode<OT>, NT extends NodeType, NN extends
                    AssignmentGraphNode<NT>> ArrayList<Edge<ECOccurrenceNode, BindingNode>> chooseEdges(
                    final BlockInterface block, final Configuration<OT, ON, NT, NN> configuration,
                    final RandomWrapper random, final Block.RowContainer rowContainer,
                    final ArrayList<Edge<ECOccurrenceNode, BindingNode>> chosenPartition) {
                return getGreedyMaximalMatching(rowContainer, random.shuffle(chosenPartition),
                        configuration::getOldNode, configuration::getNewNode, configuration.getNewNodePartition(block));
            }

            @Override
            Set<SingleFactVariable> getFactVariables(final Configuration<?, ?, ?, ?> configuration,
                    final Set<SingleFactVariable> previousFactVariables,
                    final ArrayList<Edge<ECOccurrenceNode, BindingNode>> edges) {
                return configuration.getFactVariables(previousFactVariables, edges);
            }

            @Override
            BindingPartition getBindingPartition(final Configuration<?, ?, ?, ?> config,
                    final Block.RowContainer rowContainer, final BindingPartition bindingPartition,
                    final ArrayList<Edge<ECOccurrenceNode, BindingNode>> chosenEdges) {
                return config.determineBindingPartition(rowContainer, bindingPartition, chosenEdges);
            }

            @Override
            OccurrencePartition getOccurrencePartition(final Configuration<?, ?, ?, ?> config,
                    final Block.RowContainer rowContainer, final OccurrencePartition occurrencePartition,
                    final ArrayList<Edge<ECOccurrenceNode, BindingNode>> chosenEdges) {
                return config.determineOccurrencePartition(rowContainer, occurrencePartition, chosenEdges);
            }

            @Override
            IndexedIdentityHashSet<ECOccurrenceNode> getUnboundOccurrences(final BlockInterface block,
                    final Configuration<?, ?, ?, ?> config,
                    final IndexedIdentityHashSet<ECOccurrenceNode> previouslyUnboundOccurrences,
                    final ArrayList<Edge<ECOccurrenceNode, BindingNode>> chosenEdges) {
                return config.determineUnboundOccurrences(block, previouslyUnboundOccurrences, chosenEdges);
            }

            @Override
            public <T extends NodeType, N extends AssignmentGraphNode<T>> Partition.SubSet<?, ?> getNewNodeSubSet(
                    final Partition<N, ?, ?> newNodePartition, final N newNode) {
                return null;
            }
        }, TYPE3 {
            @Override
            <OT extends NodeType, ON extends AssignmentGraphNode<OT>, NT extends NodeType, NN extends
                    AssignmentGraphNode<NT>> ArrayList<Edge<ECOccurrenceNode, BindingNode>> chooseEdges(
                    final BlockInterface block, final Configuration<OT, ON, NT, NN> configuration,
                    final RandomWrapper random, final Block.RowContainer rowContainer,
                    final ArrayList<Edge<ECOccurrenceNode, BindingNode>> chosenPartition) {
                return getGreedyMaximalMatching(rowContainer, random.shuffle(chosenPartition),
                        configuration::getOldNode, configuration::getNewNode, configuration.getNewNodePartition(block));
            }

            @Override
            Set<SingleFactVariable> getFactVariables(final Configuration<?, ?, ?, ?> configuration,
                    final Set<SingleFactVariable> previousFactVariables,
                    final ArrayList<Edge<ECOccurrenceNode, BindingNode>> edges) {
                return previousFactVariables;
            }

            @Override
            BindingPartition getBindingPartition(final Configuration<?, ?, ?, ?> config,
                    final Block.RowContainer rowContainer, final BindingPartition bindingPartition,
                    final ArrayList<Edge<ECOccurrenceNode, BindingNode>> chosenEdges) {
                return bindingPartition;
            }

            @Override
            OccurrencePartition getOccurrencePartition(final Configuration<?, ?, ?, ?> config,
                    final Block.RowContainer rowContainer, final OccurrencePartition occurrencePartition,
                    final ArrayList<Edge<ECOccurrenceNode, BindingNode>> chosenEdges) {
                return occurrencePartition;
            }

            @Override
            IndexedIdentityHashSet<ECOccurrenceNode> getUnboundOccurrences(final BlockInterface block,
                    final Configuration<?, ?, ?, ?> config,
                    final IndexedIdentityHashSet<ECOccurrenceNode> previouslyUnboundOccurrences,
                    final ArrayList<Edge<ECOccurrenceNode, BindingNode>> chosenEdges) {
                return previouslyUnboundOccurrences;
            }

            @Override
            public <T extends NodeType, N extends AssignmentGraphNode<T>> Partition.SubSet<?, ?> getNewNodeSubSet(
                    final Partition<N, ?, ?> newNodePartition, final N newNode) {
                return newNodePartition.lookup(newNode);
            }
        };

        abstract <OT extends NodeType, ON extends AssignmentGraphNode<OT>, NT extends NodeType, NN extends
                AssignmentGraphNode<NT>> ArrayList<Edge<ECOccurrenceNode, BindingNode>> chooseEdges(
                final BlockInterface block, final Configuration<OT, ON, NT, NN> configuration,
                final RandomWrapper random, final Block.RowContainer rowContainer,
                final ArrayList<Edge<ECOccurrenceNode, BindingNode>> chosenPartition);

        abstract Set<SingleFactVariable> getFactVariables(final Configuration<?, ?, ?, ?> configuration,
                final Set<SingleFactVariable> previousFactVariables,
                final ArrayList<Edge<ECOccurrenceNode, BindingNode>> edges);

        abstract BindingPartition getBindingPartition(final Configuration<?, ?, ?, ?> config,
                final Block.RowContainer rowContainer, final BindingPartition bindingPartition,
                final ArrayList<Edge<ECOccurrenceNode, BindingNode>> chosenEdges);

        abstract OccurrencePartition getOccurrencePartition(final Configuration<?, ?, ?, ?> config,
                final Block.RowContainer rowContainer, final OccurrencePartition occurrencePartition,
                final ArrayList<Edge<ECOccurrenceNode, BindingNode>> chosenEdges);

        abstract IndexedIdentityHashSet<ECOccurrenceNode> getUnboundOccurrences(final BlockInterface block,
                final Configuration<?, ?, ?, ?> config,
                final IndexedIdentityHashSet<ECOccurrenceNode> previouslyUnboundOccurrences,
                final ArrayList<Edge<ECOccurrenceNode, BindingNode>> chosenEdges);

        public abstract <T extends NodeType, N extends AssignmentGraphNode<T>> Partition.SubSet<?, ?> getNewNodeSubSet(
                final Partition<N, ?, ?> newNodePartition, final N newNode);
    }

    private static Map<EdgeType, List<Edge<ECOccurrenceNode, BindingNode>>> getEdgesByType(
            final Block.RowContainer rowContainer, final Column<ECOccurrenceNode, BindingNode> column,
            final Configuration<?, ?, ?, ?> config) {
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

    private static <OT extends NodeType, ON extends AssignmentGraphNode<OT>, NT extends NodeType, NN extends
            AssignmentGraphNode<NT>> ArrayList<Edge<ECOccurrenceNode, BindingNode>> getGreedyMaximalMatching(
            final EdgeType edgeType, final Block.RowContainer rowContainer,
            final ArrayList<Edge<ECOccurrenceNode, BindingNode>> shuffledEdges,
            final Function<Edge<ECOccurrenceNode, BindingNode>, ON> getOldNode,
            final Function<Edge<ECOccurrenceNode, BindingNode>, NN> getNewNode,
            final Partition<NN, ?, ?> newNodePartition) {
        // helper lookup map from node to row
        final Map<AssignmentGraphNode<?>, RowIdentifier> node2Row = new IdentityHashMap<>();
        Partition.SubSet<?, ?> subSet = null;
        // helper set of rows already in the matching
        final Set<RowIdentifier> rowsInMatching = Sets.newIdentityHashSet();
        // result container
        final ArrayList<Edge<ECOccurrenceNode, BindingNode>> maximalMatching = new ArrayList<>();
        for (final Edge<ECOccurrenceNode, BindingNode> edge : shuffledEdges) {
            final ON oldNode = getOldNode.apply(edge);
            // determine and cache block row of oldNode node or (if not part of the block) create a new row for it
            final RowIdentifier oldRow = node2Row.computeIfAbsent(oldNode,
                    x -> Optional.ofNullable(rowContainer.getRowIdentifier(oldNode)).orElseGet(RowIdentifier::new));
            if (rowsInMatching.contains(oldRow)) continue;
            final NN newNode = getNewNode.apply(edge);
            // same as above for newNode node
            final RowIdentifier newRow = node2Row.computeIfAbsent(newNode,
                    x -> Optional.ofNullable(rowContainer.getRowIdentifier(newNode)).orElseGet(RowIdentifier::new));
            if (rowsInMatching.contains(newRow)) continue;
            final Partition.SubSet<?, ?> found = edgeType.getNewNodeSubSet(newNodePartition, newNode);
            if (subSet == null) {
                subSet = found;
            } else if (subSet != found) {
                continue;
            }
            rowsInMatching.add(oldRow);
            rowsInMatching.add(newRow);
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
    public ImmutableMinimalSet<Column<ECOccurrenceNode, BindingNode>> getColumns() {
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
