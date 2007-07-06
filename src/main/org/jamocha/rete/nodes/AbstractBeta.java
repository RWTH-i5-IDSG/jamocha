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

import org.jamocha.rete.AlphaMemory;
import org.jamocha.rete.BetaMemory;
import org.jamocha.rete.Fact;
import org.jamocha.rete.Rete;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.exception.RetractException;

/**
 * @author Peter Lin
 * 
 * BaseJoin is the abstract base for all join node classes.
 */
public abstract class AbstractBeta extends BaseNode {

	protected AlphaMemory alphaMemory = null;

	protected BetaMemory betaMemory = null;

	protected BetaMemory mergeMemory = null;

	// per default deactivated
	protected boolean activated = false;

	public void activate(Rete engine) throws AssertException {
		if (!activated) {
			// we have to traverse the whole beta mem and eval it.
			activated = true;
			Iterator<FactTuple> itr = betaMemory.iterator();
			while (itr.hasNext()) {
				FactTuple tuple = itr.next();
				evaluateBeta(tuple, engine);
			}
		}
	}

	// protected abstract void evaluateBeta(FactTuple tuple, Rete engine) throws
	// AssertException;

	protected void evaluateBeta(FactTuple tuple, Rete engine)
			throws AssertException {
		Iterator<Fact> itr = alphaMemory.iterator();
		while (itr.hasNext()) {
			Fact rfcts = itr.next();
			if (this.evaluate(tuple, rfcts)) {
				FactTuple newTuple = tuple.addFact(rfcts);
				mergeMemory.add(newTuple);
				this.propogateAssert(newTuple, engine);
			}
		}

	}

	/**
	 * assertLeft takes an array of facts. Since the next join may be joining
	 * against one or more objects, we need to pass all previously matched
	 * facts.
	 * 
	 * @param factInstance
	 * @param engine
	 */
	public void assertLeft(FactTuple tuple, Rete engine) throws AssertException {
		betaMemory.add(tuple);
		// only if activated:
		if (activated) {
			evaluateBeta(tuple, engine);
		}
	}

	protected abstract boolean evaluate(FactTuple tuple, Fact rfcts);

	/**
	 * @param id
	 */
	public AbstractBeta(int id) {
		super(id);
		this.maxChildCount = Integer.MAX_VALUE;
		this.maxParentCount = 2;
		alphaMemory = new AlphaMemory();
		betaMemory = new BetaMemory();
		mergeMemory = new BetaMemory();
	}

	@Override
	public void assertFact(Assertable fact, Rete engine, BaseNode sender)
			throws AssertException {
		if (sender.isRightNode()) {
			assertRight((Fact) fact, engine);
		} else
			assertLeft((FactTuple) fact, engine);
	}

	public void assertRight(Fact fact, Rete engine) throws AssertException {
		alphaMemory.add(fact);
		// only if activated:
		if (activated) {
			Iterator<FactTuple> itr = betaMemory.iterator();
			while (itr.hasNext()) {
				FactTuple tuple = itr.next();
				if (this.evaluate(tuple, fact)) {
					// now we propogate
					FactTuple newTuple = tuple.addFact(fact);
					mergeMemory.add(newTuple);
					this.propogateAssert(newTuple, engine);
				}
			}
		}
	}

	@Override
	public void retractFact(Assertable fact, Rete engine, BaseNode sender)
			throws RetractException {
		if (sender.isRightNode()) {
			retractRight((Fact) fact, engine);

		} else
			retractLeft((FactTuple) fact, engine);
	}

	/**
	 * clear will clear the lists
	 */
	public void clear() {
		alphaMemory.clear();
		betaMemory.clear();
		mergeMemory.clear();
	}

	@Override
	protected void mountChild(BaseNode newChild, Rete engine)
			throws AssertException {
		// TODO Auto-generated method stub

	}

	/**
	 * Retracting from the left requires that we propogate the
	 * 
	 * @param factInstance
	 * @param engine
	 */
	public void retractLeft(FactTuple tuple, Rete engine)
			throws RetractException {
		if (betaMemory.contains(tuple)) {
			betaMemory.remove(tuple);
			// now we propogate the retract. To do that, we have
			// merge each item in the list with the Fact array
			// and call retract in the successor nodes
			Iterator<FactTuple> itr = mergeMemory.iterator();
			while (itr.hasNext()) {
				propogateRetract(itr.next(), engine);
			}
			// Todo: remove tuple from mergeMemory
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
	public void retractRight(Fact fact, Rete engine) throws RetractException {
		if (alphaMemory.contains(fact)) {
			alphaMemory.remove(fact);
			Iterator<FactTuple> itr = mergeMemory.iterator();
			while (itr.hasNext()) {
				propogateRetract(itr.next(), engine);
			}
			// Todo: remove tuple from mergeMemory
		}
	}

	@Override
	protected void unmountChild(BaseNode oldChild, Rete engine)
			throws RetractException {
		// TODO Auto-generated method stub

	}

	public boolean isRightNode() {
		return false;
	}

	@Override
	protected BaseNode evAdded(BaseNode newParentNode, Rete engine) {

		// ======= Briefing ===========
		// this: the child
		// newParentNode: the parent
		// return-value: the child or another (maybe an inserted LIANode leading
		// to the child)
		// we want to test whether we need a new LIANode between child and
		// parent
		if (this.parentNodes.length > 0 && this.parentNodes[0].isRightNode()
				&& newParentNode.isRightNode()) {
			// now, indeed, we need a new LIANode between them
			LIANode adaptor = new LIANode(engine.nextNodeId());
			try {
				adaptor.addNode(this, engine);
				return adaptor.evAdded(newParentNode, engine);
			} catch (AssertException e) {
				e.printStackTrace();
			}
			return super.evAdded(newParentNode, engine);

		} else {
			// no additional node needed.
			// it is okay to have same behaviour like superclass here
			return super.evAdded(newParentNode, engine);
		}

	}

	public String toPPString() {
		StringBuffer sb = new StringBuffer();
		sb.append(super.toPPString());
		sb.append("Alpha-Input: ");
		sb.append(alphaMemory.toPPString());
		sb.append("\nBeta-Input: ");
		sb.append(betaMemory.toPPString());
		sb.append("\nOutput: ");
		sb.append(mergeMemory.toPPString());
		return sb.toString();
	}

	// /**
	// * This node has been added to the given parant node
	// *
	// * @param n
	// * @return
	// */
	// @Override
	// protected BaseNode evAdded(BaseNode newParentNode) {
	// // we have been added to the new parent, add parent to own list:
	// if (!containsNode(this.parentNodes, newParentNode) && childNodes.length <
	// maxParentCount) {
	// // add to own list:
	// this.parentNodes = ConversionUtils.add(this.parentNodes, newParentNode);
	// return this;
	// }
	// return null;
	// }

}
