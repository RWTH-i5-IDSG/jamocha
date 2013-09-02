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

package org.jamocha.engine.nodes;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.jamocha.engine.memory.FactAddress;
import org.jamocha.engine.memory.MemoryFactory;
import org.jamocha.engine.memory.MemoryHandler;
import org.jamocha.engine.memory.MemoryHandlerMain;
import org.jamocha.engine.memory.MemoryHandlerTemp;
import org.jamocha.filter.Filter;
import org.jamocha.filter.Path;
import org.jamocha.filter.PathTransformation;
import org.jamocha.filter.PathTransformation.PathInfo;

/**
 * Base class for all node types
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public abstract class Node {

	public static interface Edge {
		public void processPlusToken(final MemoryHandler memory);

		public void processMinusToken(final MemoryHandler memory);

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
		public FactAddress localizeAddress(
				final FactAddress addressInParent);

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

		public FactAddress getMemoryFactAddress();

		public void setMemoryFactAddress(
				final FactAddress memoryFactAddress);
	}

	abstract protected class EdgeImpl implements Edge {
		protected final Node targetNode;
		protected final Node sourceNode;
		protected Filter filter;
		protected FactAddress memoryFactAddress;

		public EdgeImpl(final Node sourceNode, final Node targetNode) {
			this.targetNode = targetNode;
			this.sourceNode = sourceNode;
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

		@Override
		public FactAddress getMemoryFactAddress() {
			return this.memoryFactAddress;
		}

		@Override
		public void setMemoryFactAddress(
				final FactAddress memoryFactAddress) {
			this.memoryFactAddress = memoryFactAddress;
		}
	}

	protected Edge[] inputs;
	final protected Set<Edge> children = new HashSet<>();
	final protected MemoryHandlerMain memory;
	
	protected Node() {
		memory = null;
	}

	public Node(final MemoryFactory memoryFactory, final Filter filter) {
		final Set<Path> paths = filter.gatherPaths();
		final Map<Node, Integer> nodesUsed = new HashMap<>();
		while (!paths.isEmpty()) {
			// get next path
			final Path path = paths.iterator().next();
			final PathInfo pathInfo = PathTransformation.addressMapping
					.get(path);
			// mark all paths as done
			final Set<Path> joinedWith = pathInfo.getJoinedWith();
			paths.removeAll(joinedWith);
			final Node clNode = pathInfo.getCurrentlyLowestNode();
			// FIXME
			final Integer stored = nodesUsed.get(clNode);
			final Integer used = (stored == null ? 0 : stored);
			// final NetworkFactAddress output = clNode.getOutput(used);
			nodesUsed.put(clNode, used + 1);
			// create input
			final Edge nodeInput = connectParent(clNode);
			// nodeInput.setMemoryFactAddress(output.memoryFactAddressInTarget);

		}
		this.memory = memoryFactory.newMemoryHandlerMain(inputs);
	}

	private Edge connectParent(final Node parent) {
		final Edge input = newEdge(parent);
		parent.acceptChild(input);
		return input;
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
	 * Called when a child is added. Defaults to adding the child to the
	 * children.
	 * 
	 * @param child
	 *            the child to be added
	 */
	protected void acceptChild(final Edge child) {
		this.children.add(child);
	}

	/**
	 * Called when a child is removed. Defaults to removing the child from the
	 * children.
	 * 
	 * @param child
	 *            child to be removed
	 */
	protected void removeChild(final Edge child) {
		this.children.remove(child);
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
	 * Returns an unmodifiable set of the children.
	 * 
	 * @return an unmodifiable set of the children
	 */
	public Set<Edge> getChildren() {
		return Collections.unmodifiableSet(this.children);
	}

	/**
	 * Returns the list of the children.
	 * 
	 * @return the list of the children
	 */
	public Edge[] getInputs() {
		return this.inputs;
	}

	public void distributeTempFacts() {

	}

	public MemoryHandlerMain getMemory() {
		return this.memory;
	}

	public int numChildren() {
		return this.children.size();
	}

	/**
	 * Transforms an address valid for the target node of its inputs into the
	 * corresponding address valid for the source node of its input.
	 * 
	 * @param localMemoryFactAddress
	 *            an address valid in the current node
	 * @return an address valid in the parent node
	 */
	// TODO add the map: address-here -> address in parent
	public abstract AddressPredecessor delocalizeAddress(
			FactAddress localNetworkFactAddress);

}
