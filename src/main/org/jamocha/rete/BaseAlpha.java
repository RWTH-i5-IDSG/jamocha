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
import java.util.Iterator;
import java.util.List;

import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.exception.RetractException;

/**
 * @author Peter Lin
 *
 * BaseAlpha is the abstract base class for 1-input nodes. Alpha nodes
 * have an boolean field for Just-In-Time optimization. The algorithm
 * for JIT is the following:
 * 
 * Let's start out with an example rule to provide some context.
 * <pre>
 * Rule:
 *   if
 *     the account purchases for the last 12 months exceed 500
 *     the account type is premium
 *     the account last activity was less than 1 week
 *     the purchase total is greater than 150.00
 *   then
 *     give the user a 15% discount
 * </pre>
 * Since RETE remembers which alpha nodes matched, we know how many
 * facts matched for each condition. When an user writes the rule,
 * they shouldn't have to know the optimal order of the conditions.
 * Lets say the match count for the rule above is this:
 * <pre>
 * Rule:
 *   if
 *     the account purchases for the last 12 months exceed 500 (300)
 *     the account type is premium (250)
 *     the account last activity was less than 1 week (100)
 *     the purchase total is greater than 150.00 (80)
 *   then
 *     give the user a 15% discount
 * </pre>
 * By re-ordering the alpha nodes, we can reduce the number of partial
 * matches. Although it may seem trivial, it can result in a dramatic
 * decrease in memory usage and provide a significant performance
 * boost. If the nodes are ordered in reverse, that means there are 450
 * fewer partial matches. If we scale this problem up to thousands of
 * rules and half million records, it's easy to see it ends up saving
 * a ton of partial matches. The problem isn't noticeable for small
 * applications, but as an application scales up in the number of
 * rules and facts, it can potentially reduce partial matches by an
 * order of magnitude or more.
 */
public abstract class BaseAlpha extends BaseNode {

    /**
     * The operator to compare two values
     */
    protected int operator = Constants.EQUAL;


    public BaseAlpha(int id){
        super(id);
    }
    
    /**
     * Alpha nodes must implement this method
     * @param factInstance
     * @param engine
     */
    public abstract void assertFact(Fact factInstance, Rete engine, WorkingMemory mem) 
    throws AssertException;

    /**
     * Alpha nodes must implement this method. Retract should remove
     * a fact from the node and propogate through the RETE network.
     * @param factInstance
     * @param engine
     */
    public abstract void retractFact(Fact factInstance, Rete engine, WorkingMemory mem) 
    throws RetractException;
    
    public int successorCount() {
    	return this.successorNodes.length;
    }
    
    /**
     * method for propogating the retract
     * @param fact
     * @param engine
     */
    protected void propogateRetract(Fact fact, Rete engine, WorkingMemory mem)
    throws RetractException
    {
        for (int idx=0; idx < this.successorNodes.length; idx++) {
            Object nNode = this.successorNodes[idx];
            if (nNode instanceof BaseAlpha) {
                BaseAlpha next = (BaseAlpha) nNode;
                next.retractFact(fact,engine,mem);
            } else if (nNode instanceof BaseJoin) {
            	BaseJoin next = (BaseJoin) nNode;
                // AlphaNodes always call retractRight in the
                // BetaNode
                next.retractRight(fact,engine,mem);
            } else if (nNode instanceof TerminalNode) {
            	Fact[] facts = new Fact[]{fact};
            	((TerminalNode)nNode).retractFacts(facts,engine,mem);
            }
        }
    }

    /**
     * Method is used to pass a fact to the successor nodes
     * @param fact
     * @param engine
     */
    protected void propogateAssert(Fact fact, Rete engine, WorkingMemory mem)
    throws AssertException
    {
        for (int idx=0; idx < this.successorNodes.length; idx++) {
            Object nNode = this.successorNodes[idx];
            if (nNode instanceof BaseAlpha) {
                BaseAlpha next = (BaseAlpha) nNode;
                next.assertFact(fact, engine, mem);
            } else if (nNode instanceof BaseJoin) {
            	BaseJoin next = (BaseJoin) nNode;
                next.assertRight(fact,engine,mem);
            } else if (nNode instanceof TerminalNode) {
                TerminalNode next = (TerminalNode)nNode;
                next.assertFacts(new Fact[]{fact},engine,mem);
            }
        }
    }

    /**
     * Set the next node in the sequence of 1-input nodes.
     * The next node can be an AlphaNode or a LIANode.
     * @param node
     */
    public void addSuccessorNode(BaseNode node, Rete engine, WorkingMemory mem) 
    throws AssertException 
    {
        if (addNode(node)) {
            // if there are matches, we propogate the facts to 
            // the new successor only
            AlphaMemory alpha = (AlphaMemory)mem.getAlphaMemory(this);
            if (alpha.size() > 0){
                Iterator itr = alpha.iterator();
                while (itr.hasNext()){
                    if (node instanceof BaseAlpha) {
                        BaseAlpha next = (BaseAlpha) node;
                        next.assertFact((Fact)itr.next(),engine,mem);
                    } else if (node instanceof BaseJoin) {
                        BaseJoin next = (BaseJoin) node;
                        next.assertRight((Fact)itr.next(),engine,mem);
                    } else if (node instanceof TerminalNode) {
                    	TerminalNode next = (TerminalNode)node;
                    	next.assertFacts(new Fact[]{(Fact)itr.next()},engine,mem);
                    }
                }
            }
        }
    }
    
    /**
     * Remove a successor node
     * @param node
     * @param engine
     * @param mem
     * @throws AssertException
     */
    public void removeSuccessorNode(BaseNode node, Rete engine, WorkingMemory mem) 
    throws RetractException
    {
        if (removeNode(node)) {
            // we retract the memories first, before removing the node
            AlphaMemory alpha = (AlphaMemory)mem.getAlphaMemory(this);
            if (alpha.size() > 0) {
                Iterator itr = alpha.iterator();
                while (itr.hasNext()) {
                    if (node instanceof BaseAlpha) {
                        BaseAlpha next = (BaseAlpha)node;
                        next.retractFact((Fact)itr.next(),engine,mem);
                    } else if (node instanceof BaseJoin) {
                        BaseJoin next = (BaseJoin)node;
                        next.retractRight((Fact)itr.next(),engine,mem);
                    }
                }
            }
        }
    }
    
    /**
     * Get the list of facts that have matched the node
     * @return
     */
    public AlphaMemory getMemory(WorkingMemory mem){
        return (AlphaMemory)mem.getAlphaMemory(this);
    }
    
    /**
     * implementation simply clear the arraylist
     */
    public void clear(WorkingMemory mem) {
        getMemory(mem).clear();
    }
    
    /**
     * Abstract implementation returns an int code for the
     * operator. To get the string representation, it should
     * be converted.
     */
    public int getOperator() {
        return this.operator;
    }
    
    /**
     * Subclasses need to implement this method. The hash string
     * should be the slotId + operator + value
     */
    public abstract String hashString();
    /**
     * subclasses need to implement PrettyPrintString and print
     * out user friendly representation fo the node
     */
    public abstract String toPPString();
    /**
     * subclasses need to implement the toString and return a textual
     * form representation of the node.
     */
    public abstract String toString();
    
	/**
	 * Method is used to decompose the network and make sure
	 * the nodes are detached from each other.
	 */
	public void removeAllSuccessors() {
		for (int idx=0; idx < this.successorNodes.length; idx++) {
			BaseNode bn = (BaseNode)this.successorNodes[idx];
			bn.removeAllSuccessors();
		}
		this.successorNodes = null;
	}
}