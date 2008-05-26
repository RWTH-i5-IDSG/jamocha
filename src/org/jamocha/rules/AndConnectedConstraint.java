package org.jamocha.rules;

import org.jamocha.formatter.Formatter;
import org.jamocha.parser.JamochaValue;
import org.jamocha.engine.StopCompileException;
import org.jamocha.engine.nodes.Node;
import org.jamocha.engine.rules.rulecompiler.sfp.SFRuleCompiler;

public class AndConnectedConstraint extends AbstractConnectedConstraint {

	public AndConnectedConstraint(Constraint left, Constraint right, boolean negated) {
		super(left, right, negated);
	}

	private static final long serialVersionUID = 1L;

	public Node compile(SFRuleCompiler compiler, Rule rule,
			int conditionIndex) throws StopCompileException {
		return compiler.compile(this, rule, conditionIndex);
	}

	public String format(Formatter visitor) {
		return visitor.visit(this);
	}
	
}
