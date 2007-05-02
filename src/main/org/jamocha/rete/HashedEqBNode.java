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

import java.util.Map;
import java.util.Iterator;

import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.exception.RetractException;

/**
 * @author Peter Lin
 * 
 * HashedBetaNode indexes the right input to improve cross product performance.
 */
public class HashedEqBNode extends BaseJoin {

	static final long serialVersionUID = 0xDeadBeafCafeBabeL;
	
    /**
     * The operator for the join by default is equal. The the join doesn't
     * comparing values, the operator should be set to -1.
     */
    protected int operator = Constants.EQUAL;

    public HashedEqBNode(int id) {
        super(id);
    }

    /**
     * Set the bindings for this join
     * 
     * @param binds
     */
    public void setBindings(Binding[] binds) {
        this.binds = binds;
    }

    /**
     * clear will clear the lists
     */
    public void clear(WorkingMemory mem) {
        Map leftmem = (Map) mem.getBetaLeftMemory(this);
        HashedAlphaMemoryImpl rightmem = (HashedAlphaMemoryImpl) mem
                .getBetaRightMemory(this);
        Iterator itr = leftmem.keySet().iterator();
        // first we iterate over the list for each fact
        // and clear it.
        while (itr.hasNext()) {
            BetaMemory bmem = (BetaMemory) leftmem.get(itr.next());
            bmem.clear();
        }
        // now that we've cleared the list for each fact, we
        // can clear the Map.
        leftmem.clear();
        // TODO clear the right memory
        rightmem.clear();
    }

    /**
     * convienance method for getting the values based on the bindings
     * 
     * @param ft
     * @return
     */
    protected Object[] getRightValues(Fact ft) {
        Object[] vals = new Object[this.binds.length];
        for (int idx = 0; idx < this.binds.length; idx++) {
            vals[idx] = ft.getSlotValue(this.binds[idx].getRightIndex());
        }
        return vals;
    }

    /**
     * get the values from the left side
     * 
     * @param facts
     * @return
     */
    protected Object[] getLeftValues(Fact[] facts) {
        Object[] vals = new Object[this.binds.length];
        for (int idx = 0; idx < this.binds.length; idx++) {
            vals[idx] = facts[binds[idx].getLeftRow()].getSlotValue(binds[idx]
                    .getLeftIndex());
        }
        return vals;
    }

