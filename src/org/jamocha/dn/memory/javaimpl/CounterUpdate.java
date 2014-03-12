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

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * 
 */
public class CounterUpdate {
	final FactTuple row;
	final int increment[];

	public CounterUpdate(final FactTuple row) {
		this.row = row;
		this.increment = new int[row.getCounters().length];
	}

	public void increment(final CounterColumn counterColumn, final int increment) {
		this.increment[counterColumn.index] += increment;
	}

	public void apply() {
		final int[] counters = row.getCounters();
		for (int i = 0; i < increment.length; ++i) {
			counters[i] += increment[i];
		}
	}
}
