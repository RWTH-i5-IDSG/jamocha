package org.jamocha.rete.exception;

import org.jamocha.parser.EvaluationException;

public class ConstraintViolationException extends EvaluationException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public ConstraintViolationException(String message) {
	super(message);
    }

    public ConstraintViolationException(Throwable cause) {
	super(cause);
    }

    public ConstraintViolationException(String message, Throwable cause) {
	super(message, cause);
    }

}
