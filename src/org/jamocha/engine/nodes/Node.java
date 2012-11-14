/*
 * Copyright 2002-2008 The Jamocha Team
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

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * Base class for all node types
 */
public abstract class Node {

	public static interface NodeInput {
		public Message[] acceptPlusToken(final Token.PlusToken token);

		public Message[] acceptMinusToken(final Token.MinusToken token);

		public WeakReference<NodeInput> getWeakReference();

		public WeakReference<Node> getSourceNode();

		public WeakReference<Node> getTargetNode();
	}

	abstract protected class NodeInputImpl implements NodeInput {
		protected final WeakReference<NodeInput> weakReference = new WeakReference<NodeInput>(
				this);
		protected final WeakReference<Node> shelteringNode;
		protected final WeakReference<Node> parent;

		public NodeInputImpl(final WeakReference<Node> shelteringNode,
				final WeakReference<Node> parent) {
			this.shelteringNode = shelteringNode;
			this.parent = parent;
		}

		public WeakReference<NodeInput> getWeakReference() {
			return this.weakReference;
		}

		@Override
		public WeakReference<Node> getSourceNode() {
			return this.parent;
		}

		@Override
		public WeakReference<Node> getTargetNode() {
			return this.shelteringNode;
		}
	}

	final protected HashSet<NodeInput> inputs = new HashSet<NodeInput>();
	final protected Set<NodeInput> children = Collections
			.newSetFromMap(new WeakHashMap<NodeInput, Boolean>());
	final protected WeakReference<Node> weakReference = new WeakReference<Node>(
			this);
	final protected Memory memory;

	public Node(final Memory memory) {
		this.memory = memory;
	}

	/**
	 * Connects the parent node given to the input with the index given.
	 * 
	 * @param parent
	 *            parent node to connect
	 * @param index
	 *            index of the input to connect to
	 * @throws IndexOutOfBoundsException
	 *             if the index is out of range
	 * 
	 */
	final public WeakReference<NodeInput> connectTo(
			final WeakReference<Node> parent) {
		final NodeInputImpl input = newNodeInput(parent);
		this.inputs.add(input);
		return input.getWeakReference();
	}

	/**
	 * Disconnects the nodeInput from the formerly connected node. Hopefully the
	 * last strong reference to the input is lost after the call to this
	 * function and the NodeInput vanishes.
	 * 
	 * @param nodeInput
	 *            input to disconnect a node from
	 */
	final public void disconnect(final WeakReference<NodeInput> nodeInput) {
		this.inputs.remove(nodeInput.get());
	}

	abstract protected NodeInputImpl newNodeInput(
			final WeakReference<Node> parent);

	protected Set<NodeInput> getChildren() {
		return this.children;
	}

	public Memory getMemory() {
		return this.memory;
	}

	public void flushMemory() {
		this.memory.flush();
	}

}
