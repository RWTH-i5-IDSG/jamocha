package org.jamocha.rete.nodes;

import org.jamocha.rete.Fact;
import org.jamocha.rete.Rete;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.exception.RetractException;
import org.jamocha.rule.Action;


//TODO implement all that stuff
public class BetaFunctionNode extends AbstractBeta {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3602113459245678044L;
	
	Action evaluatingAction = null;
	
	public Action getEvaluatingAction() {
		return evaluatingAction;
	}

	public void setEvaluatingAction(Action evaluatingAction) {
		this.evaluatingAction = evaluatingAction;
	}

	public BetaFunctionNode(int id) {
		super(id);
	}

	@Override
	protected boolean evaluate(FactTuple tuple, Fact rfcts) {
		// TODO Auto-generated method stub
		return false;
	}

	
}
