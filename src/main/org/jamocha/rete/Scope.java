package org.jamocha.rete;

import java.util.Map;

import org.jamocha.parser.JamochaValue;

public interface Scope {

	void pushScope(Scope scope);

	Scope popScope();

	JamochaValue getBindingValue(String name);

	void setBindingValue(String name, JamochaValue value);
	
	Map<String, JamochaValue> getBindings();
	
	boolean hasBindingInTotalRange(String name);
	
	Scope getOuterScope();
}
