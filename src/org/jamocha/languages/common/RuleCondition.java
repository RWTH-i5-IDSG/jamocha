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
package org.jamocha.languages.common;

import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import org.jamocha.dn.memory.SlotAddress;
import org.jamocha.dn.memory.SlotType;
import org.jamocha.dn.memory.Template;
import org.jamocha.dn.memory.Template.Slot;
import org.jamocha.filter.ECCollector;
import org.jamocha.filter.Path;
import org.jamocha.function.fwa.ECLeaf;
import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.function.fwa.PathLeaf;
import org.jamocha.function.fwa.SymbolLeaf;
import org.jamocha.languages.common.ScopeStack.Scope;
import org.jamocha.languages.common.ScopeStack.VariableSymbol;
import org.jamocha.languages.common.SingleFactVariable.SingleSlotVariable;

import com.google.common.collect.Sets;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 */
@Getter
@RequiredArgsConstructor
public class RuleCondition {

	@Getter
	@RequiredArgsConstructor
	public static class MatchingConfiguration {
		final SingleFactVariable factVariable;
		final List<SlotAddress> matchingAddresses = new ArrayList<>();
	}

	private final Scope scope;
	private final List<MatchingConfiguration> matchings = new ArrayList<>();
	private final List<ConditionalElement<SymbolLeaf>> conditionalElements = new ArrayList<>();
	private final Set<VariableSymbol> variableSymbols = new HashSet<>();

	public void addSymbol(final VariableSymbol symbol) {
		this.variableSymbols.add(symbol);
	}

	public void addMatchingConfiguration(final MatchingConfiguration matchingConfiguration) {
		this.matchings.add(matchingConfiguration);
	}

