package org.jamocha.parser;

public class IllegalParameterException extends EvaluationException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public IllegalParameterException(int parameterCount) {
		super("Expected parameter count "+parameterCount);
	}

}
