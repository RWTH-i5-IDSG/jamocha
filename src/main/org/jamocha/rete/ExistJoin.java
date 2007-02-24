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
import java.util.List;
import java.util.Map;

import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.exception.RetractException;

/**
 * @author Peter Lin
 * 
 * ExistJoin is implemented differently than how CLIPS does it. According
 * to CLIPS beginners guide, Exist is convert to (not (not (blah) ) ).
 * Rather than do that, I'm experimenting with a specialized Existjoin
 * node instead. The benefit is reduce memory and fewer nodes in the 
 * network. 
 */
public class ExistJoin extends BaseJoin {

	/**
	 * The operator for the join by default is equal. The the join
	 * doesn't comparing values, the operator should be set to -1.
	 */
	protected int operator = Constants.EQUAL;

	/**
	 * for convienance, we have a field for the current BetaMemory
	 * being evaluated.
	 */
	protected BetaMemory currentMem = null;

	public ExistJoin(int id) {
		super(id);
	}

	/**
	 * Set the bindings for this join
	 * @param binds
	 */
	public void setBindings(Binding[] binds) {
		this.binds = binds;
	}

	/**
	 * clear will clear the lists
	 */
	public void clear(WorkingMemory mem) {
		Map rightmem = (Map) mem.getBetaRightMemory(this);
		Map leftmem = (Map) mem.getBetaRightMemory(this);
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
		rightmem.clear();
	}

	/**
	 * assertLeft takes an array of facts. Since the next join may be
	 * joining against one or more objects, we need to pass all
	 * previously matched facts.
	 * @param factInstance
	 * @param engine
	 */
	public void assertLeft(Index linx, Rete engine, WorkingMemory mem)
			throws AssertException {
		Map leftmem = (Map) mem.getBetaLeftMemory(this);
		if (!leftmem.containsKey(linx)) {
			// we create a new list for storing the matches.
			// any fact that isn't in the list will be evaluated.
			BetaMemory bmem = new BetaMemoryImpl(linx);
			this.currentMem = bmem;
			leftmem.put(bmem.getIndex(), bmem);
			Map rightmem = (Map) mem.getBetaRightMemory(this);
			int prevCount = currentMem.matchCount();
			Iterator itr = rightmem.values().iterator();
			while (itr.hasNext()) {
				Fact rfcts = (Fact) itr.next();
				if (this.evaluate(linx.getFacts(), rfcts)) {
					// it matched, so we add it to the beta memory
					this.currentMem.addMatch(rfcts);
				}
			}
			// if the previous count was zero and now the match 
			// count is 1, we propogate
			if (prevCount == 0 && this.currentMem.matchCount() > 0) {
				// there should only be one match, so we get the item
				// at index zero. if the previous count was 1 or greater,
				// we don't propogate
				this.propogateAssert(linx, engine, mem);
			}
		}
	}

	/**
	 * Assert from the right side is always going to be from an
	 * Alpha node.
	 * @param factInstance
	 * @param engine
	 */
	public void assertRight(Fact rfact, Rete engine, WorkingMemory mem)
			throws AssertException {
		// we only proceed if the fact hasn't already entered
		// the join node
		Index inx = new Index(new Fact[] { rfact });
		Map rightmem = (Map) mem.getBetaRightMemory(this);
		if (!rightmem.containsKey(inx)) {
			rightmem.put(inx, rfact);
			// now that we've added the facts to the list, we
			// proceed with evaluating the fact
			Map leftmem = (Map) mem.getBetaLeftMemory(this);
			Iterator itr = leftmem.values().iterator();
			while (itr.hasNext()) {
				BetaMemory bmem = (BetaMemory) itr.next();
				Fact[] lfcts = bmem.getLeftFacts();
				int prevCount = bmem.matchCount();
				if (this.evaluate(lfcts, rfact)) {
					bmem.addMatch(rfact);
				}
				// if the previous count was zero and now the match 
				// count is 1, we propogate
				if (prevCount == 0 && bmem.matchCount() == 1) {
					this.propogateAssert(bmem.getIndex(), engine, mem);
				}
			}
		}
	}

	/**
	 * Retracting from the left is different than retractRight for couple
	 * of reasons.
	 * <ul>
	 * <li> NotJoin will only propogate the facts from the left</li>
	 * <li> NotJoin never needs to merge the left and right</li>
	 * </ul>
	 * @param factInstance
	 * @param engine
	 */
	public void retractLeft(Index inx, Rete engine, WorkingMemory mem)
			throws RetractException {
		Map leftmem = (Map) mem.getBetaLeftMemory(this);
		if (leftmem.containsKey(inx)) {
			// the left memory contains the fact array, so we 
			// retract it.
			BetaMemory bmem = (BetaMemory) leftmem.remove(inx);
			propogateRetract(inx, engine, mem);
			bmem.clear();
			bmem = null;
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
			throws RetractException {
		Index inx = new Index(new Fact[] { rfact });
		Map rightmem = (Map) mem.getBetaRightMemory(this);
		if (rightmem.containsKey(inx)) {
			rightmem.remove(inx);
			// now we see the left memory matched and remove it also
			Map leftmem = (Map) mem.getBetaLeftMemory(this);
			Iterator itr = leftmem.values().iterator();
			while (itr.hasNext()) {
				BetaMemory bmem = (BetaMemory) itr.next();
				int previous = bmem.matchCount();
				if (bmem.matched(rfact)) {
					bmem.removeMatch(rfact);
					if (previous != 0 && bmem.matchCount() == 0) {
						propogateRetract(bmem.getIndex(), engine, mem);
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
	public boolean evaluate(Fact[] leftlist, Fact right) {
		boolean eval = true;
		// we iterate over the binds and evaluate the facts
		for (int idx = 0; idx < this.binds.length; idx++) {
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
			if (left == right
					|| !this.evaluate(left, bnd.getLeftIndex(), right, bnd
							.getRightIndex(), opr)) {
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
	public boolean evaluate(Fact left, int leftId, Fact right, int rightId,
			int opr) {
		if (opr == Constants.NOTEQUAL) {
			return Evaluate.evaluateNotEqual(left.getSlotValue(leftId), right
					.getSlotValue(rightId));
		} else {
			return Evaluate.evaluateEqual(left.getSlotValue(leftId), right
					.getSlotValue(rightId));
		}
	}

	/**
	 * simple implementation for toString. may need to change the format
	 * later so it looks nicer.
	 */
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("Exist - ");
		for (int idx = 0; idx < this.binds.length; idx++) {
			if (idx > 0) {
				buf.append(" && ");
			}
			buf.append(this.binds[idx].toBindString());
		}
		return buf.toString();
	}

	/**
	 * The current implementation is similar to BetaNode
	 */
	public String toPPString() {
		StringBuffer buf = new StringBuffer();
		buf.append("<node-" + this.nodeID + "> Exist - ");
		for (int idx = 0; idx < this.binds.length; idx++) {
			if (idx > 0) {
				buf.append(" && ");
			}
			buf.append(this.binds[idx].toBindString());
		}
		return buf.toString();
	}
}
