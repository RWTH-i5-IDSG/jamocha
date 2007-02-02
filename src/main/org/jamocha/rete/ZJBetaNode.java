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
 * ZJBetaNode is different than other BetaNodes in that it
 * has no bindings. We optimize the performance for those
 * cases by skipping evaluation and just propogate
 */
public class ZJBetaNode extends BaseJoin {

	/**
	 * The operator for the join by default is equal. The the join
	 * doesn't comparing values, the operator should be set to -1.
	 */
	protected int operator = Constants.EQUAL;

	public ZJBetaNode(int id) {
		super(id);
	}

	/**
	 * Set the bindings for this join
	 * @param binds
	 */
	public void setBindings(Binding[] binds) {
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
	public void assertLeft(Index inx, Rete engine, WorkingMemory mem)
			throws AssertException {
		Map leftmem = (Map) mem.getBetaLeftMemory(this);

		// we create a new list for storing the matches.
		// any fact that isn't in the list will be evaluated.
		BetaMemory bmem = new BetaMemoryImpl2(inx);
		leftmem.put(bmem.getIndex(), bmem);
		Map rightmem = (Map) mem.getBetaRightMemory(this);
		Iterator itr = rightmem.values().iterator();
		while (itr.hasNext()) {
			Fact rfcts = (Fact) itr.next();
			// now we propogate
			this.propogateAssert(inx.add(rfcts), engine, mem);
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
		Index inx = new Index(new Fact[] { rfact });
			rightmem.put(inx, rfact);
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
				// now we propogate
				this.propogateAssert(bmem.getIndex().add(rfact), engine, mem);
			}

	}

	/**
	 * Retracting from the left requires that we propogate the
	 * @param factInstance
	 * @param engine
	 */
	public void retractLeft(Index linx, Rete engine, WorkingMemory mem)
			throws RetractException {
		Map leftmem = (Map) mem.getBetaLeftMemory(this);
		if (leftmem.containsKey(linx)) {
			// the left memory contains the fact array, so we 
			// retract it.
			BetaMemory bmem = (BetaMemory) leftmem.remove(linx);
			Map rightmem = (Map) mem.getBetaRightMemory(this);
			Iterator itr = rightmem.values().iterator();
			while (itr.hasNext()) {
				propogateRetract(linx.add((Fact) itr
						.next()), engine, mem);
			}
			bmem.clear();
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
			// first we remove the fact from the right
			rightmem.remove(inx);
			// now we see the left memory matched and remove it also
			Map leftmem = (Map) mem.getBetaLeftMemory(this);
			Iterator itr = leftmem.values().iterator();
			while (itr.hasNext()) {
				BetaMemory bmem = (BetaMemory) itr.next();
				// now we propogate
				propogateRetract(bmem.getIndex().add(rfact), engine, mem);
			}
		}
	}

	/**
	 * Basic implementation will return string format of the betaNode
	 */
	public String toString() {
		return "ZJBetaNode";
	}

	/**
	 * implementation just returns the node id and the text
	 * zero-bind join.
	 */
	public String toPPString() {
		return "ZJBetaNode-" + this.nodeID + "> ";
	}
}
