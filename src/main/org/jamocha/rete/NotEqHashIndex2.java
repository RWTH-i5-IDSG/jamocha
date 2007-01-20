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

/**
 * @author Peter Lin<p/>
 *
 * NotEqHashIndex is different from EqHashIndex is 2 ways. The first is
 * it only uses the values of equality comparison and ignores the not
 * equal values. The second is it takes BindValue[] instead of just
 * Object[].
 */
public class NotEqHashIndex2 implements HashIndex, Serializable {

    private BindValue[] values = null;
    private int eqhashCode;
    private EqHashIndex negindex;
    
	/**
	 * 
	 */
	public NotEqHashIndex2(BindValue[] thevalues) {
		super();
		this.values = thevalues;
        calculateHash();
	}
    
    /**
     * The implementation is different than EqHashIndex. It ignores
     * any Bindings that are negated
     */
    private void calculateHash() {
    	Object[] neg = new Object[this.values.length];
    	int z = 0;
    	if (this.values != null && this.values.length > 0) {
        	for (int idx=0; idx < values.length; idx++) {
        		if (values[idx] != null && !values[idx].negated()) {
            		this.eqhashCode += values[idx].getValue().hashCode();
        		} else {
        			neg[z] = values[idx].getValue();
        			z++;
        		}
        	}
    	}
    	Object[] neg2 = new Object[z];
    	System.arraycopy(neg,0,neg2,0,z);
    	negindex = new EqHashIndex(neg2);
    	neg = null;
    	neg2 = null;
    }
    
    public void clear() {
    	this.negindex.clear();
    	this.values = null;
    }
    
    /**
     * return the subindex
     * @return
     */
    public EqHashIndex getSubIndex() {
    	return negindex;
    }
    
    /**
     * The implementation is similar to the index class.
     */
    public boolean equals(Object val) {
        if (this == val) {
            return true;
        }
        if (val == null || !(val instanceof NotEqHashIndex2) ) {
            return false;
        }
        NotEqHashIndex2 eval = (NotEqHashIndex2)val;
        boolean eq = true;
        for (int idx=0; idx < values.length; idx++) {
        	if (!values[idx].negated() && 
        			!eval.values[idx].getValue().equals(this.values[idx].getValue())) {
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
        return this.eqhashCode;
    }
    
}
