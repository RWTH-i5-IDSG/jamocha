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

import java.util.Map;
import java.util.Collection;
import java.util.Iterator;

import org.jamocha.rete.util.CollectionsFactory;


/**
 * @author Peter Lin
 *
 * BetaMemory stores the matches
 */
public class BetaMemoryImpl implements BetaMemory {

	protected Index index = null;

	protected Map matches = CollectionsFactory.newMap();

	/**
	 * 
	 */
	public BetaMemoryImpl(Index index) {
		super();
		this.index = index;
	}

	/**
	 * Return the index of the beta memory
	 * @return
	 */
	public Index getIndex() {
		return this.index;
	}

	/**
	 * Get the array of facts
	 * @return
	 */
	public Fact[] getLeftFacts() {
		return this.index.getFacts();
	}

	/**
	 * Return the array containing the facts entering
	 * the right input that matched
	 * @return
	 */
	public Iterator iterateRightFacts() {
		return this.matches.keySet().iterator();
	}

	/**
	 * The method will check to see if the fact has
	 * previously matched
	 * @param rightfacts
	 * @return
	 */
	public boolean matched(Fact rightfact) {
		return this.matches.containsKey(rightfact);
	}

	/**
	 * Add a match to the list
	 * @param rightfacts
	 */
	public void addMatch(Fact rightfact) {
		this.matches.put(rightfact, null);
	}

	public void removeMatch(Fact rightfact) {
		this.matches.remove(rightfact);
	}

	/**
	 * clear will clear the memory
	 */
	public void clear() {
		this.matches.clear();
		this.index = null;
	}

	/**
	 * method simply returns the size
	 */
	public int matchCount() {
		return matches.size();
	}

	/**
	 * The implementation will append the facts for the left followed
	 * by double colon "::" and then the matches from the right
	 */
	public String toPPString() {
		StringBuffer buf = new StringBuffer();
		for (int idx = 0; idx < this.index.getFacts().length; idx++) {
			if (idx > 0) {
				buf.append(", ");
			}
			buf.append(this.index.getFacts()[idx].getFactId());
		}
		buf.append(": ");
		Iterator itr = this.matches.keySet().iterator();
		while (itr.hasNext()) {
			Fact f = (Fact) itr.next();
			buf.append(f.getFactId() + ", ");
		}
		return buf.toString();
	}
}
