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
import static org.jamocha.util.ToArray.toArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import org.jamocha.dn.compiler.SymbolToPathTranslator;
import org.jamocha.dn.memory.MemoryHandlerTerminal.AssertOrRetract;
import org.jamocha.dn.memory.Template;
import org.jamocha.dn.nodes.SlotInFactAddress;
import org.jamocha.dn.nodes.TerminalNode;
import org.jamocha.filter.Path;
import org.jamocha.filter.PathFilter;
import org.jamocha.function.Function;
import org.jamocha.function.fwa.Assert;
import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.function.fwatransformer.FWADeepCopy;
import org.jamocha.function.fwatransformer.FWAPathToAddressTranslator;
import org.jamocha.languages.common.RuleCondition;
import org.jamocha.languages.common.SingleFactVariable;

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
	@RequiredArgsConstructor
	public static class Defrule {
		final String name;
		final String description;
		final int salience;
		final RuleCondition condition;
		final FunctionWithArguments[] actionList;
		final ArrayList<Translated> translatedVersions = new ArrayList<>();

		public Defrule(final String name, final String description, final int salience,
				final RuleCondition condition, final ArrayList<FunctionWithArguments> actionList) {
			this(name, description, salience, condition, toArray(actionList,
					FunctionWithArguments[]::new));
		}

		public Translated newTranslated(final List<PathFilter> condition,
				final Map<SingleFactVariable, Path> pathTranslationMap) {
			final Translated translated =
					new Translated(condition, new ActionList(toArray(
							Arrays.stream(actionList).map(
									fwa -> SymbolToPathTranslator.translate(FWADeepCopy.copy(fwa),
											pathTranslationMap)), FunctionWithArguments[]::new)));
			translatedVersions.add(translated);
			return translated;
		}

		@Data
		@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
		public class Translated {
			final List<PathFilter> condition;
			final ActionList actionList;
			TerminalNode terminalNode;

			public Defrule getParent() {
				return Defrule.this;
			}

			public void setTerminalNode(final TerminalNode terminalNode) {
				this.terminalNode = terminalNode;
				this.actionList.translatePathToAddress();
			}
		}

		@Value
		public static class ActionList {
			@Data
			private static class FWAWithAddresses {
				final FunctionWithArguments fwa;
				SlotInFactAddress[] addresses;
			}

			final FWAWithAddresses[] actions;

			public ActionList(final FunctionWithArguments[] actions) {
				this.actions =
						toArray(Arrays.stream(actions).map(FWAWithAddresses::new),
								FWAWithAddresses[]::new);
			}

			public void translatePathToAddress() {
				for (final FWAWithAddresses action : actions) {
					final ArrayList<SlotInFactAddress> addresses = new ArrayList<>();
					FWAPathToAddressTranslator.translate(action.fwa, addresses);
					action.addresses = toArray(addresses, SlotInFactAddress[]::new);
				}
			}

			public void evaluate(final AssertOrRetract<?> token) {
				for (final FWAWithAddresses action : actions) {
					final SlotInFactAddress[] addresses = action.addresses;
					final Object[] params = new Object[addresses.length];
					for (int i = 0; i < addresses.length; ++i) {
						final SlotInFactAddress slotInFactAddress = addresses[i];
						params[i] =
								token.getValue(slotInFactAddress.getFactAddress(),
										slotInFactAddress.getSlotAddress());
					}
					action.fwa.evaluate(params);
				}
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
		return this.terminalNode2Rules.computeIfAbsent(terminal, t -> new HashSet<>());
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
