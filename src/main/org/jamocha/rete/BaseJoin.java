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
 * BaseJoin is the abstract base for all join node classes.
 */
public abstract class BaseJoin extends BaseNode {

	/**
	 * @param id
	 */
	public BaseJoin(int id) {
		super(id);
	}

	/**
	 * Subclasses must implement this method. assertLeft takes
	 * inputs from left input adapter nodes and join nodes.
	 * @param lfacts
	 * @param engine
	 */
	public abstract void assertLeft(Fact[] lfacts, Rete engine,
			WorkingMemory mem) throws AssertException;

	/**
	 * Subclasses must implement this method. assertRight takes
	 * input from alpha nodes.
	 * @param rfact
	 * @param engine
	 */
	public abstract void assertRight(Fact rfact, Rete engine, WorkingMemory mem)
			throws AssertException;

	/**
	 * Subclasses must implement this method. retractLeft takes
	 * input from left input adapter nodes and join nodes.
	 * @param lfacts
	 * @param engine
	 */
	public abstract void retractLeft(Fact[] lfacts, Rete engine,
			WorkingMemory mem) throws RetractException;

	/**
	 * Subclasses must implement this method. retractRight takes
	 * input from alpha nodes.
	 * @param rfact
	 * @param engine
	 */
	public abstract void retractRight(Fact rfact, Rete engine, WorkingMemory mem)
			throws RetractException;

	public abstract void setBindings(Binding[] binds);

	/**
	 * When new Successor nodes are added, we propogate the facts that matched to
	 * the new join node.
	 * @param node
	 * @param engine
	 * @param mem
	 * @throws AssertException
	 */
	public void addSuccessorNode(BaseJoin node, Rete engine, WorkingMemory mem)
			throws AssertException {
        if (addNode(node)) {
			// first, we get the memory for this node
			Map leftmem = (Map) mem.getBetaLeftMemory(this);
			// now we iterate over the entry set
			Iterator itr = leftmem.entrySet().iterator();
			while (itr.hasNext()) {
				BetaMemory bmem = (BetaMemory) itr.next();
				// get the Fact[] array for the left
				Fact[] left = bmem.getLeftFacts();
				// iterate over the matches
				Iterator ritr = bmem.iterateRightFacts();
				while (ritr.hasNext()) {
					Fact rfcts = (Fact) ritr.next();
					// merge the left and right fact into a new Array
					Fact[] merged = ConversionUtils.mergeFacts(left, rfcts);
					// now assert in the new join node
					node.assertLeft(merged, engine, mem);
				}
			}
		}
	}

	/**
	 * it's unlikely 2 rules are identical, except for the name. The implementation
	 * gets the current memory and propogates, but I wonder how much sense this
	 * makes in a real production environment. An user really shouldn't be deploying
	 * identical rules with different rule name.
	 * @param node
	 * @param engine
	 * @param mem
	 * @throws AssertException
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
					Fact[] left = bmem.getLeftFacts();
					// iterate over the matches
					Iterator ritr = bmem.iterateRightFacts();
					while (ritr.hasNext()) {
						Fact rfcts = (Fact) ritr.next();
						// merge the left and right fact into a new Array
						Fact[] merged = ConversionUtils.mergeFacts(left, rfcts);
						// now assert in the new join node
						node.assertFacts(merged, engine, mem);
					}
				}
			}
		}
	}

	/**
	 * Method is used to decompose the network and make sure
	 * the nodes are detached from each other.
	 */
	public void removeAllSuccessors() {
		for (int idx=0; idx < this.successorNodes.length; idx++) {
			BaseNode bn = (BaseNode) this.successorNodes[idx];
			bn.removeAllSuccessors();
		}
		this.successorNodes = null;
	}
	
	/**
	 * Method is used to pass a fact to the successor nodes
	 * @param fact
	 * @param engine
	 */
	protected void propogateAssert(Fact[] facts, Rete engine, WorkingMemory mem)
			throws AssertException {
        for (int idx=0; idx < this.successorNodes.length; idx++) {
            BaseNode node = this.successorNodes[idx];
			if (node instanceof BaseJoin) {
				((BaseJoin) node).assertLeft(facts, engine, mem);
			} else if (node instanceof TerminalNode) {
				((TerminalNode) node).assertFacts(facts, engine, mem);
			}
		}
	}

	/**
	 * method for propogating the retract
	 * @param fact
	 * @param engine
	 */
	protected void propogateRetract(Fact[] facts, Rete engine, WorkingMemory mem)
			throws RetractException {
        for (int idx=0; idx < this.successorNodes.length; idx++) {
            BaseNode node = this.successorNodes[idx];
			if (node instanceof BaseJoin) {
				((BaseJoin) node).retractLeft(facts, engine, mem);
			} else if (node instanceof TerminalNode) {
				((TerminalNode) node).retractFacts(facts, engine, mem);
			}
		}
	}

}
