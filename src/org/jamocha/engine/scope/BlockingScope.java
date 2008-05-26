/*
 * Copyright 2002-2008 Peter Lin & The Jamocha Team
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

import java.util.Map;

import org.jamocha.parser.JamochaValue;

public class BlockingScope extends AbstractScope {

	public JamochaValue getBindingValue(final String name) {
		if (values.containsKey(name))
			return values.get(name);
		else
			return null;
	}

	public void setBindingValue(final String name, final JamochaValue value) {
		values.put(name, value);
	}

	public boolean hasBindingInTotalRange(final String name) {
		return values.containsKey(name);
	}

	public Map<String, JamochaValue> getBindings() {
		return values;
	}

}
