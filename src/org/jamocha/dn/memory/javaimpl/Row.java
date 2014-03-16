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

import static java.util.Arrays.copyOf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Class holding a line of facts and counter columns. Enables identification of a specific line
 * using a reference to this class instead of an index or the like.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@AllArgsConstructor
@Getter
@Setter
public class Row {
	private static int[] empty = new int[0];

	private Fact[] factTuple;

	public int[] getCounters() {
		return empty;
	}

	public int getCounter(@SuppressWarnings("unused") final CounterColumn counterColumn) {
		throw new UnsupportedOperationException(
				"getCounter not supported on counter-less fact-tuple!");
	}

	public void setCounter(@SuppressWarnings("unused") final CounterColumn counterColumn,
			@SuppressWarnings("unused") final int value) {
		throw new UnsupportedOperationException(
				"setCounter not supported on counter-less fact-tuple!");
	}

	public void incrementCounter(@SuppressWarnings("unused") final CounterColumn counterColumn,
			@SuppressWarnings("unused") final int increment) {
		throw new UnsupportedOperationException(
				"incrementCounter not supported on counter-less fact-tuple!");
	}

	protected Fact[] copyFacts() {
		return copyOf(factTuple, factTuple.length);
	}

	protected void copyFacts(final int offset, final Row row) {
		System.arraycopy(row.factTuple, 0, this.factTuple, offset, row.factTuple.length);
	}

	public Row copy() {
		return new Row(copyFacts());
	}

	public Row copy(final int offset, final Row row) {
		copyFacts(offset, row);
		return this;
	}
}
