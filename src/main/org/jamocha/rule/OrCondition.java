package org.jamocha.rule;

import org.jamocha.rete.nodes.BaseNode;
import org.jamocha.rete.SFRuleCompiler;

public class OrCondition extends BooleanOperatorCondition {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BaseNode compile(SFRuleCompiler compiler, Rule rule, int conditionIndex) {
		return compiler.compile(this, rule, conditionIndex);
	}
}
