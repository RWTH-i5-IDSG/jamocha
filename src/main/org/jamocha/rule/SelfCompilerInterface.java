package org.jamocha.rule;

import org.jamocha.rete.RuleCompiler;

public interface SelfCompilerInterface {

	/**
	 * compile yourself!
	 * 
	 * @param compiler
	 * @return an object ;)
	 */
	Object compile(RuleCompiler compiler,
			int conditionIndex, Rule rule);

}
