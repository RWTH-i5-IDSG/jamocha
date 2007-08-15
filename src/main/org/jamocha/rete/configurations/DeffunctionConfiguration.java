/**
 * 
 */
package org.jamocha.rete.configurations;

import org.jamocha.formatter.Formatter;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.ExpressionSequence;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;

/**
 * @author charlie
 *
 */
public class DeffunctionConfiguration extends AbstractConfiguration  {

	private String functionName = null;
	
	private String functionDescription = null;
	
	private Parameter[] params = null;
	
	private ExpressionSequence actions = null;
	
	private String functionGroup = null; 
	
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

	public String getFunctionDescription() {
		return functionDescription;
	}

	public void setFunctionDescription(String functionDescription) {
		this.functionDescription = functionDescription;
	}

	public String getFunctionName() {
		return functionName;
	}

	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}

	public Parameter[] getParams() {
		return params;
	}

	public void setParams(Parameter[] params) {
		this.params = params;
	}

	public boolean definesFunctionGroup() {
		return this.functionGroup != null;
	}

	public String getFunctionGroup() {
		return functionGroup;
	}

	public void setFunctionGroup(String functionGroup) {
		this.functionGroup = functionGroup;
	}

	public String format(Formatter visitor) {
		return visitor.visit(this);
	}

}
