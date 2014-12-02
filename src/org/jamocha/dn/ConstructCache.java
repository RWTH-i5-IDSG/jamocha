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

import static org.jamocha.util.ToArray.toArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import org.apache.logging.log4j.Marker;
import org.jamocha.dn.compiler.SymbolToPathTranslator;
import org.jamocha.dn.memory.MemoryHandlerTerminal.AssertOrRetract;
import org.jamocha.dn.memory.Template;
import org.jamocha.dn.nodes.SlotInFactAddress;
import org.jamocha.filter.Path;
import org.jamocha.filter.PathFilterList.PathFilterSharedListWrapper;
import org.jamocha.function.Function;
import org.jamocha.function.fwa.Assert;
import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.function.fwa.PathLeaf;
import org.jamocha.function.fwa.PathLeaf.ParameterLeaf;
import org.jamocha.function.fwa.SymbolLeaf;
import org.jamocha.function.fwatransformer.FWADeepCopy;
import org.jamocha.function.fwatransformer.FWAPathToAddressTranslator;
import org.jamocha.languages.common.RuleCondition;
import org.jamocha.languages.common.SingleFactVariable;
import org.jamocha.logging.MarkerType;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class ConstructCache {
	final private HashMap<String, Template> template = new HashMap<>();
	final private HashMap<String, Function<?>> functions = new HashMap<>();
	final private HashMap<String, Defrule> rules = new HashMap<>();
	final private HashMap<String, Deffacts> deffacts = new HashMap<>();

	@Value
	public static class Deffacts {
		final String name;
		final String description;
		final List<Assert.TemplateContainer<ParameterLeaf>> containers;
	}

	@Value
	public static class Defrule {
		final String name;
		final String description;
		final int salience;
		final RuleCondition condition;
		final FunctionWithArguments<SymbolLeaf>[] actionList;
		final ArrayList<TranslatedPath> translatedPathVersions = new ArrayList<>();
		final Marker fireMarker;
		final Marker activationMarker;

		public Defrule(final String name, final String description, final int salience, final RuleCondition condition,
				final ArrayList<FunctionWithArguments<SymbolLeaf>> actionList) {
			this(name, description, salience, condition, toArray(actionList, FunctionWithArguments[]::new));
		}

		public Defrule(final String name, final String description, final int salience, final RuleCondition condition,
				final FunctionWithArguments<SymbolLeaf>[] actionList) {
			this.name = name;
			this.description = description;
			this.salience = salience;
			this.condition = condition;
			this.actionList = actionList;
			this.fireMarker = MarkerType.RULES.createChild(name);
			this.activationMarker = MarkerType.ACTIVATIONS.createChild(name);
		}

		public TranslatedPath newTranslated(final PathFilterSharedListWrapper.PathFilterSharedList condition,
				final Map<SingleFactVariable, Path> pathTranslationMap) {
			@SuppressWarnings("unchecked")
			final TranslatedPath translated =
					new TranslatedPath(condition,
							new PathActionList(toArray(
									Arrays.stream(actionList).map(
											fwa -> SymbolToPathTranslator.translate(FWADeepCopy.copy(fwa),
													pathTranslationMap)), FunctionWithArguments[]::new)));
			translatedPathVersions.add(translated);
			return translated;
		}

		@Data
		@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
		public class TranslatedPath {
			final PathFilterSharedListWrapper.PathFilterSharedList condition;
			final PathActionList actionList;

			public Defrule getParent() {
				return Defrule.this;
			}

			public Translated translatePathToAddress() {
				return new Translated(condition, actionList.translatePathToAddress());
			}
		}

		@Data
		public class Translated {
			final PathFilterSharedListWrapper.PathFilterSharedList condition;
			final AddressesActionList actionList;

			public Defrule getParent() {
				return Defrule.this;
			}
		}
	}

	@Value
	public static class PathActionList {
		final FunctionWithArguments<PathLeaf>[] actions;

		public AddressesActionList translatePathToAddress() {
			return new AddressesActionList(toArray(
					Arrays.stream(actions).map(
							action -> {
								final ArrayList<SlotInFactAddress> addresses = new ArrayList<>();
								final FunctionWithArguments<ParameterLeaf> fwa =
										FWAPathToAddressTranslator.translate(action, addresses);
								return new AddressesActionList.FWAWithAddresses(fwa, toArray(addresses,
										SlotInFactAddress[]::new));
							}), AddressesActionList.FWAWithAddresses[]::new));
		}
	}

	@Value
	public static class AddressesActionList {
		@Value
		private static class FWAWithAddresses {
			FunctionWithArguments<ParameterLeaf> fwa;
			SlotInFactAddress[] addresses;
		}

		final FWAWithAddresses[] actions;

		public void evaluate(final AssertOrRetract<?> token) {
			for (final FWAWithAddresses action : actions) {
				final SlotInFactAddress[] addresses = action.addresses;
				final Object[] params = new Object[addresses.length];
				for (int i = 0; i < addresses.length; ++i) {
					final SlotInFactAddress slotInFactAddress = addresses[i];
					params[i] = token.getValue(slotInFactAddress.getFactAddress(), slotInFactAddress.getSlotAddress());
				}
				action.fwa.evaluate(params);
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

	public void clear() {
		this.deffacts.clear();
		this.functions.clear();
		this.rules.clear();
		this.template.clear();
	}
}
