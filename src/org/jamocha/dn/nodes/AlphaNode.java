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

import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import org.jamocha.dn.Network;
import org.jamocha.dn.memory.FactAddress;
import org.jamocha.dn.memory.MemoryHandlerMinusTemp;
import org.jamocha.dn.memory.MemoryHandlerPlusTemp;
import org.jamocha.dn.memory.MemoryHandlerTemp;
import org.jamocha.dn.memory.Template;
import org.jamocha.filter.AddressFilter;
import org.jamocha.filter.Path;
import org.jamocha.filter.PathFilter;

/**
 * Alpha {@link Node} implementation.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @author Kai Schwarz <kai.schwarz@rwth-aachen.de>
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 */
public class AlphaNode extends Node {
	static final LinkedList<MemoryHandlerPlusTemp> empty = new LinkedList<>();

	/**
	 * {@link AlphaNode} {@link Edge} implementation.
	 * 
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 * @see AlphaNode
	 * @see Edge
	 */
	protected class AlphaEdgeImpl extends EdgeImpl {
		FactAddress addressInTarget = null;

		public AlphaEdgeImpl(final Node sourceNode, final Node targetNode,
				final AddressFilter filter) {
			super(sourceNode, targetNode, filter);
		}

		@Override
		public FactAddress localizeAddress(final FactAddress addressInSource) {
			assert null != this.addressInTarget;
			return this.addressInTarget;
		}

		@Override
		public void setAddressMap(final Map<? extends FactAddress, ? extends FactAddress> map) {
			assert map != null;
			assert map.size() == 1;
			final Entry<? extends FactAddress, ? extends FactAddress> entry =
					map.entrySet().iterator().next();
			this.addressInTarget = entry.getValue();
			this.targetNode.delocalizeMap.put(this.addressInTarget, new AddressPredecessor(this,
					entry.getKey()));
		}

		@Override
		public LinkedList<MemoryHandlerPlusTemp> getTempMemories() {
			assert empty.isEmpty();
			return empty;
		}

		@Override
		public void processPlusToken(final MemoryHandlerTemp memory)
				throws CouldNotAcquireLockException {
			final MemoryHandlerTemp mem =
					this.targetNode.memory.processTokenInAlpha(memory, this, this.filter);
			if (mem.size() == 0) {
				return;
			}
			for (final Edge edge : this.targetNode.outgoingEdges) {
				mem.enqueueInEdge(edge);
			}
		}

		@Override
		public void processMinusToken(final MemoryHandlerTemp memory)
				throws CouldNotAcquireLockException {
			final MemoryHandlerTemp mem =
					this.targetNode.memory.processTokenInAlpha(memory, this, this.filter);
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
			return false;
		}
	}

	protected AlphaNode(final Network network, final Template template, final Path... paths) {
		super(network, template, paths);
	}

	public AlphaNode(final Network network, final PathFilter filter) {
		super(network, filter);
	}

	@Override
	protected Edge newEdge(final Node source) {
		return new AlphaEdgeImpl(source, this, this.filter);
	}

	@Override
	public void shareNode(final Path... paths) {
		assert null != paths;
		assert 1 == paths.length;
		final Path path = paths[0];
		assert 1 == this.delocalizeMap.size();
		final Entry<FactAddress, AddressPredecessor> entry =
				this.delocalizeMap.entrySet().iterator().next();
		assert path.getFactAddressInCurrentlyLowestNode() == entry.getValue().getAddress();
		path.setCurrentlyLowestNode(this);
		path.setFactAddressInCurrentlyLowestNode(entry.getKey());
	}

}
