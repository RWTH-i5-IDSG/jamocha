/*
 * Copyright 2002-2013 The Jamocha Team
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
import java.util.Collection;
import java.util.List;

import org.jamocha.dn.memory.FactAddress;
import org.jamocha.dn.memory.SlotAddress;
import org.jamocha.dn.nodes.CouldNotAcquireLockException;
import org.jamocha.dn.nodes.Node.Edge;
import org.jamocha.filter.Filter;
import org.jamocha.filter.Filter.FilterElement;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * 
 */
public class MemoryHandlerMinusTemp extends MemoryHandlerTemp implements
		org.jamocha.dn.memory.MemoryHandlerMinusTemp {

	private static MemoryHandlerTemp empty = new MemoryHandlerMinusTemp(null,
			new ArrayList<Fact[]>(0));

	static MemoryHandlerMinusTemp newRootTemp(final MemoryHandlerMain memoryHandlerMain,
			final org.jamocha.dn.memory.Fact[] facts) {
		final ArrayList<Fact[]> factList = new ArrayList<>();
		for (org.jamocha.dn.memory.Fact fact : facts) {
			factList.add(new Fact[] { new Fact(fact.getSlotValues()) });
		}
		return new MemoryHandlerMinusTemp(memoryHandlerMain, factList);
	}

	@Override
	public MemoryHandlerTemp newBetaTemp(
			final org.jamocha.dn.memory.MemoryHandlerMain originatingMainHandler,
			final Edge originIncomingEdge, final Filter filter) throws CouldNotAcquireLockException {
		// TODO filter main memory using this.facts
		// first, we have to determine into which column originIncomingEdge has been joined

		return this;
	}

	@Override
	public MemoryHandlerTemp newAlphaTemp(
			final org.jamocha.dn.memory.MemoryHandlerMain originatingMainHandler,
			final Edge originIncomingEdge, final Filter filter) throws CouldNotAcquireLockException {
		final int factsSize = this.facts.size();
		if (1 == factsSize) {
			final Fact[] fact = this.facts.get(0);
			assert 1 == fact.length;
			for (final FilterElement element : filter.getFilterElements()) {
				if (!applyFilterElement(fact[0], element)) {
					return MemoryHandlerMinusTemp.empty;
				}
			}
			return this;
		}
		final List<Fact[]> elementsPassed = new ArrayList<Fact[]>();
		factLoop: for (final Fact[] fact : this.facts) {
			assert 1 == fact.length;
			for (final FilterElement element : filter.getFilterElements()) {
				if (!applyFilterElement(fact[0], element)) {
					continue factLoop;
				}
			}
			elementsPassed.add(fact);
		}
		final int elementsPassedSize = elementsPassed.size();
		if (0 == elementsPassedSize) {
			return MemoryHandlerMinusTemp.empty;
		}
		if (elementsPassedSize == factsSize) {
			return this;
		}
		return new MemoryHandlerMinusTemp(originatingMainHandler, elementsPassed);
	}

	public MemoryHandlerMinusTemp(
			final org.jamocha.dn.memory.MemoryHandlerMain originatingMainHandler,
			final List<Fact[]> facts) {
		super(originatingMainHandler, facts);
	}

	@Override
	public void processInMemory(final org.jamocha.dn.memory.MemoryHandlerMain main) {
		main.remove(this);
	}

	@Override
	public void enqueueInEdges(final Collection<? extends Edge> edges) {
		for (final Edge edge : edges) {
			edge.enqueueMinusMemory(this);
		}
	}

	@Override
	public Object getValue(final FactAddress address, final SlotAddress slot, final int row) {
		return this.facts.get(row)[0].getValue(slot);
	}

}
