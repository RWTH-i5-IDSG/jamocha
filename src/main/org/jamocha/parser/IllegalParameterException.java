package org.jamocha.parser;

public class IllegalParameterException extends EvaluationException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public IllegalParameterException(int parameterCount) {
		this(parameterCount,false);
	}
	

	public IllegalParameterException(int parameterCount, boolean orMore) {
		super("Expected parameter count "+parameterCount+(orMore?"+":""));
	}

}
