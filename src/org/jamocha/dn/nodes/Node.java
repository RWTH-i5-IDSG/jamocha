/*
 * Copyright 2002-2012 The Jamocha Team
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

package org.jamocha.dn.nodes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.jamocha.dn.Network;
import org.jamocha.dn.Token;
import org.jamocha.dn.memory.FactAddress;
import org.jamocha.dn.memory.MemoryHandlerMain;
import org.jamocha.dn.memory.MemoryHandlerMinusTemp;
import org.jamocha.dn.memory.MemoryHandlerPlusTemp;
import org.jamocha.dn.memory.MemoryHandlerTemp;
import org.jamocha.dn.memory.Template;
import org.jamocha.filter.AddressFilter;
import org.jamocha.filter.Path;
import org.jamocha.filter.PathFilter;
import org.jamocha.filter.visitor.FilterTranslator;
import org.jamocha.filter.visitor.PathCollector;

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
		 * Transforms an address valid for the source node of the input into the corresponding
		 * address valid for the target node of the input.
		 * 
		 * @param addressInSource
		 *            an address valid in the source node of the input
		 * @return an address valid in the target node of the input
		 */
		public FactAddress localizeAddress(final FactAddress addressInSource);

		/**
		 * Sets the map used for {@link Edge#localizeAddress(FactAddress)}
		 * 
		 * @param map
		 *            Map used for {@link Edge#localizeAddress(FactAddress)}
		 */
		public void setAddressMap(final Map<? extends FactAddress, ? extends FactAddress> map);

		/**
		 * Disconnects the nodeInput from the formerly connected nodes. This will remove the input
		 * from the target node inputs as well as from the source node children.
		 * 
		 */
		public void disconnect();

		public void setFilter(final AddressFilter filter);

		public AddressFilter getFilter();

		public LinkedList<MemoryHandlerPlusTemp> getTempMemories();

		public void enqueuePlusMemory(final MemoryHandlerPlusTemp mem);

		public void enqueueMinusMemory(final MemoryHandlerMinusTemp mem);
	}

	@AllArgsConstructor
	abstract protected class EdgeImpl implements Edge {
		protected final Network network;
		protected final Node sourceNode;
		protected final Node targetNode;
		protected AddressFilter filter;

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
		public void setFilter(final AddressFilter filter) {
			this.filter = filter;
		}

		@Override
		public AddressFilter getFilter() {
			return this.filter;
		}

		protected void newPlusToken(final MemoryHandlerTemp mem) {
			this.targetNode.enqueue(new Token.PlusToken(mem, this));
		}

		protected void newMinusToken(final MemoryHandlerTemp mem) {
			this.targetNode.enqueue(new Token.MinusToken(mem, this));
		}

	}

	abstract protected class PositiveEdgeImpl extends EdgeImpl implements PositiveEdge {
		public PositiveEdgeImpl(final Network network, final Node sourceNode,
				final Node targetNode, final AddressFilter filter) {
			super(network, sourceNode, targetNode, filter);
		}

		@Override
		public void enqueuePlusMemory(final MemoryHandlerPlusTemp mem) {
			this.targetNode.enqueue(new Token.PlusToken(mem, this));
		}

		@Override
		public void enqueueMinusMemory(final MemoryHandlerMinusTemp mem) {
			this.targetNode.enqueue(new Token.MinusToken(mem, this));
		}
	}

	abstract protected class NegativeEdgeImpl extends EdgeImpl implements NegativeEdge {
		public NegativeEdgeImpl(final Network network, final Node sourceNode,
				final Node targetNode, final AddressFilter filter) {
			super(network, sourceNode, targetNode, filter);
		}

		@Override
		public void enqueuePlusMemory(final MemoryHandlerPlusTemp mem) {
			this.targetNode.enqueue(new Token.MinusToken(mem, this));
		}

		@Override
		public void enqueueMinusMemory(final MemoryHandlerMinusTemp mem) {
			this.targetNode.enqueue(new Token.PlusToken(mem, this));
		}
	}

	@Getter
	final protected Edge[] incomingEdges;

	/**
	 * Returns a collection of the outgoing positive edges.
	 * 
	 * @return a collection of the outgoing positive edges
	 */
	@Getter
	final protected Collection<PositiveEdge> outgoingPositiveEdges = new LinkedList<>();

	/**
	 * Returns a collection of the outgoing negative edges.
	 * 
	 * @return a collection of the outgoing negative edges
	 */
	@Getter
	final protected Collection<NegativeEdge> outgoingNegativeEdges = new LinkedList<>();

	final protected Map<FactAddress, AddressPredecessor> delocalizeMap = new HashMap<>();

	/**
	 * Returns the {@link MemoryHandlerMain main memory handler} of the node.
	 * 
	 * @return the {@link MemoryHandlerMain main memory handler} of the node
	 */
	@Getter
	final protected MemoryHandlerMain memory;
	final protected Network network;
	final protected TokenQueue tokenQueue;

	/**
	 * Returns the filter that has originally been set to all inputs.
	 * 
	 * @return the filter that has originally been set to all inputs
	 */
	@Getter
	final protected AddressFilter filter;

	@RequiredArgsConstructor
	public class TokenQueue implements Runnable {
		final static int tokenQueueCapacity = Integer.MAX_VALUE;
		final Queue<Token> tokenQueue = new LinkedList<>();
		final Network network;

		synchronized public void enqueue(final Token token) {
			final boolean empty = this.tokenQueue.isEmpty();
			this.tokenQueue.add(token);
			if (empty) {
				this.network.getScheduler().enqueue(this);
			}
		}

		@Override
		public void run() {
			final Token token = this.tokenQueue.peek();
			assert null != token; // queue shouldn't have been in the work queue
			try {
				token.run();
				synchronized (this) {
					this.tokenQueue.remove();
					if (!this.tokenQueue.isEmpty()) {
						this.network.getScheduler().enqueue(this);
					}
				}
			} catch (final CouldNotAcquireLockException ex) {
				this.network.getScheduler().enqueue(this);
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
			this.incomingEdges[i] = this.connectPositiveParent(parents[i]);
		}
		this.memory = network.getMemoryFactory().newMemoryHandlerMain(this.incomingEdges);
		this.filter = AddressFilter.empty;
	}

	protected Node(final Network network, final Template template, final Path... paths) {
		this.network = network;
		this.tokenQueue = new TokenQueue(network);
		this.incomingEdges = new Edge[0];
		this.memory = network.getMemoryFactory().newMemoryHandlerMain(template, paths);
		this.filter = AddressFilter.empty;
	}

	public Node(final Network network, final PathFilter filter) {
		this.network = network;
		this.tokenQueue = new TokenQueue(network);
		final LinkedHashSet<Path> paths =
				PathCollector.newLinkedHashSet().collect(filter).getPaths();
		final Map<Edge, Set<Path>> edgesAndPaths = new HashMap<>();
		final ArrayList<Edge> edges = new ArrayList<>();
		final Set<Path> joinedPaths = new HashSet<>();
		while (!paths.isEmpty()) {
			// get next path
			final Path path = paths.iterator().next();
			// FIXME find out whether positive or negative edge is needed
			final boolean negated = path.equals(null);
			final Node clNode = path.getCurrentlyLowestNode();
			// create new edge from clNode to this
			final Edge edge;
			if (!negated) {
				// mark all paths as done
				final Set<Path> joinedWith = path.getJoinedWith();
				joinedPaths.addAll(joinedWith);
				paths.removeAll(joinedWith);
				edge = connectPositiveParent(clNode);
				edgesAndPaths.put(edge, joinedWith);
			} else {
				edge = connectNegativeParent(clNode);
			}
			edges.add(edge);
		}
		this.incomingEdges = edges.toArray(new Edge[edges.size()]);
		this.memory = network.getMemoryFactory().newMemoryHandlerMain(this.incomingEdges);
		// update all Paths from joinedWith to new addresses
		for (final Entry<Edge, Set<Path>> entry : edgesAndPaths.entrySet()) {
			final Edge edge = entry.getKey();
			final Set<Path> joinedWith = entry.getValue();
			for (final Path path : joinedWith) {
				final FactAddress factAddressInCurrentlyLowestNode =
						path.getFactAddressInCurrentlyLowestNode();
				path.setCurrentlyLowestNode(this);
				path.setFactAddressInCurrentlyLowestNode(edge
						.localizeAddress(factAddressInCurrentlyLowestNode));
				path.setJoinedWith(joinedPaths);
			}
		}
		this.filter = FilterTranslator.translate(filter);
		for (final Edge edge : this.incomingEdges) {
			edge.setFilter(this.filter);
		}
	}

	protected Edge connectPositiveParent(final Node parent) {
		final PositiveEdge edge = newPositiveEdge(parent);
		parent.acceptEdgeToChild(edge);
		return edge;
	}

	protected Edge connectNegativeParent(final Node parent) {
		final NegativeEdge edge = newNegativeEdge(parent);
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
	 * Called when a child is added. Defaults to adding the edge to the child to the list of
	 * outgoing edges.
	 * 
	 * @param edgeToChild
	 *            the edge to the child to be added
	 */
	protected void acceptEdgeToChild(final PositiveEdge edgeToChild) {
		this.outgoingPositiveEdges.add(edgeToChild);
	}

	/**
	 * Called when a child is added. Defaults to adding the edge to the child to the list of
	 * outgoing edges.
	 * 
	 * @param edgeToChild
	 *            the edge to the child to be added
	 */
	protected void acceptEdgeToChild(final NegativeEdge edgeToChild) {
		this.outgoingNegativeEdges.add(edgeToChild);
	}

	/**
	 * Called when a child is removed. Defaults to removing the edge to the child from the list of
	 * outgoing edges.
	 * 
	 * @param edgeToChild
	 *            the edge to the child to be removed
	 */
	protected void removeChild(final Edge edgeToChild) {
		this.outgoingPositiveEdges.remove(edgeToChild);
		this.outgoingNegativeEdges.remove(edgeToChild);
	}

	/**
	 * Creates a new NodeInput which will connect this node (as the input's target node) and the
	 * given source node (as its parent).
	 * 
	 * @param source
	 *            source node to connect to this node via a nodeInput to be constructed
	 * @return NodeInput connecting the given source node with this node
	 */
	abstract protected PositiveEdge newPositiveEdge(final Node source);

	/**
	 * Creates a new NodeInput which will connect this node (as the input's target node) and the
	 * given source node (as its parent).
	 * 
	 * @param source
	 *            source node to connect to this node via a nodeInput to be constructed
	 * @return NodeInput connecting the given source node with this node
	 */
	abstract protected NegativeEdge newNegativeEdge(final Node source);

	public int getNumberOfOutgoingEdges() {
		return this.outgoingPositiveEdges.size() + this.outgoingNegativeEdges.size();
	}

	/**
	 * Transforms an address valid for the target node of its inputs into the corresponding address
	 * valid for the source node of its input.
	 * 
	 * @param localFactAddress
	 *            an address valid in the current node
	 * @return an address valid in the parent node
	 */
	public AddressPredecessor delocalizeAddress(final FactAddress localFactAddress) {
		assert null != localFactAddress;
		assert null != this.delocalizeMap.get(localFactAddress);
		return this.delocalizeMap.get(localFactAddress);
	}

	private void enqueue(final Token token) {
		this.tokenQueue.enqueue(token);
	}

	/**
	 * Shared node with the paths given.
	 */
	public abstract void shareNode(final Path... paths);

}
