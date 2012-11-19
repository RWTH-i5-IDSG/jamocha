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

		public Node getSourceNode();

		public Node getTargetNode();
		
		public FactAddress getAddress(FactAddress add);
	}
	
	protected static class FactAddress {
		protected FactAddress() {
			
		}
	}

	abstract protected class NodeInputImpl implements NodeInput {
		protected final WeakReference<NodeInput> weakReference = new WeakReference<NodeInput>(
				this);
		protected final Node shelteringNode;
		protected final Node parent;

		public NodeInputImpl(
				final Node shelteringNode,
				final Node parent) {
			this.shelteringNode = shelteringNode;
			this.parent = parent;
		}

		@Override
		public Node getSourceNode() {
			return this.parent;
		}

		@Override
		public Node getTargetNode() {
			return this.shelteringNode;
		}
	}

	final protected HashSet<NodeInput> inputs = new HashSet<>();
	final protected Set<NodeInput> children = Collections
			.newSetFromMap(new WeakHashMap<NodeInput, Boolean>());
	final protected WeakReference<? extends Node> weakReference = new WeakReference<>(
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
	final public NodeInput connectTo(
			final Node parent) {
		final NodeInput input = newNodeInput(parent);
		this.inputs.add(input);
		parent.acceptChild(input);
		return input;
	}

	protected void acceptChild(NodeInput child) {
		this.children.add(child);
	}
	
	protected void removeChild(final NodeInput child) {
		this.children.remove(child);
	}

	/**
	 * Disconnects the nodeInput from the formerly connected node. Hopefully the
	 * last strong reference to the input is lost after the call to this
	 * function and the NodeInput vanishes.
	 * 
	 * @param nodeInput
	 *            input to disconnect a node from
	 */
	final public void disconnect(final NodeInput nodeInput) {
		this.inputs.remove(nodeInput);
		nodeInput.getSourceNode().removeChild(nodeInput);
	}

	abstract protected NodeInputImpl newNodeInput(
			final Node parent);

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
