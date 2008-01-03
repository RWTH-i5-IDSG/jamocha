package org.jamocha.rete.nodes;

import org.jamocha.rete.memory.WorkingMemory;
import org.jamocha.rete.memory.WorkingMemoryElement;
import org.jamocha.rete.visualisation.NodeDrawer;
import org.jamocha.rete.visualisation.nodedrawers.RootNodeDrawer;

/**
 * @author Josef Alexander Hahn <mail@josef-hahn.de>
 * the root in our rete network. here, all facts will come into play and
 * will be propagated to the child nodes. so, here, no filter logic is implemented.
 */
public class RootNode extends Node {

	public RootNode(int id, WorkingMemory memory, ReteNet net) {
		super(id,memory,net);
	}
	
	public void addWME(WorkingMemoryElement newElem) throws NodeException {
		addAndPropagate(newElem);
	}

	public Node[] getParentNodes() {
		final Node[] empty = {};
		return empty;
	}

	public void removeWME(WorkingMemoryElement oldElem) throws NodeException {
		removeAndPropagate(oldElem);
	}

	@Override
	public boolean outputsBeta() {
		return true;
	}

	@Override
	public Node registerParent(Node n) {
		return this;
		// do nothing here, because a RootNode never can become a parent
	}
	
	protected NodeDrawer newNodeDrawer() {
		return new RootNodeDrawer(this);
	}

}
