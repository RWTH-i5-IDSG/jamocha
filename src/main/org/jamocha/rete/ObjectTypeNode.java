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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.exception.RetractException;
import org.jamocha.rete.util.CollectionsFactory;


/**
 * @author Peter Lin
 *
 * ObjectTypeNode is the input node for a specific type. The node
 * is created with the appropriate Class. A couple of important notes
 * about the implementation of ObjectTypeNode.
 * 
 * <ul>
 *   <li> the assertFact method does not check the deftemplate matches
 * the fact. this is because of inheritance.
 *   <li> WorkingMemoryImpl checks to see if the fact's deftemplate
 * has parents. If it does, it will keep checking to see if there is
 * an ObjectTypeNode for the parent.
 *   <li> if the template has a parent, it will assert it. this means
 *   <li> any patterns for parent templates will attempt to pattern
 * match
 * </ul>
 */
public class ObjectTypeNode extends BaseAlpha implements Serializable {

    /**
     * The Class that defines object type
     */
    private Template deftemplate = null;
    
    /**
     * If we can gaurantee Uniqueness of the AlphaNodes, set it to true
     */
    private boolean gauranteeUnique = true;
    
    /**
     * HashMap entries for unique AlphaNodes
     */
    private Map entries = CollectionsFactory.localMap();
    
    /**
     * Second ArrayList for all nodes that do not use ==, null, !null
     * operators
     */
    protected ArrayList successor2 = new ArrayList();
    
    /**
     * operator count
     */
    private int opCount = 4;
    
    public static final int[] operators = {Constants.EQUAL,
            Constants.NILL,Constants.NOTNILL};
    
	/**
	 * 
	 */
	public ObjectTypeNode(int id, Template deftemp) {
		super(id);
        this.deftemplate = deftemp;
	}

    public Template getDeftemplate(){
        return this.deftemplate;
    }
    
    /**
     * clear the memory. for now the method does not
     * remove all the successor nodes. need to think it over a bit.
     */
    public void clear(WorkingMemory mem){
    	AlphaMemory am = (AlphaMemory) mem.getAlphaMemory(this);
    	am.clear();
    }

    /**
     * method to clear the successors. method doesn't iterate over
     * the succesors and clear them individually.
     */
    public void clearSuccessors() {
    	this.successor2.clear();
    	this.successorNodes = null;
    	this.entries.clear();
    }
    
    /**
     * assert the fact and propogate. ObjectTypeNode does not call
     * assertEvent, since it's not that important and doesn't really
     * help debugging.
     * @param fact
     * @param engine
     */
    public void assertFact(Fact fact, Rete engine, WorkingMemory mem)
    throws AssertException
    {
        // ObjectTypeNode doesn't bother checking the deftemplate.
        ((AlphaMemory) mem.getAlphaMemory(this)).addPartialMatch(fact);
		// if the number of succesor nodes is less than (slot count * opCount)
		if (this.gauranteeUnique && fact.getDeftemplate().getAllSlots().length > 0
				&& this.successorNodes.length > (fact.getDeftemplate()
						.getAllSlots().length * opCount)) {
			this.assertFactWithMap(fact, engine, mem);
		} else {
			this.assertAllSuccessors(fact, engine, mem);
		}
    }

    /**
	 * assert using HashMap approach
	 * 
	 * @param fact
	 * @param engine
	 * @param mem
	 */
    public void assertFactWithMap(Fact fact, Rete engine, WorkingMemory mem) 
    throws AssertException
    {
        Slot[] slots = fact.getDeftemplate().getAllSlots();
        // iterate over the slots
        for (int idx=0; idx < slots.length; idx++) {
            // only if the slot's node count is greater than zero 
            // do we go ahead and lookup in the HashMap
            if (slots[idx].getNodeCount() > 0) {
                // iterate over the operators
                for (int ops=0; ops < operators.length; ops++) {
                    CompositeIndex comIndex = 
                        new CompositeIndex(
                                slots[idx].getName(),operators[ops],fact.getSlotValue(idx));
                    
                    Object node = entries.get(comIndex);
                    if (node != null) {
                        if (node instanceof BaseAlpha){
                            ((BaseAlpha)node).assertFact(fact,engine,mem);
                        } else if (node instanceof BaseJoin){
                            ((BaseJoin)node).assertRight(fact,engine,mem);
                        } else if (node instanceof TerminalNode) {
                        	Fact[] facts = new Fact[]{fact};
                        	((TerminalNode)node).assertFacts(facts,engine,mem);
                        }
                    }
                }
            }
        }
        assertSecondSuccessors(fact,engine,mem);
    }
 
    /**
     * Propogate the fact using the normal way of iterating over the
     * successors and calling assert on AlphaNodes and assertRight on
     * BetaNodes.
     * @param fact
     * @param engine
     * @param mem
     * @throws AssertException
     */
    public void assertAllSuccessors(Fact fact, Rete engine, WorkingMemory mem) 
    throws AssertException
    {
        for (int idx=0; idx < this.successorNodes.length; idx++) {
            Object node = this.successorNodes[idx];
            if (node instanceof BaseAlpha){
                ((BaseAlpha)node).assertFact(fact,engine,mem);
            } else if (node instanceof BaseJoin){
                ((BaseJoin)node).assertRight(fact,engine,mem);
            } else if (node instanceof TerminalNode) {
            	Fact[] facts = new Fact[]{fact};
            	((TerminalNode)node).assertFacts(facts,engine,mem);
            }
        }
        assertSecondSuccessors(fact,engine,mem);
    }
    
