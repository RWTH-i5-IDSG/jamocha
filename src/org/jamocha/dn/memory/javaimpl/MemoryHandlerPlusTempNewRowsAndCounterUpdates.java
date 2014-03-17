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

import lombok.EqualsAndHashCode;

import org.jamocha.dn.memory.MemoryHandlerTemp;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class MemoryHandlerPlusTempNewRowsAndCounterUpdates
		extends
		MemoryHandlerPlusTempValidRowsAdder<MemoryHandlerMainWithExistentials, MemoryHandlerPlusTempNewRowsAndCounterUpdates.ExtendedData> {

	@lombok.Data
	@EqualsAndHashCode(callSuper = true)
	protected static class ExtendedData extends MemoryHandlerPlusTempValidRowsAdder.Data {
		final ArrayList<CounterUpdate> counterUpdates;
		final ArrayList<Row> newRows;

		public ExtendedData(final ArrayList<CounterUpdate> counterUpdates,
				final ArrayList<Row> newRows, final ArrayList<Row> newValidRows) {
			super(newValidRows);
			this.counterUpdates = counterUpdates;
			this.newRows = newRows;
		}
	}

	/**
	 * remember the number of children to pass it to the temps possibly produced by commitToMain
	 */
	final int numChildren;

	protected MemoryHandlerPlusTempNewRowsAndCounterUpdates(
			final MemoryHandlerMainWithExistentials originatingMainHandler,
			final ExtendedData original, final int numChildren, final boolean empty,
			final boolean omitSemaphore) {
		super(originatingMainHandler, original, numChildren, empty, omitSemaphore);
		this.numChildren = numChildren;
	}

	public static MemoryHandlerPlusTempNewRowsAndCounterUpdates newInstance(
			final MemoryHandlerMainWithExistentials originatingMainHandler,
			final ArrayList<CounterUpdate> counterUpdates, final ArrayList<Row> newRows,
			final ArrayList<Row> newValidRows, final int numChildren, final boolean omitSemaphore) {
		return new MemoryHandlerPlusTempNewRowsAndCounterUpdates(originatingMainHandler,
				new ExtendedData(counterUpdates, newRows, newValidRows), numChildren,
				newRows.isEmpty() && newValidRows.isEmpty() && counterUpdates.isEmpty(),
				omitSemaphore);
	}

	public static MemoryHandlerPlusTempNewRowsAndCounterUpdates newInstance(
			final MemoryHandlerMainWithExistentials originatingMainHandler,
			final ArrayList<CounterUpdate> counterUpdates, final ArrayList<Row> newRows,
			final int numChildren, final boolean omitSemaphore) {
		return newInstance(originatingMainHandler, counterUpdates, newRows,
				validPart(originatingMainHandler.counter, newRows), numChildren, omitSemaphore);
	}

	static ArrayList<Row> validPart(final Counter counter, final ArrayList<Row> allRows) {
		final LazyListCopy copy = new LazyListCopy(allRows);
		for (int index = 0; index < allRows.size(); index++) {
			final Row row = allRows.get(index);
			if (counter.isValid(row)) {
				copy.keep(index);
			} else {
				copy.drop(index);
			}
		}
		return copy.getList();
	}

	@Override
	public ArrayList<Row> getAllRows() {
		return this.filtered.orElse(this.original).newRows;
	}

	@Override
	protected MemoryHandlerTemp commitToMain() {
		// add source.newValidRows to main.filtered
		final MemoryHandlerTemp nil = super.commitToMain();
		assert nil == null;
		// add source.newRows to main.unfiltered
		final ExtendedData dataSource = this.filtered.orElse(this.original);
		final ArrayList<Row> newRows = dataSource.newRows;
		for (final Row row : newRows) {
			// FIXME do we need the overloaded get all rows?
			this.originatingMainHandler.getAllRows().add(row);
		}
		if (dataSource.counterUpdates.isEmpty())
			return null;
		final Counter counter = this.originatingMainHandler.counter;
		final ArrayList<Row> rowsToAdd = new ArrayList<>();
		final ArrayList<Row> rowsToDel = new ArrayList<>();
		for (final CounterUpdate counterUpdate : dataSource.counterUpdates) {
			final Row row = counterUpdate.row;
			final boolean wasValid = counter.isValid(row);
			counterUpdate.apply();
			final boolean isValid = counter.isValid(row);
			if (!wasValid && isValid) {
				// changed to valid
				rowsToAdd.add(counterUpdate.row);
			} else if (wasValid && !isValid) {
				// changed to invalid
				rowsToDel.add(counterUpdate.row);
			}
			// else: no change
		}
		final boolean noLinesToAdd = rowsToAdd.isEmpty();
		final boolean noLinesToDel = rowsToDel.isEmpty();
		if (noLinesToAdd && noLinesToDel) {
			// no change
			return null;
		}
		if (noLinesToAdd) {
			// TODO create -token for invalidated rows
			return null;
		}
		if (noLinesToDel) {
			// TODO create +token for validated rows: MemoryHandlerPlusTempValidRowsAdder(rowsToAdd)
			return MemoryHandlerPlusTempValidRowsAdder.newInstance(originatingMainHandler,
					rowsToAdd, numChildren, false);
		}
		// TODO create both tokens as above and wrap them
		return new MemoryHandlerTempPairDistributer(
				MemoryHandlerPlusTempValidRowsAdder.newInstance(originatingMainHandler, rowsToAdd,
						numChildren, false), null);
	}
}
