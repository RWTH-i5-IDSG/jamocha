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
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.AssignmentGraphNode;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.binding.BindingNode;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.occurrence.ECOccurrenceNode;
import org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.ImmutableMinimalSet;

import java.util.Set;

/**
 * Counts the number of connected components of a subgraph.
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public final class ConnectedComponentCounter extends ConnectedComponentTraversal {
    int connectedComponents = 0;

    private ConnectedComponentCounter(final AssignmentGraph assignmentGraph,
            final AssignmentGraph.UnrestrictedGraph.SubGraph subgraph) {
        super(assignmentGraph, subgraph, subgraph);
    }

    public static int countConnectedComponents(final AssignmentGraph assignmentGraph,
            final AssignmentGraph.UnrestrictedGraph.SubGraph subgraph) {
        return new ConnectedComponentCounter(assignmentGraph, subgraph).countConnectedComponents();
    }

    public int countConnectedComponents() {
        final Set<AssignmentGraphNode<?>> allNodes = Sets.newIdentityHashSet();
        this.subgraph.bindingNodeSet().forEach(allNodes::add);
        this.subgraph.occurrenceNodeSet().forEach(allNodes::add);
        while (!allNodes.isEmpty()) {
            final AssignmentGraphNode<?> node = allNodes.iterator().next();
            this.queued.add(node);
            this.queue.add(node);
            node.accept(this);
            allNodes.removeAll(this.done);
            this.connectedComponents++;
        }
        return this.connectedComponents;
    }

    @Override
    protected <T extends AssignmentGraphNode<?>> void handleNode(final T node,
            final Function<AssignmentGraph.Edge<ECOccurrenceNode, BindingNode>, AssignmentGraphNode<?>> getOtherNode,
            final Function<T, ImmutableMinimalSet<AssignmentGraph.Edge<ECOccurrenceNode, BindingNode>>> getEdges) {
        this.done.add(node);
        final ImmutableMinimalSet<AssignmentGraph.Edge<ECOccurrenceNode, BindingNode>> edges = getEdges.apply(node);
        for (final AssignmentGraph.Edge<ECOccurrenceNode, BindingNode> edge : edges) {
            final AssignmentGraphNode<?> otherNode = getOtherNode.apply(edge);
            if (this.done.contains(otherNode)) continue;
            enqueueNode(otherNode);
        }
    }
}
