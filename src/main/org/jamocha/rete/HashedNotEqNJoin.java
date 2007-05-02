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
import java.util.Map;

import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.exception.RetractException;

/**
 * @author Peter Lin
 * 
 * HashedNotEqBNode2 indexes the right input for joins that use
 * not equal to. It uses 2 levels of indexing. The first is the bindings
 * for equal to, the second is not equal to.
 */
public class HashedNotEqNJoin extends BaseJoin {

	static final long serialVersionUID = 0xDeadBeafCafeBabeL;

    /**
     * The operator for the join by default is equal. The the join
     * doesn't comparing values, the operator should be set to -1.
     */
    protected int operator = Constants.EQUAL;
    
    public HashedNotEqNJoin(int id){
        super(id);
    }

    /**
     * Set the bindings for this join
     * @param binds
     */
    public void setBindings(Binding[] binds){
        this.binds = binds;
    }
    
    /**
     * clear will clear the lists
     */
    public void clear(WorkingMemory mem){
        Map leftmem = (Map)mem.getBetaLeftMemory(this);
        HashedAlphaMemory2 rightmem = 
        	(HashedAlphaMemory2)mem.getBetaRightMemory(this);
        Iterator itr = leftmem.keySet().iterator();
        // first we iterate over the list for each fact
        // and clear it.
        while (itr.hasNext()){
            BetaMemory bmem = (BetaMemory)leftmem.get(itr.next());
            bmem.clear();
        }
        // now that we've cleared the list for each fact, we
        // can clear the Map.
        leftmem.clear();
        rightmem.clear();
    }

    /**
     * convienance method for getting the values based on the
     * bindings
     * @param ft
     * @return
     */
    protected BindValue[] getRightValues(Fact ft) {
    	BindValue[] vals = new BindValue[this.binds.length];
    	for (int idx=0; idx < this.binds.length; idx++) {
    		vals[idx] = new BindValue(
    			ft.getSlotValue(this.binds[idx].getRightIndex()),
    			this.binds[idx].negated());
    	}
    	return vals;
    }
    
    /**
     * get the values from the left side
     * @param facts
     * @return
     */
    protected BindValue[] getLeftValues(Fact[] facts) {
    	BindValue[] vals = new BindValue[this.binds.length];
    	for (int idx=0; idx < this.binds.length; idx++) {
    		vals[idx] = new BindValue(
    			facts[binds[idx].getLeftRow()].
    			getSlotValue(binds[idx].getLeftIndex()),
    			binds[idx].negated());
    	}
    	return vals;
    }
    
    /**
     * assertLeft takes an array of facts. Since the next join may be
     * joining against one or more objects, we need to pass all
     * previously matched facts.
     * @param factInstance
     * @param engine
     */
    public void assertLeft(Index linx, Rete engine, WorkingMemory mem) 
    throws AssertException
    {
        Map leftmem = (Map) mem.getBetaLeftMemory(this);

		leftmem.put(linx, linx);
		// need to think the getLeftValues through better to
		// account for cases when a join has no bindings
		NotEqHashIndex2 inx = new NotEqHashIndex2(getLeftValues(linx.getFacts()));
		HashedAlphaMemory2 rightmem = (HashedAlphaMemory2) mem
				.getBetaRightMemory(this);
		if (rightmem.zeroMatch(inx)) {
            this.propogateAssert(linx, engine, mem);
		}
    }

    /**
	 * Assert from the right side is always going to be from an Alpha node.
	 * 
	 * @param factInstance
	 * @param engine
	 */
    public void assertRight(Fact rfact, Rete engine, WorkingMemory mem)
    throws AssertException
    {
        // get the memory for the node
		HashedAlphaMemory2 rightmem = (HashedAlphaMemory2) mem
				.getBetaRightMemory(this);
		NotEqHashIndex2 inx = new NotEqHashIndex2(getRightValues(rfact));

		rightmem.addPartialMatch(inx, rfact);
        boolean zm = rightmem.zeroMatch(inx);
		Map leftmem = (Map) mem.getBetaLeftMemory(this);
		Iterator itr = leftmem.values().iterator();
		while (itr.hasNext()) {
			Index linx = (Index) itr.next();
			if (this.evaluate(linx.getFacts(), rfact)) {
                if (!zm) {
                    try {
                        this.propogateRetract(linx, engine, mem);
                    } catch (RetractException e) {
                        throw new AssertException("NotJion - " + e.getMessage());
                    }
                }
			}
		}
    }

    /**
	 * Retracting from the left requires that we propogate the
	 * 
	 * @param factInstance
	 * @param engine
	 */
    public void retractLeft(Index linx, Rete engine, WorkingMemory mem)
    throws RetractException
    {
        Map leftmem = (Map)mem.getBetaLeftMemory(this);
        leftmem.get(linx);
        propogateRetract(linx,engine,mem);
    }
    
