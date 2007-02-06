package org.jamocha.rete;

import org.jamocha.parser.JamochaValue;

public interface Scope {
	JamochaValue getBindingValue(String name);

	void setBindingValue(String name, JamochaValue value);
}
