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

import java.util.Iterator;
import java.util.Map;

import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.exception.RetractException;
import org.jamocha.rete.nodes.BaseJoin;

/**
 * @author Peter Lin
 * 
 * ExistJoinFrst is a special implementation for situations
 * when the first Conditional Element is an Exists. The main
 * difference is the left input is a dummy and doesn't do
 * anything. This gets around needing an InitialFact when the
 * first CE is Exists. 
 */
public class ExistJoinFrst extends BaseJoin {

	static final long serialVersionUID = 0xDeadBeafCafeBabeL;
	
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

	public ExistJoinFrst(int id) {
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
	 * assertLeft is a dummy, since we don't need an initial
	 * fact or LeftInputAdapater.
	 * @param factInstance
	 * @param engine
	 */
	public void assertLeft(Index lfacts, Rete engine, WorkingMemory mem)
			throws AssertException {
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
			int count = rightmem.size();
			rightmem.put(inx, rfact);
			// now that we've added the facts to the list, we
			// proceed with evaluating the fact
			if (count == 0 && rightmem.size() == 1) {
				Fact[] lfcts = new Fact[]{rfact};
				this.propogateAssert(inx, engine, mem);
			}
		}
	}

	/**
	 * retractLeft is a dummy and doesn't do anything
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
			int count = rightmem.size();
			rightmem.remove(inx);
			if (count == 1 && rightmem.size() == 0) {
				propogateRetract(inx, engine, mem);
			}
		}
	}

	/**
	 * method returns string format for the node
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
