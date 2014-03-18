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
import java.util.Optional;

import org.jamocha.dn.nodes.Edge;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * 
 */
public abstract class MemoryHandlerPlusTempGenericValidRowsAdder<T extends MemoryHandlerMain, D extends MemoryHandlerPlusTempValidRowsAdder.Data>
		extends MemoryHandlerPlusTemp<T> {
	@lombok.Data
	protected static class Data {
		final ArrayList<Row> newValidRows;
	}

	final D original;
	Optional<D> filtered = Optional.empty();

	@Override
	public ArrayList<Row> getRowsForSucessorNodes() {
		return this.original.newValidRows;
	}

	// public ArrayList<Row> getValidRows() {
	// return this.getFilteredData().newValidRows;
	// }
	//
	// public ArrayList<Row> getAllRows() {
	// return this.getFilteredData().newValidRows;
	// }

	D getFilteredData() {
		return this.filtered.orElse(original);
	}

	void setFilteredData(final D data) {
		this.filtered = Optional.of(data);
	}

	@Override
	public void enqueueInEdge(final Edge edge) {
		edge.enqueueMemory(this);
	}

	@Override
	protected org.jamocha.dn.memory.MemoryHandlerTemp commitToMain() {
		// add newValidRows to main.filtered
		final ArrayList<Row> facts = this.getFilteredData().newValidRows;
		for (final Row row : facts) {
			this.originatingMainHandler.getAllRows().add(row);
		}
		return null;
	}

	protected MemoryHandlerPlusTempGenericValidRowsAdder(final T originatingMainHandler,
			final D original, final int numChildren, final boolean empty,
			final boolean omitSemaphore) {
		super(originatingMainHandler, numChildren, empty, omitSemaphore);
		this.original = original;
	}
}
