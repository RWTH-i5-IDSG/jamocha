package org.jamocha.rule;

import org.jamocha.rete.nodes.BaseNode;
import org.jamocha.rete.SFRuleCompiler;

public class NotCondition extends BooleanOperatorCondition {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public BaseNode compile(SFRuleCompiler compiler, Rule rule, int conditionIndex, BindingHelper bindingHelper) {
		return compiler.compile(this, rule, conditionIndex, bindingHelper);
	}

}
