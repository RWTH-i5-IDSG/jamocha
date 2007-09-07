package org.jamocha.rete.rulecompiler.hokifisch;

public class ConstraintTypeNotImplementedException extends
		RuleCompilingException {

	public ConstraintTypeNotImplementedException() {
		super();
	}

	public ConstraintTypeNotImplementedException(String msg, Throwable cause) {
		super("Constraint type not implemented: "+msg, cause);
	}

	public ConstraintTypeNotImplementedException(String msg) {
		super("Constraint type not implemented: "+msg);
	}

	public ConstraintTypeNotImplementedException(Throwable cause) {
		super(cause);
	}

}
