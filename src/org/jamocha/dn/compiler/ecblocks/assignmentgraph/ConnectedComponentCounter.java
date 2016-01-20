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
package org.jamocha.dn.compiler.ecblocks.assignmentgraph;


import com.google.common.base.Function;
import com.google.common.collect.Sets;

import java.util.Set;

/**
 * Counts the number of connected components of a subgraph.
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class ConnectedComponentCounter extends ConnectedComponentTraversal {
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
		allNodes.addAll(this.subgraph.bindingNodeSet());
		allNodes.addAll(this.subgraph.occurrenceNodeSet());
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
			final Function<AssignmentGraph.Edge, AssignmentGraphNode<?>> getOtherNode,
			final Function<T, Set<AssignmentGraph.Edge>> getEdges) {
		this.done.add(node);
		final Set<AssignmentGraph.Edge> edges = getEdges.apply(node);
		for (final AssignmentGraph.Edge edge : edges) {
			final AssignmentGraphNode<?> otherNode = getOtherNode.apply(edge);
			if (this.done.contains(otherNode)) continue;
			enqueueNode(otherNode);
		}
	}
}
