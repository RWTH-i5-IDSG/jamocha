package org.jamocha.rule;

import org.jamocha.formatter.Formatter;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.StopCompileException;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.nodes.BaseNode;
import org.jamocha.rete.rulecompiler.sfp.SFRuleCompiler;

public class OrderedFactConstraint extends AbstractConstraint {
	
	private static final long serialVersionUID = 1L;
	
	private JamochaValue val;

	public boolean getNegated() {
		// does nothing
		return false;
	}

	public JamochaValue getValue() {
		return val;
	}

	public void setNegated(boolean negate) {
		// does nothing
	}

	public void setValue(JamochaValue val) {
		this.val = val;
	}

	public BaseNode compile(SFRuleCompiler compiler, Rule rule,
			int conditionIndex) throws AssertException, StopCompileException {
		// TODO Auto-generated method stub
		return null;
	}

	public String format(Formatter visitor) {
		// TODO Auto-generated method stub
		return null;
	}

}
