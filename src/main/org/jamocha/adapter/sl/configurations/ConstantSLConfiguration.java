package org.jamocha.adapter.sl.configurations;

public class ConstantSLConfiguration implements SLConfiguration {

	private String constantValue = "";

	public ConstantSLConfiguration() {
	}

	public ConstantSLConfiguration(String constantValue) {
		this.constantValue = constantValue;
	}

	public String getConstantValue() {
		return constantValue;
	}

	public void setConstantValue(String constantValue) {
		this.constantValue = constantValue;
	}

	public String compile(SLCompileType compileType) {
		return constantValue;
	}

}
