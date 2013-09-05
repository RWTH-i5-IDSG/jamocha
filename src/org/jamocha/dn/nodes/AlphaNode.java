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
import org.jamocha.dn.Token;
import org.jamocha.dn.memory.FactAddress;
import org.jamocha.dn.memory.MemoryHandlerTemp;
import org.jamocha.dn.memory.Template;
import org.jamocha.filter.Filter;
import org.jamocha.filter.Path;
import org.jamocha.filter.PathTransformation;

/**
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @author Kai Schwarz <kai.schwarz@rwth-aachen.de>
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 */
public class AlphaNode extends Node {

	protected class AlphaEdgeImpl extends EdgeImpl {
		FactAddress addressInSource = null;

		public AlphaEdgeImpl(final Network network, final Node sourceNode, final Node targetNode, final Filter filter) {
			super(network, sourceNode, targetNode, filter);
		}

		@Override
		public void processPlusToken(final MemoryHandlerTemp memory)
				throws CouldNotAcquireLockException {
			final MemoryHandlerTemp mem =
					targetNode.memory.processTokenInAlpha(memory, this, this.filter);
			for (final Edge edge : targetNode.outgoingEdges) {
				edge.getTargetNode().enqueue(new Token.PlusToken(mem, edge));
			}
		}

		@Override
		public void processMinusToken(final MemoryHandlerTemp memory)
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
		public LinkedList<MemoryHandlerTemp> getTempMemories() {
			// TODO Auto-generated method stub
			return null;
		}

	}

	protected AlphaNode(final Network network, final Template template, final Path... paths) {
		super(network, template, paths);
	}

	public AlphaNode(final Network network, final Filter filter) {
		super(network, filter);
	}

	@Override
	protected EdgeImpl newEdge(final Node source) {
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
		assert PathTransformation.getFactAddressInCurrentlyLowestNode(path) == entry.getValue()
				.getAddress();
		PathTransformation.setCurrentlyLowestNode(path, this);
		PathTransformation.setFactAddressInCurrentlyLowestNode(path, entry.getKey());
	}

}
