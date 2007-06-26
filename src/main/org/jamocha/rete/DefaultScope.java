/*
 * Copyright 2007 Sebastian Reinartz Christoph Emonds
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.jamocha.org/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.jamocha.rete;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.jamocha.parser.JamochaValue;

/**
 * A default implementation of the scope interface.
 * 
 * @author Sebastian Reinartz
 * @author Christoph Emonds
 * 
 */
public class DefaultScope extends AbstractScope {

	public JamochaValue getBindingValue(String name) {
		if (values.containsKey(name))
			return values.get(name);
		else if (outerScope != null)
			return outerScope.getBindingValue(name);
		else
			return null;
	}

	public void setBindingValue(String name, JamochaValue value) {
		if (values.containsKey(name) || !hasBindingInTotalRange(name))
			values.put(name, value);
		else
			outerScope.setBindingValue(name, value);
	}

	public boolean hasBindingInTotalRange(String name) {
		if (values.containsKey(name))
			return true;
		else if (outerScope != null)
			return outerScope.hasBindingInTotalRange(name);
		else
			return false;
	}

	public Map<String, JamochaValue> getBindings() {
		Map<String, JamochaValue> result = new HashMap<String, JamochaValue>();
		if (outerScope != null) {
			Map<String, JamochaValue> outerBindings = outerScope.getBindings();
			Set<String> keys = outerBindings.keySet();
			for (String key : keys) {
				result.put(key, outerBindings.get(key));
			}
		}
		Set<String> keys = values.keySet();
		for (String key : keys) {
			result.put(key, values.get(key));
		}
		return result;
	}

}
