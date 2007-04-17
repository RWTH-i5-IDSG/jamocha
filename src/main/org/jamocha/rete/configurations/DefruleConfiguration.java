package org.jamocha.rete.configurations;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.ExpressionSequence;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;

public class DefruleConfiguration implements Parameter {

	String ruleName = null;
	
	String ruleDescription = null;
	
	DeclarationConfiguration declarationConfiguration = null;
	
	Parameter[] params = null;
	
	ExpressionSequence actions = null;
	
	 
	
	/* (non-Javadoc)
	 * @see org.jamocha.rete.Parameter#isObjectBinding()
	 */
	public boolean isObjectBinding() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.jamocha.parser.Expression#getExpressionString()
	 */
	public String getExpressionString() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.jamocha.parser.Expression#getValue(org.jamocha.rete.Rete)
	 */
	public JamochaValue getValue(Rete engine) throws EvaluationException {
		// TODO Auto-generated method stub
		return null;
	}

	public ExpressionSequence getActions() {
		return actions;
	}

	public void setActions(ExpressionSequence actions) {
		this.actions = actions;
	}

	public DeclarationConfiguration getDeclarationConfiguration() {
		return declarationConfiguration;
	}

	public void setDeclarationConfiguration(
			DeclarationConfiguration declarationConfiguration) {
		this.declarationConfiguration = declarationConfiguration;
	}

	public Parameter[] getParams() {
		return params;
	}

	public void setParams(Parameter[] params) {
		this.params = params;
	}

	public String getRuleDescription() {
		return ruleDescription;
	}

	public void setRuleDescription(String ruleDescription) {
		this.ruleDescription = ruleDescription;
	}

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}
}
