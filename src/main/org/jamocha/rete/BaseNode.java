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

/**
 * @author Peter Lin
 *
 * BaseNode is meant to define common logic that all nodes must have
 * and implement common logic.
 */
public abstract class rtBaseNode implements Serializable {

	protected int nodeID;

    /**
     * We use an object Array to keep things efficient
     */
    protected BaseNode[] successorNodes = new BaseNode[0];
    
	/**
	 * The useCount is used to keep track of how many times
	 * an Alpha node is shared. This is needed so that we
	 * can dynamically remove a rule at run time and remove
	 * the node from the network. If we didn't keep count,
	 * it would be harder to figure out if we can remove the node.
	 */
	protected int useCount = 0;
    
	/**
	 * BaseNode has only one constructor which takes an unique
	 * node id. All subclasses need to call the constructor.
	 */
	public BaseNode(int id) {
		super();
		this.nodeID = id;
	}

	/**
	 * Returns the successor nodes
	 */
	public Object[] getSuccessorNodes() {
		return successorNodes;
	}


    protected boolean containsNode(Object[] list, Object node) {
    	boolean cn = false;
    	for (int idx=0; idx < list.length; idx++) {
    		if (list[idx] == node) {
    			cn = true;
    			break;
    		}
    	}
    	return cn;
    }
    
    /**
     * Add the node to the list of successors
     * @param n
     * @return
     */
    protected boolean addNode(BaseNode n) {
    	boolean add = false;
    	if (!containsNode(this.successorNodes,n)) {
    		this.successorNodes = ConversionUtils.add(this.successorNodes,n);
    		add = true;
    	}
    	return add;
    }
    
    /**
     * remove the node from the succesors
     * @param n
     * @return
     */
    public boolean removeNode(BaseNode n) {
    	boolean rem = false;
    	if (containsNode(this.successorNodes,n)) {
    		this.successorNodes = ConversionUtils.remove(this.successorNodes,n);
    		rem = true;
    	}
    	return rem;
    }
    
	/**
	 * Subclasses need to implement clear and make sure all
	 * memories are cleared properly.
	 */
	public abstract void clear(WorkingMemory mem);

	/**
	 * toString should return a string format of the node and
	 * the pattern it matches.
	 */
	public abstract String toString();

	/**
	 * hashString should return a string which can be used as
	 * a key for HashMap or HashTable
	 * @return
	 */
	public String hashString() {
		return "";
	}

	/**
	 * Return the node id
	 * @return
	 */
	public int getNodeId() {
		return this.nodeID;
	}
	
	/**
	 * every time the node is shared, the method
	 * needs to be called so we keep an accurate count.
	 */
	public void incrementUseCount() {
		this.useCount++;
	}
	
	/**
	 * every time a rule is removed from the network
	 * we need to decrement the count. Once the count
	 * reaches zero, we can remove the node by calling
	 * it's finalize.
	 */
	public void decrementUseCount() {
		this.useCount--;
	}

	/**
	 * toPPString should return a string format, but formatted
	 * nicely so it's easier for humans to read. Chances are
	 * this method will be used in debugging mode, so the more
	 * descriptive the string is, the easier it is to figure out
	 * what the node does.
	 * @return
	 */
	public abstract String toPPString();

	/**
	 * Method is used to decompose the network and make sure
	 * the nodes are detached from each other
	 */
	public abstract void removeAllSuccessors();
}
