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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class FunctionCallOrFactSLConfiguration implements SLConfiguration {

	/**
	 * If the <code>name</code> is one of these we always do an assert also if
	 * compile type is ACTION_AND_ASSERT.
	 */
	private static final String[] alwaysAssert = { "inform", "inform-if",
			"inform-ref", "proxy", "request" };

	private SLConfiguration name;

	private Map<SLConfiguration, SLConfiguration> slots = new HashMap<SLConfiguration, SLConfiguration>();

	// needed to keep the order in compile for the slots. This is essential for
	// function calls.
	private List<SLConfiguration> slotNames = new LinkedList<SLConfiguration>();

	public Map<SLConfiguration, SLConfiguration> getSlots() {
		return slots;
	}

	public void addSlot(SLConfiguration name, SLConfiguration value) {
		this.slotNames.add(name);
		this.slots.put(name, value);
	}

	public SLConfiguration getSlot(SLConfiguration name) {
		return slots.get(name);
	}

	public SLConfiguration getSlot(String name, SLCompileType compileType) {
		for (SLConfiguration slotName : slotNames) {
			if (name.equals(slotName.compile(compileType))) {
				return slots.get(slotName);
			}
		}
		return null;
	}

	public SLConfiguration getName() {
		return name;
	}

	public void setName(SLConfiguration templateName) {
		this.name = templateName;
	}

	public String compile(SLCompileType compileType) {
		StringBuilder res = new StringBuilder();
		String nameStr = name.compile(compileType);
		if (compileType.equals(SLCompileType.ACTION_AND_ASSERT)) {
			// Here we treat inform different than a normal function. Inform
			// will be asserted as a fact and not called as a function so we
			// just switch directly to ASSERT.
			for (String temp : alwaysAssert) {
				if (nameStr.equals(temp)) {
					compileType = SLCompileType.ASSERT;
					break;
				}
			}
		}
		switch (compileType) {
		case ACTION_AND_ASSERT:
			// If this node is called directly after an ActionExpression with
			// keyword
			// action, this node is a function call and all following will be
			// asserts.
			res.append("(").append(name.compile(compileType));
			for (SLConfiguration slotName : slotNames) {
				if (slots.get(slotName) != null) {
					res.append(" ");
					res.append(slots.get(slotName)
							.compile(SLCompileType.ASSERT));
				}
			}
			res.append(")");
			break;
		case ASSERT:
			// Here we have an assert of a fact.
			res.append("(assert (").append(name.compile(compileType));
			for (SLConfiguration slotName : slotNames) {
				res.append(" (").append(slotName.compile(compileType)).append(
						" ");
				if (slots.get(slotName) != null)
					res.append(slots.get(slotName).compile(compileType));
				res.append(")");
			}
			res.append("))");
			break;
		case RULE_LHS:
			// This is a lefthand side of a rule and by this will result in a
			// Node of the rete network.
			res.append("(").append(name.compile(compileType));
			for (SLConfiguration slotName : slotNames) {
				res.append(" (").append(slotName.compile(compileType)).append(
						" ");
				if (slots.get(slotName) != null)
					res.append(slots.get(slotName).compile(compileType));
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
