/*
 * Copyright 2002-2008 The Jamocha Team
 * 
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

package org.jamocha.engine.scope;

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

	public JamochaValue getBindingValue(final String name) {
		if (values.containsKey(name))
			return values.get(name);
		else if (outerScope != null)
			return outerScope.getBindingValue(name);
		else
			return null;
	}

	public void setBindingValue(final String name, final JamochaValue value) {
		if (values.containsKey(name) || !hasBindingInTotalRange(name))
			values.put(name, value);
		else
			outerScope.setBindingValue(name, value);
	}

	public boolean hasBindingInTotalRange(final String name) {
		if (values.containsKey(name))
			return true;
		else if (outerScope != null)
			return outerScope.hasBindingInTotalRange(name);
		else
			return false;
	}

	public Map<String, JamochaValue> getBindings() {
		final Map<String, JamochaValue> result = new HashMap<String, JamochaValue>();
		if (outerScope != null) {
			final Map<String, JamochaValue> outerBindings = outerScope
					.getBindings();
			final Set<String> keys = outerBindings.keySet();
			for (final String key : keys)
				result.put(key, outerBindings.get(key));
		}
		final Set<String> keys = values.keySet();
		for (final String key : keys)
			result.put(key, values.get(key));
		return result;
	}

}
