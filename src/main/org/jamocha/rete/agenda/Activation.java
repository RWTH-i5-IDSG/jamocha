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
	
	protected boolean valid = true;
	
	protected FactTuple tuple;
	
	public boolean equals(Object o) {
		if (o instanceof Activation) {
			Activation that = (Activation)o;
			return this.rule == that.rule && this.tuple.equals(that);
		}
		return false;
	}
	
	
	
	public Activation(Rule rule, FactTuple tuple){
		setRule(rule);
		setTuple(tuple);
	}
	
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
		engine.pushScope(rule);
		rule.setTriggerFacts(tuple.getFacts());
		for (Action action : rule.getActions()) {
			action.executeAction(engine, tuple.getFacts());
		}
		engine.popScope();
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

}
