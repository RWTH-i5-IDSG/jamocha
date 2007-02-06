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

import java.util.Map;

import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.util.CollectionsFactory;

/**
 * A default implementation of the scope interface.
 * 
 * @author Sebastian Reinartz 
 * @author Christoph Emonds
 *
 */
@SuppressWarnings("unchecked")
public class DefaultScope implements Scope {
	
	private Map<String, JamochaValue> values = CollectionsFactory.localMap();

	public JamochaValue getBindingValue(String name) {
		return values.get(name);
	}

	public void setBindingValue(String name, JamochaValue value) {
		values.put(name, value);
	}

}