    /**
     * assertLeft takes an array of facts. Since the next join may be joining
     * against one or more objects, we need to pass all previously matched
     * facts.
     * 
     * @param factInstance
     * @param engine
     */
    public void assertLeft(Index linx, Rete engine, WorkingMemory mem)
            throws AssertException {
        Map leftmem = (Map) mem.getBetaLeftMemory(this);
        // we expect the fact hasn't already entered the node
        // and the RETE network is generated correctly. If it
        // isn't, it could cause the same facts to enter the
        // node multiple times and have negative effects.
        leftmem.put(linx, linx);
        EqHashIndex inx = new EqHashIndex(getLeftValues(linx.getFacts()));
        HashedAlphaMemoryImpl rightmem = (HashedAlphaMemoryImpl) mem
                .getBetaRightMemory(this);
        Iterator itr = rightmem.iterator(inx);
        if (itr != null) {
            while (itr.hasNext()) {
                Fact vl = (Fact) itr.next();
                if (vl != null) {
                    this.propogateAssert(linx.add(vl), engine, mem);
                }
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
            throws AssertException {
        HashedAlphaMemoryImpl rightmem = (HashedAlphaMemoryImpl) mem
                .getBetaRightMemory(this);
        EqHashIndex inx = new EqHashIndex(getRightValues(rfact));

        rightmem.addPartialMatch(inx, rfact);
        // now that we've added the facts to the list, we
        // proceed with evaluating the fact
        Map leftmem = (Map) mem.getBetaLeftMemory(this);
        // since there may be key collisions, we iterate over the
        // values of the HashMap. If we used keySet to iterate,
        // we could encounter a ClassCastException in the case of
        // key collision.
        Iterator itr = leftmem.values().iterator();
        while (itr.hasNext()) {
            Index linx = (Index) itr.next();
            Fact[] lfcts = linx.getFacts();
            if (this.evaluate(lfcts, rfact)) {
                // now we propogate
                this.propogateAssert(linx.add(rfact), engine, mem);
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
            throws RetractException {
        Map leftmem = (Map) mem.getBetaLeftMemory(this);
        if (leftmem.containsKey(linx)) {

            EqHashIndex eqinx = new EqHashIndex(getLeftValues(linx.getFacts()));
            HashedAlphaMemoryImpl rightmem = (HashedAlphaMemoryImpl) mem
                    .getBetaRightMemory(this);

            // now we propogate the retract. To do that, we have
            // merge each item in the list with the Fact array
            // and call retract in the successor nodes
            Iterator itr = rightmem.iterator(eqinx);
            if (itr != null) {
                while (itr.hasNext()) {
                    propogateRetract(linx.add((Fact)itr.next()), engine, mem);
                }
            }
        }
    }

    /**
     * Retract from the right works in the following order. 1. remove the fact
     * from the right memory 2. check which left memory matched 3. propogate the
     * retract
     * 
     * @param factInstance
     * @param engine
     */
    public void retractRight(Fact rfact, Rete engine, WorkingMemory mem)
            throws RetractException {
        EqHashIndex inx = new EqHashIndex(getRightValues(rfact));
        HashedAlphaMemoryImpl rightmem = (HashedAlphaMemoryImpl) mem
                .getBetaRightMemory(this);
        if (rightmem.isPartialMatch(inx, rfact)) {
            // first we remove the fact from the right
            rightmem.removePartialMatch(inx, rfact);
            // now we see the left memory matched and remove it also
            Map leftmem = (Map) mem.getBetaLeftMemory(this);
            Iterator itr = leftmem.values().iterator();
            while (itr.hasNext()) {
                Index linx = (Index) itr.next();
                if (this.evaluate(linx.getFacts(), rfact)) {
                    // it matched, so we need to retract it from
                    // succeeding nodes
                    propogateRetract(linx.add(rfact), engine, mem);
                }
            }
        } else {
            inx.clear();
        }
    }

    /**
     * Method will use the right binding to perform the evaluation of the join.
     * Since we are building joins similar to how CLIPS and other rule engines
     * handle it, it means 95% of the time the right fact list only has 1 fact.
     * 
     * @param leftlist
     * @param right
     * @return
     */
    public boolean evaluate(Fact[] leftlist, Fact right) {
        boolean eval = true;
        // we iterate over the binds and evaluate the facts
        for (int idx = 0; idx < this.binds.length; idx++) {
            // we got the binding
            if (binds[idx] instanceof Binding2) {
                Binding2 bnd = (Binding2) binds[idx];
                // we may want to consider putting the fact array into
                // a map to make it more efficient. for now I just want
                // to get it working.
                if (leftlist.length >= bnd.getLeftRow()) {
                    Fact left = leftlist[bnd.getLeftRow()];
                    if (left == right
                            || !this.evaluate(left, bnd.getLeftIndex(), right,
                                    bnd.getRightIndex(), bnd.getOperator())) {
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
                    if (left == right
                            || !this.evaluate(left, bnd.getLeftIndex(), right,
                                    bnd.getRightIndex(), opr)) {
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
     * 
     * @param left
     * @param leftId
     * @param right
     * @param rightId
     * @return
     */
    public boolean evaluate(Fact left, int leftId, Fact right, int rightId,
            int op) {
        return Evaluate.evaluate(op, left.getSlotValue(leftId), right
                .getSlotValue(rightId));
    }

    /**
     * Basic implementation will return string format of the betaNode
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        for (int idx = 0; idx < this.binds.length; idx++) {
            if (idx > 0) {
                buf.append(" && ");
            }
            buf.append(this.binds[idx].toBindString());
        }
        return buf.toString();
    }

    /**
     * returns the node named + node id and the bindings in a string format
     */
    public String toPPString() {
        StringBuffer buf = new StringBuffer();
        buf.append("HashedEqBNode-" + this.nodeID + "> ");
        for (int idx = 0; idx < this.binds.length; idx++) {
            if (idx > 0) {
                buf.append(" && ");
            }
            if (this.binds[idx] != null) {
                buf.append(this.binds[idx].toPPString());
            }
        }
        return buf.toString();
    }
}
