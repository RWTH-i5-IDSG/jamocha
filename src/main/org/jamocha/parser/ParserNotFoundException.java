package org.jamocha.parser;

public class ParserNotFoundException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public ParserNotFoundException() {
	}

	public ParserNotFoundException(String message) {
		super(message);
	}

	public ParserNotFoundException(Throwable cause) {
		super(cause);
	}

	public ParserNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
