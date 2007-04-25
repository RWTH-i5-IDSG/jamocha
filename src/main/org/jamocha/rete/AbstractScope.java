package org.jamocha.rete;

import java.util.Map;

import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.util.CollectionsFactory;

@SuppressWarnings("unchecked")
public abstract class AbstractScope implements Scope {

	protected Map<String, JamochaValue> values = CollectionsFactory.localMap();

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
