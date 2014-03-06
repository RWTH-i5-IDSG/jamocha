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

import java.util.ArrayList;

import org.jamocha.dn.nodes.CouldNotAcquireLockException;
import org.jamocha.dn.nodes.Edge;
import org.jamocha.filter.AddressFilter;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * 
 */
public class MemoryHandlerExistentialTemp extends MemoryHandlerTemp implements
		org.jamocha.dn.memory.MemoryHandlerTemp {

	final MemoryHandlerPlusTemp pos;
	final MemoryHandlerMinusTemp neg;

	public MemoryHandlerExistentialTemp(final MemoryHandlerMain originatingMainHandler,
			final ArrayList<FactTuple> rows, final MemoryHandlerPlusTemp pos,
			final MemoryHandlerMinusTemp neg) {
		super(originatingMainHandler, rows);
		this.pos = pos;
		this.neg = neg;
	}

	@Override
	public void enqueueInEdge(final Edge edge) {
		edge.enqueueMemory(this.neg);
		edge.enqueueMemory(this.pos);
	}

	@Override
	public org.jamocha.dn.memory.MemoryHandlerTemp newAlphaTemp(
			org.jamocha.dn.memory.MemoryHandlerMain originatingMainHandler,
			Edge originIncomingEdge, AddressFilter filter) throws CouldNotAcquireLockException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.jamocha.dn.memory.MemoryHandlerTemp newBetaTemp(
			org.jamocha.dn.memory.MemoryHandlerMain originatingMainHandler,
			Edge originIncomingEdge, AddressFilter filter) throws CouldNotAcquireLockException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.jamocha.dn.memory.MemoryHandlerTemp releaseLock() {
		if (!this.pos.internalReleaseLock())
			return null;
		return this.originatingMainHandler.add(this);
	}

}
