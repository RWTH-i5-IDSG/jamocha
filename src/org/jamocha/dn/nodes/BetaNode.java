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

import static org.jamocha.util.ToArray.toArray;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import org.jamocha.dn.Network;
import org.jamocha.dn.memory.FactAddress;
import org.jamocha.dn.memory.MemoryHandlerMinusTemp;
import org.jamocha.dn.memory.MemoryHandlerPlusTemp;
import org.jamocha.dn.memory.MemoryHandlerTemp;
import org.jamocha.filter.AddressNodeFilterSet;
import org.jamocha.filter.Path;
import org.jamocha.filter.PathNodeFilterSet;

/**
 * Beta {@link Node} implementation.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @author Kai Schwarz <kai.schwarz@rwth-aachen.de>
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 * @see Node
 */
public class BetaNode extends Node {

	/**
	 * {@link BetaNode Beta node} {@link Edge} implementation.
	 * 
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 * @see BetaNode
	 * @see Edge
	 */
	protected class BetaEdgeImpl extends EdgeImpl {
		private Map<? extends FactAddress, ? extends FactAddress> addressMap;
		private final LinkedList<MemoryHandlerPlusTemp> tempMemories = new LinkedList<>();

		public BetaEdgeImpl(final Node sourceNode, final Node targetNode, final AddressNodeFilterSet filter) {
			super(sourceNode, targetNode, filter);
		}

		@Override
		public FactAddress localizeAddress(final FactAddress addressInSource) {
			assert null != addressInSource;
			assert this.addressMap.containsKey(addressInSource);
			return this.addressMap.get(addressInSource);
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
		public void processPlusToken(final MemoryHandlerTemp memory) throws CouldNotAcquireLockException {
			final MemoryHandlerTemp mem = this.targetNode.memory.processTokenInBeta(memory, this, this.filter);
			if (mem.size() == 0) {
				return;
			}
			for (final Edge edge : this.targetNode.outgoingEdges) {
				mem.enqueueInEdge(edge);
			}
		}

		@Override
		public void processMinusToken(final MemoryHandlerTemp memory) throws CouldNotAcquireLockException {
			final MemoryHandlerTemp mem = this.targetNode.memory.processTokenInBeta(memory, this, this.filter);
			if (mem.size() == 0) {
				return;
			}
			for (final Edge edge : this.targetNode.outgoingEdges) {
				mem.enqueueInEdge(edge);
			}
		}

		@Override
		public void enqueueMemory(final MemoryHandlerPlusTemp mem) {
			newPlusToken(mem);
		}

		@Override
		public void enqueueMemory(final MemoryHandlerMinusTemp mem) {
			newMinusToken(mem);
		}

		@Override
		public boolean targetsBeta() {
			return true;
		}
	}

	public BetaNode(final Network network, final PathNodeFilterSet filter) {
		super(network, filter);
	}

	@Override
	protected Edge newEdge(final Node source) {
		return new BetaEdgeImpl(source, this, this.filter);
	}

	@Override
	public void shareNode(final PathNodeFilterSet filter, final Map<Path, FactAddress> map, final Path... paths) {
		assert 0 < this.incomingEdges.length;
		getPathNodeFilterSets().add(filter);
		final Path[] distinctPaths =
				toArray(Arrays.stream(paths).flatMap(p -> p.getJoinedWith().stream()).distinct(), Path[]::new);
		for (final Path path : distinctPaths) {
			final FactAddress factAddress = map.get(path);
			if (null == factAddress) {
				throw new Error("Missing FactAddress for a path in the given map!");
			}
			path.setFactAddressInCurrentlyLowestNode(factAddress);
			path.setCurrentlyLowestNode(this);
		}
		if (distinctPaths.length > 0)
			Path.setJoinedWithForAll(distinctPaths);
	}

	@Override
	public <V extends NodeVisitor> V accept(final V visitor) {
		visitor.visit(this);
		return visitor;
	}
}
