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

import org.jamocha.dn.ConflictSet;
import org.jamocha.dn.ConstructCache.Defrule;
import org.jamocha.dn.Network;
import org.jamocha.dn.memory.FactAddress;
import org.jamocha.dn.memory.MemoryHandlerMain;
import org.jamocha.dn.memory.MemoryHandlerMinusTemp;
import org.jamocha.dn.memory.MemoryHandlerPlusTemp;
import org.jamocha.dn.memory.MemoryHandlerTemp;
import org.jamocha.dn.memory.MemoryHandlerTerminal;
import org.jamocha.dn.memory.MemoryHandlerTerminal.Assert;
import org.jamocha.dn.memory.MemoryHandlerTerminal.Retract;
import org.jamocha.filter.AddressNodeFilterSet;
import org.jamocha.filter.AddressNodeFilterSet.AddressFilter;

/**
 * Terminal node implementation (not part of the {@link Node} type hierarchy).
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 */
public class TerminalNode {

	/**
	 * {@link TerminalNode} {@link Edge} implementation.
	 * 
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 */
	protected class TerminalEdgeImpl implements Edge {
		protected final Node sourceNode;
		protected final TerminalNode targetNode;
		protected AddressNodeFilterSet filter;
		protected AddressFilter[] filterParts;

		private Map<? extends FactAddress, ? extends FactAddress> addressMap;
		private final LinkedList<MemoryHandlerPlusTemp> tempMemories = new LinkedList<>();

		public TerminalEdgeImpl(final Node sourceNode, final TerminalNode targetNode) {
			this.sourceNode = sourceNode;
			this.targetNode = targetNode;
		}

		@Override
		public void processPlusToken(final MemoryHandlerTemp memory) throws CouldNotAcquireLockException {
		}

		@Override
		public void processMinusToken(final MemoryHandlerTemp memory) throws CouldNotAcquireLockException {
		}

		@Override
		public FactAddress localizeAddress(final FactAddress addressInParent) {
			assert this.addressMap.containsKey(addressInParent);
			return this.addressMap.get(addressInParent);
		}

		@Override
		public void setAddressMap(final Map<? extends FactAddress, ? extends FactAddress> map) {
			assert map != null;
			this.addressMap = map;
			for (final Entry<? extends FactAddress, ? extends FactAddress> entry : map.entrySet()) {
				this.targetNode.delocalizeMap.put(entry.getValue(), new AddressPredecessor(this, entry.getKey()));
			}
		}

		@Override
		public LinkedList<MemoryHandlerPlusTemp> getTempMemories() {
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
		public void setFilter(final AddressNodeFilterSet filter) {
			this.filter = filter;
			this.filterParts = this.sourceNode.memory.getRelevantExistentialFilterParts(filter, this);
		}

		@Override
		public AddressNodeFilterSet getFilter() {
			return this.filter;
		}

		@Override
		public AddressFilter[] getFilterPartsForCounterColumns() {
			return this.filterParts;
		}

		@Override
		public void enqueueMemory(final MemoryHandlerPlusTemp mem) {
			if (0 == mem.size())
				return;
			this.targetNode.getMemory().addPlusMemory(this.targetNode, mem);
			mem.releaseLock();
		}

		@Override
		public void enqueueMemory(final MemoryHandlerMinusTemp mem) {
			if (0 == mem.size())
				return;
			this.targetNode.getMemory().addMinusMemory(this.targetNode, mem);
			mem.releaseLock();
		}

		@Override
		public boolean targetsBeta() {
			return false;
		}
	}

	/**
	 * Returns the {@link MemoryHandlerTerminal terminal memory handler} of the node.
	 * 
	 * @return the {@link MemoryHandlerTerminal terminal memory handler} of the node
	 */
	@Getter
	final protected MemoryHandlerTerminal memory;
	final protected Network network;
	/**
	 * Returns the single incoming {@link Edge}.
	 * 
	 * @return the single incoming {@link Edge}
	 */
	@Getter
	final protected Edge edge;
	final protected Map<FactAddress, AddressPredecessor> delocalizeMap = new HashMap<>();
	/**
	 * Returns the rule corresponding to the {@link TerminalNode}.
	 * 
	 * @return the rule corresponding to the {@link TerminalNode}
	 */
	@Getter
	final protected Defrule.Translated rule;

	public TerminalNode(final Network network, final Node parent, final Defrule.PathRule pathRule) {
		this.network = network;
		final MemoryHandlerMain parentMemory = parent.getMemory();
		this.memory = parentMemory.newMemoryHandlerTerminal();
		this.rule = pathRule.translatePathToAddress();
		this.edge = new TerminalEdgeImpl(parent, this);
		parent.deactivateTokenQueue();
		parent.acceptRegularEdgeToChild(edge);
		parentMemory.newNewNodeToken().enqueueInEdge(edge);
		parent.activateTokenQueue();
	}

	/**
	 * Passes this {@link Assert} to the {@link ConflictSet}.
	 * 
	 * @param plus
	 *            the {@link Assert} to be passed to the {@link ConflictSet}
	 */
	public void enqueueAssert(final Assert plus) {
		this.network.getConflictSet().addAssert(this, plus);
	}

	/**
	 * Passes this {@link Retract} to the {@link ConflictSet}.
	 * 
	 * @param minus
	 *            the {@link Retract} to be passed to the {@link ConflictSet}
	 */
	public void enqueueRetract(final Retract minus) {
		this.network.getConflictSet().addRetract(this, minus);
	}

	/**
	 * Calls {@link MemoryHandlerTerminal#flush()} on the memory of this node.
	 */
	public void flush() {
		this.memory.flush();
	}
}
