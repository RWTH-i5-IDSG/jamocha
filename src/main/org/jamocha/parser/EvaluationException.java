package org.jamocha.parser;

public class EvaluationException extends Exception {

	public EvaluationException(String message) {
		super(message);
	}

	public EvaluationException(String message, Throwable cause) {
		super(message, cause);
	}

	public EvaluationException(Throwable cause) {
		super(cause);
	}

}
