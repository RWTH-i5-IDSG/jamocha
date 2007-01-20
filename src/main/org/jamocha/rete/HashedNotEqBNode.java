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

import java.util.ArrayList;
import java.util.Map;
import java.util.Iterator;

import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.exception.RetractException;

/**
 * @author Peter Lin
 * 
 * HashedNotEqBNode2 indexes the right input for joins that use
 * not equal to. It uses 2 levels of indexing. The first is the bindings
 * for equal to, the second is not equal to.
 */
public class HashedNotEqBNode extends BaseJoin {

    /**
     * The operator for the join by default is equal. The the join
     * doesn't comparing values, the operator should be set to -1.
     */
    protected int operator = Constants.EQUAL;
    
    /**
     * binding for the join
     */
    protected Binding[] binds = null;

    public HashedNotEqBNode(int id){
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
    public void assertLeft(Fact[] lfacts, Rete engine, WorkingMemory mem) 
    throws AssertException
    {
        Map leftmem = (Map) mem.getBetaLeftMemory(this);
		Index linx = new Index(lfacts);

		BetaMemory bmem = new BetaMemoryImpl2(linx);
		leftmem.put(bmem.getIndex(), bmem);
		// need to think the getLeftValues through better to
		// account for cases when a join has no bindings
		NotEqHashIndex2 inx = new NotEqHashIndex2(getLeftValues(lfacts));
		HashedAlphaMemory2 rightmem = (HashedAlphaMemory2) mem
				.getBetaRightMemory(this);
		Object[] objs = rightmem.iterator(inx);
		if (objs != null && objs.length > 0) {
			for (int idx = 0; idx < objs.length; idx++) {
				Fact rfcts = (Fact) objs[idx];
				// now we propogate
				Fact[] merged = ConversionUtils.mergeFacts(lfacts, rfcts);
				this.propogateAssert(merged, engine, mem);
			}
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
		// now that we've added the facts to the list, we
		// proceed with evaluating the fact
		// else we compare the fact to all facts in the left
		Map leftmem = (Map) mem.getBetaLeftMemory(this);
		// since there may be key collisions, we iterate over the
		// values of the HashMap. If we used keySet to iterate,
		// we could encounter a ClassCastException in the case of
		// key collision.
		Iterator itr = leftmem.values().iterator();
		while (itr.hasNext()) {
			BetaMemory bmem = (BetaMemory) itr.next();
			Fact[] lfcts = bmem.getLeftFacts();
			if (this.evaluate(lfcts, rfact)) {
				// now we propogate
				Fact[] merged = ConversionUtils.mergeFacts(lfcts, rfact);
				this.propogateAssert(merged, engine, mem);
			}
		}
    }

    /**
	 * Retracting from the left requires that we propogate the
	 * 
	 * @param factInstance
	 * @param engine
	 */
    public void retractLeft(Fact[] lfacts, Rete engine, WorkingMemory mem)
    throws RetractException
    {
        Index linx = new Index(lfacts);
        Map leftmem = (Map)mem.getBetaLeftMemory(this);
        if (leftmem.containsKey(linx)){
    		NotEqHashIndex2 eqinx = new NotEqHashIndex2(getLeftValues(lfacts));
    		HashedAlphaMemory2 rightmem = (HashedAlphaMemory2) mem
    				.getBetaRightMemory(this);
    		Object[] objs = rightmem.iterator(eqinx);
            for (int idx=0; idx < objs.length; idx++){
                Fact[] merged = ConversionUtils.mergeFacts(lfacts,(Fact)objs[idx]);
                propogateRetract(merged,engine,mem);
            }
            linx.clear();
        } else {
        	linx.clear();
        }
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
        if (rightmem.isPartialMatch(inx,rfact)){
            // first we remove the fact from the right
            rightmem.removePartialMatch(inx,rfact);
            // now we see the left memory matched and remove it also
            Map leftmem = (Map)mem.getBetaLeftMemory(this);
            Iterator itr = leftmem.values().iterator();
            while (itr.hasNext()){
                BetaMemory bmem = (BetaMemory)itr.next();
                if (this.evaluate(bmem.getLeftFacts(), rfact)){
                    // it matched, so we need to retract it from
                    // succeeding nodes
                    Fact[] merged = 
                        ConversionUtils.mergeFacts(bmem.getLeftFacts(),rfact);
                    propogateRetract(merged,engine,mem);
                }
            }
        } else {
        	inx.clear();
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
        buf.append("HNEqBNode-" + this.nodeID + "> ");
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
