/*
 * Copyright 2002-2008 The Jamocha Team
 * 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.jamocha.org/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package org.jamocha.engine.nodes;

import java.util.Iterator;

import org.jamocha.application.gui.retevisualisation.NodeDrawer;
import org.jamocha.engine.ReteNet;
import org.jamocha.engine.workingmemory.WorkingMemory;
import org.jamocha.engine.workingmemory.WorkingMemoryElement;

/**
 * @author Josef Alexander Hahn <mail@josef-hahn.de> A left input adaptor node
 *         converts incoming alpha-wmes to beta-wmes by wrapping them in a
 *         length-1-tuple
 */
public abstract class Node {

	protected Node[] childs;

	protected int nodeId;

	protected WorkingMemory workingMemory;

	protected ReteNet net;

	protected NodeDrawer drawer;

	protected boolean activated = false;

	public Node() {
		childs = new Node[0];
	}

	/**
	 * returns the node id
	 */
	public int getId() {
		return nodeId;
	}

	public boolean isActivated() {
		return activated;
	}

	protected void activate2() throws NodeException {
		// we are activated
		activated = true;
		// activate subnodes recursively
		for (final Node n : getChildNodes())
			n.activate2();
	}
	
	public void activate() throws NodeException {
		activate2();
		// fetch working memory elements from above and evaluate them
		for (final Node child : getChildNodes())
			for (final WorkingMemoryElement wme : child.memory())
				child.propagateAddition(wme);
	}

	protected void getDescriptionString(final StringBuilder sb) {
		sb.append("|id:").append(nodeId).append("|");
		sb.append("childs:");
		for (final Node child : getChildNodes())
			sb.append(child.getId()).append(",");
		sb.append("|");
		sb.append("parents:");
		for (final Node parent : getParentNodes())
			sb.append(parent.getId()).append(",");
		int len = net.getEngine().getWorkingMemory().size(this);
		sb.append("|elements-in-memory:").append(len);

	}

	/**
	 * returns a complete description of the node, including the working memory
	 */
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("[").append(this.getClass().getSimpleName());
		getDescriptionString(sb);
		sb.append("]\n");
		return sb.toString();
	}

	/**
	 * adds a child
	 */
	public void addChild(final Node n) throws NodeException {
		final Node[] newArr = new Node[childs.length + 1];
		System.arraycopy(childs, 0, newArr, 0, childs.length);
		final Node newChild = n.registerParent(this);
		newArr[childs.length] = newChild;
		childs = newArr;
		for (final WorkingMemoryElement wme : memory())
			newChild.addWME(wme);
	}

	/**
	 * returns the node's working memory (which is the output of the node)
	 */
	public Iterable<WorkingMemoryElement> memory() {
		return workingMemory.getMemory(this);
	}

	public Node(final int id, final WorkingMemory memory, final ReteNet net) {
		this();
		nodeId = id;
		workingMemory = memory;
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
	public abstract void addWME(WorkingMemoryElement newElem)
			throws NodeException;

	/**
	 * this method is called from outside, when a wme is removed from the input.
	 * whether this wme comes from the alpha- or beta-input is determined
	 * automatically.
	 */
	public abstract void removeWME(WorkingMemoryElement oldElem)
			throws NodeException;

	/**
	 * propagates the addition of a new wme to the child nodes.
	 */
	protected void propagateAddition(final WorkingMemoryElement elem)
			throws NodeException {
		for (final Node child : getChildNodes())
			child.addWME(elem);
	}

	/**
	 * propagates the removal of an old wme to the child nodes.
	 */
	protected void propagateRemoval(final WorkingMemoryElement elem)
			throws NodeException {
		for (final Node child : getChildNodes())
			child.removeWME(elem);
	}

	/**
	 * adds a new wme and propagates the changes
	 */
	protected void addAndPropagate(final WorkingMemoryElement e)
			throws NodeException {
		if (workingMemory.add(this, e))
			propagateAddition(e);
	}

	/**
	 * removes an old wme and propagates the changes
	 */
	protected void removeAndPropagate(final WorkingMemoryElement e)
			throws NodeException {
		if (workingMemory.remove(this, e))
			propagateRemoval(e);
	}

	/**
	 * flushes the node's working memory and propagates this.
	 */
	public void flush() throws NodeException {
		// that will not happen very often, so we can live with this
		// slow-but-clean implementation
		final Iterator<WorkingMemoryElement> i = memory().iterator();
		while (i.hasNext()) {
			i.next();
			i.remove();
		}
	}

	/**
	 * returns the NodeDrawer for this node. It is used by the visualizer.
	 */
	public NodeDrawer getNodeDrawer() {
		if (drawer == null)
			drawer = newNodeDrawer();
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
