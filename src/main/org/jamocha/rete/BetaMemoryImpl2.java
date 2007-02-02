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

import java.util.Iterator;

/**
 * @author Peter Lin
 * 
 * BetaMemory stores the matches
 */
public class BetaMemoryImpl2 implements BetaMemory {

	protected Index index = null;

	/**
	 * 
	 */
	public BetaMemoryImpl2(Index index) {
		super();
		this.index = index;
	}

	/**
	 * Return the index of the beta memory
	 * 
	 * @return
	 */
	public Index getIndex() {
		return this.index;
	}

	/**
	 * Get the array of facts
	 * 
	 * @return
	 */
	public Fact[] getLeftFacts() {
		return this.index.getFacts();
	}

	/**
	 * this version doesn't store the right matches, so it returns null
	 * 
	 * @return
	 */
	public Iterator iterateRightFacts() {
		return new Iterator() {

			public boolean hasNext() {
				return false;
			}

			public Object next() {
				return null;
			}

			public void remove() {
			}

		};
	}

	/**
	 * method is not implemented for this version, since it does not store the
	 * matching facts from the right
	 * 
	 * @param rightfacts
	 * @return
	 */
	public boolean matched(Fact rightfact) {
		return false;
	}

	/**
	 * method is not implemented for this version, since it does not store the
	 * matching facts from the right
	 * 
	 * @param rightfacts
	 */
	public void addMatch(Fact rightfact) {
	}

	/**
	 * method is not implemented for this version, since it does not store the
	 * matching facts from the right
	 */
	public void removeMatch(Fact rightfact) {
	}

	/**
	 * clear will clear the memory
	 */
	public void clear() {
		this.index = null;
	}

	/**
	 * method simply returns the size
	 */
	public int matchCount() {
		return 0;
	}

	/**
	 * The implementation will append the facts for the left followed by double
	 * colon "::" and then the matches from the right
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
		return buf.toString();
	}
}
