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
import java.util.Iterator;
import java.util.Map;

import org.jamocha.rete.util.CollectionsFactory;


/**
 * @author Peter Lin
 * HashedAlphaMemory2 is different in that it has 2 levels of
 * indexing. The first handles equal to comparisons. The second
 * level handles not equal to.
 */
public class HashedAlphaMemory2 extends HashedAlphaMemoryImpl {

    /**
	 * 
	 */
	public HashedAlphaMemory2(String name) {
		super(name);
	}

	/**
     * addPartialMatch stores the fact with the factId as the
     * key.
	 */
	public void addPartialMatch(NotEqHashIndex2 index, Fact fact) {
		Map matches = (Map)this.memory.get(index);
		if (matches == null) {
			this.addNewPartialMatch(index,fact);
		} else {
			Map submatch = (Map)matches.get(index.getSubIndex());
			if (submatch == null) {
				submatch = CollectionsFactory.newHashMap();
				submatch.put(fact,fact);
				matches.put(index.getSubIndex(),submatch);
			} else {
				submatch.put(fact,fact);
			}
		}
		this.counter++;
	}
	
	public void addNewPartialMatch(NotEqHashIndex2 index, Fact fact) {
		Map matches = CollectionsFactory.newHashMap();
		Map submatch = CollectionsFactory.newHashMap();
		submatch.put(fact,fact);
		matches.put(index.getSubIndex(),submatch);
		this.memory.put(index,matches);
	}

	/**
     * clear the memory.
	 */
	public void clear() {
		Iterator itr = this.memory.keySet().iterator();
		while (itr.hasNext()) {
			Object key = itr.next();
			Map matches = (Map)this.memory.get(key);
			Iterator itr2 = matches.keySet().iterator();
			while (itr2.hasNext()) {
				Object subkey = itr2.next();
				Map submatch = (Map)matches.get(subkey);
				submatch.clear();
			}
			matches.clear();
		}
        this.memory.clear();
	}

	public boolean isPartialMatch(NotEqHashIndex2 index, Fact fact) {
		Map match = (Map)this.memory.get(index);
		if (match != null) {
			Map submatch = (Map)match.get(index.getSubIndex());
			if (submatch != null) {
				return submatch.containsKey(fact);
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	/**
     * remove a partial match from the memory
	 */
	public void removePartialMatch(NotEqHashIndex2 index, Fact fact) {
		Map match = (Map)this.memory.get(index);
		if (match != null) {
			Map submatch = (Map)match.get(index.getSubIndex());
			submatch.remove(fact);
			if (submatch.size() == 0) {
				match.remove(index.getSubIndex());
			}
			this.counter--;
		}
	}

    /**
     * Return the number of memories of all hash buckets
     */
    public int size() {
    	Iterator itr = this.memory.keySet().iterator();
    	int count = 0;
    	while (itr.hasNext()) {
    		Map matches = (Map)this.memory.get(itr.next());
    		Iterator itr2 = matches.keySet().iterator();
    		while (itr2.hasNext()) {
    			Map submatch = (Map)itr2.next();
        		count += submatch.size();
    		}
    	}
        return count;
    }

    public int bucketCount() {
    	return this.memory.size();
    }
    
    /**
     * Return an iterator of the values
     */
    public Object[] iterator(NotEqHashIndex2 index) {
    	Map matches = (Map)this.memory.get(index);
    	Object[] list = new Object[this.counter];
    	Object[] trim = null;
    	int idz = 0;
		if (matches != null) {
			Iterator itr = matches.keySet().iterator();
			while (itr.hasNext()) {
				Object key = itr.next();
				// if the key doesn't match the subindex, we
				// add it to the list. If it matches, we exclude
				// it.
				if (!index.getSubIndex().equals(key)) {
					Map submatch = (Map)matches.get(key);
					Iterator itr2 = submatch.keySet().iterator();
					while (itr2.hasNext()) {
						list[idz] = itr2.next();
						idz++;
					}
				}
				trim = new Object[idz];
				System.arraycopy(list,0,trim,0,idz);
			}
			list = null;
	        return trim;
		} else {
			return null;
		}
    }
    
    /**
     * if there are zero matches for the NotEqHashIndex2, the method
     * return true. If there are matches, the method returns false.
     * False means there's 1 or more matches
     * @param index
     * @return
     */
    public boolean zeroMatch(NotEqHashIndex2 index) {
        Map matches = (Map)this.memory.get(index);
        int idz = 0;
        if (matches != null) {
            Iterator itr = matches.keySet().iterator();
            while (itr.hasNext()) {
                Object key = itr.next();
                // if the key doesn't match the subindex, add it to the
                // counter.
                if (!index.getSubIndex().equals(key)) {
                    Map submatch = (Map)matches.get(key);
                    idz += submatch.size();
                }
                if (idz > 0) {
                    break;
                }
            }
            return false;
        } else {
            return true;
        }
    }
    
    /**
     * return an arraylist with all the facts
     * @return
     */
    public Object[] iterateAll() {
    	Object[] facts = new Object[this.counter];
    	Iterator itr = this.memory.keySet().iterator();
    	int idx = 0;
    	while (itr.hasNext()) {
    		Map matches = (Map)this.memory.get(itr.next());
    		Iterator itr2 = matches.keySet().iterator();
    		while (itr2.hasNext()) {
    			Map submatch = (Map)matches.get(itr2.next());
    			Iterator itr3 = submatch.values().iterator();
    			while (itr3.hasNext()) {
        			facts[idx] = itr3.next();
        			idx++;
    			}
    		}
    	}
    	Object[] trim = new Object[idx];
    	System.arraycopy(facts,0,trim,0,idx);
    	facts = null;
    	return trim;
    }
}
