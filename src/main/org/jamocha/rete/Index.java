/*
 * Copyright 2002-2006 Peter Lin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://ruleml-dev.sourceforge.net/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.jamocha.rete;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @author Peter Lin<p/>
 *
 * Index is used for the Alpha and BetaMemories for the index. Instead of the
 * original quick and dirty String index implementation, this implementation
 * takes an Array of Fact[] objects and calculates the hash.
 * The class overrides hashCode and equals(Object) so that it works correctly
 * as the key for HashMaps. There is an unit test called IndexTest in the
 * test directory.
 * 
 * The implementation for now is very simple. Later on, we may need to update
 * it and make sure it works for memory snapshots and other features.
 */
public class Index implements Serializable, HashIndex {

	private Fact[] facts = null;

	private int hashCode;

	/**
	 * 
	 */
	public Index(Fact[] facts) {
		super();
		this.facts = facts;
		calculateHash();
	}
	
	public Index(Fact[] facts, int hashCode) {
		super();
		this.facts = facts;
		this.hashCode = hashCode;
	}
	/**
	 * This is a very simple implementation that basically adds the hashCodes
	 * of the Facts in the array.
	 */
	private void calculateHash() {
		int hash = 0;
		for (int idx = 0; idx < facts.length; idx++) {
			hash += facts[idx].hashCode();
		}
		this.hashCode = hash;
	}

	protected Fact[] getFacts() {
		return this.facts;
	}

	/**
	 * The implementation is very close to Drools FactHandleList implemented
	 * by simon. The main difference is that Drools uses interfaces and 
	 * Sumatra doesn't. I don't see a need to abstract this out to an
	 * interface, since no one other than an experience rule engine
	 * developer would be writing a new Index class. And even then, it only
	 * makes sense to replace the implementation. Having multiple index
	 * implementations doesn't really make sense.
	 */
	public boolean equals(Object val) {
		if (this == val) {
			return true;
		}
		if (val == null || getClass() != val.getClass()) {
			return false;
		}

		return Arrays.equals(this.facts, ((Index) val).facts);
	}

    public void clear() {
        this.facts = null;
        this.hashCode = 0;
    }
    
	/**
	 * Method simply returns the cached hashCode.
	 */
	public int hashCode() {
		return this.hashCode;
	}

	public Index add(Fact fact) {
		Fact[] facts = new Fact[this.facts.length+1];
		System.arraycopy(this.facts, 0, facts, 0, this.facts.length);
		facts[this.facts.length] = fact;
		return new Index(facts, this.hashCode+fact.hashCode());
	}

	
	public Index addAll(Index index) {
		Fact[] facts = new Fact[this.facts.length+index.facts.length];
		System.arraycopy(this.facts, 0, facts, 0, this.facts.length);
		System.arraycopy(index.facts, 0, facts, this.facts.length, index.facts.length);
		return new Index(facts, this.hashCode+index.hashCode);

	}
}
