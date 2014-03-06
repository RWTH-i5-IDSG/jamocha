package org.jamocha.dn.memory.javaimpl;

import static java.util.Arrays.copyOf;
import lombok.Getter;
import lombok.Setter;

class FactTupleAndCounter extends FactTuple {
	@Getter(onMethod = @_({ @Override }))
	@Setter(onMethod = @_({ @Override }))
	private int[] counters;

	public FactTupleAndCounter(final Fact[] factTuple, final int[] counters) {
		super(factTuple);
		this.counters = counters;
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