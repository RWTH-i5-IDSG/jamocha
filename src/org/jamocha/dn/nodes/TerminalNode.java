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

import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import org.jamocha.dn.Network;
import org.jamocha.dn.memory.FactAddress;
import org.jamocha.dn.memory.MemoryHandlerTemp;
import org.jamocha.filter.Path;

/**
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 * 
 */
public class TerminalNode extends BetaNode {

	protected class TerminalEdgeImpl extends EdgeImpl {

		private Map<? extends FactAddress, ? extends FactAddress> addressMap;
		private final LinkedList<MemoryHandlerTemp> tempMemories = new LinkedList<>();

		public TerminalEdgeImpl(final Network network, final Node sourceNode, final Node targetNode) {
			super(network, sourceNode, targetNode, null);
		}

		@Override
		public void processPlusToken(final MemoryHandlerTemp memory)
				throws CouldNotAcquireLockException {
			// TODO process Plus Token in TerminalNodeEdge
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

	}

	public TerminalNode(final Network network, Path... paths) {
		super(network, null);
		// FIXME TerminalNode constructor
	}

	@Override
	protected EdgeImpl newEdge(Node source) {
		return new TerminalEdgeImpl(network, source, this);
	}

	@Override
	protected void acceptEdgeToChild(final Edge edgeToChild) {
		throw new Error(
				"Terminal nodes are supposed to be the end of a network, so no nodes can be connected to them.");
	}

}
