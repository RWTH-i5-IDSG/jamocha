package org.jamocha.rete.nodes;

import java.util.Iterator;

import org.jamocha.rete.memory.WorkingMemory;
import org.jamocha.rete.memory.WorkingMemoryElement;
import org.jamocha.rete.visualisation.NodeDrawer;
import org.jamocha.rete.visualisation.nodedrawers.*;


/**
 * @author Josef Alexander Hahn <mail@josef-hahn.de>
 * A left input adaptor node converts incoming alpha-wmes to
 * beta-wmes by wrapping them in a length-1-tuple 
 */
public abstract class Node {
	
	protected Node[] childs;
	
	protected int nodeId;
	
	protected WorkingMemory workingMemory;
	
	protected ReteNet net;
	
	protected NodeDrawer drawer;
		
	public Node() {
		childs = new Node[0];
	}
	
	/**
	 * returns the node id
	 */
	public int getId() {
		return nodeId;
	}

	protected void getDescriptionString(StringBuilder sb) {
		sb.append("|id:").append(nodeId).append("|");
		sb.append("childs:");
		for (Node child:getChildNodes()) sb.append(child.getId()).append(",");
		sb.append("|");
		sb.append("parents:");
		for (Node parent:getParentNodes()) sb.append(parent.getId()).append(",");
		
	}
	
	/**
	 * returns a complete description of the node, including the working memory
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[").append(this.getClass().getSimpleName());
		getDescriptionString(sb);
		sb.append("|working-memory:\n");
		for (WorkingMemoryElement wme : memory()) sb.append(wme).append("\n");
		sb.append("]\n");
		return sb.toString();
	}
	
	/**
	 * adds a child 
	 */
	public void addChild(Node n) throws NodeException {
		Node[] newArr = new Node[childs.length+1];
		System.arraycopy(childs, 0, newArr, 0, childs.length);
		Node newChild =  n.registerParent(this);
		newArr[childs.length] = newChild;
		childs = newArr;
		for (WorkingMemoryElement wme : memory()) {
			newChild.addWME(wme);
		}
	}
	
	/**
	 * returns the node's working memory (which is the output of the node) 
	 */
	public Iterable<WorkingMemoryElement> memory() {
		return workingMemory.getMemory(this);
	}
	
	public Node(int id, WorkingMemory memory, ReteNet net) {
		this();
		this.nodeId = id;
		this.workingMemory = memory;
		this.net = net;
	}
	
	/**
	 * returns the parent nodes
	 */
	public abstract Node[] getParentNodes();
	
	/**
	 * returns the child nodes
	 */
	public Node[] getChildNodes() {
		return childs;
	}
	
	/**
	 * this method is called from outside, when a new wme is added to the input.
	 * whether this wme comes from the alpha- or beta-input is determined
	 * automatically.
	 */
	public abstract void addWME(WorkingMemoryElement newElem) throws NodeException;
	
	/**
	 * this method is called from outside, when a wme is removed from the input.
	 * whether this wme comes from the alpha- or beta-input is determined
	 * automatically.
	 */
	public abstract void removeWME(WorkingMemoryElement oldElem) throws NodeException;
	
	/**
	 * propagates the addition of a new wme to the child nodes. 
	 */
	protected void propagateAddition(WorkingMemoryElement elem) throws NodeException {
		for (Node child : getChildNodes()) child.addWME(elem);
	}
	
	/**
	 * propagates the removal of an old wme to the child nodes.
	 */
	protected void propagateRemoval(WorkingMemoryElement elem) throws NodeException {
		for (Node child : getChildNodes()) child.removeWME(elem);
	}
	
	/**
	 * adds a new wme and propagates the changes
	 */
	protected void addAndPropagate(WorkingMemoryElement e) throws NodeException {
		if ( workingMemory.add(this, e)) propagateAddition(e);
	}

	/**
	 * removes an old wme and propagates the changes
	 */
	protected void removeAndPropagate(WorkingMemoryElement e) throws NodeException {
		if ( workingMemory.remove(this, e)) propagateRemoval(e);
	}
	
	/**
	 * flushes the node's working memory and propagates this.
	 */
	public void flush() throws NodeException {
		// that will not happen very often, so we can live with this
		// slow-but-clean implementation
		Iterator<WorkingMemoryElement> i = memory().iterator();
		while (i.hasNext()) {
			i.next();
			i.remove();
		}
	}
	
	/**
	 * returns the NodeDrawer for this node. It is used by the visualizer.
	 */
	public NodeDrawer getNodeDrawer(){
		if (drawer == null) {
			drawer = newNodeDrawer();
		}
		return drawer;
	}
	
	protected abstract NodeDrawer newNodeDrawer();
	
	/**
	 * returns true, iff the output is a beta one.
	 */
	public abstract boolean outputsBeta();
	
	/**
	 * this method is called from a node, which tries to add us as a new child.
	 */
	protected abstract Node registerParent(Node n) throws NodeException;
}
