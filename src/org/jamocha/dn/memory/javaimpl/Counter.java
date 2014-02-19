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

import java.util.ArrayList;
import java.util.List;

import org.jamocha.filter.Filter;
import org.jamocha.filter.Filter.FilterElement;

/**
 * Class holding the counter columns for existential filter elements providing the typical methods
 * performed on the counters.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class Counter {
	// static part (cache)
	final static List<int[]> emptyRowCache = new ArrayList<>();
	final static int numPrecreatedEmptyRows = 10;
	static {
		for (int i = 0; i < numPrecreatedEmptyRows; ++i) {
			assert i == emptyRowCache.size();
			emptyRowCache.add(new int[i]);
		}
	}
	// actual attributes
	final TIntArrayList counters = new TIntArrayList();
	final int columns;
	final boolean[] negated;
	final int emptyRow[];

	public Counter(final boolean... negated) {
		this.columns = negated.length;
		this.negated = negated;
		int tempEmptyRow[];
		try {
			tempEmptyRow = emptyRowCache.get(this.columns);
		} catch (final IndexOutOfBoundsException e) {
			for (int i = emptyRowCache.size(); i <= this.columns; ++i) {
				assert i == emptyRowCache.size();
				emptyRowCache.add(new int[i]);
			}
			tempEmptyRow = emptyRowCache.get(this.columns);
		}
		this.emptyRow = tempEmptyRow;
	}

	public Counter(final Filter<? extends FilterElement> filter) {
		this(ExistentialFilterElementCounter.getNegatedArrayFromFilter(filter));
	}

	public int getCounter(final int row, final int column) {
		assert column >= 0 && column < this.columns;
		return this.counters.get(row * this.columns + column);
	}

	public void addEmptyRow() {
		this.counters.add(this.emptyRow);
	}

	public void addEmptyRows(final int rows) {
		final int size = rows * columns;
		if (size >= emptyRowCache.size())
			this.counters.add(new int[size]);
		else
			this.counters.add(emptyRowCache.get(size));
	}

	/**
	 * Returns the number of rows in the Counter class
	 * 
	 * @return the number of rows in the Counter class
	 */
	public int size() {
		return this.counters.size() / this.columns;
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
		final int start = row * this.columns;
		for (int i = 0; i < this.columns; ++i) {
			if (this.negated[i] == (this.counters.get(start + i) != 0))
				return false;
		}
		return true;
	}

	public int increment(final int row, final int column, final int increment) {
		final int offset = row * this.columns + column;
		final int value = this.counters.get(offset) + increment;
		this.counters.set(offset, value);
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