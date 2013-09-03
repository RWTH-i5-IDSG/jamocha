/*
 * Copyright 2002-2012 The Jamocha Team
 * 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.jamocha.org/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package org.jamocha.dn.nodes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.jamocha.dn.Network;
import org.jamocha.dn.Token;
import org.jamocha.dn.memory.FactAddress;
import org.jamocha.dn.memory.MemoryHandlerMain;
import org.jamocha.dn.memory.MemoryHandlerTemp;
import org.jamocha.dn.memory.Template;
import org.jamocha.filter.Filter;
import org.jamocha.filter.Path;
import org.jamocha.filter.PathTransformation;
import org.jamocha.filter.PathTransformation.PathInfo;

/**
 * Base class for all node types
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @author Kai Schwarz <kai.schwarz@rwth-aachen.de>
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 */
public abstract class Node {

	public static interface Edge {
		public void processPlusToken(final MemoryHandlerTemp memory)
				throws CouldNotAcquireLockException;

		public void processMinusToken(final MemoryHandlerTemp memory)
				throws CouldNotAcquireLockException;

		public Node getSourceNode();

		public Node getTargetNode();

		/**
		 * Transforms an address valid for the source node of the input into the
		 * corresponding address valid for the target node of the input.
		 * 
		 * @param addressInParent
		 *            an address valid in the source node of the input
		 * @return an address valid in the target node of the input
		 */
		public FactAddress localizeAddress(final FactAddress addressInParent);

		/**
		 * Sets the map used for {@link Edge#localizeAddress(FactAddress)}
		 * 
		 * @param map
		 *            Map used for {@link Edge#localizeAddress(FactAddress)}
		 */
		public void setAddressMap(
				final Map<? extends FactAddress, ? extends FactAddress> map);

		/**
		 * Disconnects the nodeInput from the formerly connected nodes. This
		 * will remove the input from the target node inputs as well as from the
		 * source node children.
		 * 
		 */
		public void disconnect();

		public void setFilter(final Filter filter);

		public Filter getFilter();

		public LinkedList<MemoryHandlerTemp> getTempMemories();
	}

	@AllArgsConstructor
	abstract protected class EdgeImpl implements Edge {
		protected final Network network;
		protected final Node sourceNode;
		protected final Node targetNode;
		protected Filter filter;

		public EdgeImpl(final Network network, final Node sourceNode,
				final Node targetNode) {
			this(network, sourceNode, targetNode, null);
		}

		@Override
		public Node getSourceNode() {
			return this.sourceNode;
		}

		@Override
		public Node getTargetNode() {
			return this.targetNode;
		}

		@Override
		public void disconnect() {
			this.sourceNode.removeChild(this);
		}

		@Override
		public void setFilter(final Filter filter) {
			this.filter = filter;
		}

		@Override
		public Filter getFilter() {
			return this.filter;
		}
	}

	@Getter
	final protected Edge[] incomingEdges;
	final protected Queue<Edge> outgoingEdges = new LinkedList<>();
	// TODO is filled in BetaNode#BetaEdgeImpl#setAddressMap, has to be filled
	// in other children!
	final protected Map<FactAddress, AddressPredecessor> delocalizeMap = new HashMap<>();
	final protected MemoryHandlerMain memory;
	final protected Network network;
	final protected TokenQueue tokenQueue;

	@RequiredArgsConstructor
	public class TokenQueue implements Runnable {
		final static int tokenQueueCapacity = Integer.MAX_VALUE;
		final Queue<Token> tokenQueue = new LinkedList<>();
		final Network network;

		synchronized public void enqueue(final Token token) {
			final boolean empty = this.tokenQueue.isEmpty();
			this.tokenQueue.add(token);
			if (empty) {
				network.getScheduler().enqueue(this);
			}
		}

		@Override
		public void run() {
			final Token token = this.tokenQueue.peek();
			assert null != token; // queue shouldn't have been in the work queue
			boolean success = true;
			try {
				token.run();
			} catch (final CouldNotAcquireLockException ex) {
				success = false;
			}
			synchronized (this) {
				boolean moreThanOne = this.tokenQueue.size() > 1;
				this.tokenQueue.remove();
				if (!success) {
					enqueue(token);
					return;
				}
				if (!moreThanOne) {
					network.getScheduler().enqueue(this);
				}
			}
		}
	}

	/**
	 * Only for testing purposes.
	 */
	@Deprecated
	protected Node(final Network network, final Node... parents) {
		this.network = network;
		this.tokenQueue = new TokenQueue(network);
		this.incomingEdges = new Edge[parents.length];
		for (int i = 0; i < parents.length; i++) {
			this.incomingEdges[i] = this.connectParent(parents[i]);
		}
		this.memory = network.getMemoryFactory().newMemoryHandlerMain(
				incomingEdges);
	}

