/*
 * Copyright 2007 Josef Alexander Hahn, Sebastian Reinartz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://jamocha.org/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.jamocha.rete.modules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.jamocha.rete.Fact;
import org.jamocha.rule.Rule;

/**
 * @author Josef Alexander Hahn, Sebastian Reinartz
 * 
 */
public class RuleDataContainer extends ModulesDataContainer {

	public RuleDataContainer() {
		super();
		idToCLIPSElement = new HashMap<String, Fact>();
	}

	@Override
	protected void handleClear() {
		// TODO: we have to remove from all modules
		// TODO Auto-generated method stub
	}

	public Rule get(String ruleName, Module module) {
		return (Rule) idToCLIPSElement.get(toKeyString(ruleName, module.getModuleName()));
	}

	public void add(Rule rule, Module module) {
		this.idToCLIPSElement.put(toKeyString(rule.getName(), module.getModuleName()), rule);
	}

	public Rule remove(String ruleName, Module module) {
		return (Rule) idToCLIPSElement.remove(toKeyString(ruleName, module.getModuleName()));
	}

	public boolean containsRule(Rule rule, Module module) {
		return this.idToCLIPSElement.containsKey(toKeyString(rule.getName(), module.getModuleName()));
	}

	private String toKeyString(String ruleName, String moduleName) {
		return moduleName + "::" + ruleName;
	}

	public List<Rule> getRules(Module module) {
		List<Rule> rules = new ArrayList<Rule>();
		// clearadd all templates from hashmap to resulting list:
		Iterator itr = this.idToCLIPSElement.keySet().iterator();
		while (itr.hasNext()) {
			Object key = itr.next();
			Rule rule = (Rule) this.idToCLIPSElement.get(key);
			rules.add(rule);
		}
		return rules;
	}
	
}