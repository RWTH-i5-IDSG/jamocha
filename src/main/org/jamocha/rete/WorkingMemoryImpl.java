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
import org.jamocha.rete.util.CollectionsFactory;
import org.jamocha.rule.Condition;
import org.jamocha.rule.Rule;

/**
 * @author Peter Lin
 *
 * WorkingMemoryImpl is a basic implementation of the WorkingMemory
 * interface. A couple of important things about ava - Code Style - Code Templates
 */
public class WorkingMemoryImpl implements WorkingMemory {

	private static final long serialVersionUID = 1L;

	protected Rete engine = null;

	protected RootNode root = new RootNode();

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
		this.compiler = new SFRuleCompiler(engine, this, this.root
				.getObjectTypeNodes());
		this.compiler.addListener(engine);
	}

	/**
	 * Return the rootnode of the RETE network
	 * @return
	 */
	public RootNode getRootNode() {
		return this.root;
	}

	protected void setRootNode(RootNode rnode) {
		this.root = rnode;
	}

	/**
	 * the implementation will lookup the alpha memory. If one exists,
	 * it will return it. If not, it will create a new AlphaMemory.
	 */
	public Object getAlphaMemory(Object key) {
		if (this.alphaMemories.containsKey(key)) {
			return this.alphaMemories.get(key);
		} else {
			// for now return null, it should create a new memory
			// and return it.
			String mname = "alphamem" + ((BaseNode) key).nodeID;
			AlphaMemoryImpl alpha = new AlphaMemoryImpl(mname);
			this.alphaMemories.put(key, alpha);
			return alpha;
		}
	}

	/**
	 * The current implementation simply removes the alpha memory for
	 * a given AlphaNode.
	 */
	public void removeAlphaMemory(Object key) {
		this.alphaMemories.remove(key);
	}

	/**
	 * the key should be the BetaNode. The left memory is a HashMap,
	 * which has Index for the key and BetaMemory for the value.
	 */
	public Object getBetaLeftMemory(Object key) {
		Object val = this.betaLeftMemories.get(key);
		if (val != null) {
			return val;
		} else {
			// it should create a new memory
			// and return it.
			String mname = "blmem" + ((BaseNode) key).nodeID;
			Map left = CollectionsFactory.newBetaMemoryMap(mname);
			this.betaLeftMemories.put(key, left);
			return left;
		}
	}

	/**
	 * The key should be the BetaNode. The right memory is also
	 * a HashMap, which has Index for the key and a single fact
	 * for the value.
	 */
	public Object getBetaRightMemory(Object key) {
		Object val = this.betaRightMemories.get(key);
		if (val != null) {
			return val;
		} else {
			if (key instanceof HashedEqBNode || key instanceof HashedEqNJoin) {
				String mname = "hnode" + ((BaseNode) key).nodeID;
				HashedAlphaMemoryImpl alpha = new HashedAlphaMemoryImpl(mname);
				this.betaRightMemories.put(key, alpha);
				return alpha;
			} else if (key instanceof HashedNotEqBNode || 
                    key instanceof HashedNotEqNJoin) {
				String mname = "hneq" + ((BaseNode) key).nodeID;
				HashedAlphaMemory2 alpha = new HashedAlphaMemory2(mname);
				this.betaRightMemories.put(key, alpha);
				return alpha;
			} else {
				String mname = "brmem" + ((BaseNode) key).nodeID;
				Map right = CollectionsFactory.newAlphaMemoryMap(mname);
				this.betaRightMemories.put(key, right);
				return right;
			}
		}
	}

	/**
	 * The current implementation will lookup the memory. If one
	 * does not exist, it will create a new HashMap and use the
	 * node as the key.
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
		this.root.assertObject(fact, engine, this);
	}

	/**
	 * Retract an object from the Working memory
	 * @param objInstance
	 */
	public synchronized void retractObject(Fact fact) throws RetractException {
		this.root.retractObject(fact, engine, this);
	}

	/**
	 * The implementation returns the default RuleCompiler
	 */
	public RuleCompiler getRuleCompiler() {
		return compiler;
	}

	public void printWorkingMemory(boolean detailed, boolean inputNodes) {
		engine.writeMessage("AlphaNode count " + this.alphaMemories.size()
				+ Constants.LINEBREAK);
		Iterator itr = this.alphaMemories.keySet().iterator();
		int memTotal = 0;
		while (itr.hasNext()) {
			BaseNode key = (BaseNode) itr.next();
			if (!(key instanceof ObjectTypeNode) && !(key instanceof LIANode)) {
				AlphaMemory am = (AlphaMemory) this.alphaMemories.get(key);
				if (detailed) {
					engine.writeMessage(key.toPPString() + " count="
							+ am.size() + Constants.LINEBREAK);
				}
				memTotal += am.size();
			} else {
				if (inputNodes) {
					AlphaMemory am = (AlphaMemory) this.alphaMemories.get(key);
					engine.writeMessage(key.toPPString() + " count="
							+ am.size() + Constants.LINEBREAK);

				}
			}
		}
		engine.writeMessage("total AlphaMemories = " + memTotal + Constants.LINEBREAK);

		// now write out the left beta memory
		engine.writeMessage("BetaNode Count " + this.betaLeftMemories.size() + Constants.LINEBREAK);
		int betaTotal = 0;
		itr = this.betaLeftMemories.keySet().iterator();
		while (itr.hasNext()) {
			BaseNode key = (BaseNode) itr.next();
			if (key instanceof BaseJoin) {
				this.printBetaNodes((BaseJoin)key, detailed, betaTotal);
			}
		}
		engine.writeMessage("total BetaMemories = " + betaTotal + Constants.LINEBREAK);
	}

	protected void printBetaNodes(BaseJoin bjoin, boolean detailed, int betaTotal) {
		if (bjoin instanceof HashedEqBNode) {
			HashedEqBNode hebj = (HashedEqBNode)bjoin;
			Map bm = (Map) this.betaLeftMemories.get(hebj);
			// we iterate over the keys in the HashMap
			Iterator bitr = bm.keySet().iterator();
			while (bitr.hasNext()) {
				Index bmm = (Index) bm.get(bitr.next());
				if (detailed) {
					engine.writeMessage(bjoin.toPPString(),Constants.DEFAULT_OUTPUT);
					HashedAlphaMemoryImpl rightmem = (HashedAlphaMemoryImpl)
							this.getBetaRightMemory(hebj);

					EqHashIndex eqinx = new EqHashIndex(hebj.getLeftValues(bmm.getFacts()));
					// add to the total count
					betaTotal += rightmem.count(eqinx);
					engine.writeMessage(" count=" + betaTotal,
                            Constants.DEFAULT_OUTPUT);
					Iterator ritr = rightmem.iterator(eqinx);
					if (ritr != null) {
						StringBuffer buf = new StringBuffer();
						while (ritr.hasNext()) {
							buf.append( ((Fact)ritr.next()).getFactId() + ",");
						}
						engine.writeMessage(buf.toString(),Constants.DEFAULT_OUTPUT);
					}
					engine.writeMessage(Constants.LINEBREAK, Constants.DEFAULT_OUTPUT);
				}
			}
		} else if (bjoin instanceof HashedEqNJoin) {
			HashedEqNJoin henj = (HashedEqNJoin)bjoin;
			Map bm = (Map) this.betaLeftMemories.get(henj);
			// we iterate over the keys in the HashMap
			Iterator bitr = bm.keySet().iterator();
			while (bitr.hasNext()) {
				Index bmm = (Index) bm.get(bitr.next());
				if (detailed) {
					engine.writeMessage(bjoin.toPPString(),Constants.DEFAULT_OUTPUT);
					HashedAlphaMemoryImpl rightmem = (HashedAlphaMemoryImpl)
							this.getBetaRightMemory(henj);

					EqHashIndex eqinx = new EqHashIndex(henj.getLeftValues(bmm.getFacts()));
					// add to the total count
					betaTotal += rightmem.count(eqinx);
					engine.writeMessage(" count=" + betaTotal
                            , Constants.DEFAULT_OUTPUT);
					Iterator ritr = rightmem.iterator(eqinx);
					if (ritr != null) {
						StringBuffer buf = new StringBuffer();
						while (ritr.hasNext()) {
							buf.append( ((Fact)ritr.next()).getFactId() + ",");
						}
						engine.writeMessage(buf.toString(),Constants.DEFAULT_OUTPUT);
					}
					engine.writeMessage(Constants.LINEBREAK, Constants.DEFAULT_OUTPUT);
				}
			}
		} else {
			Map bm = (Map) this.betaLeftMemories.get(bjoin);
			// we iterate over the keys in the HashMap
			Iterator bitr = bm.keySet().iterator();
			while (bitr.hasNext()) {
				BetaMemory bmm = (BetaMemory) bm.get(bitr.next());
				if (detailed) {
					engine.writeMessage(bjoin.toPPString() + " count="
							+ bmm.matchCount() + " - " + bmm.toPPString() + Constants.LINEBREAK);
				}
				betaTotal += bmm.matchCount();
			}
		}
	}
	
	/**
	 * The method will print out the facts in the right input for all
	 * BetaNodes.
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
					for (int idz=0; idz < fitr.length; idz++) {
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
	 * Printout the memory for the given rule.
	 * @param rule
	 */
	public void printWorkingMemory(Rule rule) {
		engine.writeMessage("Memories for " + rule.getName());
		Condition[] conds = rule.getConditions();
		int memTotal = 0;
		for (int idx = 0; idx < conds.length; idx++) {
			Condition c = conds[idx];
			List l = c.getNodes();
			Iterator itr = l.iterator();
			while (itr.hasNext()) {
				BaseNode key = (BaseNode) itr.next();
				AlphaMemory am = (AlphaMemory) this.alphaMemories.get(key);
				engine.writeMessage(key.toPPString() + " count=" + am.size() + Constants.LINEBREAK);
				memTotal += am.size();
			}
		}
	}

	/**
	 * Printout the memory with a given filter.
	 */
	public void printWorkingMemory(Map filter) {
		if (filter != null && filter.size() > 0) {
			// not implemented yet
		} else {
			printWorkingMemory(true, false);
		}
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
