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
 * HashEqNJoin stands for Hashed Equal NotJoin. It is different from
 * NotJoin. The right facts are hashed to improve performance. This means
 * the node performs index joins to see if there's a matching facts on
 * the right side. If there is, the node will propogate without
 * performing evaluation. We can do this because only facts that match
 * would have the same index.
 */
public class HashedEqNJoin extends BaseJoin {

	
	static final long serialVersionUID = 0xDeadBeafCafeBabeL;
	
    /**
     * The operator for the join by default is equal. The the join
     * doesn't comparing values, the operator should be set to -1.
     */
    protected int operator = Constants.EQUAL;
    
    public HashedEqNJoin(int id){
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
        Map rightmem = (Map)mem.getBetaRightMemory(this);
        Map leftmem = (Map)mem.getBetaRightMemory(this);
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
    protected Object[] getRightValues(Fact ft) {
    	Object[] vals = new Object[this.binds.length];
    	for (int idx=0; idx < this.binds.length; idx++) {
    		vals[idx] = ft.getSlotValue(this.binds[idx].getRightIndex());
    	}
    	return vals;
    }
    
    /**
     * get the values from the left side
     * @param facts
     * @return
     */
    protected Object[] getLeftValues(Fact[] facts) {
    	Object[] vals = new Object[this.binds.length];
    	for (int idx=0; idx < this.binds.length; idx++) {
    		vals[idx] = facts[binds[idx].getLeftRow()].
    			getSlotValue(binds[idx].getLeftIndex());
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
		leftmem.put(linx,linx);
		EqHashIndex inx = new EqHashIndex(getLeftValues(linx.getFacts()));
		HashedAlphaMemoryImpl rightmem = (HashedAlphaMemoryImpl) mem
				.getBetaRightMemory(this);
		// we don't bother adding the right fact to the left, since
		// the right side is already Hashed
		if (rightmem.count(inx) == 0) {
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
		HashedAlphaMemoryImpl rightmem = (HashedAlphaMemoryImpl) mem
				.getBetaRightMemory(this);
		EqHashIndex inx = new EqHashIndex(getRightValues(rfact));
		// the count before adding
		int prevCount = rightmem.count(inx);
		rightmem.addPartialMatch(inx, rfact);
		int after = rightmem.count(inx);
		// now that we've added the facts to the list, we
		// proceed with evaluating the fact
		Map leftmem = (Map) mem.getBetaLeftMemory(this);
		Iterator itr = leftmem.values().iterator();
		while (itr.hasNext()) {
			Index linx = (Index) itr.next();
			if (this.evaluate(linx.getFacts(), rfact)) {
				if (prevCount == 0 && after != 0) {
					// we have to retract
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
	 * Retracting from the left is different than retractRight for couple of
	 * reasons.
	 * <ul>
	 * <li> NotJoin will only propogate the facts from the left</li>
	 * <li> NotJoin never needs to merge the left and right</li>
	 * </ul>
	 * 
	 * @param factInstance
	 * @param engine
	 */
    public void retractLeft(Index inx, Rete engine, WorkingMemory mem)
    throws RetractException
    {
        Map leftmem = (Map)mem.getBetaLeftMemory(this);
        // the left memory contains the fact array, so we 
        // retract it.
        if (leftmem.containsKey(inx)){
            // if watch is turned on, we send an event
            this.propogateRetract(inx,engine,mem);
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
        HashedAlphaMemoryImpl rightmem = 
        	(HashedAlphaMemoryImpl)mem.getBetaRightMemory(this);
        EqHashIndex inx = new EqHashIndex(getRightValues(rfact));
        if (rightmem.isPartialMatch(inx,rfact)){
        	// get the count before
        	int prevCount = rightmem.count(inx);
            // remove the fact from the right
            rightmem.removePartialMatch(inx,rfact);
            // get count after it's removed
            int after = rightmem.count(inx);
            // now we see the left memory matched and remove it also
            Map leftmem = (Map)mem.getBetaLeftMemory(this);
            Iterator itr = leftmem.values().iterator();
            while (itr.hasNext()){
                Index linx = (Index)itr.next();
                if (this.evaluate(linx.getFacts(), rfact)){
                    if (prevCount != 0 && after == 0 ) {
                        try {
                            propogateAssert(linx,engine,mem);
                        } catch (AssertException e) {
                            throw new RetractException("NotJion - " + e.getMessage());
                        }
                    }
                }
            }
            inx = null;
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
            Binding bnd = binds[idx];
            int opr = this.operator;
            if (bnd.negated) {
            	opr = Constants.NOTEQUAL;
            }
            // we may want to consider putting the fact array into
            // a map to make it more efficient. for now I just want
            // to get it working.
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
        return Evaluate.evaluate(op, left.getSlotValue(leftId), right
                .getSlotValue(rightId));
    }
    
    /**
     * NotJoin has to have a special addSuccessorNode since it needs
     * to just propogate the left facts if it has zero matches.
     */
    public void addSuccessorNode(TerminalNode node, Rete engine,
            WorkingMemory mem) throws AssertException {
        if (addNode(node)) {
            // first, we get the memory for this node
            Map leftmem = (Map) mem.getBetaLeftMemory(this);
            // now we iterate over the entry set
            Iterator itr = leftmem.values().iterator();
            while (itr.hasNext()) {
                Object omem = itr.next();
                if (omem instanceof BetaMemory) {
                    BetaMemory bmem = (BetaMemory) omem;
                    // get the Fact[] array for the left
                    // iterate over the matches
                    if (bmem.matchCount() == 0) {
                        node.assertFacts(bmem.getIndex(), engine, mem);
                    }
                }
            }
        }
    }

    /**
     * method returns a simple format for the node
     */
    public String toString(){
        StringBuffer buf = new StringBuffer();
        buf.append("HashedEqNJoin- ");
        for (int idx=0; idx < this.binds.length; idx++){
            if (idx > 0){
                buf.append(" && ");
            }
            buf.append(this.binds[idx].toBindString());
        }
        return buf.toString();
    }

    /**
     * The current implementation is similar to BetaNode
     */
    public String toPPString(){
        StringBuffer buf = new StringBuffer();
        buf.append("HashedEqNJoin-" + this.nodeID + "> ");
        for (int idx=0; idx < this.binds.length; idx++){
            if (idx > 0){
                buf.append(" && ");
            }
            buf.append(this.binds[idx].toPPString());
        }
        return buf.toString();
    }
}
