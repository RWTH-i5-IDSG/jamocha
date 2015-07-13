/*
 * Copyright 2002-2014 The Jamocha Team
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
package org.jamocha.dn.memory.javaimpl;

import java.util.List;

import org.jamocha.dn.memory.FactAddress;
import org.jamocha.dn.memory.FactIdentifier;
import org.jamocha.dn.memory.MemoryHandler;
import org.jamocha.dn.memory.SlotAddress;
import org.jamocha.dn.memory.Template;
import org.jamocha.dn.nodes.Edge;

import lombok.RequiredArgsConstructor;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor
public class MemoryHandlerTempPairDistributer implements org.jamocha.dn.memory.MemoryHandlerTemp {

	final org.jamocha.dn.memory.MemoryHandlerPlusTemp plus;
	final org.jamocha.dn.memory.MemoryHandlerMinusTemp minus;

	@Override
	public void enqueueInEdge(final Edge edge) {
		edge.enqueueMemory(this.minus);
		edge.enqueueMemory(this.plus);
	}

	@Override
	public int size() {
		return this.plus.size() + this.minus.size();
	}

	static final private String unsupported = "MemoryHandlerExistential only supports enqueueInEdges and size.";

	@Override
	public Template[] getTemplate() {
		throw new UnsupportedOperationException(unsupported);
	}

	@Override
	public Object getValue(final FactAddress address, final SlotAddress slot, final int row) {
		throw new UnsupportedOperationException(unsupported);
	}

	@Override
	public FactIdentifier[] getFactIdentifiers(final int row) {
		throw new UnsupportedOperationException(unsupported);
	}

	@Override
	public void releaseLock() {
		throw new UnsupportedOperationException(unsupported);
	}

	@Override
	public List<MemoryHandler> splitIntoChunksOfSize(final int size) {
		throw new UnsupportedOperationException(unsupported);
	}
}