    public void assertSecondSuccessors(Fact fact, Rete engine, WorkingMemory mem) 
    throws AssertException
    {
        Iterator itr = this.successor2.iterator();
        while (itr.hasNext()){
            BaseNode node = (BaseNode)itr.next();
            if (node instanceof BaseAlpha){
                ((BaseAlpha)node).assertFact(fact,engine,mem);
            } else if (node instanceof BaseJoin){
                ((BaseJoin)node).assertRight(fact,engine,mem);
            } else if (node instanceof TerminalNode) {
            	Fact[] facts = new Fact[]{fact};
            	((TerminalNode)node).assertFacts(facts,engine,mem);
            }
        }
    }
    
    /**
     * Retract the fact to the succeeding nodes. ObjectTypeNode does not call
     * assertEvent, since it's not that important and doesn't really
     * help debugging.
     * @param fact
     * @param engine
     */
    public void retractFact(Fact fact, Rete engine, WorkingMemory mem)
    throws RetractException
    {
        if (fact.getDeftemplate() == this.deftemplate){
            ((AlphaMemory)mem.getAlphaMemory(this)).removePartialMatch(fact);
            for (int idx=0; idx < this.successorNodes.length; idx++) {
                Object node = this.successorNodes[idx];
                if (node instanceof BaseAlpha){
                    ((BaseAlpha)node).retractFact(fact,engine,mem);
                } else if (node instanceof BaseJoin){
                    ((BaseJoin)node).retractRight(fact,engine,mem);
                }
            }
            Iterator itr2 = this.successor2.iterator();
            while (itr2.hasNext()){
                BaseNode node = (BaseNode)itr2.next();
                if (node instanceof BaseAlpha){
                    ((BaseAlpha)node).retractFact(fact,engine,mem);
                } else if (node instanceof BaseJoin){
                    ((BaseJoin)node).retractRight(fact,engine,mem);
                } else if (node instanceof TerminalNode) {
                	Fact[] facts = new Fact[]{fact};
                	((TerminalNode)node).retractFacts(facts,engine,mem);
                }
            }
        }
    }

    /**
     * return the number of successor nodes
     * @return
     */
    public int getSuccessorCount(){
        return this.successorNodes.length + this.successor2.size();
    }

    /**
     * Add a successor node
     */
    public void addSuccessorNode(BaseNode node, Rete engine, WorkingMemory mem) 
    throws AssertException 
    {
        if (!containsNode(this.successorNodes,node) && 
        		!this.successor2.contains(node)) {
            if (node instanceof BaseJoin || node instanceof TerminalNode) {
                this.successor2.add(node);
            } else {
                // we test to see if the operator is ==, nil, not nil
                // if the node isn't BaseJoin, it should be BaseAlpha
                BaseAlpha ba = (BaseAlpha)node;
                if (ba.getOperator() == Constants.LESS || 
                        ba.getOperator() == Constants.GREATER || 
                        ba.getOperator() == Constants.LESSEQUAL ||
                        ba.getOperator() == Constants.GREATEREQUAL ||
                        ba.getOperator() == Constants.NOTEQUAL) {
                    this.successor2.add(node);
                } else {
                    addNode(node);
                }
            }
            if (gauranteeUnique && node instanceof AlphaNode) {
                // now we use CompositeIndex instead of HashString
                AlphaNode anode = (AlphaNode)node;
                entries.put(anode.getHashIndex() ,node);
                // we increment the node count for the slot
                this.deftemplate.getSlot(anode.slot.getId()).incrementNodeCount();
            }
            // if there are matches, we propogate the facts to 
            // the new successor only
            AlphaMemory alpha = (AlphaMemory)mem.getAlphaMemory(this);
            if (alpha.size() > 0){
                Iterator itr = alpha.iterator();
                while (itr.hasNext()){
                    Fact f = (Fact)itr.next();
                    if (node instanceof BaseAlpha) {
                        BaseAlpha next = (BaseAlpha) node;
                        next.assertFact(f,engine,mem);
                    } else if (node instanceof BaseJoin) {
                        BaseJoin next = (BaseJoin) node;
                        next.assertRight(f,engine,mem);
                    } else if (node instanceof TerminalNode) {
                        TerminalNode t = (TerminalNode)node;
                        t.assertFacts(new Fact[]{f}, engine, mem);
                    }
                }
            }
        }
    }
    
    public boolean removeNode(BaseNode n) {
    	boolean rem = super.removeNode(n);
    	this.successor2.remove(n);
    	if (n instanceof AlphaNode) {
        	this.entries.remove(((AlphaNode)n).getHashIndex());
    	}
    	return rem;
    }
    
    /**
     * For the ObjectTypeNode, the method just returns toString
     */
    public String hashString() {
        return toString();
    }
    
    /**
     * this returns name of the deftemplate
     */
    public String toString(){
        return "ObjectTypeNode(" + this.deftemplate.getName() + ")";
    }

    /**
     * this returns name of the deftemplate
     */
    public String toPPString(){
        return "InputNode for Template(" + this.deftemplate.getName() + ")";
    }
    
    public Object[] getSuccessorNodes() {
    	ArrayList successors = new ArrayList();
    	successors.addAll(this.successor2);
    	for (int idx=0; idx < this.successorNodes.length; idx ++) {
    		successors.add(this.successorNodes[idx]);
    	}
    	return successors.toArray();
    }
}
