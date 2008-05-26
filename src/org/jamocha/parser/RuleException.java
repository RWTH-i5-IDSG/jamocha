package org.jamocha.parser;

public class RuleException extends EvaluationException {


	/**
	 * @param message
	 */
	public RuleException(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public RuleException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param cause
	 */
	public RuleException(Throwable cause) {
		super(cause);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
