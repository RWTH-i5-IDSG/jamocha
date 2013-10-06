/*
 * Copyright 2002-2008 The Jamocha Team
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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import lombok.Getter;

import org.jamocha.dn.Network;
import org.jamocha.dn.memory.FactAddress;
import org.jamocha.dn.memory.MemoryHandlerTemp;
import org.jamocha.dn.memory.MemoryHandlerTerminal;
import org.jamocha.dn.memory.MemoryHandlerTerminal.Assert;
import org.jamocha.dn.memory.MemoryHandlerTerminal.Retract;
import org.jamocha.dn.nodes.Node.Edge;
import org.jamocha.filter.Filter;

/**
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 * 
 */
public class TerminalNode {

	protected class TerminalEdgeImpl implements Edge {
		protected final Network network;
		protected final Node sourceNode;
		protected final TerminalNode targetNode;
		protected Filter filter;

		private Map<? extends FactAddress, ? extends FactAddress> addressMap;
		private final LinkedList<MemoryHandlerTemp> tempMemories = new LinkedList<>();

		public TerminalEdgeImpl(final Network network, final Node sourceNode,
				final TerminalNode targetNode) {
			this.network = network;
			this.sourceNode = sourceNode;
			this.targetNode = targetNode;
		}

		@Override
		public void processPlusToken(final MemoryHandlerTemp memory)
				throws CouldNotAcquireLockException {
		}

		@Override
		public void processMinusToken(final MemoryHandlerTemp memory)
				throws CouldNotAcquireLockException {
		}

		@Override
		public FactAddress localizeAddress(FactAddress addressInParent) {
			assert addressMap.containsKey(addressInParent);
			return addressMap.get(addressInParent);
		}

		@Override
		public void setAddressMap(final Map<? extends FactAddress, ? extends FactAddress> map) {
			assert map != null;
			addressMap = map;
			for (final Entry<? extends FactAddress, ? extends FactAddress> entry : map.entrySet()) {
				targetNode.delocalizeMap.put(entry.getValue(),
						new AddressPredecessor(this, entry.getKey()));
			}
		}

		@Override
		public LinkedList<MemoryHandlerTemp> getTempMemories() {
			return this.tempMemories;
		}

		@Override
		public Node getSourceNode() {
			return this.sourceNode;
		}

		@Override
		public Node getTargetNode() {
			throw new UnsupportedOperationException(
					"Edges to terminal nodes don't have target 'nodes', as TerminalNode is not part of the Node hierarchy.");
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
		public void enqueuePlusMemory(final MemoryHandlerTemp mem) {
			this.targetNode.enqueueAssert(this.targetNode.getMemory().addPlusMemory(mem));
		}

		@Override
		public void enqueueMinusMemory(final MemoryHandlerTemp mem) {
			this.targetNode.enqueueRetract(this.targetNode.getMemory().addMinusMemory(mem));
		}

	}

	/**
	 * Returns the {@link MemoryHandlerTerminal terminal memory handler} of the node.
	 * 
	 * @return the {@link MemoryHandlerTerminal terminal memory handler} of the node
	 */
	@Getter
	protected MemoryHandlerTerminal memory;
	final protected Network network;
	final protected Node parent;
	final protected Map<FactAddress, AddressPredecessor> delocalizeMap = new HashMap<>();

	public TerminalNode(final Network network, final Node parent) {
		this.network = network;
		this.parent = parent;
		this.memory = parent.getMemory().newMemoryHandlerTerminal();
	}

	public void enqueueAssert(final Assert plus) {
		this.network.getConflictSet().addAssert(this, plus);
	}

	public void enqueueRetract(final Retract minus) {
		this.network.getConflictSet().addRetract(this, minus);
	}

	public MemoryHandlerTerminal flush() {
		final MemoryHandlerTerminal old = this.getMemory();
		this.memory = this.parent.getMemory().newMemoryHandlerTerminal();
		return old;
	}

}
