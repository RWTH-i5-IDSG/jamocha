/*
 * Copyright 2002-2007 Peter Lin
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
package org.jamocha.rete;

import java.io.Serializable;

/**
 * @author Peter Lin<p/>
 *
 * EqHashIndex is used by the BetaNode for indexing the facts that
 * enter from the right.
 */
public class EqHashIndex implements HashIndex, Serializable {

	static final long serialVersionUID = 0xDeadBeafCafeBabeL;
	
    private Object[] values = null;
    private int hashCode;
    
	/**
	 * 
	 */
	public EqHashIndex(Object[] thevalues) {
		super();
		this.values = thevalues;
        calculateHash();
	}
    
    /**
     * This is a very simple implementation that gets the slot hash from
     * the deffact.
     */
    private void calculateHash() {
    	if (this.values != null && this.values.length > 0) {
        	for (int idx=0; idx < values.length; idx++) {
        		if (values[idx] != null) {
            		this.hashCode += values[idx].hashCode();
        		}
        	}
    	}
    }
    
    public void clear() {
    	this.values = null;
    }
    
    /**
     * The implementation is similar to the index class.
     */
    public boolean equals(Object val) {
        if (this == val) {
            return true;
        }
        if (val == null || !(val instanceof EqHashIndex) ) {
            return false;
        }
        EqHashIndex eval = (EqHashIndex)val;
        boolean eq = true;
        for (int idx=0; idx < values.length; idx++) {
        	if (!eval.values[idx].equals(this.values[idx])) {
        		eq = false;
        		break;
        	}
        }
        return eq;
    }
    
    /**
     * Method simply returns the cached hashCode.
     */
    public int hashCode() {
        return this.hashCode;
    }
    
}
