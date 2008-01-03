package org.jamocha.rete.nodes;

import org.jamocha.rete.memory.WorkingMemory;
import org.jamocha.rete.memory.WorkingMemoryElement;
import org.jamocha.rete.visualisation.NodeDrawer;
import org.jamocha.rete.visualisation.nodedrawers.LIANodeDrawer;
import org.jamocha.rete.visualisation.nodedrawers.RootNodeDrawer;

/**
 * @author Josef Alexander Hahn <mail@josef-hahn.de>
 * A left input adaptor node converts incoming alpha-wmes to
 * beta-wmes by wrapping them in a length-1-tuple 
 */
public class LeftInputAdaptorNode extends OneInputNode {

	public LeftInputAdaptorNode(int id, WorkingMemory memory, ReteNet net) {
		super(id, memory, net);
	}

	@Override
	public void addWME(WorkingMemoryElement elem) throws NodeException {
		WorkingMemoryElement newElem = elem.getFactTuple();
		addAndPropagate(newElem);
	}

	@Override
	public void removeWME(WorkingMemoryElement elem) throws NodeException {
		WorkingMemoryElement newElem = elem.getFactTuple();
		removeAndPropagate(newElem);
	}

	@Override
	public boolean outputsBeta() {
		return true;
	}

	protected NodeDrawer newNodeDrawer() {
		return new LIANodeDrawer(this);
	}
	
}
