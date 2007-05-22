package org.jamocha.rule;

import org.jamocha.rete.nodes.BaseNode;
import org.jamocha.rete.SFRuleCompiler;

public interface Compileable {

	/**
	 * compile yourself!
	 * 
	 * @param compiler
	 * @param bindingHelper 
	 * @return an object ;)
	 */
	BaseNode compile(SFRuleCompiler compiler, Rule rule, int conditionIndex);
}
