package org.jamocha.rete;

import org.jamocha.rete.nodes.AbstractBeta;
import org.jamocha.rete.nodes.FactTuple;

public class BetaNotNode extends AbstractBeta {

	public BetaNotNode(int id) {
		super(id);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected boolean evaluate(FactTuple tuple, Fact rfcts) {
		// TODO Auto-generated method stub
		return false;
	}

}
