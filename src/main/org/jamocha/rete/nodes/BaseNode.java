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

import org.jamocha.rete.ConversionUtils;
import org.jamocha.rete.Rete;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.exception.RetractException;

/**
 * @author Sebastian Reinartz
 * 
 * BaseNode is meant to define common logic that all nodes must have and
 * implement common logic.
 */
public abstract class BaseNode implements Serializable {

	protected int nodeID;

	/**
	 * We use an object Array to keep things efficient
	 */
	protected BaseNode[] childNodes = new BaseNode[0];

	protected BaseNode[] parentNodes = new BaseNode[0];

	// override these values in subclasses:
	protected int maxParentCount = 1;

	protected int maxChildCount = 1;

	/**
	 * BaseNode has only one constructor which takes an unique node id. All
	 * subclasses need to call the constructor.
	 */
	public BaseNode(int id) {
		super();
		this.nodeID = id;
	}

	public BaseNode[] getParentNodes() {
		return parentNodes;
	}

	public BaseNode[] getChildNodes() {
		return childNodes;
	}
	
	public int getChildCount(){
		return childNodes.length;
	}
	
	public int getParentCount(){
		return parentNodes.length;
	}

	/**
	 * Add the node to the list of successors
	 * 
	 * @param n
	 * @return
	 */
	public boolean addNode(BaseNode n, Rete engine) throws AssertException {
		boolean add = false;
		// check if not inserted yet and free space for subchild:
//		if (!containsNode(this.childNodes, n) && childNodes.length < maxChildCount) {
		if (childNodes.length < maxChildCount) {
			// inform added child node:
			BaseNode weWillAddThisNode = n.evAdded(this, engine);
			if (weWillAddThisNode != null) {
				// add to own list:
				this.childNodes = ConversionUtils.add(this.childNodes, weWillAddThisNode);
				mountChild(weWillAddThisNode, engine);
				add = true;}
			else{
				throw new AssertException("Adding Node not Possible, Child does not want to be added");
			}
		}
		
		try {
			checkForConsistence();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return add;
	}

	protected abstract void mountChild(BaseNode newChild, Rete engine) throws AssertException;

	/**
	 * This node has been added to the given parant node
	 * 
	 * @param n
	 * @return
	 */
	protected BaseNode evAdded(BaseNode newParentNode, Rete engine) {
		// we have been added to the new parent, add parent to own list:
		if (!containsNode(this.parentNodes, newParentNode) && parentNodes.length < maxParentCount) {
			// add to own list:
			this.parentNodes = ConversionUtils.add(this.parentNodes, newParentNode);
			return this;
		}
		return null;
	}

	/**
	 * remove the node from the succesors
	 * 
	 * @param n
	 * @return
	 * @throws RetractException 
	 */
	public boolean removeNode(BaseNode n, Rete engine) throws RetractException {
		boolean rem = false;
		if (containsNode(this.childNodes, n))
			// inform removed child node:
			if (n.evRemoved(this)) {
				this.childNodes = ConversionUtils.remove(this.childNodes, n);
				unmountChild(n, engine);
				// dec own node use count
				if (getChildCount() == 0)
					evZeroUseCount(engine);
				rem = true;
			}
		return rem;
	}

	protected abstract void unmountChild(BaseNode oldChild, Rete engine) throws RetractException ;

	public void destroy(Rete engine) throws RetractException {
		for( BaseNode node: parentNodes){
		node.removeNode(this, engine);
			
		}
	//	for (int i = 0; i < parentNodes.length; i++) {
		//	parentNodes[i].removeNode(this, engine);
		//}
	}

	/**
	 * This node has been added to the given parant node
	 * 
	 * @param n
	 * @return
	 */
	private boolean evRemoved(BaseNode oldParentNode) {
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
	protected void evZeroUseCount(Rete engine)  {
		try {
			destroy(engine);
		} catch (RetractException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Subclasses need to implement clear and make sure all memories are cleared
	 * properly.
	 */
	public void propagateClear() {
		for (BaseNode nNode : childNodes) {
			clear();
			nNode.propagateClear();
		}
	}

	public abstract void clear();
	

	/**
	 * toPPString should return a string format, but formatted nicely so it's
	 * easier for humans to read. Chances are this method will be used in
	 * debugging mode, so the more descriptive the string is, the easier it is
	 * to figure out what the node does.
	 * 
	 * @return
	 */
	public String toPPString(){
		return toString();
	}

	public String toString() {
		StringBuffer result = new StringBuffer();
		result.append("Type: ");
		result.append(this.getClass().getSimpleName());
		result.append("\nID: ");
		result.append(getNodeId());
		result.append("\nSubnodes: ");
		result.append(getChildCount());
		result.append("\n");
		return result.toString();
	}

	protected boolean containsNode(BaseNode[] list, BaseNode n) {
		boolean cn = false;
		for (int idx = 0; idx < list.length; idx++) {
			if (list[idx] == n) {
				cn = true;
				break;
			}
		}
		return cn;
	}

	/**
	 * method for propogating the retract
	 * 
	 * @param fact
	 * @param engine
	 */
	protected void propogateRetract(Assertable fact, Rete engine) throws RetractException {
		for (BaseNode nNode : childNodes) {
			nNode.retractFact(fact, engine, this);
		}
	}

	/**
	 * Method is used to pass a fact to the successor nodes
	 * 
	 * @param fact
	 * @param engine
	 */
	protected void propogateAssert(Assertable fact, Rete engine) throws AssertException {
		for (BaseNode nNode : childNodes) {
			nNode.assertFact(fact, engine, this);
		}
	}

	// use of good old Delphi sender...
	public abstract void assertFact(Assertable fact, Rete engine, BaseNode sender) throws AssertException;

	public abstract void retractFact(Assertable fact, Rete engine, BaseNode sender) throws RetractException;

	public boolean isRightNode() {
		return true;
	}

	public int getNodeId() {
		return this.nodeID;
	}

	/*
	 * this method is for debugging purposes only. it checks the
	 * consistency of parent- and child-arrays. since that is a
	 * non-productive method, it is not optimized and a bit redundant ;)
	 */
	protected void checkForConsistence() throws Exception {
		for (BaseNode child : childNodes) {
			if (!containsNode(child.parentNodes, this)) {
				throw new Exception("Array inconsistent. my("+this.getNodeId()+") child-array contains "+child.getNodeId()+" but it doesnt holds me as parent!");
			}
		}
		for (BaseNode parent : parentNodes) {
			if (!containsNode(parent.childNodes, this)) {
				throw new Exception("Array inconsistent. my("+this.getNodeId()+") parent-array contains "+parent.getNodeId()+" but it doesnt holds me as child!");
			}			
		}
	}
	
}
