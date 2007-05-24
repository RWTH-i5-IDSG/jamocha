package org.jamocha.rete;

import java.util.HashMap;
import java.util.Map;

import org.jamocha.parser.JamochaValue;

public abstract class AbstractScope implements Scope {

	protected Map<String, JamochaValue> values = new HashMap<String, JamochaValue>();

	protected Scope outerScope = null;

	public Scope popScope() {
		return outerScope;
	}

	public void pushScope(Scope scope) {
		outerScope = scope;
	}

	public Scope getOuterScope() {
		return outerScope;
	}

}
