package org.jamocha.rule;

import org.jamocha.parser.EvaluationException;
import org.jamocha.rete.StopCompileException;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.nodes.Node;
import org.jamocha.rete.rulecompiler.sfp.SFRuleCompiler;

public interface Compileable {

	/**
	 * compile yourself!
	 * 
	 * @param compiler
	 * @param bindingHelper 
	 * @return an object ;)
	 * @throws StopCompileException 
	 * @throws AssertException 
	 * @throws EvaluationException 
	 */
	Node compile(SFRuleCompiler compiler, Rule rule, int conditionIndex) throws AssertException, StopCompileException, EvaluationException;
}
