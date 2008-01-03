package org.jamocha.rete.nodes;

import org.jamocha.rete.memory.WorkingMemory;
import org.jamocha.rete.memory.WorkingMemoryElement;

/**
 * @author Josef Alexander Hahn <mail@josef-hahn.de>
 * an abstract class for nodes, which has only one (alpha-) input
 */
public abstract class OneInputNode extends Node {

	protected Node alphaInput;
	
	public OneInputNode(int id, WorkingMemory memory, ReteNet net) {
		super(id,memory,net);
		alphaInput = null;
	}
	
	@Override
	public Node[] getParentNodes() {
		final Node[] empty = {};
		if (alphaInput == null) return empty;
		Node[] result = {alphaInput};
		return result;
	}
	
	/**
	 * returns the alpha input node
	 */
	public Node getAlphaInput() {
		return alphaInput;
	}
	
	protected void plugAlphaParent(Node node) throws InvalidOperationException, NodeException {
		if (alphaInput != null) throw new InvalidOperationException("there already is an alpha input");
		alphaInput = node;
		node.addChild(this);
	}
	
	protected Node registerParent(Node n) throws NodeException {
		alphaInput = n;
		return this;
	}
	
	/**
	 * returns the alpha input wmes
	 */
	protected Iterable<WorkingMemoryElement> alpha() {
		return getAlphaInput().memory();
	}

}
