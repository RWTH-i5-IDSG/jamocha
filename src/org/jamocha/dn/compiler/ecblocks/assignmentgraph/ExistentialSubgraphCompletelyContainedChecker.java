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
import org.jamocha.dn.compiler.ecblocks.ExistentialInfo;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.occurrence.FilterOccurrenceNode;
import org.jamocha.filter.ECFilter;

import java.util.Set;
import java.util.TreeMap;
import java.util.stream.IntStream;

import static org.jamocha.util.Lambdas.toIdentityHashSet;

/**
 * Checks whether an existential part of a rule (finalised by the filter given) is completely contained in a block.
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class ExistentialSubgraphCompletelyContainedChecker extends ConnectedComponentTraversal {
	boolean valid = true;

	private ExistentialSubgraphCompletelyContainedChecker(final AssignmentGraph assignmentGraph,
			final AssignmentGraph.UnrestrictedGraph.SubGraph subgraph) {
		super(assignmentGraph, assignmentGraph.getGraph(), subgraph);
	}

	public static boolean check(final AssignmentGraph assignmentGraph,
			final AssignmentGraph.UnrestrictedGraph.SubGraph subgraph, final ECFilter filter,
			final ExistentialInfo existentialInfo) {
		return new ExistentialSubgraphCompletelyContainedChecker(assignmentGraph, subgraph)
				.check(filter, existentialInfo);
	}

	public boolean check(final ECFilter filter, final ExistentialInfo existentialInfo) {
		final TreeMap<Integer, FilterOccurrenceNode> argumentMap =
				this.assignmentGraph.getFilterToOccurrenceNodes().get(filter);
		final int[] existentialArgumentPositions = existentialInfo.getExistentialArguments();
		final Set<FilterOccurrenceNode> existentialArguments =
				IntStream.of(existentialArgumentPositions).boxed().map(argumentMap::get).collect(toIdentityHashSet());
		// every node under the existential argument nodes is existential and has to be contained in the subgraph
		this.queue.addAll(existentialArguments);
		this.queued.addAll(existentialArguments);
		while (this.valid && !this.queue.isEmpty()) {
			final AssignmentGraphNode next = this.queue.pollFirst();
			next.accept(this);
		}
		return this.valid;
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
			if (!this.subgraph.containsEdge(edge)) {
				this.valid = false;
				return;
			}
			enqueueNode(otherNode);
		}
	}
}
