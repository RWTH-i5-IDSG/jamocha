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

import java.util.Arrays;

import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@ToString
class RowWithCounters implements Row {
    private final Fact[] factTuple;
    private final int[] counters;

    @Override
    public int[] getCounters() {
        return counters;
    }

    @Override
    public Fact[] getFactTuple() {
        return factTuple;
    }

    @Override
    public int getCounter(final CounterColumn counterColumn) {
        return this.counters[counterColumn.index];
    }

    @Override
    public void setCounter(final CounterColumn counterColumn, final int value) {
        this.counters[counterColumn.index] = value;
    }

    @Override
    public void incrementCounter(final CounterColumn counterColumn, final int increment) {
        this.counters[counterColumn.index] += increment;
    }

    public RowWithoutCounters stripCounters() {
        return new RowWithoutCounters(factTuple);
    }

    @Override
    public RowWithCounters copy(final int offset, final Row src) {
        Row.copyFacts(offset, src, this);
        return this;
    }

    @Override
    public RowWithCounters copy() {
        return new RowWithCounters(Row.copyFacts(this), Arrays.copyOf(counters, counters.length));
    }
}
