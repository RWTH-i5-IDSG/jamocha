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

package org.jamocha.engine.nodes;

import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import org.jamocha.engine.memory.FactAddress;
import org.jamocha.engine.memory.MemoryFactory;
import org.jamocha.engine.memory.MemoryHandler;
import org.jamocha.engine.memory.MemoryHandlerTemp;
import org.jamocha.filter.Filter;

/**
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @author Kai Schwarz <kai.schwarz@rwth-aachen.de>
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 */

public abstract class BetaNode extends Node {

	protected class BetaEdgeImpl extends EdgeImpl {

		private Map<? extends FactAddress, ? extends FactAddress> addressMap;
		private final LinkedList<MemoryHandlerTemp> tempMemories = new LinkedList<>();

		public BetaEdgeImpl(final Node sourceNode, final Node targetNode) {
			super(sourceNode, targetNode);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void processPlusToken(final MemoryHandler memory) {
			// TODO Auto-generated method stub

		}

		@Override
		public void processMinusToken(final MemoryHandler memory) {
			// TODO Auto-generated method stub

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

	public BetaNode(final MemoryFactory memoryFactory, final Filter filter) {
		super(memoryFactory, filter);
		// TODO Auto-generated constructor stub
	}

}
