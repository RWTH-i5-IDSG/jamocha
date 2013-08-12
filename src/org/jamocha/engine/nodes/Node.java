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
import java.util.HashSet;
import java.util.Set;

import lombok.RequiredArgsConstructor;

import org.jamocha.engine.memory.Memory;
import org.jamocha.engine.memory.Memory.DoubleMemoryHandler;
import org.jamocha.engine.memory.MemoryHandler;
import org.jamocha.engine.workingmemory.elements.Template;

/**
 * Base class for all node types
 */
public abstract class Node {

	public static interface NodeInput {
		public void processPlusToken(final MemoryHandler memory);

		public void processMinusToken(final MemoryHandler memory);

		public Node getSourceNode();

		public Node getTargetNode();

		public NetworkFactAddress localizeAddress(
				final NetworkFactAddress addressInParent);

		/**
		 * Disconnects the nodeInput from the formerly connected nodes. This
		 * will remove the input from the target node inputs as well as from the
		 * source node children.
		 * 
		 */
		public void disconnect();

		public void setFilter(final Filter filter);

		public Filter getFilter();
	}

	abstract protected class NodeInputImpl implements NodeInput {
		protected final Node targetNode;
		protected final Node sourceNode;
		protected Filter filter;

		public NodeInputImpl(final Node sourceNode, final Node targetNode) {
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
	}

	protected NodeInput[] inputs;
	final protected Set<NodeInput> children = new HashSet<>();
	final protected Template template;
	final protected Memory memoryModule;
	final protected MemoryHandler memory, tempMemory;

	@RequiredArgsConstructor
	static class NodeWithFilter {
		final Node node;
		final Filter filter;
	}

	public Node(final Template template, final Memory memoryModule,
			final NodeWithFilter[] parentsWithFilters) {
		this.template = template;
		this.memoryModule = memoryModule;
		final DoubleMemoryHandler dmh = memoryModule.getMemory(template);
		this.memory = dmh.getMemory();
		this.tempMemory = dmh.getTempMemory();

		connectNewParents(parentsWithFilters);
		setFilters(parentsWithFilters);
	}

	private void connectNewParents(final NodeWithFilter[] parentsWithFilters) {
		this.inputs = new NodeInput[parentsWithFilters.length];
		for (int i = 0; i < parentsWithFilters.length; ++i) {
			final NodeWithFilter nwf = parentsWithFilters[i];
			final NodeInput input = connectParent(nwf.node);
			inputs[i] = input;
		}
	}

	private void setFilters(final NodeWithFilter[] parentsWithFilters) {
		for (int i = 0; i < parentsWithFilters.length; ++i) {
			final NodeWithFilter nwf = parentsWithFilters[i];
			inputs[i].setFilter(nwf.filter);
		}
	}

	private NodeInput connectParent(final Node parent) {
		final NodeInput input = newNodeInput(parent);
		parent.acceptChild(input);
		return input;
	}

	/**
	 * Connects the current node to the parents given. Nodes can occur multiple
	 * times in <code>parents</code>.
	 * 
	 * @param parentsWithFilters
	 *            parent nodes to connect and their filter templates
	 */
	public void rebuild(final NodeWithFilter[] parentsWithFilters) {
		// FIXME acquire read lock on main memory of every old parent
		for (final NodeInput input : this.inputs) {
			input.disconnect();
		}
		this.memory.flush();
		this.tempMemory.flush();
		connectNewParents(parentsWithFilters);
		setFilters(parentsWithFilters);
		// FIXME release read lock on main memory of every old parent
	}

	/**
	 * Called when a child is added. Defaults to adding the child to the
	 * children.
	 * 
	 * @param child
	 *            the child to be added
	 */
	protected void acceptChild(final NodeInput child) {
		this.children.add(child);
	}

	/**
	 * Called when a child is removed. Defaults to removing the child from the
	 * children.
	 * 
	 * @param child
	 *            child to be removed
	 */
	protected void removeChild(final NodeInput child) {
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
	abstract protected NodeInputImpl newNodeInput(final Node source);

	/**
	 * Returns an unmodifiable set of the children.
	 * 
	 * @return an unmodifiable set of the children
	 */
	protected Set<NodeInput> getChildren() {
		return Collections.unmodifiableSet(this.children);
	}

	public void distributeTempFacts() {

	}

	public MemoryHandler getMemory() {
		return this.memory;
	}

}
