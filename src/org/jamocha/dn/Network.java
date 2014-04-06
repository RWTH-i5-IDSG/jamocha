/*
 * Copyright 2002-2013 The Jamocha Team
 * 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.jamocha.org/
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.jamocha.dn;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;

import lombok.Getter;

import org.jamocha.dn.memory.FactAddress;
import org.jamocha.dn.memory.MemoryFactory;
import org.jamocha.dn.memory.MemoryHandlerMain;
import org.jamocha.dn.memory.MemoryHandlerPlusTemp;
import org.jamocha.dn.nodes.AlphaNode;
import org.jamocha.dn.nodes.BetaNode;
import org.jamocha.dn.nodes.Edge;
import org.jamocha.dn.nodes.Node;
import org.jamocha.dn.nodes.RootNode;
import org.jamocha.dn.nodes.TerminalNode;
import org.jamocha.filter.AddressFilter;
import org.jamocha.filter.FactAddressCollector;
import org.jamocha.filter.FilterFunctionCompare;
import org.jamocha.filter.Path;
import org.jamocha.filter.PathCollector;
import org.jamocha.filter.PathFilter;

/**
 * The Network class encapsulates the central objects for {@link MemoryFactory} and
 * {@link Scheduler} which are required all over the whole discrimination network.
 * 
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * 
 * @see MemoryFactory
 * @see Scheduler
 * @see Node
 */

@Getter
public class Network {

	/**
	 * -- GETTER --
	 * 
	 * Gets the memoryFactory to generate the nodes {@link MemoryHandlerMain} and
	 * {@link MemoryHandlerPlusTemp}.
	 * 
	 * @return the networks memory Factory
	 */
	private final MemoryFactory memoryFactory;

	/**
	 * -- GETTER --
	 * 
	 * Gets the capacity of the token queues in all token processing {@link Node nodes}.
	 * 
	 * @return the capacity for token queues
	 */
	private final int tokenQueueCapacity;

	/**
	 * -- GETTER --
	 * 
	 * Gets the scheduler handling the dispatching of token processing to different threads.
	 * 
	 * @return the networks scheduler
	 */
	private final Scheduler scheduler;

	/**
	 * -- GETTER --
	 * 
	 * Gets the {@link RootNode} of the network.
	 * 
	 * @return the {@link RootNode} of the network
	 */
	private final RootNode rootNode;

	/**
	 * -- GETTER --
	 * 
	 * Gets the {@link ConflictSet conflict set}.
	 * 
	 * @return conflict set
	 */
	private final ConflictSet conflictSet = new ConflictSet();

	/**
	 * Creates a new network object.
	 * 
	 * @param memoryFactory
	 *            the {@link MemoryFactory} to use in the created network
	 * @param tokenQueueCapacity
	 *            the capacity of the token queues in all token processing {@link Node nodes}
	 * @param scheduler
	 *            the {@link Scheduler} to handle the dispatching of token processing
	 */
	public Network(final MemoryFactory memoryFactory, final int tokenQueueCapacity,
			final Scheduler scheduler) {
		this.memoryFactory = memoryFactory;
		this.tokenQueueCapacity = tokenQueueCapacity;
		this.scheduler = scheduler;
		this.rootNode = new RootNode();
	}

	/**
	 * Creates a new network object with the {@link org.jamocha.dn.memory.javaimpl default memory
	 * implementation}.
	 * 
	 * @param tokenQueueCapacity
	 *            the capacity of the token queues in all token processing {@link Node nodes}
	 * @param scheduler
	 *            the {@link Scheduler} to handle the dispatching of token processing
	 */
	public Network(final int tokenQueueCapacity, final Scheduler scheduler) {
		this(org.jamocha.dn.memory.javaimpl.MemoryFactory.getMemoryFactory(), tokenQueueCapacity,
				scheduler);
	}

	/**
	 * Creates a new network object with the {@link org.jamocha.dn.memory.javaimpl default memory
	 * implementation} and {@link ThreadPoolScheduler scheduler}.
	 */
	public Network() {
		this(org.jamocha.dn.memory.javaimpl.MemoryFactory.getMemoryFactory(), Integer.MAX_VALUE,
				new ThreadPoolScheduler(10));
	}

