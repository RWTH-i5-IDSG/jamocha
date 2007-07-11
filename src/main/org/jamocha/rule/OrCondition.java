package org.jamocha.rule;

import java.util.List;

import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.nodes.BaseNode;
import org.jamocha.rete.SFRuleCompiler;
import org.jamocha.rete.StopCompileException;

public class OrCondition extends ConditionWithNested {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BaseNode compile(SFRuleCompiler compiler, Rule rule, int conditionIndex) throws AssertException, StopCompileException {
		return compiler.compile(this, rule, conditionIndex);
	}

	public List<Constraint> getConstraints() {
		return null;
	}
	
	protected String clipsName() {return "or";}
}
