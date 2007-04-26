package org.jamocha.parser;

public class FunctionNotFoundException extends EvaluationException {

	private static final long serialVersionUID = 1L;

	public FunctionNotFoundException(String funcName) {
		super("The function " + funcName + " could not be found.");
	}
}
