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

import static java.util.stream.Collectors.toList;
import static org.jamocha.util.ToArray.toArray;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.StreamSupport;

import org.apache.logging.log4j.Marker;
import org.jamocha.dn.memory.MemoryHandlerTerminal.AssertOrRetract;
import org.jamocha.dn.memory.Template;
import org.jamocha.filter.ECFilterSet;
import org.jamocha.filter.Path;
import org.jamocha.filter.PathCollector;
import org.jamocha.filter.PathFilterList.PathSharedListWrapper;
import org.jamocha.filter.PathFilterSet;
import org.jamocha.filter.TrivialPathSetToPathListConverter;
import org.jamocha.function.Function;
import org.jamocha.function.fwa.Assert;
import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.function.fwa.ParameterLeaf;
import org.jamocha.function.fwa.PathLeaf;
import org.jamocha.function.fwa.RHSVariableLeaf;
import org.jamocha.function.fwa.SymbolLeaf;
import org.jamocha.function.fwa.VariableValueContext;
import org.jamocha.function.fwatransformer.FWASymbolToRHSVariableLeafTranslator;
import org.jamocha.languages.common.RuleCondition;
import org.jamocha.languages.common.RuleCondition.EquivalenceClass;
import org.jamocha.languages.common.SingleFactVariable;
import org.jamocha.logging.MarkerType;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Value;

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

		public ECFilterSetCondition newECFilterSetCondition(final Set<ECFilterSet> condition,
				final Set<SingleFactVariable> factVariables, final Set<EquivalenceClass> equivalenceClasses,
				final int specificity) {
			return new ECFilterSetCondition(condition, factVariables, equivalenceClasses, actionList, specificity);
		}

		@Data
		@RequiredArgsConstructor
		public class ECFilterSetCondition {
			final Set<ECFilterSet> condition;
			final Set<SingleFactVariable> factVariables;
			final Set<EquivalenceClass> equivalenceClasses;
			final FunctionWithArguments<SymbolLeaf>[] actionList;
			final int specificity;

			public Defrule getParent() {
				return Defrule.this;
			}

			public PathRule bindVariables(final Map<EquivalenceClass, PathLeaf> equivalenceClassToPathLeaf) {
				// FIXME implement binding
				// new TranslatedPath(condition, actionList, equivalenceClassToPathLeaf,
				// specificity)
				return null;
			}
		}

		public PathRule newTranslated(final PathSharedListWrapper.PathSharedList condition, final Set<Path> resultPaths,
				final Map<EquivalenceClass, PathLeaf> equivalenceClassToPathLeaf, final int specificity) {
			return new PathRule(condition, resultPaths, actionList, equivalenceClassToPathLeaf, specificity);
		}

		public PathRule newTranslated(final PathSharedListWrapper.PathSharedList condition,
				final Map<EquivalenceClass, PathLeaf> equivalenceClassToPathLeaf) {
			return newTranslated(condition, PathCollector.newHashSet().collectOnlyNonExistential(condition).getPaths(),
					equivalenceClassToPathLeaf, (int) StreamSupport.stream(condition.spliterator(), false).count());
		}

		@Data
		@RequiredArgsConstructor
		public class PathSetBasedRule {
			final Set<PathFilterSet> condition;
			final Set<Path> resultPaths;
			final FunctionWithArguments<SymbolLeaf>[] actionList;
			final Map<EquivalenceClass, PathLeaf> equivalenceClassToPathLeaf;
			final int specificity;

			public Defrule getParent() {
				return Defrule.this;
			}

			public PathRule trivialToPathRule() {
				return new PathRule(
						new PathSharedListWrapper().newSharedElement(
								condition.stream().map(TrivialPathSetToPathListConverter::convert).collect(toList())),
						resultPaths, actionList, equivalenceClassToPathLeaf, specificity);
			}
		}

		@Data
		@RequiredArgsConstructor
		public class PathRule {
			final PathSharedListWrapper.PathSharedList condition;
			final Set<Path> resultPaths;
			final FunctionWithArguments<SymbolLeaf>[] actionList;
			final Map<EquivalenceClass, PathLeaf> equivalenceClassToPathLeaf;
			final int specificity;

			public Defrule getParent() {
				return Defrule.this;
			}

			public Translated translatePathToAddress() {
				final VariableValueContext context = new VariableValueContext();
				return new Translated(condition, new AddressesActionList(context, FWASymbolToRHSVariableLeafTranslator
						.translate(equivalenceClassToPathLeaf, context, actionList)), specificity);
			}
		}

		@Data
		public class Translated {
			final PathSharedListWrapper.PathSharedList condition;
			final AddressesActionList actionList;
			final int specificity;

			public Defrule getParent() {
				return Defrule.this;
			}
		}
	}

	@Value
	public static class AddressesActionList {
		final VariableValueContext context;
		final FunctionWithArguments<RHSVariableLeaf>[] actions;

		public void evaluate(final AssertOrRetract<?> token) {
			context.initialize(token);
			for (final FunctionWithArguments<RHSVariableLeaf> action : actions) {
				action.evaluate();
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
