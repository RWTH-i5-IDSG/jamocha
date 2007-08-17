package org.jamocha.rete.configurations;

import org.jamocha.formatter.Formatter;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;

public class DeclarationConfiguration extends AbstractConfiguration {

	
	private Parameter version = null;
	
	private Parameter salience = null;
	
	private Parameter autoFocus = null;
	
	
	public boolean isObjectBinding() {
		// TODO Auto-generated method stub
		return false;
	}

	public String getExpressionString() {
		// TODO Auto-generated method stub
		return null;
	}

	public JamochaValue getValue(Rete engine) throws EvaluationException {
		// TODO Auto-generated method stub
		return null;
	}

	public Parameter getAutoFocus() {
		return autoFocus;
	}

	public void setAutoFocus(Parameter autoFocus) {
		this.autoFocus = autoFocus;
	}

	public Parameter getSalience() {
		return salience;
	}

	public void setSalience(Parameter salience) {
		this.salience = salience;
	}

	public Parameter getVersion() {
		return version;
	}

	public void setVersion(Parameter version) {
		this.version = version;
	}

	public String format(Formatter visitor) {
		return visitor.visit(this);
	}

}
