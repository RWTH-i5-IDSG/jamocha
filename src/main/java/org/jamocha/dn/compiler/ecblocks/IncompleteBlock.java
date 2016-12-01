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

import com.google.common.collect.*;
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
import org.jamocha.dn.memory.SlotAddress;
import org.jamocha.dn.memory.Template;
import org.jamocha.filter.ECFilter;
import org.jamocha.languages.common.SingleFactVariable;
import org.jamocha.util.Lambdas;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;
import static org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.indexed.IndexedIdentityHashSet
        .toIndexedIdentityHashSet;
import static org.jamocha.util.Lambdas.groupingIntoListOfLists;
import static org.jamocha.util.Lambdas.negate;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor
public class IncompleteBlock implements BlockInterface {
    public static final int EDGE_TYPE_THRESHOLD = 50;
    public static final int NUM_COLUMN_CHOOSING_TRIES = 100;
    public static final int NUM_EXTENSION_TRIES = 100;
    public static final int NUM_BINDING_TRIES = 100;

    private static final Iterable<? extends SlotAddress> NULL_SLOT_ADDRESS = Collections.singleton(null);

    final BlockInterface block;
    final IndexedIdentityHashSet<ECOccurrenceNode> unboundOccurrences;

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

    public Block randomExtension(final MaximalColumns maximalColumns, final RandomWrapper randomWrapper) {
        final Block.RowContainer rowContainer = this.block.getRowContainer();
        if (0 == rowContainer.getRowCount()) {
            // block is empty => we can't just choose an adjacent column
            for (int i = 0; i < NUM_COLUMN_CHOOSING_TRIES; ++i) {
                final Column<ECOccurrenceNode, BindingNode> randomColumn =
                        randomWrapper.choose(maximalColumns.getStartingColumns());
                final Block potentialBlock = binding(randomWrapper, maximalColumns, randomColumn);
                if (potentialBlock != null) {
                    return potentialBlock;
                }
            }
            return null;
        }
        final ArrayList<AssignmentGraph.UnrestrictedGraph.SubGraph> rows = new ArrayList<>(rowContainer.getRows());
        for (int i = 0; i < NUM_COLUMN_CHOOSING_TRIES; ++i) {
            final AssignmentGraph.UnrestrictedGraph.SubGraph randomRow = randomWrapper.choose(rows);
            final IndexedImmutableSet<BindingNode> bindingNodes = randomRow.bindingNodeSet();
            final BindingNode randomBindingNode = randomWrapper.choose(bindingNodes);
            final ArrayList<AssignmentGraph.Edge<ECOccurrenceNode, BindingNode>> edges =
                    randomRow.incomingEdgesOf(randomBindingNode).stream()
                            // throw away edges already contained in the block
                            .filter(negate(randomRow::containsEdge))
                            // only consider FE occurrence nodes already part of the block to allow binding them
                            // multiple times, but prevent adding FEs not used as a binding
                            .filter(e -> {
                                final ECOccurrenceNode occurrenceNode = e.getSource();
                                return occurrenceNode.getNodeType() != OccurrenceType.FUNCTIONAL_OCCURRENCE
                                        || rowContainer.containsNode(occurrenceNode);
                            }).collect(Collectors.toCollection(ArrayList::new));
            final AssignmentGraph.Edge<ECOccurrenceNode, BindingNode> randomEdge = randomWrapper.choose(edges);
            final Column<ECOccurrenceNode, BindingNode> column = maximalColumns.getColumn(randomEdge);

            final Block binding = binding(randomWrapper, maximalColumns, column);
            if (binding != null) {
                return binding;
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

        BindingPartition determineBindingPartition(final AssignmentGraph assignmentGraph,
                final Block.RowContainer rowContainer, final BindingPartition bindingPartition,
                final ArrayList<Edge<ECOccurrenceNode, BindingNode>> edges);

        OccurrencePartition determineOccurrencePartition(final AssignmentGraph assignmentGraph,
                final Block.RowContainer rowContainer, final OccurrencePartition occurrencePartition,
                final ArrayList<Edge<ECOccurrenceNode, BindingNode>> edges);

        IndexedIdentityHashSet<ECOccurrenceNode> determineUnboundOccurrences(final BlockInterface block,
                final IndexedIdentityHashSet<ECOccurrenceNode> previouslyUnboundOccurrences,
                final ArrayList<Edge<ECOccurrenceNode, BindingNode>> chosenEdges);

        default BindingPartition addSiblingsToBindingPartition(final AssignmentGraph assignmentGraph,
                final Block.RowContainer rowContainer, final BindingPartition bindingPartition,
                final ArrayList<Edge<ECOccurrenceNode, BindingNode>> edges,
                final SlotOrFactBindingNode someBindingNode) {
            final Template template = someBindingNode.getGroupingFactVariable().getTemplate();
            final Map<RowIdentifier, SingleFactVariable> map = edges.stream().collect(
                    toMap(edge -> rowContainer.getRowIdentifier(getNewNode(edge)),
                            edge -> ((SlotOrFactBindingNode) ((ImplicitOccurrenceNode) edge.getSource())
                                    .getCorrespondingBindingNode()).getGroupingFactVariable()));
            final IdentityHashMap<SingleFactVariable, IdentityHashMap<SlotAddress, SlotOrFactBindingNode>> fvToBN =
                    assignmentGraph.getTemplateInstanceToBindingNodes();
            return Lambdas.foldl((partition, slotAddress) -> partition.add(new BindingPartition.BindingSubSet(
                            new SimpleMinimalIdentityHashMap<>(
                                    Maps.transformValues(map, fv -> fvToBN.get(fv).get(slotAddress))))),
                    bindingPartition,
                    Iterables.concat(NULL_SLOT_ADDRESS, template.getSlotAddresses()));
        }

        default OccurrencePartition addSiblingsToOccurrencePartition(final AssignmentGraph assignmentGraph,
                final Block.RowContainer rowContainer, final OccurrencePartition occurrencePartition,
                final ArrayList<Edge<ECOccurrenceNode, BindingNode>> edges,
                final SlotOrFactBindingNode someBindingNode) {
            final Template template = someBindingNode.getGroupingFactVariable().getTemplate();
            final Map<RowIdentifier, SingleFactVariable> map = edges.stream().collect(
                    toMap(edge -> rowContainer.getRowIdentifier(getNewNode(edge)),
                            edge -> ((SlotOrFactBindingNode) ((ImplicitOccurrenceNode) edge.getSource())
                                    .getCorrespondingBindingNode()).getGroupingFactVariable()));
            final IdentityHashMap<SingleFactVariable, IdentityHashMap<SlotAddress, SlotOrFactBindingNode>> fvToBN =
                    assignmentGraph.getTemplateInstanceToBindingNodes();
            final IdentityHashMap<BindingNode, ImplicitOccurrenceNode> bnToImplicit =
                    assignmentGraph.getBindingNodeToImplicitOccurrence();
            return Lambdas.foldl((partition, slotAddress) -> partition.add(new OccurrencePartition.OccurrenceSubSet(
                            new SimpleMinimalIdentityHashMap<>(
                                    Maps.transformValues(map, fv -> bnToImplicit.get(fvToBN.get(fv).get(slotAddress))
                                    )))),
                    occurrencePartition, Iterables.concat(NULL_SLOT_ADDRESS, template.getSlotAddresses()));
        }

        static OccurrencePartition addFEOccurrences(final AssignmentGraph assignmentGraph,
                final Block.RowContainer rowContainer, final OccurrencePartition extendedPartition,
                final FunctionalExpressionBindingNode someCorrespondingBindingNode,
                final Iterable<FunctionalExpressionBindingNode> bindingNodeStream) {
            final IdentityHashMap<FunctionalExpressionBindingNode, TreeMap<Integer, FunctionalExpressionOccurrenceNode>>
                    feToOccs = assignmentGraph.getFunctionalExpressionBindingToOccurrenceNodes();
            final Set<Integer> parameterPositions = feToOccs.get(someCorrespondingBindingNode).keySet();
            final ImmutableMap<RowIdentifier, FunctionalExpressionBindingNode> map =
                    Maps.uniqueIndex(bindingNodeStream, rowContainer.wNode2Identifier::get);
            return Lambdas.foldl((partition, parameter) -> partition.add(new OccurrencePartition.OccurrenceSubSet(
                            new SimpleMinimalIdentityHashMap<>(
                                    Maps.transformValues(map, bn -> feToOccs.get(bn).get(parameter))))),
                    extendedPartition,
                    parameterPositions);
        }

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
                        final Edge<ECOccurrenceNode, BindingNode> someEdge = edges.iterator().next();
                        if (someEdge.getSource().getNodeType() != OccurrenceType.IMPLICIT_OCCURRENCE) {
                            return previousFactVariables;
                        }
                        if (((ImplicitOccurrenceNode) someEdge.getSource()).getCorrespondingBindingNode().getNodeType()
                                != BindingType.SLOT_OR_FACT_BINDING) {
                            return previousFactVariables;
                        }
                        return Sets.union(previousFactVariables, edges.stream()
                                .map(edge -> ((SlotOrFactBindingNode) ((ImplicitOccurrenceNode) edge.getSource())
                                        .getCorrespondingBindingNode()).getGroupingFactVariable()).collect(toSet()));
                    }

                    @Override
                    public BindingPartition determineBindingPartition(final AssignmentGraph assignmentGraph,
                            final Block.RowContainer rowContainer, final BindingPartition bindingPartition,
                            final ArrayList<Edge<ECOccurrenceNode, BindingNode>> edges) {
                        final Edge<ECOccurrenceNode, BindingNode> someEdge = edges.iterator().next();
                        final ECOccurrenceNode someSource = someEdge.getSource();
                        if (someSource.getNodeType() != OccurrenceType.IMPLICIT_OCCURRENCE) return bindingPartition;
                        final BindingNode someCorrespondingBindingNode =
                                ((ImplicitOccurrenceNode) someSource).getCorrespondingBindingNode();
                        if (someCorrespondingBindingNode.getNodeType() != BindingType.SLOT_OR_FACT_BINDING) {
                            final SimpleMinimalIdentityHashMap<RowIdentifier, BindingNode> map = edges.stream().collect(
                                    toMap(edge -> rowContainer.getRowIdentifier(getNewNode(edge)),
                                            edge -> ((ImplicitOccurrenceNode) edge.getSource())
                                                    .getCorrespondingBindingNode(), null,
                                            SimpleMinimalIdentityHashMap::new));
                            return bindingPartition.add(new BindingPartition.BindingSubSet(map));
                        }
                        // for slot / fact binding nodes, we have to add all siblings, too
                        return addSiblingsToBindingPartition(assignmentGraph, rowContainer, bindingPartition, edges,
                                ((SlotOrFactBindingNode) someCorrespondingBindingNode));
                    }

                    @Override
                    public OccurrencePartition determineOccurrencePartition(final AssignmentGraph assignmentGraph,
                            final Block.RowContainer rowContainer, final OccurrencePartition occurrencePartition,
                            final ArrayList<Edge<ECOccurrenceNode, BindingNode>> edges) {
                        // when adding type 1 edges we know that we produce new unbound bindings since only the
                        // occurrence node is already contained in the active part of the block
                        final SimpleMinimalIdentityHashMap<RowIdentifier, ECOccurrenceNode> newOccurrenceSubSet =
                                edges.stream().collect(
                                        toMap(edge -> rowContainer.getRowIdentifier(getNewNode(edge)), Edge::getSource,
                                                null, SimpleMinimalIdentityHashMap::new));
                        final OccurrencePartition extendedPartition =
                                occurrencePartition.add(new OccurrencePartition.OccurrenceSubSet(newOccurrenceSubSet));
                        final Edge<ECOccurrenceNode, BindingNode> someEdge = edges.iterator().next();
                        final ECOccurrenceNode someSource = someEdge.getSource();
                        if (someSource.getNodeType() != OccurrenceType.IMPLICIT_OCCURRENCE) return extendedPartition;
                        final BindingNode someCorrespondingBindingNode =
                                ((ImplicitOccurrenceNode) someSource).getCorrespondingBindingNode();
                        if (someCorrespondingBindingNode.getNodeType() == BindingType.FUNCTIONAL_EXPRESSION) {
                            final List<FunctionalExpressionBindingNode> bindingNodes = edges.stream()
                                    .map(edge -> ((FunctionalExpressionBindingNode) ((ImplicitOccurrenceNode) edge
                                            .getSource()).getCorrespondingBindingNode())).collect(Collectors.toList());
                            return addFEOccurrences(assignmentGraph, rowContainer, extendedPartition,
                                    ((FunctionalExpressionBindingNode) someCorrespondingBindingNode), bindingNodes);
                        }
                        if (someCorrespondingBindingNode.getNodeType() == BindingType.SLOT_OR_FACT_BINDING) {
                            return addSiblingsToOccurrencePartition(assignmentGraph, rowContainer, extendedPartition,
                                    edges, ((SlotOrFactBindingNode) someCorrespondingBindingNode));
                        }
                        return extendedPartition;
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
                        final Edge<ECOccurrenceNode, BindingNode> someEdge = chosenEdges.iterator().next();
                        final ECOccurrenceNode someSourceNode = someEdge.getSource();
                        switch (someSourceNode.getNodeType()) {
                        case IMPLICIT_OCCURRENCE: {
                            if (someEdge.getTarget().getNodeType() != BindingType.FUNCTIONAL_EXPRESSION) break;
                            final IdentityHashMap<FunctionalExpressionBindingNode, TreeMap<Integer,
                                    FunctionalExpressionOccurrenceNode>>
                                    functionalExpressionBindingToOccurrenceNodes =
                                    block.getGraph().getFunctionalExpressionBindingToOccurrenceNodes();
                            chosenEdges.stream().flatMap(edge -> functionalExpressionBindingToOccurrenceNodes
                                    .get(((FunctionalExpressionBindingNode) edge.getTarget())).values().stream())
                                    .forEach(unboundOccurrences::add);
                            break;
                        }
                        case FILTER_OCCURRENCE: {
                            final IdentityHashMap<ECFilter, TreeMap<Integer, FilterOccurrenceNode>>
                                    filterToOccurrenceNodes = block.getGraph().getFilterToOccurrenceNodes();
                            chosenEdges.stream().flatMap(
                                    e -> filterToOccurrenceNodes.get(((FilterOccurrenceNode) e.getSource()).getFilter())
                                            .values().stream()).forEach(unboundOccurrences::add);
                            break;
                        }
                        case FUNCTIONAL_OCCURRENCE: {
                            final IdentityHashMap<FunctionalExpressionBindingNode, TreeMap<Integer,
                                    FunctionalExpressionOccurrenceNode>>
                                    functionalExpressionBindingToOccurrenceNodes =
                                    block.getGraph().getFunctionalExpressionBindingToOccurrenceNodes();
                            chosenEdges.stream().flatMap(e -> functionalExpressionBindingToOccurrenceNodes
                                    .get(((FunctionalExpressionOccurrenceNode) e.getSource()).getGroupingBindingNode())
                                    .values().stream()).forEach(unboundOccurrences::add);
                            break;
                        }
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
                    public BindingPartition determineBindingPartition(final AssignmentGraph assignmentGraph,
                            final Block.RowContainer rowContainer, final BindingPartition bindingPartition,
                            final ArrayList<Edge<ECOccurrenceNode, BindingNode>> edges) {
                        final Edge<ECOccurrenceNode, BindingNode> someEdge = edges.iterator().next();
                        final BindingNode someBindingNode = someEdge.getTarget();
                        if (someBindingNode.getNodeType() != BindingType.SLOT_OR_FACT_BINDING) {
                            final SimpleMinimalIdentityHashMap<RowIdentifier, BindingNode> newBindingSubSet =
                                    edges.stream().collect(
                                            toMap(edge -> rowContainer.getRowIdentifier(getNewNode(edge)),
                                                    Edge::getTarget, null, SimpleMinimalIdentityHashMap::new));
                            return bindingPartition.add(new BindingPartition.BindingSubSet(newBindingSubSet));
                        }
                        // for slot / fact binding nodes, we have to add all siblings, too
                        return addSiblingsToBindingPartition(assignmentGraph, rowContainer, bindingPartition, edges,
                                (SlotOrFactBindingNode) someBindingNode);
                    }

                    @Override
                    public OccurrencePartition determineOccurrencePartition(final AssignmentGraph assignmentGraph,
                            final Block.RowContainer rowContainer, final OccurrencePartition occurrencePartition,
                            final ArrayList<Edge<ECOccurrenceNode, BindingNode>> edges) {
                        // if the implicit occurrences corresponding to the targets of the edges were not part of the
                        // block, they are currently being added to the block and they have to be included in the
                        // occurrence partition
                        final Edge<ECOccurrenceNode, BindingNode> someEdge = edges.iterator().next();
                        final BindingNode someTarget = someEdge.getTarget();
                        final IdentityHashMap<BindingNode, ImplicitOccurrenceNode> implicitLookup =
                                assignmentGraph.getBindingNodeToImplicitOccurrence();
                        final ImplicitOccurrenceNode correspondingImplicitON = implicitLookup.get(someTarget);
                        final OccurrencePartition.OccurrenceSubSet occurrenceSubSet =
                                occurrencePartition.lookup(correspondingImplicitON);
                        if (null != occurrenceSubSet) {
                            return occurrencePartition;
                        }
                        final SimpleMinimalIdentityHashMap<RowIdentifier, ECOccurrenceNode> map = edges.stream()
                                .collect(toMap(edge -> rowContainer.getRowIdentifier(getNewNode(edge)),
                                        edge -> implicitLookup.get(edge.getTarget()), null,
                                        SimpleMinimalIdentityHashMap::new));
                        final OccurrencePartition extendedPartition =
                                occurrencePartition.add(new OccurrencePartition.OccurrenceSubSet(map));
                        if (someTarget.getNodeType() == BindingType.FUNCTIONAL_EXPRESSION) {
                            final List<FunctionalExpressionBindingNode> feBindingNodes =
                                    edges.stream().map(e -> ((FunctionalExpressionBindingNode) e.getTarget()))
                                            .collect(toList());
                            return addFEOccurrences(assignmentGraph, rowContainer, extendedPartition,
                                    ((FunctionalExpressionBindingNode) someTarget), feBindingNodes);
                        }
                        if (someTarget.getNodeType() == BindingType.SLOT_OR_FACT_BINDING) {
                            return addSiblingsToOccurrencePartition(assignmentGraph, rowContainer, extendedPartition,
                                    edges, ((SlotOrFactBindingNode) someTarget));
                        }
                        return extendedPartition;
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
        if (edges.size() <= 1) {
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
            final ArrayList<Edge<ECOccurrenceNode, BindingNode>> chosenEdges =
                    getGreedyMaximalMatching(edgeType, oldRowContainer, random.shuffle(chosenPartition),
                            config::getOldNode, config::getNewNode, config.getNewNodePartition(this.block));
            if (chosenEdges.size() <= 1) {
                continue;
            }

            final Block.RowContainer adjustedRowContainer = oldRowContainer.addColumn(chosenEdges, config::getOldNode);
            final Block.RowContainer newRowContainer;

            final AssignmentGraph assignmentGraph = this.getGraph();
            {
                final Edge<ECOccurrenceNode, BindingNode> someEdge = chosenEdges.iterator().next();
                final ECOccurrenceNode source = someEdge.getSource();
                final BindingNode target = someEdge.getTarget();
                final AssignmentGraph.UnrestrictedGraph unrestrictedGraph = assignmentGraph.getGraph();
                final IdentityHashMap<BindingNode, ImplicitOccurrenceNode> implicitLookup =
                        assignmentGraph.getBindingNodeToImplicitOccurrence();
                if (source.getNodeType() == OccurrenceType.IMPLICIT_OCCURRENCE && !oldRowContainer
                        .containsNode(source)) {
                    // adding binding node to block (might add unbound occurrences in case of FE binding node)
                    final BindingNode someCorrespondingBindingNode =
                            ((ImplicitOccurrenceNode) source).getCorrespondingBindingNode();
                    if (someCorrespondingBindingNode.getNodeType() == BindingType.SLOT_OR_FACT_BINDING) {
                        // adding new template instance node to the block => we need to add all connected slot / fact
                        // binding nodes to the block => we need to add all their implicit occurrences to the block
                        final IdentityHashMap<SingleFactVariable, IdentityHashMap<SlotAddress, SlotOrFactBindingNode>>
                                fvToBNs = assignmentGraph.getTemplateInstanceToBindingNodes();
                        final Function<Edge<ECOccurrenceNode, BindingNode>, SlotOrFactBindingNode> bindingNodeGetter =
                                edge -> ((SlotOrFactBindingNode) ((ImplicitOccurrenceNode) edge.getSource())
                                        .getCorrespondingBindingNode());
                        final Map<RowIdentifier, List<Edge<ECOccurrenceNode, BindingNode>>> newEdgeMap =
                                getMapOfImplicitSiblingEdges(oldRowContainer, chosenEdges, unrestrictedGraph,
                                        implicitLookup, fvToBNs, bindingNodeGetter);
                        newRowContainer = adjustedRowContainer.addColumns(newEdgeMap);
                    } else {
                        // since target is part of the block and source wasn't, add the 'corresponding' edge for source
                        newRowContainer = adjustedRowContainer.addColumn(chosenEdges.stream()
                                .map(edge -> unrestrictedGraph.getEdge(edge.getSource(),
                                        ((ImplicitOccurrenceNode) edge.getSource()).getCorrespondingBindingNode()))
                                .collect(toList()), Edge::getSource);
                    }
                } else if (!oldRowContainer.containsNode(target)) {
                    // adding implicit occurrence node to block (might add unbound occurrences in case of FE binding
                    // node)
                    // since source is part of the block and target wasn't, add the 'corresponding' edge for source
                    if (target.getNodeType() == BindingType.SLOT_OR_FACT_BINDING) {
                        final IdentityHashMap<SingleFactVariable, IdentityHashMap<SlotAddress, SlotOrFactBindingNode>>
                                fvToBNs = assignmentGraph.getTemplateInstanceToBindingNodes();
                        final Function<Edge<ECOccurrenceNode, BindingNode>, SlotOrFactBindingNode> bindingNodeGetter =
                                edge -> ((SlotOrFactBindingNode) edge.getTarget());
                        final Map<RowIdentifier, List<Edge<ECOccurrenceNode, BindingNode>>> newEdgeMap =
                                getMapOfImplicitSiblingEdges(oldRowContainer, chosenEdges, unrestrictedGraph,
                                        implicitLookup, fvToBNs, bindingNodeGetter);
                        newRowContainer = adjustedRowContainer.addColumns(newEdgeMap);
                    } else {
                        newRowContainer = adjustedRowContainer.addColumn(chosenEdges.stream()
                                .map(edge -> unrestrictedGraph
                                        .getEdge(implicitLookup.get(edge.getTarget()), edge.getTarget()))
                                .collect(toList()), Edge::getTarget);
                    }
                } else {
                    newRowContainer = adjustedRowContainer;
                }
            }

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
                    edgeType.getBindingPartition(config, assignmentGraph, newRowContainer, adjustedBindingPartition,
                            chosenEdges);

            // reduce occurrence partition to relevant rows before extending them by columns
            final OccurrencePartition oldOccurrencePartition = this.block.getOccurrencePartition();
            final OccurrencePartition adjustedOccurrencePartition = oldOccurrencePartition.remove(removedRows);
            final OccurrencePartition newOccurrencePartition =
                    edgeType.getOccurrencePartition(config, assignmentGraph, newRowContainer,
                            adjustedOccurrencePartition, chosenEdges);

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

    private Map<RowIdentifier, List<Edge<ECOccurrenceNode, BindingNode>>> getMapOfImplicitSiblingEdges(
            final Block.RowContainer oldRowContainer, final ArrayList<Edge<ECOccurrenceNode, BindingNode>> chosenEdges,
            final AssignmentGraph.UnrestrictedGraph unrestrictedGraph,
            final IdentityHashMap<BindingNode, ImplicitOccurrenceNode> implicitLookup,
            final IdentityHashMap<SingleFactVariable, IdentityHashMap<SlotAddress, SlotOrFactBindingNode>> fvToBNs,
            final Function<Edge<ECOccurrenceNode, BindingNode>, SlotOrFactBindingNode> bindingNodeGetter) {
        return chosenEdges.stream().collect(toMap(edge -> oldRowContainer.wNode2Identifier.get(edge.getTarget()),
                edge -> fvToBNs.get(bindingNodeGetter.apply(edge).getGroupingFactVariable()).values().stream()
                        .map(bn -> unrestrictedGraph.getEdge(implicitLookup.get(bn), bn)).collect(toList())));
    }

    private enum EdgeType {
        TYPE1AND2 {
            @Override
            Set<SingleFactVariable> getFactVariables(final Configuration<?, ?, ?, ?> configuration,
                    final Set<SingleFactVariable> previousFactVariables,
                    final ArrayList<Edge<ECOccurrenceNode, BindingNode>> edges) {
                return configuration.getFactVariables(previousFactVariables, edges);
            }

            @Override
            BindingPartition getBindingPartition(final Configuration<?, ?, ?, ?> config,
                    final AssignmentGraph assignmentGraph, final Block.RowContainer rowContainer,
                    final BindingPartition bindingPartition,
                    final ArrayList<Edge<ECOccurrenceNode, BindingNode>> chosenEdges) {
                return config.determineBindingPartition(assignmentGraph, rowContainer, bindingPartition, chosenEdges);
            }

            @Override
            OccurrencePartition getOccurrencePartition(final Configuration<?, ?, ?, ?> config,
                    final AssignmentGraph assignmentGraph, final Block.RowContainer rowContainer,
                    final OccurrencePartition occurrencePartition,
                    final ArrayList<Edge<ECOccurrenceNode, BindingNode>> chosenEdges) {
                return config
                        .determineOccurrencePartition(assignmentGraph, rowContainer, occurrencePartition, chosenEdges);
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
            Set<SingleFactVariable> getFactVariables(final Configuration<?, ?, ?, ?> configuration,
                    final Set<SingleFactVariable> previousFactVariables,
                    final ArrayList<Edge<ECOccurrenceNode, BindingNode>> edges) {
                return previousFactVariables;
            }

            @Override
            BindingPartition getBindingPartition(final Configuration<?, ?, ?, ?> config,
                    final AssignmentGraph assignmentGraph, final Block.RowContainer rowContainer,
                    final BindingPartition bindingPartition,
                    final ArrayList<Edge<ECOccurrenceNode, BindingNode>> chosenEdges) {
                return bindingPartition;
            }

            @Override
            OccurrencePartition getOccurrencePartition(final Configuration<?, ?, ?, ?> config,
                    final AssignmentGraph assignmentGraph, final Block.RowContainer rowContainer,
                    final OccurrencePartition occurrencePartition,
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

        abstract Set<SingleFactVariable> getFactVariables(final Configuration<?, ?, ?, ?> configuration,
                final Set<SingleFactVariable> previousFactVariables,
                final ArrayList<Edge<ECOccurrenceNode, BindingNode>> edges);

        abstract BindingPartition getBindingPartition(final Configuration<?, ?, ?, ?> config,
                final AssignmentGraph assignmentGraph, final Block.RowContainer rowContainer,
                final BindingPartition bindingPartition,
                final ArrayList<Edge<ECOccurrenceNode, BindingNode>> chosenEdges);

        abstract OccurrencePartition getOccurrencePartition(final Configuration<?, ?, ?, ?> config,
                final AssignmentGraph assignmentGraph, final Block.RowContainer rowContainer,
                final OccurrencePartition occurrencePartition,
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
        return column.getEdges().stream().unordered().parallel().filter(edge -> {
            final RowIdentifier rowIdentifier = rowContainer.getRowIdentifier(config.getOldNode(edge));
            // throw away edges not connected to the block
            if (null == rowIdentifier) return false;
            final AssignmentGraph.UnrestrictedGraph.SubGraph subGraph =
                    rowContainer.row2Identifier.inverse().get(rowIdentifier);
            // throw away edges already contained in the block
            return !subGraph.containsEdge(edge);
        }).collect(groupingByConcurrent(edge -> {
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
