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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jamocha.rete.Template;
import org.jamocha.rule.Rule;

/**
 * @author Josef Alexander Hahn, Sebastian Reinartz
 * 
 */
public class RuleDataContainer extends ModulesDataContainer {

	private Map<String, Set<Rule>> moduleToRules;

	public RuleDataContainer() {
		super();
		idToCLIPSElement = new HashMap<String, Rule>();
		moduleToRules = new HashMap<String, Set<Rule>>();
	}

	@Override
	protected void handleClear() {
		Set<String> keys = moduleToRules.keySet();
		for (String key : keys) {
			moduleToRules.remove(key);
		}
	}

	public Rule get(String ruleName, Module module) {
		return (Rule) idToCLIPSElement.get(toKeyString(ruleName, module
				.getModuleName()));
	}

	public boolean add(Rule rule, Module module) {
		String ruleKey = toKeyString(rule.getName(), module.getModuleName());
		if (this.idToCLIPSElement.containsKey(ruleKey))
			return false;
		else {
			this.idToCLIPSElement.put(ruleKey, rule);
			// add to modules templateset
			Set moduleSet = (Set) this.moduleToRules
					.get(module.getModuleName());
			// Does this Set exists?
			if (moduleSet == null) {
				moduleSet = new HashSet<Template>();
				this.moduleToRules.put(module.getModuleName(), moduleSet);
			}
			moduleSet.add(rule);
			return true;
		}
	}

	public Rule remove(String ruleName, Module module) {
		Rule result = (Rule) idToCLIPSElement.remove(toKeyString(ruleName,
				module.getModuleName()));
		Set moduleSet = (Set) this.moduleToRules.get(module.getModuleName());
		moduleSet.remove(result);
		return result;
	}

	public boolean containsRule(Rule rule, Module module) {
		return this.idToCLIPSElement.containsKey(toKeyString(rule.getName(),
				module.getModuleName()));
	}

	private String toKeyString(String ruleName, String moduleName) {
		return moduleName + "::" + ruleName;
	}

	public List<Rule> getRules(Module module) {
		List<Rule> rules = new ArrayList<Rule>();
		// get set of templates from hashmap
		Set templs = (Set) this.moduleToRules.get(module.getModuleName());
		if (templs != null) {
			Iterator itr = templs.iterator();
			while (itr.hasNext()) {
				Rule rule = (Rule) itr.next();
				rules.add(rule);
			}
		}
		return rules;
	}

	public List<Rule> getRules() {
		List<Rule> rules = new ArrayList<Rule>();
		// clearadd all templates from hashmap to resulting list:
		for (Object key : idToCLIPSElement.keySet()) {
			Rule rule = (Rule) this.idToCLIPSElement.get(key);
			rules.add(rule);
		}
		return rules;
	}

}