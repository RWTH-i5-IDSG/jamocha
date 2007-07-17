package org.jamocha.rule;

import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.SFRuleCompiler;
import org.jamocha.rete.StopCompileException;
import org.jamocha.rete.nodes.BaseNode;

public class OrConnectedConstraint extends AbstractConnectedConstraint {

	public String toClipsFormat(int indent) {
		return left.toClipsFormat(0)+" | "+right.toClipsFormat(0);
	}

	
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean getNegated() {
		// TODO Auto-generated method stub
		return false;
	}

	public JamochaValue getValue() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setName(String name) {
		// TODO Auto-generated method stub

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

}
