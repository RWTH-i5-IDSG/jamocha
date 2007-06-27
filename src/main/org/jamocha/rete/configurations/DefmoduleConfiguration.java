package org.jamocha.rete.configurations;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Rete;

public class DefmoduleConfiguration extends AbstractConfiguration {

	private String moduleName;
	
	private String moduleDescription;
	
	public String getModuleDescription() {
		return moduleDescription;
	}

	public void setModuleDescription(String moduleDescription) {
		this.moduleDescription = moduleDescription;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

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

}
