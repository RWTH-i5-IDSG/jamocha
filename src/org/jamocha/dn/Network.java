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

import java.util.Iterator;
import java.util.LinkedHashSet;

import lombok.Getter;

import org.jamocha.dn.memory.MemoryFactory;
import org.jamocha.dn.memory.MemoryHandlerMain;
import org.jamocha.dn.memory.MemoryHandlerTemp;
import org.jamocha.dn.nodes.AlphaNode;
import org.jamocha.dn.nodes.BetaNode;
import org.jamocha.dn.nodes.Node;
import org.jamocha.dn.nodes.Node.Edge;
import org.jamocha.dn.nodes.RootNode;
import org.jamocha.dn.nodes.TerminalNode;
import org.jamocha.filter.Filter;
import org.jamocha.filter.Path;
import org.jamocha.filter.PathTransformation;

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
	 * {@link MemoryHandlerTemp}.
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
	
	private boolean tryToShareNode(Filter filter, Path... paths) {
		// TODO check if node for sharing is available
		LinkedHashSet<Node> nodes = new LinkedHashSet<>();
		for (Path path : paths) {
			nodes.add(PathTransformation.getCurrentlyLowestNode(path));
		}
		final LinkedHashSet<Node> candidates = new LinkedHashSet<>();
		assert nodes.size() > 0;
		Iterator<Node> i = nodes.iterator();
		Node node = i.next();
		for (Edge edge : node.getOutgoingEdges()) {
			candidates.add(edge.getTargetNode());
		}
		for (;i.hasNext(); node = i.next()) {
			final LinkedHashSet<Node> cutSet = new LinkedHashSet<>();
			for (Edge edge : node.getOutgoingEdges()) {
				cutSet.add(edge.getTargetNode());
			}
			candidates.retainAll(cutSet);
		}
		for (Node candidate : candidates) {
			candidate.getFilter() // FIXME as soon as filters are used and saved by Nodes 
		}
		return false;
	}
	
	public TerminalNode buildRule(Filter... filters) {
		final LinkedHashSet<Path> allPaths = new LinkedHashSet<>();
		for (Filter filter : filters) {
			final LinkedHashSet<Path> paths = filter.gatherPaths();
			allPaths.addAll(paths);
			final Path[] pathArray = paths.toArray(new Path[paths.size()]);
			this.rootNode.addPaths(this, pathArray);
			if (!tryToShareNode(filter, pathArray))
				if (paths.size() == 1) {
					new AlphaNode(this, filter);
				} else {
					new BetaNode(this, filter);
				}
		}
		return new TerminalNode(this, allPaths.toArray(new Path[allPaths.size()]));
	}

	/**
	 * A default network object with a basic setup, used for testing and other quick and dirty
	 * networks.
	 */
	public final static Network DEFAULTNETWORK = new Network(
			org.jamocha.dn.memory.javaimpl.MemoryFactory.getMemoryFactory(), Integer.MAX_VALUE,
			new ThreadPoolScheduler(10));

}