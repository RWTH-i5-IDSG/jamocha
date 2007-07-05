package org.jamocha.rete.nodes;

import java.util.Iterator;

import org.jamocha.rete.Fact;
import org.jamocha.rete.Rete;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.exception.RetractException;

public class BetaQuantorFilterNode extends BetaFilterNode {

	public boolean negated;
	
	public BetaQuantorFilterNode(int id,boolean negated) {
		super(id);
		this.negated=negated;
	}

	public BetaQuantorFilterNode(int id) {
		this(id,false);
	}
	
	public boolean controlledNot(boolean control, boolean data){
		if (control) {
			return !data;
		} else {
			return data;
		}
		
	}

	
	@Override
	protected boolean evaluate(FactTuple tuple, Fact rfcts) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public void activate(Rete engine) throws AssertException {
		if (!activated) {
			// we have to traverse the whole beta mem and eval it.
			activated = true;
			if ( controlledNot(negated, alphaMemory.getSize()>0 ) ){
				try {
					fire(engine);
				} catch (RetractException e) {
					throw new AssertException(e);
				}
			}
		}
	}
	
	private void fire(Rete engine) throws RetractException{
		for (FactTuple t : betaMemory) {
			FactTuple newTuple = t.addFact(engine.getFactById(0));
			try {
				propogateAssert(newTuple, engine);
			} catch (AssertException e) {
				throw new RetractException(e);
			}
			
		}
	}
	
	private void backfire(Rete engine) throws RetractException{
		for (FactTuple t : betaMemory) {
			this.propogateRetract(t, engine);
		}
	}
	
	
	public void retractRight(Fact fact, Rete engine) throws RetractException {
		alphaMemory.remove(fact);
		if (alphaMemory.isEmpty() && negated) {
			fire(engine);
		} else if (alphaMemory.isEmpty() && !negated) {
			backfire(engine);
		}
	}
	
	
	public void assertRight(Fact fact, Rete engine) throws AssertException {
		if (alphaMemory.isEmpty()) {
			alphaMemory.add(fact);
			if (activated) {
				try{
					if (negated) backfire(engine);	else fire(engine);
				} catch (RetractException e){
					throw new AssertException(e);
				}
			}
		} else {
			alphaMemory.add(fact);			
		}
	}
}
