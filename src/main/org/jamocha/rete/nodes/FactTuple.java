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

import org.jamocha.rete.Fact;
import org.jamocha.rete.memory.WorkingMemoryElement;

public class FactTuple implements WorkingMemoryElement {

	private static final long serialVersionUID = 1L;

	protected Fact[] facts = null;

	protected int index = 0;

	public FactTuple(Fact[] facts) {
		super();
		setFacts(facts);
	}

	public int length() {
		return facts.length;

	}

	public FactTuple(Fact fact) {
		this(new Fact[] { fact });
	}

	public Fact[] getFacts() {
		return facts;
	}

	public void setFacts(Fact[] facts) {
		this.facts = facts;
		int count = facts.length;
		index = 0;
		for (int i = 0; i < count; ++i) {
			index += facts[i].equalityIndex().hashCode();
		}
	}

	public FactTuple addFact(Fact fact) {
		Fact[] facts = new Fact[this.facts.length + 1];
		System.arraycopy(this.facts, 0, facts, 0, this.facts.length);
		facts[this.facts.length] = fact;
		return new FactTuple(facts);
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
		int count = possibleSub.facts.length;
		for (int i = 0; i < count; i++) {
			if (possibleSub.facts[i] != this.facts[i])
				return false;
		}
		return true;
	}

	public boolean isMyLastFact(Fact input) {
		return (facts[facts.length - 1] == input);
	}

	public int getIndex() {
		return index;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		// if (getClass() != obj.getClass())
		// return false;
		final FactTuple other = (FactTuple) obj;
		if (index != other.index)
			return false;
		if (!Arrays.equals(facts, other.facts))
			return false;
		return true;
	}
	
	@Override
	public FactTuple getFactTuple() {
		return this;
	}

}
