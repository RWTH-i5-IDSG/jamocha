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
package org.jamocha.dn.compiler.ecblocks.assignmentgraph;


import com.google.common.base.Function;
import com.google.common.collect.Sets;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.AssignmentGraphNode;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.binding.*;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.occurrence.ECOccurrenceNode;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.occurrence.FilterOccurrenceNode;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.occurrence.FunctionalExpressionOccurrenceNode;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.occurrence.ImplicitOccurrenceNode;
import org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.ImmutableMinimalSet;
import org.jamocha.languages.common.SingleFactVariable;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Set;

/**
 * Traverses the connected components of a graph in a breadth-first manner.
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class ConnectedComponentTraversal implements AssignmentGraphNodeVisitor {
    final AssignmentGraph assignmentGraph;
    final AssignmentGraph.Graph<?, ?, ?, ?, ?, ?, ?, ?> traversedGraph;
    final AssignmentGraph.UnrestrictedGraph.SubGraph subgraph;

    final Set<AssignmentGraphNode<?>> queued = Sets.newIdentityHashSet();
    final Set<AssignmentGraphNode<?>> done = Sets.newIdentityHashSet();
    final Deque<AssignmentGraphNode<?>> queue = new LinkedList<>();


    protected void enqueueNode(final AssignmentGraphNode<?> node) {
        if (this.queued.add(node)) this.queue.add(node);
    }

    protected abstract <T extends AssignmentGraphNode<?>> void handleNode(final T node,
            final Function<AssignmentGraph.Edge<ECOccurrenceNode, BindingNode>, AssignmentGraphNode<?>> getOtherNode,
            final Function<T, ImmutableMinimalSet<AssignmentGraph.Edge<ECOccurrenceNode, BindingNode>>> getEdges);

    protected void handleBindingNode(final BindingNode node) {
        handleNode(node, AssignmentGraph.Edge::getSource, this.traversedGraph::incomingEdgesOf);
    }

    protected void handleOccurrenceNode(final ECOccurrenceNode node) {
        handleNode(node, AssignmentGraph.Edge::getTarget, this.traversedGraph::outgoingEdgesOf);
    }

    protected void handleSlotOrFactBindingNode(final SlotOrFactBindingNode node) {
        final SingleFactVariable groupingFactVariable = node.getGroupingFactVariable();
        this.assignmentGraph.getTemplateInstanceToBindingNodes().get(groupingFactVariable).forEach(this::enqueueNode);
    }

    @Override
    public void visit(final ConstantBindingNode node) {
        handleBindingNode(node);
    }

    @Override
    public void visit(final FactBindingNode node) {
        handleBindingNode(node);
        handleSlotOrFactBindingNode(node);
    }

    @Override
    public void visit(final FunctionalExpressionBindingNode node) {
        handleBindingNode(node);
        this.assignmentGraph.getFunctionalExpressionBindingToOccurrenceNodes().get(node).values()
                .forEach(this::enqueueNode);
    }

    @Override
    public void visit(final SlotBindingNode node) {
        handleBindingNode(node);
        handleSlotOrFactBindingNode(node);
    }

    @Override
    public void visit(final FilterOccurrenceNode node) {
        handleOccurrenceNode(node);
        this.assignmentGraph.getFilterToOccurrenceNodes().get(node.getFilter()).values().forEach(this::enqueueNode);

    }

    @Override
    public void visit(final FunctionalExpressionOccurrenceNode node) {
        handleOccurrenceNode(node);
        this.assignmentGraph.getFunctionalExpressionBindingToOccurrenceNodes().get(node.getGroupingBindingNode())
                .values().forEach(this::enqueueNode);

    }

    @Override
    public void visit(final ImplicitOccurrenceNode node) {
        handleOccurrenceNode(node);
    }
}
