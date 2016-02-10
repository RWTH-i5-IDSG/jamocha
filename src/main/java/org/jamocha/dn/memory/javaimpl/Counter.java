/*
 * Copyright 2002-2016 The Jamocha Team
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.jamocha.org/
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the License.
 */
package org.jamocha.dn.memory.javaimpl;

import lombok.Getter;

import org.jamocha.dn.memory.CounterColumnMatcher;
import org.jamocha.filter.PathNodeFilterSet;

/**
 * Class holding the counter columns for existential filter elements providing the typical methods performed on the
 * counters.
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public abstract class Counter {

    abstract Column[] getColumns();

    abstract int getCounter(final Row row, final CounterColumn counterColumn);

    abstract void setCounter(final Row row, final CounterColumn counterColumn, final int value);

    abstract void increment(final Row row, final CounterColumn counterColumn, final int increment);

    public static class ActualCounter extends Counter {
        @Getter
        final Column[] columns;

        protected ActualCounter(final boolean[] negated) {
            this.columns = new Column[negated.length];
            for (int i = 0; i < negated.length; ++i) {
                this.columns[i] = negated[i] ? new NegativeColumn() : new PositiveColumn();
            }
        }

        protected ActualCounter(final Column[] columns) {
            this.columns = columns;
        }

        @Override
        int getCounter(final Row row, final CounterColumn counterColumn) {
            return row.getCounter(counterColumn);
        }

        @Override
        void setCounter(final Row row, final CounterColumn counterColumn, final int value) {
            row.setCounter(counterColumn, value);
        }

        @Override
        void increment(final Row row, final CounterColumn counterColumn, final int increment) {
            row.incrementCounter(counterColumn, increment);
        }
    }

    public static class EmptyCounter extends Counter {
        private static Column[] empty = new Column[0];

        @Override
        Column[] getColumns() {
            return empty;
        }

        @Override
        int getCounter(final Row row, final CounterColumn counterColumn) {
            assert null == counterColumn;
            return 0;
        }

        @Override
        void setCounter(final Row row, final CounterColumn counterColumn, final int value) {
            assert null == counterColumn;
        }

        @Override
        void increment(final Row row, final CounterColumn counterColumn, final int increment) {
            assert null == counterColumn;
        }
    }

    public enum Change {
        NOCHANGE, CHANGETOVALID, CHANGETOINVALID;
    }

    abstract static class Column {
        abstract boolean isValid(final Row row, final int counterColumn);

        abstract Change change(final Row row, final int counterColumn, final int increment);

        void increment(final Row row, final CounterColumn counterColumn, final int increment) {
            row.getCounters()[counterColumn.index] += increment;
        }
    }

    static class PositiveColumn extends Column {
        @Override
        boolean isValid(final Row row, final int counterColumn) {
            return row.getCounters()[counterColumn] > 0;
        }

        @Override
        Change change(final Row row, final int counterColumn, final int increment) {
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
        boolean isValid(final Row row, final int counterColumn) {
            return row.getCounters()[counterColumn] <= 0;
        }

        @Override
        Change change(final Row row, final int counterColumn, final int increment) {
            final int value = row.getCounters()[counterColumn];
            if (value == 0) {
                if (increment > 0) {
                    return Change.CHANGETOINVALID;
                }
                // assert that the amount of matching allRows does not fall below 0
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

    public static Counter newCounter(final PathNodeFilterSet filter,
            final CounterColumnMatcher filterElementToCounterColumn) {
        final boolean[] negatedArrayFromFilter =
                ExistentialPathCounter.getNegatedArrayFromFilter(filter, filterElementToCounterColumn);
        return newCounter(negatedArrayFromFilter);
    }

    public static Counter newCounter(final MemoryHandlerMain memoryHandlerMain) {
        final Column[] columns = memoryHandlerMain.counter.getColumns();
        if (columns.length == 0) return new EmptyCounter();
        return new ActualCounter(columns);
    }

    public static Counter newCounter(final boolean... negated) {
        if (negated.length == 0) return new EmptyCounter();
        return new ActualCounter(negated);
    }

    public boolean isValid(final Row row, final CounterColumn counterColumn) {
        return this.getColumns()[counterColumn.index].isValid(row, counterColumn.index);
    }

    public boolean isValid(final Row row) {
        final Column[] columns = getColumns();
        for (int i = 0; i < columns.length; ++i) {
            if (!columns[i].isValid(row, i)) return false;
        }
        return true;
    }

    public Change change(final Row row, final int[] increment) {
        final Column[] columns = getColumns();
        boolean changeToValid = false, changeToInvalid = false;
        for (int i = 0; i < increment.length && !(changeToValid && changeToInvalid); ++i) {
            final Column column = columns[i];
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
        if (changeToValid == changeToInvalid) return Change.NOCHANGE;
        if (changeToValid) return Change.CHANGETOVALID;
        if (changeToInvalid) return Change.CHANGETOINVALID;
        // why is this line necessary?
        return Change.NOCHANGE;
    }
}