	/**
	 * Equivalence class whose elements are equal to each other.
	 * 
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 */
	@Getter
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	public static class EquivalenceClass {
		final LinkedList<SingleFactVariable> factVariables;
		final LinkedList<SingleSlotVariable> slotVariables;
		final LinkedList<FunctionWithArguments<ECLeaf>> constantExpressions;
		final LinkedList<FunctionWithArguments<ECLeaf>> functionalExpressions;
		final Set<EquivalenceClass> equalParentEquivalenceClasses = new HashSet<>();
		final Set<SingleFactVariable> merged = new HashSet<>();
		protected Scope maximalScope;
		@Setter
		SlotType type;

		@Override
		public String toString() {
			final StringBuilder sb = new StringBuilder();
			sb.append(Objects.toString(type));
			sb.append("-EC: {");
			if (!factVariables.isEmpty())
				sb.append(Objects.toString(factVariables));
			if (!slotVariables.isEmpty())
				sb.append(Objects.toString(slotVariables));
			if (!constantExpressions.isEmpty())
				sb.append(Objects.toString(constantExpressions));
			if (!functionalExpressions.isEmpty())
				sb.append(Objects.toString(functionalExpressions));
			if (!equalParentEquivalenceClasses.isEmpty())
				sb.append(Objects.toString(equalParentEquivalenceClasses));
			sb.append("}@");
			sb.append(Integer.toHexString(System.identityHashCode(this)));
			return sb.toString();
		}

		public static EquivalenceClass newPlainEC(final Scope maximalScope) {
			return new EquivalenceClass(new LinkedList<>(), new LinkedList<>(), new LinkedList<>(), new LinkedList<>(),
					maximalScope, null);
		}

		public static EquivalenceClass newECFromType(final Scope maximalScope, final SlotType type) {
			return new EquivalenceClass(new LinkedList<>(), new LinkedList<>(), new LinkedList<>(), new LinkedList<>(),
					maximalScope, type);
		}

		public static EquivalenceClass newECFromFactVariable(final Scope maximalScope, final SingleFactVariable fv) {
			return new EquivalenceClass(new LinkedList<>(Collections.singleton(fv)), new LinkedList<>(),
					new LinkedList<>(), new LinkedList<>(), maximalScope, SlotType.FACTADDRESS);
		}

		public static EquivalenceClass newECFromSlotVariable(final Scope maximalScope, final SingleSlotVariable sv) {
			return new EquivalenceClass(new LinkedList<>(), new LinkedList<>(Collections.singleton(sv)),
					new LinkedList<>(), new LinkedList<>(), maximalScope, sv.getType());
		}

		public static EquivalenceClass newECFromConstantExpression(final Scope maximalScope,
				final FunctionWithArguments<ECLeaf> constantExpression) {
			return new EquivalenceClass(new LinkedList<>(), new LinkedList<>(), new LinkedList<>(
					Collections.singleton(constantExpression)), new LinkedList<>(), maximalScope,
					constantExpression.getReturnType());
		}

		public static EquivalenceClass newECFromVariableExpression(final Scope maximalScope,
				final FunctionWithArguments<ECLeaf> variableExpression) {
			return new EquivalenceClass(new LinkedList<>(), new LinkedList<>(), new LinkedList<>(), new LinkedList<>(
					Collections.singleton(variableExpression)), maximalScope, variableExpression.getReturnType());
		}

		public EquivalenceClass(final EquivalenceClass copy) {
			this(new LinkedList<>(copy.factVariables), new LinkedList<>(copy.slotVariables), new LinkedList<>(
					copy.constantExpressions), new LinkedList<>(copy.functionalExpressions), copy.maximalScope, copy.type);
		}

		public Set<SingleFactVariable> getDirectlyDependentFactVariables() {
			return Sets.union(Sets.newHashSet(this.getFactVariables()),
					this.slotVariables.stream().map(SingleSlotVariable::getFactVariable).collect(toSet()));
		}

		public Set<SingleFactVariable> getDependentFactVariables() {
			return Sets.union(Sets.newHashSet(this.getFactVariables()), Sets.union(
					this.slotVariables.stream().map(SingleSlotVariable::getFactVariable).collect(toSet()),
					this.functionalExpressions.stream().flatMap(fwa -> ECCollector.collect(fwa).stream()).distinct()
							.flatMap(ec -> ec.getFactVariables().stream()).collect(toSet())));
		}

		public void merge(final EquivalenceClass other) {
			if (this.maximalScope != other.maximalScope) {
				throw new IllegalArgumentException("Only equivalence classes of the same scope can be merged!");
			}
			if (this == other)
				return;
			other.factVariables.forEach(this::add);
			other.slotVariables.forEach(this::add);
			other.constantExpressions.forEach(this::add);
			if (null == this.type)
				this.type = other.type;
			else if (null != other.type && other.type != this.type)
				throw new IllegalArgumentException("Only equivalence classes of equal types can be merged!");
			other.factVariables.forEach(fv -> fv.setEqual(this));
			other.slotVariables.forEach(sv -> {
				sv.getEqualSet().clear();
				sv.getEqualSet().add(this);
			});
		}

		/**
		 * Merges the equivalence classes contained in the slots of the equal fact variables. Does
		 * nothing if there are less than two fact variables. Does not necessarily completely merge
		 * all equivalence classes if there are fact variables of different templates in the
		 * equivalence class.
		 */
		public void mergeEquivalenceClassesOfFactVariables() {
			if (this.factVariables.isEmpty()) {
				return;
			}
			assert SlotType.FACTADDRESS == type;
			while (true) {
				final Optional<SingleFactVariable> optFactVariable =
						this.factVariables.stream().filter(fv -> !merged.contains(fv)).findAny();
				if (!optFactVariable.isPresent())
					break;
				final SingleFactVariable thisFV = optFactVariable.get();
				final SingleFactVariable mergeFV =
						merged.stream()
								.findAny()
								.orElseGet(
										() -> factVariables.stream().filter(fv -> thisFV != fv).findAny().orElse(null));
				if (null == mergeFV)
					break;
				merged.add(thisFV);
				merged.add(mergeFV);
				factVariables.remove(thisFV);
				factVariables.remove(mergeFV);
				final Template template = thisFV.template;
				assert thisFV.template == mergeFV.template;
				for (final Slot slot : template.getSlots()) {
					final SlotAddress slotAddress = template.getSlotAddress(slot.getName());
					final SingleSlotVariable thisSV = thisFV.getSlots().get(slotAddress);
					final SingleSlotVariable mergeSV = mergeFV.getSlots().get(slotAddress);
					if (null != thisSV && null != mergeSV) {
						final EquivalenceClass thisEC = thisSV.getEqual();
						final EquivalenceClass mergeEC = mergeSV.getEqual();
						thisEC.merge(mergeEC);
						thisEC.mergeEquivalenceClassesOfFactVariables();
					}
				}
			}
			if (factVariables.isEmpty()) {
				factVariables.add(merged.iterator().next());
			}
		}

		public void add(final SingleFactVariable fv) {
			if (null == type)
				type = SlotType.FACTADDRESS;
			if (SlotType.FACTADDRESS != type)
				throw new IllegalArgumentException("Tried to add a SingleFactVariable to an EquivalenceClass of type "
						+ type + " instead of FACTADDRESS!");
			if (!this.factVariables.isEmpty() && fv.getTemplate() != this.factVariables.iterator().next().getTemplate()) {
				throw new IllegalArgumentException(
						"All fact variables of an equivalence class need to have the same template!");
			}
			this.factVariables.add(fv);
		}

		public void add(final SingleSlotVariable sv) {
			if (null == type)
				type = sv.getType();
			if (sv.getType() != type) {
				throw new IllegalArgumentException("Tried to add a SingleSlotVariable of type " + sv.getType()
						+ " to an EquivalenceClass of type " + type + "!");
			}
			if (this.slotVariables.contains(sv)) {
				throw new IllegalArgumentException(
						"Tried to add a SingleSlotVariable to an EquivalenceClass that already contained it!");
			}
			this.slotVariables.add(sv);
		}

		public void add(final FunctionWithArguments<ECLeaf> fwa) {
			if (null == type)
				type = fwa.getReturnType();
			if (fwa.getReturnType() != type) {
				throw new IllegalArgumentException("Tried to add a FunctionWithArguments of type "
						+ fwa.getReturnType() + " to an EquivalenceClass of type " + type + "!");
			}
			checkContainmentAndAdd(ECCollector.collect(fwa).isEmpty() ? constantExpressions : functionalExpressions, fwa);
		}

		private static void checkContainmentAndAdd(final LinkedList<FunctionWithArguments<ECLeaf>> target,
				final FunctionWithArguments<ECLeaf> fwa) {
			if (target.contains(fwa)) {
				throw new IllegalArgumentException(
						"Tried to add a FunctionWithArguments to an EquivalenceClass that already contained it!");
			}
			target.add(fwa);
		}

		public static void addEqualParentEquivalenceClassRelation(final EquivalenceClass a, final EquivalenceClass b) {
			if (a.maximalScope.isParentOf(b.maximalScope)) {
				b.addEqualParentEquivalenceClass(a);
			} else if (b.maximalScope.isParentOf(a.maximalScope)) {
				a.addEqualParentEquivalenceClass(b);
			} else {
				throw new IllegalArgumentException(
						"The given equivalence classes are not in any child-parent relationship!");
			}
		}

		public void addEqualParentEquivalenceClass(final EquivalenceClass parent) {
			if (!parent.maximalScope.isParentOf(this.maximalScope)) {
				throw new IllegalArgumentException(
						"Given equivalence class is not part of a parenting scope w.r.t. this equivalence class!");
			}
			this.equalParentEquivalenceClasses.add(parent);
		}

		public PathLeaf getPathLeaf(final Map<EquivalenceClass, Path> ec2Path, final SingleSlotVariable sv) {
			if (!factVariables.isEmpty()) {
				return Optional.ofNullable(ec2Path.get(factVariables.getFirst().getEqual()))
						.map(path -> new PathLeaf(path, (SlotAddress) null)).orElse(null);
			}
			return Optional.ofNullable(sv).map(var -> var.getPathLeaf(ec2Path)).orElse(null);
		}

		public PathLeaf getPathLeaf(final Map<EquivalenceClass, Path> ec2Path) {
			return getPathLeaf(ec2Path, slotVariables.peekFirst());
		}

		public boolean isNonTrivial() {
			return (this.factVariables.isEmpty() ? 0 : 1) + this.slotVariables.size()
					+ this.equalParentEquivalenceClasses.size() + this.constantExpressions.size()
					+ this.functionalExpressions.size() > 1;
		}
	}
}