	protected Node(final Network network, final Template template,
			final Path... paths) {
		this.network = network;
		this.tokenQueue = new TokenQueue(network);
		this.incomingEdges = new Edge[0];
		this.memory = network.getMemoryFactory().newMemoryHandlerMain(template,
				paths);
	}

	public Node(final Network network, final Filter filter) {
		this.network = network;
		this.tokenQueue = new TokenQueue(network);
		final Set<Path> paths = filter.gatherPaths();
		final Map<Edge, Set<Path>> edgesAndPaths = new HashMap<>();
		final ArrayList<Edge> edges = new ArrayList<>();
		final Set<Path> joinedPaths = new HashSet<>();
		while (!paths.isEmpty()) {
			// get next path
			final Path path = paths.iterator().next();
			// mark all paths as done
			final Set<Path> joinedWith = PathTransformation.getJoinedWith(path);
			joinedPaths.addAll(joinedWith);
			paths.removeAll(joinedWith);
			final Node clNode = PathTransformation.getCurrentlyLowestNode(path);
			// create new edge from clNode to this
			final Edge edge = connectParent(clNode);
			edges.add(edge);
			edgesAndPaths.put(edge, joinedWith);
		}
		incomingEdges = edges.toArray(new Edge[edges.size()]);
		this.memory = network.getMemoryFactory().newMemoryHandlerMain(
				incomingEdges);
		// update all Paths from joinedWith to new addresses
		for (final Edge edge : edges) {
			final Set<Path> joinedWith = edgesAndPaths.get(edge);
			for (final Path path : joinedWith) {
				final FactAddress factAddressInCurrentlyLowestNode = PathTransformation
						.getFactAddressInCurrentlyLowestNode(path);
				PathTransformation
						.setPathInfo(
								path,
								new PathInfo(
										this,
										edge.localizeAddress(factAddressInCurrentlyLowestNode),
										joinedPaths));
			}
		}
		filter.translatePath();
	}

	protected Edge connectParent(final Node parent) {
		final Edge edge = newEdge(parent);
		parent.acceptEdgeToChild(edge);
		return edge;
	}

	// FIXME re-think, rewrite
	// public void rebuild(final Filter filter) {
	// TODO acquire read lock on main memory of every old parent
	// for (final NodeInput input : this.inputs) {
	// input.disconnect();
	// }
	// flush memory
	// connectNewParents(parentsWithFilters);
	// setFilters(parentsWithFilters);
	// TODO release read lock on main memory of every old parent
	// }

	/**
	 * Called when a child is added. Defaults to adding the edge to the child to
	 * the list of outgoing edges.
	 * 
	 * @param edgeToChild
	 *            the edge to the child to be added
	 */
	protected void acceptEdgeToChild(final Edge edgeToChild) {
		this.outgoingEdges.add(edgeToChild);
	}

	/**
	 * Called when a child is removed. Defaults to removing the edge to the
	 * child from the list of outgoing edges.
	 * 
	 * @param edgeToChild
	 *            the edge to the child to be removed
	 */
	protected void removeChild(final Edge edgeToChild) {
		this.outgoingEdges.remove(edgeToChild);
	}

	/**
	 * Creates a new NodeInput which will connect this node (as the input's
	 * target node) and the given source node (as its parent).
	 * 
	 * @param source
	 *            source node to connect to this node via a nodeInput to be
	 *            constructed
	 * @return NodeInput connecting the given source node with this node
	 */
	abstract protected EdgeImpl newEdge(final Node source);

	/**
	 * Returns an unmodifiable set of the outgoing edges.
	 * 
	 * @return an unmodifiable set of the outgoing edges
	 */
	public Queue<Edge> getOutgoingEdges() {
		return this.outgoingEdges;
	}

	public MemoryHandlerMain getMemory() {
		return this.memory;
	}

	public int numChildren() {
		return this.outgoingEdges.size();
	}

	/**
	 * Transforms an address valid for the target node of its inputs into the
	 * corresponding address valid for the source node of its input.
	 * 
	 * @param localMemoryFactAddress
	 *            an address valid in the current node
	 * @return an address valid in the parent node
	 */
	public AddressPredecessor delocalizeAddress(FactAddress localFactAddress) {
		return delocalizeMap.get(localFactAddress);
	}

	public void enqueue(final Token token) {
		this.tokenQueue.enqueue(token);
	}

}
