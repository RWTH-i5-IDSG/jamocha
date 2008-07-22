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

package org.jamocha.engine.modules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jamocha.engine.workingmemory.elements.Template;
import org.jamocha.rules.Rule;

/**
 * @author Josef Alexander Hahn, Sebastian Reinartz
 * 
 */
public class RuleDataContainer extends ModulesDataContainer {

	private final Map<String, Set<Rule>> moduleToRules;

	public RuleDataContainer() {
		super();
		idToCLIPSElement = new HashMap<String, Rule>();
		moduleToRules = new HashMap<String, Set<Rule>>();
	}

	@Override
	protected void handleClear() {
		final Set<String> keys = moduleToRules.keySet();
		for (final String key : keys)
			moduleToRules.remove(key);
	}

	public Rule get(final String ruleName, final Module module) {
		return (Rule) idToCLIPSElement.get(toKeyString(ruleName, module
				.getName()));
	}

	public boolean add(final Rule rule, final Module module) {
		final String ruleKey = toKeyString(rule.getName(), module.getName());
		if (idToCLIPSElement.containsKey(ruleKey))
			return false;
		else {
			idToCLIPSElement.put(ruleKey, rule);
			// add to modules templateset
			Set moduleSet = moduleToRules.get(module.getName());
			// Does this Set exists?
			if (moduleSet == null) {
				moduleSet = new HashSet<Template>();
				moduleToRules.put(module.getName(), moduleSet);
			}
			moduleSet.add(rule);
			return true;
		}
	}

	public Rule remove(final String ruleName, final Module module) {
		final Rule result = (Rule) idToCLIPSElement.remove(toKeyString(
				ruleName, module.getName()));
		final Set moduleSet = moduleToRules.get(module.getName());
		moduleSet.remove(result);
		return result;
	}

	public boolean containsRule(final Rule rule, final Module module) {
		return idToCLIPSElement.containsKey(toKeyString(rule.getName(), module
				.getName()));
	}

	private String toKeyString(final String ruleName, final String moduleName) {
		return moduleName + "::" + ruleName;
	}

	public List<Rule> getRules(final Module module) {
		final List<Rule> rules = new ArrayList<Rule>();
		// get set of templates from hashmap
		final Set<Rule> templs = moduleToRules.get(module.getName());
		if (templs != null) {
			final Iterator<Rule> itr = templs.iterator();
			while (itr.hasNext()) {
				final Rule rule = itr.next();
				rules.add(rule);
			}
		}
		return rules;
	}

	public List<Rule> getRules() {
		final List<Rule> rules = new ArrayList<Rule>();
		// clearadd all templates from hashmap to resulting list:
		for (final Object key : idToCLIPSElement.keySet()) {
			final Rule rule = (Rule) idToCLIPSElement.get(key);
			rules.add(rule);
		}
		return rules;
	}

}