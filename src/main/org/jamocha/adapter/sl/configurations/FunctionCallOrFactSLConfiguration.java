/*
 * Copyright 2007 Alexander Wilden
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
package org.jamocha.adapter.sl.configurations;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FunctionCallOrFactSLConfiguration implements SLConfiguration {

	private SLConfiguration name;

	private Map<SLConfiguration, SLConfiguration> slots = new HashMap<SLConfiguration, SLConfiguration>();

	public Map<SLConfiguration, SLConfiguration> getSlots() {
		return slots;
	}

	public void addSlot(SLConfiguration name, SLConfiguration value) {
		this.slots.put(name, value);
	}

	public SLConfiguration getName() {
		return name;
	}

	public void setName(SLConfiguration templateName) {
		this.name = templateName;
	}

	public String compile(SLCompileType compileType) {
		StringBuilder res = new StringBuilder();
		Set<SLConfiguration> keys = slots.keySet();
		switch (compileType) {
		case ACTION_AND_ASSERT:
			// If this node is called directly after an ActionExpression with
			// keyword
			// action, this node is a function call and all following will be
			// asserts.
			res.append("(").append(name.compile(compileType));
			for (SLConfiguration key : keys) {
				if (slots.get(key) != null) {
					res.append(" ");
					res.append(slots.get(key).compile(SLCompileType.ASSERT));
				}
			}
			res.append(")");
			break;
		case ASSERT:
			// Here we have an assert of a fact.
			res.append("(assert (").append(name.compile(compileType));
			for (SLConfiguration key : keys) {
				res.append(" (").append(key.compile(compileType)).append(" ");
				if (slots.get(key) != null)
					res.append(slots.get(key).compile(compileType));
				res.append(")");
			}
			res.append("))");
			break;
		case RULE_LHS:
			// This is a lefthand side of a rule and by this will result in a
			// Node of the rete network.
			res.append("(").append(name.compile(compileType));
			for (SLConfiguration key : keys) {
				res.append(" (").append(key.compile(compileType)).append(" ");
				if (slots.get(key) != null)
					res.append(slots.get(key).compile(compileType));
				else
					res.append("NIL");
				res.append(")");
			}
			res.append(")");
			break;
		}
		return res.toString();
	}

}
