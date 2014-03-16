package org.jamocha.dn.memory.javaimpl;

import static java.util.Arrays.copyOf;

class RowWithCounters extends Row {
	private int[] counters;

	public RowWithCounters(final Fact[] factTuple, final int[] counters) {
		super(factTuple);
		this.counters = counters;
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

	public Row stripCounters() {
		return new Row(getFactTuple());
	}

	protected int[] copyCounters() {
		return copyOf(counters, counters.length);
	}

	@Override
	public RowWithCounters copy() {
		return new RowWithCounters(copyFacts(), copyCounters());
	}

}