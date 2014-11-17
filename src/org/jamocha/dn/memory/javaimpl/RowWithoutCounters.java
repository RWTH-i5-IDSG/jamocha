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

import lombok.AllArgsConstructor;
import lombok.ToString;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 *
 */
@AllArgsConstructor
@ToString
public class RowWithoutCounters implements Row {
	private static int[] empty = new int[0];

	private final Fact[] factTuple;

	@Override
	public int[] getCounters() {
		return empty;
	}

	@Override
	public Fact[] getFactTuple() {
		return factTuple;
	}

	@Override
	public int getCounter(final CounterColumn counterColumn) {
		throw new UnsupportedOperationException("getCounter not supported on counter-less fact-tuple!");
	}

	@Override
	public void setCounter(final CounterColumn counterColumn, final int value) {
		throw new UnsupportedOperationException("setCounter not supported on counter-less fact-tuple!");
	}

	@Override
	public void incrementCounter(final CounterColumn counterColumn, final int increment) {
		throw new UnsupportedOperationException("incrementCounter not supported on counter-less fact-tuple!");
	}

	@Override
	public RowWithoutCounters copy() {
		return new RowWithoutCounters(Row.copyFacts(this));
	}

	@Override
	public RowWithoutCounters copy(final int offset, final Row src) {
		Row.copyFacts(offset, src, this);
		return this;
	}
}
