package org.jamocha.rule;

import java.util.List;

import org.jamocha.formatter.Formatter;
import org.jamocha.rete.nodes.BaseNode;
import org.jamocha.rete.SFRuleCompiler;

public class NotCondition extends ConditionWithNested {

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

	
	@Override
	protected String clipsName() {return "not";}

	public String format(Formatter visitor) {
		return visitor.visit(this);
	}

}
