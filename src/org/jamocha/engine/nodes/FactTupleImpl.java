/*
 * Copyright 2002-2008 The Jamocha Team
 * 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.jamocha.org/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package org.jamocha.engine.nodes;

import java.util.Arrays;
import java.util.Iterator;

import org.jamocha.engine.workingmemory.WorkingMemoryElement;
import org.jamocha.engine.workingmemory.elements.Fact;

public class FactTupleImpl implements FactTuple {

	public class FactTupleIterator implements Iterator<Fact> {

		private final Fact[] arr;
		int ind;

		public FactTupleIterator(final Fact[] arr) {
			ind = 0;
			this.arr = arr;
		}

		public boolean hasNext() {
			return ind < arr.length;
		}

		public Fact next() {
			final Fact result = arr[ind];
			ind++;
			return result;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	private static final long serialVersionUID = 1L;
	protected Fact[] facts = null;

	public FactTupleImpl(final Fact[] facts) {
		super();
		this.facts = facts;
	}

	public int length() {
		return facts.length;

	}

	public FactTupleImpl(final Fact fact) {
		this(new Fact[] { fact });
	}

	public Fact[] getFacts() {
		return facts;
	}

	public FactTuple appendFact(final Fact fact) {
		final Fact[] facts = new Fact[this.facts.length + 1];
		System.arraycopy(this.facts, 0, facts, 0, this.facts.length);
		facts[this.facts.length] = fact;
		return new FactTupleImpl(facts);
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("[ ");
		for (final Fact fact : facts) {
			sb.append(fact.toString());
			sb.append(" | ");
		}
		sb.append("] ");
		return sb.toString();
	}

	public boolean isMySubTuple(final FactTuple smallerOne) {
		final int count = smallerOne.length();
		for (int i = 0; i < count; i++)
			if (smallerOne.getFact(i) != facts[i])
				return false;
		return true;
	}

	public boolean isMyLastFact(final Fact input) {
		return facts[facts.length - 1] == input;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof WorkingMemoryElement))
			return false;

		final WorkingMemoryElement other = (WorkingMemoryElement) obj;

		return Arrays.equals(facts, other.getFactTuple().getFacts());
	}

	public FactTuple getFactTuple() {
		return this;
	}

	public Fact getFact(final int index) {
		return facts[index];
	}

	public Iterator<Fact> iterator() {
		return new FactTupleIterator(facts);
	}

	public boolean isStandaloneFact() {
		return false;
	}

	public Fact getFirstFact() {
		return facts[0];
	}

	public Fact getLastFact() {
		return facts[facts.length - 1];
	}

	@Override
	public int hashCode() {
		int hash = 0;
		for (final Fact f : facts)
			hash += f.hashCode();
		return hash;
	}

	public long getAggregateCreationTimestamp() {
		long result = 0;
		for (final Fact fact : facts)
			result = Math.max(result, fact.getCreationTimeStamp());
		return result;
	}
}
