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

package org.jamocha.dn.nodes;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jamocha.dn.Network;
import org.jamocha.dn.Token;
import org.jamocha.dn.memory.FactAddress;
import org.jamocha.dn.memory.MemoryHandlerTemp;
import org.jamocha.filter.Filter;
import org.jamocha.filter.Path;
import org.jamocha.filter.PathTransformation;

/**
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @author Kai Schwarz <kai.schwarz@rwth-aachen.de>
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 */

public class BetaNode extends Node {

	protected class BetaEdgeImpl extends EdgeImpl {

		private Map<? extends FactAddress, ? extends FactAddress> addressMap;
		private final LinkedList<MemoryHandlerTemp> tempMemories = new LinkedList<>();

		public BetaEdgeImpl(final Network network, final Node sourceNode,
				final Node targetNode) {
			super(network, sourceNode, targetNode);
		}

		@Override
		public void processPlusToken(final MemoryHandlerTemp memory)
				throws CouldNotAcquireLockException {
			final MemoryHandlerTemp mem = this.network.getMemoryFactory()
					.processTokenInBeta(targetNode.memory, memory, this,
							this.filter);
			for (final Edge edge : targetNode.outgoingEdges) {
				edge.getTargetNode().enqueue(new Token.PlusToken(mem, edge));
			}
		}

		@Override
		public void processMinusToken(final MemoryHandlerTemp memory)
				throws CouldNotAcquireLockException {
			// TODO process Minus Token in TerminalNodeEdge
		}

		@Override
		public FactAddress localizeAddress(FactAddress addressInParent) {
			assert addressMap.containsKey(addressInParent);
			return addressMap.get(addressInParent);
		}

		@Override
		public void setAddressMap(
				final Map<? extends FactAddress, ? extends FactAddress> map) {
			assert map != null;
			addressMap = map;
			for (final Entry<? extends FactAddress, ? extends FactAddress> entry : map
					.entrySet()) {
				targetNode.delocalizeMap.put(entry.getValue(),
						new AddressPredecessor(this, entry.getKey()));
			}
		}

		@Override
		public LinkedList<MemoryHandlerTemp> getTempMemories() {
			return this.tempMemories;
		}

	}

	public BetaNode(final Network network, final Filter filter) {
		super(network, filter);
	}

	@Override
	protected EdgeImpl newEdge(Node source) {
		return new BetaEdgeImpl(this.network, source, this);
	}

	@Override
	public void shareNode(final Path... paths) {
		assert 0 < this.delocalizeMap.size();
		assert 0 < this.incomingEdges.length;
		assert this.incomingEdges[0].getFilter().countParameters() == paths.length;
		final LinkedHashSet<Path> pathSet = new LinkedHashSet<>();
		for (final Path path : paths) {
			pathSet.add(path);
		}
		final boolean used[] = new boolean[this.incomingEdges.length];
		while (!pathSet.isEmpty()) {
			final Path path = pathSet.iterator().next();
			final Node currentlyLowestNode = PathTransformation
					.getCurrentlyLowestNode(path);
			final Set<Path> joinedWith = PathTransformation.getJoinedWith(path);
			for (int i = 0; i < this.incomingEdges.length; ++i) {
				final Edge edge = this.incomingEdges[i];
				if (edge.getSourceNode() != currentlyLowestNode
						|| used[i] == true)
					continue;
				for (final Path join : joinedWith) {
					final FactAddress localizedAddress = edge
							.localizeAddress(PathTransformation
									.getFactAddressInCurrentlyLowestNode(join));
					PathTransformation.setCurrentlyLowestNode(join, this);
					PathTransformation.setFactAddressInCurrentlyLowestNode(
							join, localizedAddress);
					PathTransformation.setJoinedWith(paths);
					pathSet.remove(join);
				}
				used[i] = true;
				break;
			}
		}
	}
}
