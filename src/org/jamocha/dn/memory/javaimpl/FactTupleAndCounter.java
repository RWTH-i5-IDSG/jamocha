package org.jamocha.dn.memory.javaimpl;

import static java.util.Arrays.copyOf;

class FactTupleAndCounter extends FactTuple {
	private int[] counters;

	public FactTupleAndCounter(final Fact[] factTuple, final int[] counters) {
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

	public FactTuple stripCounters() {
		return new FactTuple(getFactTuple());
	}

	protected int[] copyCounters() {
		return copyOf(counters, counters.length);
	}

	@Override
	public FactTupleAndCounter copy() {
		return new FactTupleAndCounter(copyFacts(), copyCounters());
	}

}