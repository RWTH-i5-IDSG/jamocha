package org.jamocha.adapter.sl.configurations;

import java.util.LinkedList;
import java.util.List;

public class FunctionCallSLConfiguration implements SLConfiguration {

	private SLConfiguration functionName;
	
	private List<SLConfiguration> parameters = new LinkedList<SLConfiguration>();
	
	public SLConfiguration getFunctionName() {
		return functionName;
	}

	public void setFunctionName(SLConfiguration functionName) {
		this.functionName = functionName;
	}

	public List<SLConfiguration> getParameters() {
		return parameters;
	}

	public void addParameter(SLConfiguration parameter) {
		parameters.add(parameter);
	}

	public String compile(SLCompileType compileType) {
		// TODO Auto-generated method stub
		return null;
	}

}
