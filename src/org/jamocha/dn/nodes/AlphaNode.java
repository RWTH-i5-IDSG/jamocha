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
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @author Kai Schwarz <kai.schwarz@rwth-aachen.de>
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 */
public class AlphaNode extends Node {
	static final LinkedList<MemoryHandlerPlusTemp> empty = new LinkedList<>();

	protected class AlphaEdgeImpl extends EdgeImpl implements PositiveEdge {
		FactAddress addressInSource = null;

		public AlphaEdgeImpl(final Network network, final Node sourceNode, final Node targetNode,
				final AddressFilter filter) {
			super(network, sourceNode, targetNode, filter);
		}

		@Override
		public void processPlusToken(final MemoryHandlerPlusTemp memory)
				throws CouldNotAcquireLockException {
			final MemoryHandlerTemp mem =
					targetNode.memory.processTokenInAlpha(memory, this, this.filter);
			if (mem.size() == 0) {
				return;
			}
			mem.enqueueInEdges(targetNode.outgoingPositiveEdges);
		}

		@Override
		public void processMinusToken(final MemoryHandlerMinusTemp memory)
				throws CouldNotAcquireLockException {
			final MemoryHandlerTemp mem =
					targetNode.memory.processTokenInAlpha(memory, this, this.filter);
			if (mem.size() == 0) {
				return;
			}
			mem.enqueueInEdges(targetNode.outgoingPositiveEdges);
		}

		@Override
		public FactAddress localizeAddress(final FactAddress addressInParent) {
			if (null != this.addressInSource)
				return this.addressInSource;
			return addressInParent;
		}

		@Override
		public void setAddressMap(final Map<? extends FactAddress, ? extends FactAddress> map) {
			assert map != null;
			assert map.size() == 1;
			final Entry<? extends FactAddress, ? extends FactAddress> entry =
					map.entrySet().iterator().next();
			this.addressInSource = entry.getValue();
			this.targetNode.delocalizeMap.put(this.addressInSource, new AddressPredecessor(this,
					entry.getKey()));
		}

		@Override
		public LinkedList<MemoryHandlerPlusTemp> getTempMemories() {
			return empty;
		}

	}

	protected AlphaNode(final Network network, final Template template, final Path... paths) {
		super(network, template, paths);
	}

	public AlphaNode(final Network network, final PathFilter filter) {
		super(network, filter);
	}

	@Override
	protected PositiveEdge newPositiveEdge(final Node source) {
		return new AlphaEdgeImpl(this.network, source, this, this.filter);
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
