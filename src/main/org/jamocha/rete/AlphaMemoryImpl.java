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
import java.util.Iterator;

import org.jamocha.rete.util.CollectionsFactory;


/**
 * @author Peter Lin
 *
 * Basic implementation of Alpha memory. It uses HashMap for storing
 * the indexes.
 */
public class AlphaMemoryImpl implements AlphaMemory {

	private Map memory = null;

	/**
	 * 
	 */
	public AlphaMemoryImpl(String name) {
		super();
		memory = CollectionsFactory.newAlphaMemoryMap(name);
	}

	/**
	 * addPartialMatch stores the fact with the factId as the
	 * key.
	 */
	public void addPartialMatch(Fact fact) {
		this.memory.put(fact, fact);
	}

	/**
	 * clear the memory.
	 */
	public void clear() {
		this.memory.clear();
	}

	/**
	 * check a fact to see if it is a partial match
	 */
	public boolean isPartialMatch(Fact fact) {
		return this.memory.containsKey(fact);
	}

	/**
	 * remove a partial match from the memory
	 */
	public void removePartialMatch(Fact fact) {
		this.memory.remove(fact);
	}

	/**
	 * Return the size of the memory
	 */
	public int size() {
		return this.memory.size();
	}

	/**
	 * Return an iterator of the values
	 */
	public Iterator iterator() {
		return this.memory.keySet().iterator();
	}
}
