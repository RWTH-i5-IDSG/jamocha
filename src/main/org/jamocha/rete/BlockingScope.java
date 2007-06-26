package org.jamocha.rete;

import java.util.Map;

import org.jamocha.parser.JamochaValue;

public class BlockingScope extends AbstractScope {

	public JamochaValue getBindingValue(String name) {
		if (values.containsKey(name))
			return values.get(name);
		else
			return null;
	}

	public void setBindingValue(String name, JamochaValue value) {
		values.put(name, value);
	}

	public boolean hasBindingInTotalRange(String name) {
		return values.containsKey(name);
	}

	public Map<String, JamochaValue> getBindings() {
		return values;
	}

}
