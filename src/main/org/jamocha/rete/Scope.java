package org.jamocha.rete;

import org.jamocha.parser.JamochaValue;

public interface Scope {

	void pushScope(Scope scope);

	Scope popScope();

	JamochaValue getBindingValue(String name);

	void setBindingValue(String name, JamochaValue value);
	
	boolean hasBindingInTotalRange(String name);
	
	Scope getOuterScope();
}
