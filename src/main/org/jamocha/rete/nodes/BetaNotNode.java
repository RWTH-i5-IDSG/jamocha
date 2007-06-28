package org.jamocha.rete.nodes;

import org.jamocha.rete.Fact;
import org.jamocha.rete.Rete;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.exception.RetractException;

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
	
	public void retractRight(Fact fact, Rete engine) throws RetractException {
		alphaMemory.remove(fact);
		if (alphaMemory.isEmpty()) {
			
			for (FactTuple t : betaMemory) {
				
				FactTuple newTuple = t.addFact(engine.getFactById(0));
				
				try {
					propogateAssert(newTuple, engine);
				} catch (AssertException e) {
					throw new RetractException(e);
				}
				
			}
			
		}
		
	}
	
	
	public void assertRight(Fact fact, Rete engine) throws AssertException {
		if (alphaMemory.isEmpty()) {
			alphaMemory.add(fact);
			if (activated) {
				for (FactTuple t : betaMemory) {
					try {
						this.propogateRetract(t, engine);
					} catch (RetractException e) {
						throw new AssertException(e);
					}
				}
			}
		} else {
			alphaMemory.add(fact);			
		}
	}
}
