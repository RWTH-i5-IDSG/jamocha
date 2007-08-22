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
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((rule == null) ? 0 : rule.hashCode());
		result = prime * result + ((tuple == null) ? 0 : tuple.hashCode());
		return result;
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Activation other = (Activation) obj;
		if (rule == null) {
			if (other.rule != null)
				return false;
		} else if (!rule.equals(other.rule))
			return false;
		if (tuple == null) {
			if (other.tuple != null)
				return false;
		} else if (!tuple.equals(other.tuple))
			return false;
		return true;
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
	
	public String toString(){
		return rule.toString() + " " + tuple.toPPString();
	}

}
