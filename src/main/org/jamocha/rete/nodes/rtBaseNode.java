/*
 * Copyright 2007 Sebastian Reinartz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://jamocha.org
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.jamocha.rete.nodes;

import java.io.Serializable;

import org.jamocha.rete.Activation;
import org.jamocha.rete.BasicActivation;
import org.jamocha.rete.ConversionUtils;
import org.jamocha.rete.Index;
import org.jamocha.rete.Rete;
import org.jamocha.rete.WorkingMemory;

/**
 * @author Sebastian Reinartz
 * 
 * BaseNode is meant to define common logic that all nodes must have and
 * implement common logic.
 */
public abstract class rtBaseNode implements Serializable {

	protected int nodeID;

	/**
	 * We use an object Array to keep things efficient
	 */
	protected rtBaseNode[] childNodes = new rtBaseNode[0];

	protected rtBaseNode[] parentNodes = new rtBaseNode[0];

	// override these values in subclasses:
	protected int maxParentCount = 1;

	protected int maxChildCount = 1;

	/**
	 * The useCount is used to keep track of how many times an Alpha node is
	 * shared. This is needed so that we can dynamically remove a rule at run
	 * time and remove the node from the network. If we didn't keep count, it
	 * would be harder to figure out if we can remove the node.
	 */
	protected int useCount = 0;

	/**
	 * BaseNode has only one constructor which takes an unique node id. All
	 * subclasses need to call the constructor.
	 */
	public rtBaseNode(int id) {
		super();
		this.nodeID = id;
	}

	public rtBaseNode[] getParentNodes() {
		return parentNodes;
	}

	public rtBaseNode[] getChildNodes() {
		return childNodes;
	}

	/**
	 * Add the node to the list of successors
	 * 
	 * @param n
	 * @return
	 */
	public boolean addNode(rtBaseNode n) {
		boolean add = false;
		// check if not inserted yet and free space for subchild:
		if (!containsNode(this.childNodes, n) && childNodes.length < maxChildCount)
			// inform added child node:
			if (n.evAdded(this)) {
				// add to own list:
				this.childNodes = ConversionUtils.add(this.childNodes, n);
				// inc own node use count
				useCount++;
				add = true;
			}
		return add;
	}

	/**
	 * This node has been added to the given parant node
	 * 
	 * @param n
	 * @return
	 */
	private boolean evAdded(rtBaseNode newParentNode) {
		// we have been added to the new parent, add parent to own list:
		if (!containsNode(this.parentNodes, newParentNode) && childNodes.length < maxParentCount) {
			// add to own list:
			this.parentNodes = ConversionUtils.add(this.parentNodes, newParentNode);
			return true;
		}
		return false;
	}

	/**
	 * remove the node from the succesors
	 * 
	 * @param n
	 * @return
	 */
	public boolean removeNode(rtBaseNode n) {
		boolean rem = false;
		if (containsNode(this.childNodes, n))
			// inform removed child node:
			if (n.evRemoved(this)) {
				this.childNodes = ConversionUtils.remove(this.childNodes, n);
				// dec own node use count
				useCount--;
				if (useCount == 0)
					evZeroUseCount();
				rem = true;
			}
		return rem;
	}
	
	public void destroy(){
		for (int i =0; i<parentNodes.length;i++){
			parentNodes[i].removeNode(this);
		}
	}

	/**
	 * This node has been added to the given parant node
	 * 
	 * @param n
	 * @return
	 */
	private boolean evRemoved(rtBaseNode oldParentNode) {
		// we have been added to the new parent, add parent to own list:
		if (containsNode(this.parentNodes, oldParentNode)) {
			// add to own list:
			this.parentNodes = ConversionUtils.remove(this.parentNodes, oldParentNode);
			return true;
		}
		return false;
	}

	/**
	 * This method stub handles what happens if the usecount gets zero possible
	 * actions: remove self from parents:
	 * 
	 * @return
	 */
	protected abstract void evZeroUseCount();
	
	/**
	 * Subclasses need to implement clear and make sure all
	 * memories are cleared properly.
	 */
	public abstract void clear();

	/**
	 * toPPString should return a string format, but formatted nicely so it's
	 * easier for humans to read. Chances are this method will be used in
	 * debugging mode, so the more descriptive the string is, the easier it is
	 * to figure out what the node does.
	 * 
	 * @return
	 */
	public abstract String toPPString();

	protected boolean containsNode(rtBaseNode[] list, rtBaseNode n) {
		boolean cn = false;
		for (int idx = 0; idx < list.length; idx++) {
			if (list[idx] == n) {
				cn = true;
				break;
			}
		}
		return cn;
	}
	
	
}
