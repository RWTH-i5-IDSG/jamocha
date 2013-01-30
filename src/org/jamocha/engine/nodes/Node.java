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

/**
 * Base class for all node types
 */
public abstract class Node {

	public static interface NodeInput {
		public Message[] acceptPlusToken(final Token.PlusToken token);

		public Message[] acceptMinusToken(final Token.MinusToken token);

		public Node getSourceNode();

		public Node getTargetNode();

		public FactAddress localizeAddress(final FactAddress addressInParent);

		/**
		 * Disconnects the nodeInput from the formerly connected nodes. This
		 * will remove the input from the target node inputs as well as from the
		 * source node children.
		 * 
		 */
		public void disconnect();
	}

	protected class FactAddress {
		final int localIndex;
		final Node localNode;

		public FactAddress(final Node localNode, final int localIndex) {
			this.localNode = localNode;
			this.localIndex = localIndex;
		}
	}

	abstract protected class NodeInputImpl implements NodeInput {
		protected final Node targetNode;
		protected final Node sourceNode;

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
			this.targetNode.removeInput(this);
		}
	}

	final protected HashSet<NodeInput> inputs = new HashSet<>();
	final protected Set<NodeInput> children = new HashSet<>();
	final protected Memory memory;
	protected int factTupleCardinality = 0;

	public Node(final Memory memory) {
		this.memory = memory;
	}

	/**
	 * Connects the child node given to this node.
	 * 
	 * @param parent
	 *            parent node to connect
	 * @return the corresponding input
	 */
	final public NodeInput connectTo(final Node child) {
		final NodeInput input = child.createAndAddNodeInput(this);
		acceptChild(input);
		return input;
	}

	/**
	 * Creates a new NodeInput for the parent given, adds it to its inputs and
	 * returns the input created.
	 * 
	 * @param parent
	 *            parent node
	 * @return input created
	 */
	final private NodeInput createAndAddNodeInput(final Node parent) {
		final NodeInput input = newNodeInput(parent);
		this.inputs.add(input);
		return input;
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
	 * Called when an input is removed. Defaults to removing the input from the
	 * inputs.
	 * 
	 * @param input
	 *            node input to be removed
	 */
	protected void removeInput(final NodeInput input) {
		this.inputs.remove(input);
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

	public Memory getMemory() {
		return this.memory;
	}

	public void flushMemory() {
		this.memory.flush();
	}

}
