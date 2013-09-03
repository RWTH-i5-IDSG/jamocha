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

import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import org.jamocha.dn.Network;
import org.jamocha.dn.memory.FactAddress;
import org.jamocha.dn.memory.MemoryHandler;
import org.jamocha.dn.memory.MemoryHandlerTemp;
import org.jamocha.dn.memory.Template;
import org.jamocha.filter.Filter;
import org.jamocha.filter.Path;

/**
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @author Kai Schwarz <kai.schwarz@rwth-aachen.de>
 */
public class AlphaNode extends Node {

	protected class AlphaEdgeImpl extends EdgeImpl {
		FactAddress addressInSource = null;

		public AlphaEdgeImpl(final Node sourceNode, final Node targetNode) {
			super(sourceNode, targetNode);
		}

		@Override
		public void processPlusToken(final MemoryHandler memory)
				throws CouldNotAcquireLockException {
			// TODO Auto-generated method stub
		}

		@Override
		public void processMinusToken(final MemoryHandler memory)
				throws CouldNotAcquireLockException {
			// TODO Auto-generated method stub
		}

		@Override
		public FactAddress localizeAddress(final FactAddress addressInParent) {
			if (null != this.addressInSource)
				return this.addressInSource;
			return addressInParent;
		}

		@Override
		public void setAddressMap(
				final Map<? extends FactAddress, ? extends FactAddress> map) {
			assert map != null;
			assert map.size() == 1;
			final Entry<? extends FactAddress, ? extends FactAddress> entry = map
					.entrySet().iterator().next();
			this.addressInSource = entry.getValue();
			this.targetNode.delocalizeMap.put(this.addressInSource,
					new AddressPredecessor(this, entry.getKey()));
		}

		@Override
		public LinkedList<MemoryHandlerTemp> getTempMemories() {
			// TODO Auto-generated method stub
			return null;
		}

	}

	public AlphaNode(final Network network, final Template template,
			final Path... paths) {
		super(network, template, paths);
	}

	public AlphaNode(final Network network, final Filter filter) {
		super(network, filter);
	}

	@Override
	protected EdgeImpl newEdge(final Node source) {
		return new AlphaEdgeImpl(source, this);
	}

}
