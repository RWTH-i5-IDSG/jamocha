/*
 * Copyright 2002-2014 The Jamocha Team
 * 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.jamocha.org/
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.jamocha.dn;

import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import org.jamocha.dn.memory.Template;
import org.jamocha.dn.nodes.TerminalNode;
import org.jamocha.filter.PathFilter;
import org.jamocha.function.Function;
import org.jamocha.function.fwa.Assert;
import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.languages.common.RuleCondition;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class ConstructCache {
	final private HashMap<String, Template> template = new HashMap<>();
	final private HashMap<String, Function<?>> functions = new HashMap<>();
	final private HashMap<String, Defrule> rules = new HashMap<>();
	final private HashMap<String, Deffacts> deffacts = new HashMap<>();
	final private HashMap<TerminalNode, Set<Defrule.Translated>> terminalNode2Rules =
			new HashMap<>();

	@Value
	public static class Deffacts {
		final String name;
		final String description;
		final List<Assert.TemplateContainer> containers;
	}

	@Value
	public static class Defrule {
		final String name;
		final String description;
		final RuleCondition condition;
		final ArrayList<FunctionWithArguments> actionList;
		final ArrayList<Translated> translatedVersions = new ArrayList<>();

		public Translated newTranslated(final List<PathFilter> condition,
				final List<FunctionWithArguments> actionList) {
			final Translated translated = new Translated(condition, actionList);
			translatedVersions.add(translated);
			return translated;
		}

		@Data
		@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
		public class Translated {
			final List<PathFilter> condition;
			final Collection<FunctionWithArguments> actionList;
			TerminalNode terminalNode;

			public Defrule getParent() {
				return Defrule.this;
			}
		}
	}

	public void addTemplate(final Template template) {
		this.template.put(template.getName(), template);
	}

	public Template getTemplate(final String name) {
		return this.template.get(name);
	}

	public Collection<Template> getTemplates() {
		return this.template.values();
	}

	public void addRule(final Defrule rule) {
		this.rules.put(rule.getName(), rule);
		for (final Defrule.Translated translated : rule.getTranslatedVersions()) {
			this.terminalNode2Rules.computeIfAbsent(translated.getTerminalNode(),
					t -> new HashSet<>()).add(translated);
		}
	}

	public Set<Defrule.Translated> getRulesForTerminalNode(final TerminalNode terminal) {
		return this.terminalNode2Rules.get(terminal);
	}

	public Set<TerminalNode> getTerminalNodesForRule(final Defrule rule) {
		return rule.getTranslatedVersions().stream().map(Defrule.Translated::getTerminalNode)
				.collect(toSet());
	}

	public Defrule getRule(final String name) {
		return this.rules.get(name);
	}

	public Collection<Defrule> getRules() {
		return this.rules.values();
	}

	public void addDeffacts(final Deffacts deffacts) {
		this.deffacts.put(deffacts.getName(), deffacts);
	}

	public Deffacts getDeffacts(final String name) {
		return this.deffacts.get(name);
	}

	public Collection<Deffacts> getDeffacts() {
		return this.deffacts.values();
	}
}
