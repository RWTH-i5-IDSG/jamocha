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

import org.jamocha.dn.memory.PathFilterElementToCounterColumn;
import org.jamocha.filter.PathFilter;

/**
 * Class holding the counter columns for existential filter elements providing the typical methods
 * performed on the counters.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class Counter {
	final Column[] columns;

	protected Counter(boolean[] negated) {
		columns = new Column[negated.length];
		for (int i = 0; i < negated.length; ++i) {
			columns[i] = negated[i] ? new NegativeColumn() : new PositiveColumn();
		}
	}

	protected Counter(final Column[] columns) {
		this.columns = columns;
	}

	public static enum Change {
		NOCHANGE, CHANGETOVALID, CHANGETOINVALID;
	}

	static abstract class Column {
		abstract boolean isValid(final FactTuple row, final int counterColumn);

		abstract Change change(final FactTuple row, final int counterColumn, final int increment);

		void increment(final FactTuple row, final CounterColumn counterColumn, final int increment) {
			row.getCounters()[counterColumn.index] += increment;
		}
	}

	static class PositiveColumn extends Column {
		@Override
		boolean isValid(final FactTuple row, final int counterColumn) {
			return row.getCounters()[counterColumn] > 0;
		}

		@Override
		Change change(FactTuple row, int counterColumn, int increment) {
			final int value = row.getCounters()[counterColumn];
			if (value == 0) {
				if (increment > 0) {
					return Change.CHANGETOVALID;
				}
				// assert that the amount of matching rows does not fall below 0
				assert increment == 0;
				return Change.NOCHANGE;
			}
			// value should not be zero now
			assert value != 0;
			if (value == -increment) {
				return Change.CHANGETOINVALID;
			}
			return Change.NOCHANGE;
		}
	}

	static class NegativeColumn extends Column {
		@Override
		boolean isValid(final FactTuple row, final int counterColumn) {
			return row.getCounters()[counterColumn] <= 0;
		}

		@Override
		Change change(FactTuple row, int counterColumn, int increment) {
			final int value = row.getCounters()[counterColumn];
			if (value == 0) {
				if (increment > 0) {
					return Change.CHANGETOINVALID;
				}
				// assert that the amount of matching rows does not fall below 0
				assert increment == 0;
				return Change.NOCHANGE;
			}
			// value should not be zero now
			assert value != 0;
			if (value == -increment) {
				return Change.CHANGETOVALID;
			}
			return Change.NOCHANGE;
		}
	}

	public static Counter newCounter(final PathFilter filter,
			final PathFilterElementToCounterColumn filterElementToCounterColumn) {
		final boolean[] negatedArrayFromFilter =
				ExistentialPathCounter.getNegatedArrayFromFilter(filter,
						filterElementToCounterColumn);
		return new Counter(negatedArrayFromFilter);
	}

	public static Counter newCounter(final MemoryHandlerMain memoryHandlerMain) {
		return new Counter(memoryHandlerMain.counter.columns);
	}

	public static Counter newCounter(final boolean... negated) {
		return new Counter(negated);
	}

	public boolean isValid(final FactTuple row, final CounterColumn counterColumn) {
		return this.columns[counterColumn.index].isValid(row, counterColumn.index);
	}

	public boolean isValid(final FactTuple row) {
		for (int i = 0; i < columns.length; ++i) {
			if (!this.columns[i].isValid(row, i))
				return false;
		}
		return true;
	}

	public Change change(final FactTuple row, final int[] increment) {
		boolean changeToValid = false, changeToInvalid = false;
		for (int i = 0; i < increment.length && !(changeToValid && changeToInvalid); ++i) {
			final Column column = this.columns[i];
			if (column.isValid(row, i)) {
				switch (column.change(row, i, increment[i])) {
				case CHANGETOVALID:
					changeToValid = true;
					break;
				case CHANGETOINVALID:
					changeToInvalid = true;
					break;
				case NOCHANGE:
					break;
				}
			}
		}
		// if there is no change or the row stays invalid as some other condition will not be
		// fulfilled return no change
		if (changeToValid == changeToInvalid)
			return Change.NOCHANGE;
		if (changeToValid)
			return Change.CHANGETOVALID;
		if (changeToInvalid)
			return Change.CHANGETOINVALID;
		// why is this line necessary?
		return Change.NOCHANGE;
	}
}