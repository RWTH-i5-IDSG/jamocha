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

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jamocha.dn.Network;
import org.jamocha.dn.memory.FactAddress;
import org.jamocha.dn.memory.MemoryHandlerMinusTemp;
import org.jamocha.dn.memory.MemoryHandlerPlusTemp;
import org.jamocha.dn.memory.MemoryHandlerTemp;
import org.jamocha.filter.AddressFilter;
import org.jamocha.filter.Path;
import org.jamocha.filter.PathFilter;

/**
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @author Kai Schwarz <kai.schwarz@rwth-aachen.de>
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 */

public class BetaNode extends Node {

	protected class BetaEdgeImpl extends EdgeImpl implements PositiveEdge {

		private Map<? extends FactAddress, ? extends FactAddress> addressMap;
		private final LinkedList<MemoryHandlerPlusTemp> tempMemories = new LinkedList<>();

		public BetaEdgeImpl(final Network network, final Node sourceNode, final Node targetNode,
				final AddressFilter filter) {
			super(network, sourceNode, targetNode, filter);
		}

		@Override
		public void processPlusToken(final MemoryHandlerPlusTemp memory)
				throws CouldNotAcquireLockException {
			final MemoryHandlerTemp mem =
					targetNode.memory.processTokenInBeta(memory, this, this.filter);
			if (mem.size() == 0) {
				return;
			}
			mem.enqueueInEdges(targetNode.outgoingPositiveEdges);
		}

		@Override
		public void processMinusToken(final MemoryHandlerMinusTemp memory)
				throws CouldNotAcquireLockException {
			final MemoryHandlerTemp mem =
					targetNode.memory.processTokenInBeta(memory, this, this.filter);
			if (mem.size() == 0) {
				return;
			}
			mem.enqueueInEdges(targetNode.outgoingPositiveEdges);
		}

		@Override
		public FactAddress localizeAddress(FactAddress addressInSource) {
			assert null != addressInSource;
			assert addressMap.containsKey(addressInSource);
			return addressMap.get(addressInSource);
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
		public LinkedList<MemoryHandlerPlusTemp> getTempMemories() {
			return this.tempMemories;
		}

	}

	public BetaNode(final Network network, final PathFilter filter) {
		super(network, filter);
	}

	@Override
	protected PositiveEdge newPositiveEdge(Node source) {
		return new BetaEdgeImpl(this.network, source, this, this.filter);
	}

	@Override
	public void shareNode(final Path... paths) {
		assert 0 < this.incomingEdges.length;
		// assertion below does not work as the address filter in the node does not contain paths
		// any more
		// assert PathCollector.newHashSet().collect(this.getFilter()).getPaths().size() ==
		// paths.length;
		final LinkedHashSet<Path> pathSet = new LinkedHashSet<>();
		for (final Path path : paths) {
			pathSet.add(path);
		}
		final boolean used[] = new boolean[this.incomingEdges.length];
		while (!pathSet.isEmpty()) {
			final Path path = pathSet.iterator().next();
			final Node currentlyLowestNode = path.getCurrentlyLowestNode();
			final Set<Path> joinedWith = path.getJoinedWith();
			int i;
			for (i = 0; i < this.incomingEdges.length; ++i) {
				final Edge edge = this.incomingEdges[i];
				if (edge.getSourceNode() != currentlyLowestNode || used[i] == true)
					continue;
				for (final Path join : joinedWith) {
					final FactAddress localizedAddress =
							edge.localizeAddress(join.getFactAddressInCurrentlyLowestNode());
					join.setCurrentlyLowestNode(this);
					join.setFactAddressInCurrentlyLowestNode(localizedAddress);
					pathSet.remove(join);
				}
				used[i] = true;
				break;
			}
			if (this.incomingEdges.length == i) {
				throw new Error("Tried to share a node with paths that do not match!");
			}
		}
		if (paths.length > 0)
			Path.setJoinedWithForAll(paths);
	}
}