	/**
	 * Tries to find a node performing the same filtering as the given filter and calls
	 * {@link Node#shareNode(Path...)} or creates a new {@link Node} for the given
	 * {@link PathFilter filter}. Returns true iff a {@link Node} to share was found.
	 * 
	 * @param filter
	 *            {@link PathFilter} to find a corresponding {@link Node} for
	 * @return true iff a {@link Node} to share was found
	 * @throws IllegalArgumentException
	 *             thrown if one of the {@link Path}s in the {@link PathFilter} was not mapped to a
	 *             {@link Node}
	 */
	// TODO work on normalized version
	public boolean tryToShareNode(final PathFilter filter) throws IllegalArgumentException {
		final Path[] paths = PathCollector.newLinkedHashSet().collect(filter).getPathsArray();

		// collect the nodes of the paths
		final LinkedHashSet<Node> filterPathNodes = new LinkedHashSet<>();
		for (final Path path : paths) {
			final Node node = path.getCurrentlyLowestNode();
			if (null == node) {
				throw new IllegalArgumentException("Paths did not point to any nodes.");
			}
			filterPathNodes.add(node);
		}

		// collect all nodes which have edges to all of the paths nodes as candidates
		final LinkedHashSet<Node> candidates = identifyShareCandidates(filterPathNodes);

		// check candidates for possible node sharing
		candidateLoop: for (final Node candidate : candidates) {
			final AddressFilter candidateFilter = candidate.getFilter();

			// check if filter matches
			if (!FilterFunctionCompare.equals(filter, candidateFilter))
				continue candidateLoop;

			final FactAddress[] addressesInTarget =
					FactAddressCollector.newLinkedHashSet().collect(candidateFilter)
							.getAddressesArray();
			assert addressesInTarget.length == paths.length;
			for (int i = 0; i < addressesInTarget.length; ++i) {
				final FactAddress address = addressesInTarget[i];
				final Path path = paths[i];
				// de-localize address
				if (candidate.delocalizeAddress(address).getAddress() != path
						.getFactAddressInCurrentlyLowestNode())
					continue candidateLoop;
			}
			candidate.shareNode(paths);
			return true;
		}
		return false;
	}

	private LinkedHashSet<Node> identifyShareCandidates(final LinkedHashSet<Node> filterPathNodes) {
		final LinkedHashSet<Node> candidates = new LinkedHashSet<>();
		assert filterPathNodes.size() > 0;
		final Iterator<Node> filterPathNodesIterator = filterPathNodes.iterator();

		// add all children of the first node
		final Collection<Edge> firstNodesOutgoingPositiveEdges =
				filterPathNodesIterator.next().getOutgoingEdges();
		for (final Edge edge : firstNodesOutgoingPositiveEdges) {
			try {
				candidates.add(edge.getTargetNode());
			} catch (final UnsupportedOperationException e) {
				// triggered by terminal node, just don't add it
			}
		}

		// remove all nodes which aren't children of all other nodes
		while (filterPathNodesIterator.hasNext()) {
			final Node node = filterPathNodesIterator.next();
			final HashSet<Node> cutSet = new HashSet<>();
			for (final Edge edge : node.getOutgoingEdges()) {
				try {
					cutSet.add(edge.getTargetNode());
				} catch (final UnsupportedOperationException e) {
					// triggered by terminal node, just don't add it
				}
			}
			candidates.retainAll(cutSet);
		}
		return candidates;
	}

	/**
	 * Creates network nodes for one rule, consisting of the passed filters.
	 * 
	 * @param filters
	 *            list of filters in order of implementation in the network. Each filter is
	 *            implemented in a separate node. Node-Sharing is used if possible
	 * @return created TerminalNode for the constructed rule
	 */
	public TerminalNode buildRule(final PathFilter... filters) {
		final LinkedHashSet<Path> allPaths = new LinkedHashSet<>();
		{
			for (final PathFilter filter : filters) {
				final LinkedHashSet<Path> paths =
						PathCollector.newLinkedHashSet().collect(filter).getPaths();
				allPaths.addAll(paths);
			}
			final Path[] pathArray = allPaths.toArray(new Path[allPaths.size()]);
			this.rootNode.addPaths(this, pathArray);
		}
		for (final PathFilter filter : filters) {
			if (!tryToShareNode(filter))
				if (PathCollector.newLinkedHashSet().collect(filter).getPaths().size() == 1) {
					new AlphaNode(this, filter);
				} else {
					new BetaNode(this, filter);
				}
		}
		final Node lowestNode = allPaths.iterator().next().getCurrentlyLowestNode();
		return new TerminalNode(this, lowestNode);
	}

	/**
	 * A default network object with a basic setup, used for testing and other quick and dirty
	 * networks.
	 */
	public final static Network DEFAULTNETWORK = new Network(
			org.jamocha.dn.memory.javaimpl.MemoryFactory.getMemoryFactory(), Integer.MAX_VALUE,
			// new ThreadPoolScheduler(10)
			new PlainScheduler());

}