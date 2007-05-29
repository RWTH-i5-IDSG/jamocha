package org.jamocha.rete.nodes;

import org.jamocha.rete.Fact;
import org.jamocha.rete.Rete;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.exception.RetractException;


//TODO implement all that stuff
public class BetaFunctionNode extends AbstractBeta {

	public BetaFunctionNode(int id) {
		super(id);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void assertLeft(FactTuple tuple, Rete engine) throws AssertException {
		// TODO Auto-generated method stub

	}

	@Override
	public void assertRight(Fact fact, Rete engine) throws AssertException {
		// TODO Auto-generated method stub

	}

	@Override
	public void retractLeft(FactTuple tupel, Rete engine)
			throws RetractException {
		// TODO Auto-generated method stub

	}

	@Override
	public void retractRight(Fact fact, Rete engine) throws RetractException {
		// TODO Auto-generated method stub

	}

}
