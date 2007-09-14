package org.jamocha.rule;

import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.nodes.BaseNode;
import org.jamocha.rete.rulecompiler.sfp.SFRuleCompiler;
import org.jamocha.rete.StopCompileException;

public interface Compileable {

	/**
	 * compile yourself!
	 * 
	 * @param compiler
	 * @param bindingHelper 
	 * @return an object ;)
	 * @throws StopCompileException 
	 * @throws AssertException 
	 */
	BaseNode compile(SFRuleCompiler compiler, Rule rule, int conditionIndex) throws AssertException, StopCompileException;
}
