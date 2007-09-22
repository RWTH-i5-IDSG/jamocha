package org.jamocha.rete.rulecompiler.hokifisch;

public class ConditionTypeNotImplementedException extends
		RuleCompilingException {

	private static final long serialVersionUID = 1L;

	public ConditionTypeNotImplementedException() {
	}

	public ConditionTypeNotImplementedException(String msg) {
		super("Condition type not implemented: "+msg);
	}

	public ConditionTypeNotImplementedException(Throwable cause) {
		super(cause);
	}

	public ConditionTypeNotImplementedException(String msg, Throwable cause) {
		super("Condition type not implemented: "+msg, cause);
	}

}
