package org.jamocha.rule;

import java.util.List;

import org.jamocha.rete.nodes.BaseNode;
import org.jamocha.rete.SFRuleCompiler;

public class NotCondition extends BooleanOperatorCondition implements IsQuantorCondition  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public BaseNode compile(SFRuleCompiler compiler, Rule rule, int conditionIndex) {
		return compiler.compile(this, rule, conditionIndex);
	}

	public List<Constraint> getConstraints() {
		return null;
	}
	
	public ObjectCondition getObjectCondition() {
		ObjectCondition oc = null;
		oc = (ObjectCondition) nestedCE.get(0);
		return oc;
	}

}
