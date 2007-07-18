package org.jamocha.rete;

public class StopCompileException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private boolean subSucc;

	public StopCompileException() {
		// TODO Auto-generated constructor stub
	}
	
	public StopCompileException(boolean subSuccess) {
		subSucc = subSuccess;
	}
	
	public boolean isSubSuccessed() {
		return subSucc;
	}

	public StopCompileException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public StopCompileException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public StopCompileException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

}
