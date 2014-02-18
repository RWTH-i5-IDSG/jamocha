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

import gnu.trove.list.array.TIntArrayList;

public class Counter {
	final TIntArrayList counters = new TIntArrayList();
	final int columns;
	final boolean[] negated;
	final int emptyRow[];

	public Counter(final boolean... negated) {
		this.columns = negated.length;
		this.negated = negated;
		this.emptyRow = new int[columns];
	}

	public int getCounter(final int row, final int column) {
		assert column >= 0 && column < columns;
		return counters.get(row * columns + column);
	}

	public void addEmptyRow() {
		counters.add(emptyRow);
	}

	/**
	 * Returns the number of rows in the Counter class
	 * 
	 * @return the number of rows in the Counter class
	 */
	public int size() {
		return counters.size() / columns;
	}

	/**
	 * Checks if the corresponding row fulfills the following conditions:
	 * <ul>
	 * <li><b>negated:</b> counter is equal to 0</li>
	 * <li><b>non-negated:</b> counter is not equal to 0</li>
	 * </ul>
	 * 
	 * @param row
	 *            row to check
	 * @return true iff row fulfills the conditions given above
	 */
	public boolean validRow(final int row) {
		final int start = row * columns;
		for (int i = 0; i < columns; ++i) {
			if (this.negated[i] == (counters.get(start + i) != 0))
				return false;
		}
		return true;
	}

	public int increment(final int row, final int column, final int increment) {
		final int offset = row * columns + column;
		final int value = counters.get(offset) + increment;
		counters.set(offset, value);
		return value;
	}

	public int increment(final int row, final int column) {
		return increment(row, column, 1);
	}

	public int decrement(final int row, final int column) {
		return increment(row, column, -1);
	}

	public int decrement(final int row, final int column, final int decrement) {
		return increment(row, column, -decrement);
	}
}