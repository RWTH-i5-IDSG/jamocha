/*
 * Copyright 2007 Josef Alexander Hahn, Sebastian Reinartz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://jamocha.org
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.jamocha.rete.nodes;

import java.util.Arrays;
import java.util.Iterator;

import org.jamocha.rete.Fact;
import org.jamocha.rete.memory.WorkingMemoryElement;

public class FactTupleImpl implements FactTuple {

	public class FactTupleIterator implements Iterator<Fact> {

		private Fact[] arr;
		
		int ind;
		
		public FactTupleIterator(Fact[] arr) {
			ind=0;
			this.arr=arr;
		}
		
		@Override
		public boolean hasNext() {
			return (ind < arr.length);
		}

		@Override
		public Fact next() {
			Fact result = arr[ind];
			ind++;
			return result;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
	}
	
	private static final long serialVersionUID = 1L;

	protected Fact[] facts = null;


	public FactTupleImpl(Fact[] facts) {
		super();
		this.facts = facts;
	}

	public int length() {
		return facts.length;

	}

	public FactTupleImpl(Fact fact) {
		this(new Fact[] { fact });
	}

	public Fact[] getFacts() {
		return facts;
	}


	public FactTuple appendFact(Fact fact) {
		Fact[] facts = new Fact[this.facts.length + 1];
		System.arraycopy(this.facts, 0, facts, 0, this.facts.length);
		facts[this.facts.length] = fact;
		return new FactTupleImpl(facts);
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[ ");
		for (Fact fact : facts) {
			// sb.append(fact.toString());
			sb.append("f-").append(fact.getFactId());
			sb.append(" | ");
		}
		sb.append("] ");
		return sb.toString();
	}

	public boolean isMySubTuple(FactTuple possibleSub) {
		int count = possibleSub.length();
		for (int i = 0; i < count; i++) {
			if (possibleSub.getFact(i) != this.facts[i])
				return false;
		}
		return true;
	}

	public boolean isMyLastFact(Fact input) {
		return (facts[facts.length - 1] == input);
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		// if (getClass() != obj.getClass())
		// return false;
		if (!(obj instanceof WorkingMemoryElement)) return false;
		
		final WorkingMemoryElement other = (WorkingMemoryElement) obj;

		return (Arrays.equals(facts, other.getFactTuple().getFacts() ));
	}
	
	@Override
	public FactTuple getFactTuple() {
		return this;
	}

	@Override
	public Fact getFact(int index) {
		return facts[index];
	}

	@Override
	public Iterator<Fact> iterator() {
		return new FactTupleIterator(facts);
	}

}
