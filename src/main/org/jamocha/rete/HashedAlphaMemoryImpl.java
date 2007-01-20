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
import java.util.Map;
import java.util.Iterator;

import org.jamocha.rete.util.CollectionsFactory;


/**
 * @author Peter Lin
 *
 * Basic implementation of Alpha memory. It uses HashMap for storing
 * the indexes.
 */
public class HashedAlphaMemoryImpl implements Serializable {

    protected Map memory = null;
    
    protected int counter = 0;
    
	/**
	 * 
	 */
	public HashedAlphaMemoryImpl(String name) {
		super();
		memory = CollectionsFactory.newAlphaMemoryMap(name);
	}

	/**
     * addPartialMatch stores the fact with the factId as the
     * key.
	 */
	public void addPartialMatch(HashIndex index, Fact fact) {
		Map matches = (Map)this.memory.get(index);
		if (matches == null) {
			this.addNewPartialMatch(index,fact);
		} else {
			matches.put(fact,fact);
		}
		this.counter++;
	}
	
	public void addNewPartialMatch(HashIndex index, Fact fact) {
		Map matches = CollectionsFactory.newMap();
		matches.put(fact,fact);
		this.memory.put(index,matches);
	}

	/**
     * clear the memory.
	 */
	public void clear() {
		Iterator itr = this.memory.values().iterator();
		while (itr.hasNext()) {
			((Map)itr.next()).clear();
		}
        this.memory.clear();
	}

	public boolean isPartialMatch(HashIndex index, Fact fact) {
		Map list = (Map)this.memory.get(index);
		if (list != null) {
			return list.containsKey(fact);
		} else {
			return false;
		}
	}
	
	/**
     * remove a partial match from the memory
	 */
	public void removePartialMatch(HashIndex index, Fact fact) {
		Map list = (Map)this.memory.get(index);
		list.remove(fact);
		if (list.size() == 0) {
			this.memory.remove(index);
		}
		this.counter--;
	}

    /**
     * Return the number of memories of all hash buckets
     */
    public int size() {
    	Iterator itr = this.memory.keySet().iterator();
    	int count = 0;
    	while (itr.hasNext()) {
    		Map matches = (Map)this.memory.get(itr.next());
    		count += matches.size();
    	}
        return count;
    }

    public int bucketCount() {
    	return this.counter;
    }
    
    /**
     * Return an iterator of the values
     */
    public Iterator iterator(HashIndex index) {
    	Map list = (Map)this.memory.get(index);
		if (list != null) {
	        return list.values().iterator();
		} else {
			return null;
		}
    }
    
    public int count(HashIndex index) {
    	Map list = (Map)this.memory.get(index);
    	if (list != null) {
    		return list.size();
    	} else {
    		return 0;
    	}
    }
    
    /**
     * return an arraylist with all the facts
     * @return
     */
    public Object[] iterateAll() {
    	Object[] all = new Object[this.counter];
    	Iterator itr = this.memory.keySet().iterator();
    	int idx = 0;
    	while (itr.hasNext()) {
    		Map f = (Map)this.memory.get(itr.next());
    		Iterator itr2 = f.values().iterator();
    		while (itr2.hasNext()) {
        		all[idx] = itr2.next();
        		idx++;
    		}
    	}
    	return all;
    }
}
