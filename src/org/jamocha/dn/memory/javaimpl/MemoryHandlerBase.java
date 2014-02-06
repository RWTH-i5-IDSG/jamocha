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

import gnu.trove.list.array.TIntArrayList;

import java.util.Arrays;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.NonFinal;

import org.jamocha.dn.memory.FactAddress;
import org.jamocha.dn.memory.MemoryHandler;
import org.jamocha.dn.memory.SlotAddress;
import org.jamocha.dn.memory.Template;

/**
 * Base class for java implementations of most handlers. Contains the template of the facts and a
 * list storing the facts handled. Provides the methods required by the MemoryHandler interface.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@EqualsAndHashCode
public class MemoryHandlerBase implements MemoryHandler {

	public static class Counter {
		final TIntArrayList counters = new TIntArrayList();
		final int columns;
		final int emptyRow[];

		public Counter(final int columns) {
			this.columns = columns;
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

		public boolean zeroRow(final int row) {
			return xRow(row, 0);
		}

		public boolean xRow(final int row, final int x) {
			final int start = row * columns;
			for (int i = start; i < start + columns; ++i) {
				if (counters.get(i) != x)
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

	@Getter
	final Template[] template;
	@NonNull
	@NonFinal
	List<Fact[]> facts;

	public MemoryHandlerBase(final Template[] template, final List<Fact[]> facts) {
		this.template = template;
		this.facts = facts;
	}

	/**
	 * @see org.jamocha.dn.memory.MemoryHandler#getValue(FactAddress, SlotAddress, int)
	 */
	@Override
	public Object getValue(final FactAddress address, final SlotAddress slot, final int row) {
		return this.facts.get(row)[((org.jamocha.dn.memory.javaimpl.FactAddress) address)
				.getIndex()].getValue(slot);
	}

	/**
	 * @see org.jamocha.dn.memory.MemoryHandler#size()
	 */
	@Override
	public int size() {
		return this.facts.size();
	}

	@Override
	public String toString() {
		return "MemoryHandlerBase(template=" + Arrays.deepToString(this.template) + ", facts="
				+ Arrays.deepToString(this.facts.toArray()) + ")";
	}
}