    /**
     * Retract from the right works in the following order.
     * 1. remove the fact from the right memory
     * 2. check which left memory matched
     * 3. propogate the retract
     * @param factInstance
     * @param engine
     */
    public void retractRight(Fact rfact, Rete engine, WorkingMemory mem)
    throws RetractException
    {
    	NotEqHashIndex2 inx = new NotEqHashIndex2(getRightValues(rfact));
        HashedAlphaMemory2 rightmem = (HashedAlphaMemory2)mem.getBetaRightMemory(this);
        // first we remove the fact from the right
        rightmem.removePartialMatch(inx,rfact);
        boolean zm = rightmem.zeroMatch(inx);
        // now we see the left memory matched and remove it also
        Map leftmem = (Map)mem.getBetaLeftMemory(this);
        Iterator itr = leftmem.values().iterator();
        while (itr.hasNext()){
            Index linx = (Index)itr.next();
            if (this.evaluate(linx.getFacts(), rfact)){
                if (zm) {
                    try {
                        propogateAssert(linx,engine,mem);
                    } catch (AssertException e) {
                        throw new RetractException("NotJion - " + e.getMessage());
                    }
                }
            }
        }
    }

    /**
     * Method will use the right binding to perform the evaluation
     * of the join. Since we are building joins similar to how
     * CLIPS and other rule engines handle it, it means 95% of the
     * time the right fact list only has 1 fact.
     * @param leftlist
     * @param right
     * @return
     */
    public boolean evaluate(Fact[] leftlist, Fact right){
        boolean eval = true;
        // we iterate over the binds and evaluate the facts
        for (int idx=0; idx < this.binds.length; idx++){
            // we got the binding
        	if (binds[idx] instanceof Binding2) {
                Binding2 bnd = (Binding2)binds[idx];
                // we may want to consider putting the fact array into
                // a map to make it more efficient. for now I just want
                // to get it working.
                if (leftlist.length >= bnd.getLeftRow()) {
                    Fact left = leftlist[bnd.getLeftRow()];
                    if (left == right || !this.evaluate(
                            left,
                            bnd.getLeftIndex(),
                            right,
                            bnd.getRightIndex(),
                            bnd.getOperator())){
                        eval = false;
                        break;
                    }
                } else {
                	eval = false;
                }
        	} else if (binds[idx] instanceof Binding) {
                Binding bnd = binds[idx];
                int opr = this.operator;
                if (bnd.negated) {
                	opr = Constants.NOTEQUAL;
                }
                // we may want to consider putting the fact array into
                // a map to make it more efficient. for now I just want
                // to get it working.
                if (leftlist.length >= bnd.getLeftRow()) {
                    Fact left = leftlist[bnd.getLeftRow()];
                    if (left == right || !this.evaluate(
                            left,
                            bnd.getLeftIndex(),
                            right,
                            bnd.getRightIndex(),
                            opr)){
                        eval = false;
                        break;
                    }
                } else {
                	eval = false;
                }
        	}
        }
        return eval;
    }
    
    /**
     * Method will evaluate a single slot from the left against the right.
     * @param left
     * @param leftId
     * @param right
     * @param rightId
     * @return
     */
    public boolean evaluate(Fact left, int leftId, Fact right, int rightId, int op){
    	if (op == Constants.EQUAL) {
            return Evaluate.evaluateEqual(left.getSlotValue(leftId),
                    right.getSlotValue(rightId));
    	} else if (op == Constants.NOTEQUAL) {
            return Evaluate.evaluateNotEqual(left.getSlotValue(leftId),
                    right.getSlotValue(rightId));
    	} else {
            return Evaluate.evaluate(op,left.getSlotValue(leftId),
                    right.getSlotValue(rightId));
    	}
    }
    
    /**
     * Basic implementation will return string format of the betaNode
     */
    public String toString(){
        StringBuffer buf = new StringBuffer();
        for (int idx=0; idx < this.binds.length; idx++){
            if (idx > 0){
                buf.append(" && ");
            }
            buf.append(this.binds[idx].toBindString());
        }
        return buf.toString();
    }

    /**
     * returs the node name + id and bindings
     */
    public String toPPString(){
        StringBuffer buf = new StringBuffer();
        buf.append("HashedNotEqNJoin-" + this.nodeID + "> ");
        for (int idx=0; idx < this.binds.length; idx++){
            if (idx > 0){
                buf.append(" && ");
            }
            if (this.binds[idx] != null) {
                buf.append(this.binds[idx].toPPString());
            }
        }
        return buf.toString();
    }
}
