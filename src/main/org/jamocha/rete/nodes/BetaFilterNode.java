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
package org.jamocha.rete.nodes;

import java.util.Iterator;

import org.jamocha.rete.Binding;
import org.jamocha.rete.Constants;
import org.jamocha.rete.Fact;
import org.jamocha.rete.Rete;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.exception.RetractException;
import org.jamocha.rete.joinfilter.JoinFilter;
import org.jamocha.rete.joinfilter.JoinFilterException;

/**
 * @author Peter Lin
 * 
 * BetaNode is the basic class for join nodes. The implementation uses
 * BetaMemory for the left and AlphaMemory for the right. When the left and
 * right match, the facts are merged and propogated to the succeeding nodes.
 * This is an important distinction for a couple of reasons.
 * 
 * 1. The next join may join one or more objects. 2. Rather than store the facts
 * needed for the next join in a global map of map, it's more efficient to
 * simply merge the two arrays and pass it on. 3. It isn't sufficient to pass
 * just the bound attributes of this node to the next.
 * 
 * Some important notes. If a rule defines a join, which doesn't compare a slot
 * from one fact against the slot of a different fact, the node simply
 * propogates.
 */
public class BetaFilterNode extends AbstractBeta {

	private static final long serialVersionUID = 1L;

	/**
	 * binding for the join
	 */
	protected JoinFilter[] filters = null;
	
	/**
	 * The operator for the join by default is equal. The the join doesn't
	 * comparing values, the operator should be set to -1.
	 */
	protected int operator = Constants.EQUAL;



	public BetaFilterNode(int id) {
		super(id);
	}

	/**
	 * Set the bindings for this join
	 * 
	 * @param binds
	 * @throws AssertException
	 */
	public void setFilters(JoinFilter[] filters, Rete engine) throws AssertException {
		this.filters = filters;
		activate(engine);
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
	protected boolean evaluate(FactTuple tuple, Fact right) {
		if (filters != null) {
			// we iterate over the binds and evaluate the facts
			for (JoinFilter filter : filters) {
				try {
					if (!filter.evaluate(right, tuple))
						return false;
				} catch (JoinFilterException e) {
					//TODO make good error output
					e.printStackTrace();
				}
			}
		}
		return true;
	}

	@Override
	protected void mountChild(BaseNode newChild, Rete engine) throws AssertException {
		Iterator<FactTuple> itr = mergeMemory.iterator();
		while (itr.hasNext()) {
			newChild.assertFact(itr.next(), engine, this);
		}
	}

	@Override
	protected void unmountChild(BaseNode oldChild, Rete engine) throws RetractException {
		Iterator<FactTuple> itr = mergeMemory.iterator();
		while (itr.hasNext()) {
			oldChild.retractFact(itr.next(), engine, this);
		}
	}
	
	public String toPPString(){
		StringBuffer sb = new StringBuffer();
		sb.append(super.toPPString());
		sb.append("\nFilters: ");
		if (filters != null){
			for(JoinFilter f : filters)
				sb.append(f.toPPString()).append("\n");
		} else sb.append("none\n");
		return sb.toString();
	}

}
