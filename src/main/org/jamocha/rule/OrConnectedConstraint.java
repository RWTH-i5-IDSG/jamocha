package org.jamocha.rule;

import org.jamocha.formatter.Formatter;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.StopCompileException;
import org.jamocha.rete.nodes.BaseNode;
import org.jamocha.rete.rulecompiler.sfp.SFRuleCompiler;

public class OrConnectedConstraint extends AbstractConnectedConstraint {

	private static final long serialVersionUID = 1L;

	public boolean getNegated() {
		// TODO Auto-generated method stub
		return false;
	}

	public JamochaValue getValue() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setNegated(boolean negate) {
		// TODO Auto-generated method stub

	}

	public void setValue(JamochaValue val) {
		// TODO Auto-generated method stub

	}

	public BaseNode compile(SFRuleCompiler compiler, Rule rule,
			int conditionIndex) throws StopCompileException {
		return compiler.compile(this, rule, conditionIndex);
	}

	public String format(Formatter visitor) {
		return visitor.visit(this);
	}

}
