package org.jamocha.rules;

import org.jamocha.formatter.Formatter;
import org.jamocha.parser.JamochaValue;
import org.jamocha.engine.AssertException;
import org.jamocha.engine.StopCompileException;
import org.jamocha.engine.nodes.Node;
import org.jamocha.engine.rules.rulecompiler.sfp.SFRuleCompiler;

/**
 * @author primary Sebastian Reinartz and/or Alexander Wilden
 * @author Josef Alexander Hahn
 *
 */
public class OrderedFactConstraint extends AbstractConstraint {

	private static final long serialVersionUID = 1L;

	private Constraint[] constraints;

	public OrderedFactConstraint(Constraint[] constraints) {
		this.constraints=constraints;
	}
	
	public boolean isNegated() {
		return false;
	}

	public Constraint[] getConstraints() {
		return constraints;
	}

	public JamochaValue getValue() {
		return null;
	}

	public Node compile(SFRuleCompiler compiler, Rule rule, 	int conditionIndex) throws AssertException, StopCompileException {
		return null;
	}

	public String format(Formatter visitor) {
		return visitor.visit(this);
	}

	public String getConstraintName() {
		return null;
	}

}
