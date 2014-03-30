package org.jamocha.dn.memory.javaimpl;

import java.util.Arrays;

import lombok.AllArgsConstructor;

@AllArgsConstructor
class RowWithCounters implements Row {
	private Fact[] factTuple;
	private int[] counters;

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