package org.jamocha.rete.agenda;

import org.jamocha.rete.Rete;
import org.jamocha.rete.exception.ExecuteException;
import org.jamocha.rete.nodes.FactTuple;
import org.jamocha.rule.Action;
import org.jamocha.rule.Rule;

/**
 * @author Josef Alexander Hahn
 */
public class Activation {

	protected Rule rule;
	
	protected FactTuple tuple;
	
	public void setRule(Rule rule){
		this.rule=rule;
	}
	
	public Rule getRule(){
		return rule;
	}

	public FactTuple getTuple() {
		return tuple;
	}

	public void setTuple(FactTuple tuple) {
		this.tuple = tuple;
	}
	
	public void fire(Rete engine) throws ExecuteException{
		for (Action action : rule.getActions()) {
			action.executeAction(engine, tuple.getFacts());
		}
	}
	
	
}
