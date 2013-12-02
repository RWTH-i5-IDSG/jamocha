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

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import lombok.Getter;

import org.jamocha.dn.memory.FactAddress;
import org.jamocha.dn.memory.MemoryFactory;
import org.jamocha.dn.memory.MemoryHandlerMain;
import org.jamocha.dn.memory.MemoryHandlerPlusTemp;
import org.jamocha.dn.nodes.AlphaNode;
import org.jamocha.dn.nodes.BetaNode;
import org.jamocha.dn.nodes.Node;
import org.jamocha.dn.nodes.Node.Edge;
import org.jamocha.dn.nodes.RootNode;
import org.jamocha.dn.nodes.SlotInFactAddress;
import org.jamocha.dn.nodes.TerminalNode;
import org.jamocha.filter.Filter;
import org.jamocha.filter.Filter.FilterElement;
import org.jamocha.filter.Path;

/**
 * The Network class encapsulates the central objects for {@link MemoryFactory} and
 * {@link Scheduler} which are required all over the whole discrimination network.
 * 
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
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
	 * Creates an new network object.
	 * 
	 * @param memoryFactory
	 *            the {@link MemoryFactory} to use in the created network
	 * @param tokenQueueCapacity
	 *            the capacity of the token queues in all token processing {@link Node nodes}
	 * @param scheduler
	 *            the {@link Scheduler} to handle the dispatching of token processing
	 */
	public Network(final MemoryFactory memoryFactory, int tokenQueueCapacity,
			final Scheduler scheduler) {
		this.memoryFactory = memoryFactory;
		this.tokenQueueCapacity = tokenQueueCapacity;
		this.scheduler = scheduler;
		this.rootNode = new RootNode();
	}

	/**
	 * Creates an new network object with the {@link org.jamocha.dn.memory.javaimpl default memory
	 * implementation}.
	 * 
	 * @param tokenQueueCapacity
	 *            the capacity of the token queues in all token processing {@link Node nodes}
	 * @param scheduler
	 *            the {@link Scheduler} to handle the dispatching of token processing
	 */
	public Network(int tokenQueueCapacity, final Scheduler scheduler) {
		this(org.jamocha.dn.memory.javaimpl.MemoryFactory.getMemoryFactory(), tokenQueueCapacity,
				scheduler);
	}

	/**
	 * Creates an new network object with the {@link org.jamocha.dn.memory.javaimpl default memory
	 * implementation} and {@link ThreadPoolScheduler scheduler}.
	 */
	public Network() {
		this(org.jamocha.dn.memory.javaimpl.MemoryFactory.getMemoryFactory(), Integer.MAX_VALUE,
				new ThreadPoolScheduler(10));
	}

	private boolean tryToShareNode(Filter filter) throws IllegalArgumentException{ // TODO remove order dependencies
		
		final LinkedHashSet<Path> pathList = filter.gatherPaths();
		final Path[] paths = pathList.toArray(new Path[pathList.size()]);

		// collect the nodes of the paths
		LinkedHashSet<Node> nodes = new LinkedHashSet<>();
		for (Path path : paths) {
			Node node = path.getCurrentlyLowestNode();
			if (null == node) {
				throw new IllegalArgumentException("Paths did not point to any nodes.");
			}
			nodes.add(node);
		}

		// collect all nodes which have edges to all of the paths nodes as candidates
		final LinkedHashSet<Node> candidates = new LinkedHashSet<>();
		assert nodes.size() > 0;
		final Iterator<Node> i = nodes.iterator();
		Node node = i.next();

		for (Edge edge : node.getOutgoingPositiveEdges()) { // add all children of the first node
			candidates.add(edge.getTargetNode());
		}

		for (; i.hasNext(); node = i.next()) { // remove all nodes which aren't children of all
												// other nodes
			final HashSet<Node> cutSet = new HashSet<>();
			for (Edge edge : node.getOutgoingPositiveEdges()) {
				cutSet.add(edge.getTargetNode());
			}
			candidates.retainAll(cutSet);
		}

		// check candidates for possible node sharing
		candidateLoop: for (final Node candidate : candidates) {
			final Filter candidateFilter = candidate.getFilter();

			if (!candidateFilter.equalsInFunction(filter)) // check if filter matches
				continue candidateLoop;

			final FilterElement[] candidateFilterElements = candidateFilter.getFilterElements();
			final FilterElement[] filterElements = filter.getFilterElements();
			for (int j = 0; j < filterElements.length; j++) {
				final SlotInFactAddress[] addressesInTarget =
						candidateFilterElements[j].getAddressesInTarget();
				final LinkedList<Path> elementPathSet =
						filterElements[j].getFunction().gatherPaths(new LinkedList<Path>());
				final Path[] elementPaths = elementPathSet.toArray(new Path[elementPathSet.size()]);
				for (int k = 0; k < addressesInTarget.length; k++) {
					final FactAddress addressInSource =
							candidate.delocalizeAddress(addressesInTarget[k].getFactAddress())
									.getAddress();
					if (!addressInSource.equals(elementPaths[k]
							.getFactAddressInCurrentlyLowestNode()))
						continue candidateLoop;
				}
			}
			candidate.shareNode(paths);
			return true;
		}
		return false;
	}

	/**
	 * Creates network nodes for one rule, consisting of the passed filters. 
	 * 
	 * @param filters list of filters in order of implementation in the network. Each filter is implemented in a separate node. Node-Sharing is used if possible
	 * @return created TerminalNode for the constructed rule
	 */
	public TerminalNode buildRule(final Filter... filters) {
		final LinkedHashSet<Path> allPaths = new LinkedHashSet<>();
		{
			for (Filter filter : filters) {
				final LinkedHashSet<Path> paths = filter.gatherPaths();
				allPaths.addAll(paths);
			}
			final Path[] pathArray = allPaths.toArray(new Path[allPaths.size()]);
			this.rootNode.addPaths(this, pathArray);
		}
		for (Filter filter : filters) {	
			if (!tryToShareNode(filter))
				if (filter.gatherPaths().size() == 1) {
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