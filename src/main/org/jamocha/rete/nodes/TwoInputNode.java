package org.jamocha.rete.nodes;

import org.jamocha.rete.memory.WorkingMemory;
import org.jamocha.rete.memory.WorkingMemoryElement;

/**
 * @author Josef Alexander Hahn <mail@josef-hahn.de> an abstract node with two
 *         inputs: an alpha- and a beta- input. it is a subclass from
 *         OneInputNode, since a node with two inputs can be seen as a node with
 *         one input and an additional one ;)
 */
public abstract class TwoInputNode extends OneInputNode {

	protected Node betaInput;

	public TwoInputNode(int id, WorkingMemory memory, ReteNet net) {
		super(id, memory, net);
		betaInput = null;
	}

	@Override
	public Node[] getParentNodes() {
		if (alphaInput == null && betaInput == null) {
			Node[] result = {};
			return result;
		} else if (alphaInput == null) {
			Node[] result = { betaInput };
			return result;
		} else if (betaInput == null) {
			Node[] result = { alphaInput };
			return result;
		} else {
			Node[] result = { alphaInput, betaInput };
			return result;
		}
	}

	/**
	 * returns the beta input node
	 */
	public Node getBetaInput() {
		return betaInput;
	}

	protected void plugBetaParent(Node node) throws InvalidOperationException,
			NodeException {
		if (betaInput != null)
			throw new InvalidOperationException("there already is a beta input");
		betaInput = node;
		node.addChild(this);
	}

	protected Node registerParent(Node n) throws NodeException {
		if (n.outputsBeta()) {
			betaInput = n;
			return this;
		} else {
			// maybe, we need an adaptor node
			if (alphaInput == null) {
				alphaInput = n;
				return this;
			} else {
				LeftInputAdaptorNode lia = new LeftInputAdaptorNode(net
						.nextNodeId(), workingMemory, net);
				lia.addChild(this);
				lia.registerParent(n);
				betaInput = lia;
				return lia;
			}
		}
	}

	/**
	 * returns the beta input wmes
	 */
	protected Iterable<WorkingMemoryElement> beta() {
		if (getBetaInput() != null) {
			return getBetaInput().memory();
		}
		return null;
	}

}
