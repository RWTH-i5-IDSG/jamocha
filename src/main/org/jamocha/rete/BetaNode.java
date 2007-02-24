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

import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.exception.RetractException;

/**
 * @author Peter Lin
 * 
 * BetaNode is the basic class for join nodes. The implementation uses
 * BetaMemory for the left and AlphaMemory for the right. When the left
 * and right match, the facts are merged and propogated to the
 * succeeding nodes. This is an important distinction for a couple of
 * reasons.
 * 
 * 1. The next join may join one or more objects.
 * 2. Rather than store the facts needed for the next join in a global
 * map of map, it's more efficient to simply merge the two arrays and
 * pass it on.
 * 3. It isn't sufficient to pass just the bound attributes of this
 * node to the next.
 * 
 * Some important notes. If a rule defines a join, which doesn't compare
 * a slot from one fact against the slot of a different fact, the node
 * simply propogates.
 */
public class BetaNode extends BaseJoin {

	/**
	 * The operator for the join by default is equal. The the join
	 * doesn't comparing values, the operator should be set to -1.
	 */
	protected int operator = Constants.EQUAL;

	public BetaNode(int id) {
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
		Map leftmem = (Map) mem.getBetaLeftMemory(this);
		Map rightmem = (Map) mem.getBetaRightMemory(this);
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
	public void assertLeft(Index index, Rete engine, WorkingMemory mem)
			throws AssertException {
		Map leftmem = (Map) mem.getBetaLeftMemory(this);

		BetaMemory bmem = new BetaMemoryImpl(index);
		leftmem.put(bmem.getIndex(), bmem);
		Map rightmem = (Map) mem.getBetaRightMemory(this);
		Iterator itr = rightmem.values().iterator();
		while (itr.hasNext()) {
			Fact rfcts = (Fact) itr.next();
			if (this.evaluate(index.getFacts(), rfcts)) {
				// it matched, so we add it to the beta memory
				bmem.addMatch(rfcts);
				// now we propogate
				this.propogateAssert(index.add(rfcts), engine, mem);
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
		Map rightmem = (Map) mem.getBetaRightMemory(this);
		Index index = new Index(new Fact[] { rfact });

		rightmem.put(index, rfact);
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
				bmem.addMatch(rfact);
				// now we propogate
				Fact[] merged = ConversionUtils.mergeFacts(lfcts, rfact);
				this.propogateAssert(index.add(rfact), engine, mem);
			}
		}
	}

	/**
	 * Retracting from the left requires that we propogate the
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
			// now we propogate the retract. To do that, we have
			// merge each item in the list with the Fact array
			// and call retract in the successor nodes
			Iterator itr = bmem.iterateRightFacts();
			while (itr.hasNext()) {
				propogateRetract(inx.add((Fact) itr.next()), engine, mem);
			}
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
				if (bmem.matched(rfact)) {
					bmem.removeMatch(rfact);
					// it matched, so we need to retract it from
					// succeeding nodes
					propogateRetract(bmem.getIndex().add(rfact), engine, mem);
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
	 * @param left
	 * @param leftId
	 * @param right
	 * @param rightId
	 * @return
	 */
	public boolean evaluate(Fact left, int leftId, Fact right, int rightId,
			int op) {
		if (op == Constants.EQUAL) {
			return Evaluate.evaluateEqual(left.getSlotValue(leftId), right
					.getSlotValue(rightId));
		} else if (op == Constants.NOTEQUAL) {
			return Evaluate.evaluateNotEqual(left.getSlotValue(leftId), right
					.getSlotValue(rightId));
		} else {
			return Evaluate.evaluate(op, left.getSlotValue(leftId), right
					.getSlotValue(rightId));
		}
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
	 * method returns a string of the node id and bindings details
	 */
	public String toPPString() {
		StringBuffer buf = new StringBuffer();
		buf.append("BetaNode-" + this.nodeID + "> ");
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
