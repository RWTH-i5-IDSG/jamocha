package org.jamocha.rule;

import org.jamocha.rete.nodes.BaseNode;
import org.jamocha.rete.SFRuleCompiler;

public interface SelfCompilerInterface {

	/**
	 * compile yourself!
	 * 
	 * @param compiler
	 * @return an object ;)
	 */
	BaseNode compile(SFRuleCompiler compiler, Rule rule, int conditionIndex);
}
