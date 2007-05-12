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

import org.jamocha.parser.ParserFactory;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.exception.RetractException;
import org.jamocha.rete.nodes.BaseJoin;
import org.jamocha.rete.nodes.BaseNode;
import org.jamocha.rete.nodes.LIANode;
import org.jamocha.rete.nodes.ObjectTypeNode;
import org.jamocha.rete.nodes.RootNode;
import org.jamocha.rete.util.CollectionsFactory;
import org.jamocha.rule.Condition;
import org.jamocha.rule.Rule;

/**
 * @author Peter Lin
 * 
 * WorkingMemoryImpl is a basic implementation of the WorkingMemory interface. A
 * couple of important things about ava - Code Style - Code Templates
 */
public class WorkingMemoryImpl implements WorkingMemory {

	private static final long serialVersionUID = 1L;

	protected Rete engine = null;

	protected RootNode root = null;

	protected Map alphaMemories = CollectionsFactory.newMap();

	protected Map betaLeftMemories = CollectionsFactory.newMap();

	protected Map betaRightMemories = CollectionsFactory.newMap();

	protected Map terminalMemories = CollectionsFactory.newMap();

	protected RuleCompiler compiler = null;

	/**
	 * 
	 */
	public WorkingMemoryImpl(Rete engine) {
		super();
		this.engine = engine;
		this.root = new RootNode(engine.nextNodeId());
		this.compiler = ParserFactory.getRuleCompiler(engine, this, this.root);
		this.compiler.addListener(engine);
	}

	/**
	 * Return the rootnode of the RETE network
	 * 
	 * @return
	 */
	public RootNode getRootNode() {
		return this.root;
	}



	/**
	 * The current implementation simply removes the alpha memory for a given
	 * AlphaNode.
	 */
	public void removeAlphaMemory(Object key) {
		this.alphaMemories.remove(key);
	}

	/**
	 * the key should be the BetaNode. The left memory is a HashMap, which has
	 * Index for the key and BetaMemory for the value.
	 */
	public Object getBetaLeftMemory(Object key) {
		Object val = this.betaLeftMemories.get(key);
		if (val != null) {
			return val;
		} else {
			// it should create a new memory
			// and return it.
			String mname = "blmem" + ((BaseNode) key).getNodeId();
			Map left = CollectionsFactory.newBetaMemoryMap(mname);
			this.betaLeftMemories.put(key, left);
			return left;
		}
	}


	/**
	 * The current implementation will lookup the memory. If one does not exist,
	 * it will create a new HashMap and use the node as the key.
	 */
	public Object getTerminalMemory(Object key) {
		if (this.terminalMemories.containsKey(key)) {
			return this.terminalMemories.get(key);
		} else {
			Map mem = CollectionsFactory.newTerminalMap();
			this.terminalMemories.put(key, mem);
			return mem;
		}
	}

	public synchronized void assertObject(Fact fact) throws AssertException {
		// we assume Rete has already checked to see if the object
		// has been added to the working memory, so we just assert.
		// we need to lookup the defclass and deftemplate to assert
		// the object to the network
		this.root.assertObject(fact, engine);
	}

	/**
	 * Retract an object from the Working memory
	 * 
	 * @param objInstance
	 */
	public synchronized void retractObject(Fact fact) throws RetractException {
		this.root.retractObject(fact, engine);
	}

	/**
	 * The implementation returns the default RuleCompiler
	 */
	public RuleCompiler getRuleCompiler() {
		return compiler;
	}


	/**
	 * The method will print out the facts in the right input for all BetaNodes.
	 */
	public void printWorkingMemoryBetaRight() {
		StringBuffer buf = new StringBuffer();
		Iterator itr = this.betaRightMemories.keySet().iterator();
		while (itr.hasNext()) {
			BaseJoin key = (BaseJoin) itr.next();
			buf.append(key.toPPString());
			Object rmem = this.betaRightMemories.get(key);
			StringBuffer buf2 = new StringBuffer();
			if (rmem instanceof Map) {
				int count = 0;
				Iterator fitr = ((Map) rmem).values().iterator();
				buf2.append(": ");
				while (fitr.hasNext()) {
					Fact ft = (Fact) fitr.next();
					buf2.append(ft.getFactId() + ",");
					count++;
				}
				buf.append("- total=" + count + " ");
				buf.append(buf2.toString());
				buf.append(Constants.LINEBREAK);
			} else {
				HashedAlphaMemoryImpl ham = (HashedAlphaMemoryImpl) rmem;
				int count = 0;
				Object[] fitr = ham.iterateAll();
				if (fitr != null) {
					for (int idz = 0; idz < fitr.length; idz++) {
						Fact ft = (Fact) fitr[idz];
						buf2.append(ft.getFactId() + ",");
						count++;
					}
				}
				buf.append("- total=" + count + " :");
				buf.append(buf2.toString());
				buf.append(Constants.LINEBREAK);
			}
		}
		engine.writeMessage(buf.toString());
	}

	/**
	 * We may want to iterate over the HashMaps and aggressively clear things.
	 */
	public synchronized void clear() {
		Iterator amitr = this.alphaMemories.values().iterator();
		while (amitr.hasNext()) {
			AlphaMemory am = (AlphaMemory) amitr.next();
			am.clear();
		}
		this.alphaMemories.clear();
		Iterator blitr = this.betaLeftMemories.values().iterator();
		while (blitr.hasNext()) {
			Object bval = blitr.next();
			if (bval instanceof Map) {
				Map lmem = (Map) bval;
				// now iterate over the betamemories
				Iterator bmitr = lmem.keySet().iterator();
				while (bmitr.hasNext()) {
					Index indx = (Index) bmitr.next();
					indx.clear();
				}
				lmem.clear();
			}
		}
		this.betaLeftMemories.clear();
		Iterator britr = this.betaRightMemories.values().iterator();
		while (britr.hasNext()) {
			Object val = britr.next();
			if (val instanceof HashedAlphaMemoryImpl) {
				((HashedAlphaMemoryImpl) val).clear();
			} else {
				Map mem = (Map) val;
				mem.clear();
			}
		}
		this.betaRightMemories.clear();
		this.terminalMemories.clear();
		this.root.clear();
	}
}
