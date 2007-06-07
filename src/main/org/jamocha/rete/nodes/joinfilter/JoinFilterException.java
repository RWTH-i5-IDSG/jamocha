package org.jamocha.rete.nodes.joinfilter;

public class JoinFilterException extends Exception {

	private static final long serialVersionUID = 1L;

	public JoinFilterException() {
	}

	public JoinFilterException(String message) {
		super(message);
	}

	public JoinFilterException(Throwable cause) {
		super(cause);
	}

	public JoinFilterException(String message, Throwable cause) {
		super(message, cause);
	}

}
