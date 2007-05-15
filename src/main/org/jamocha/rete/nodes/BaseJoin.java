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

import java.util.AbstractCollection;
import java.util.Vector;

import org.jamocha.rete.AlphaMemory;
import org.jamocha.rete.BetaMemory;
import org.jamocha.rete.Binding;
import org.jamocha.rete.Fact;
import org.jamocha.rete.Rete;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.exception.RetractException;

/**
 * @author Peter Lin
 * 
 * BaseJoin is the abstract base for all join node classes.
 */
public abstract class BaseJoin extends BaseNode {

	/**
	 * binding for the join
	 */
	protected Binding[] binds = null;
	
	protected AlphaMemory alphaMemory = null;
	
	protected BetaMemory betaMemory = null;
	
	protected AbstractCollection<FactTuple> mergeMemory = null;

	/**
	 * @param id
	 */
	public BaseJoin(int id) {
		super(id);
		this.maxChildCount = Integer.MAX_VALUE;
		this.maxParentCount = 2;
		alphaMemory = new AlphaMemory();
		betaMemory = new BetaMemory();
		mergeMemory = new Vector<FactTuple>();
	}

	@Override
	public void assertFact(Assertable fact, Rete engine, BaseNode sender) throws AssertException {
		if (sender.isRightNode()) {
			 assertRight((Fact)fact, engine);
		} else
			 assertLeft((FactTuple)fact, engine);
	}

	/**
	 * Subclasses must implement this method. assertLeft takes inputs from left
	 * input adapter nodes and join nodes.
	 * 
	 * @param lfacts
	 * @param engine
	 */
	public abstract void assertLeft(FactTuple tuple, Rete engine) throws AssertException;

	/**
	 * Subclasses must implement this method. assertRight takes input from alpha
	 * nodes.
	 * 
	 * @param rfact
	 * @param engine
	 */
	public abstract void assertRight(Fact fact, Rete engine) throws AssertException;

	@Override
	public void retractFact(Assertable fact, Rete engine, BaseNode sender) throws RetractException {
		if (sender.isRightNode()) {
			retractRight((Fact)fact, engine);
			
		} else
			retractLeft((FactTuple)fact, engine);
	}

	/**
	 * Subclasses must implement this method. retractLeft takes input from left
	 * input adapter nodes and join nodes.
	 * 
	 * @param lfacts
	 * @param engine
	 */
	public abstract void retractLeft(FactTuple tupel, Rete engine) throws RetractException;

	/**
	 * Subclasses must implement this method. retractRight takes input from
	 * alpha nodes.
	 * 
	 * @param rfact
	 * @param engine
	 */
	public abstract void retractRight(Fact fact, Rete engine) throws RetractException;

	public abstract void setBindings(Binding[] binds);
	
	
	/**
	 * clear will clear the lists
	 */
	public void clear() {
		alphaMemory.clear();
		betaMemory.clear();
		mergeMemory.clear();
	}
	
	@Override
	public boolean addNode(BaseNode n, Rete engine) throws AssertException {
		
		BaseNode forAdd = n;
		if (parentNodes.length > 0 && parentNodes[0].isRightNode()){
			// here is already an alpha node and i want to add another alpha node
			// so i have to create a LIAnode between me and the new alpha node
			
			LIANode adapter = new LIANode(engine.nextNodeId());
			if (!adapter.addNode(n,engine))
				throw new AssertException("Could not add LIANode");
			forAdd = adapter;
			
		}
		return super.addNode(forAdd, engine);
	}

	@Override
	protected void mountChild(BaseNode newChild, Rete engine) throws AssertException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String toPPString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void unmountChild(BaseNode oldChild, Rete engine) throws RetractException {
		// TODO Auto-generated method stub
		
	}

	public boolean isRightNode() {
		return false;
	}
	
}
