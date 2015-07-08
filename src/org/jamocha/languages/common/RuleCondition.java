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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
import org.jamocha.filter.Path;
import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.function.fwa.PathLeaf;
import org.jamocha.function.fwa.SymbolLeaf;
import org.jamocha.languages.common.ScopeStack.Scope;
import org.jamocha.languages.common.ScopeStack.VariableSymbol;
import org.jamocha.languages.common.SingleFactVariable.SingleSlotVariable;

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
	private final List<ConditionalElement> conditionalElements = new ArrayList<>();
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
		final Scope correspondingScope;
		final LinkedList<SingleFactVariable> factVariables;
		final LinkedList<SingleSlotVariable> equalSlotVariables;
		final LinkedList<FunctionWithArguments<SymbolLeaf>> equalFWAs;
		final Set<EquivalenceClass> equalParentEquivalenceClasses = new HashSet<>();
		final Set<EquivalenceClass> unequalEquivalenceClasses = new HashSet<>();
		final Set<SingleFactVariable> merged = new HashSet<>();
		@Setter
		SlotType type;

		public EquivalenceClass(final Scope correspondingScope) {
			this(correspondingScope, new LinkedList<>(), new LinkedList<>(), new LinkedList<>(), null);
		}

		public EquivalenceClass(final Scope correspondingScope, final SlotType type) {
			this(correspondingScope, new LinkedList<>(), new LinkedList<>(), new LinkedList<>(), type);
		}

		public EquivalenceClass(final Scope correspondingScope, final FunctionWithArguments<SymbolLeaf> fwa) {
			this(correspondingScope, new LinkedList<>(), new LinkedList<>(), new LinkedList<>(
					Collections.singletonList(fwa)), fwa.getReturnType());
		}

		public EquivalenceClass(final Scope correspondingScope, final SingleFactVariable fv) {
			this(correspondingScope, new LinkedList<>(Collections.singleton(fv)), new LinkedList<>(),
					new LinkedList<>(), SlotType.FACTADDRESS);
		}

		public EquivalenceClass(final Scope correspondingScope, final SingleSlotVariable sv) {
			this(correspondingScope, new LinkedList<>(), new LinkedList<>(Collections.singletonList(sv)),
					new LinkedList<>(), sv.getType());
		}

		public EquivalenceClass(final EquivalenceClass copy) {
			this(copy.correspondingScope, new LinkedList<>(copy.factVariables), new LinkedList<>(
					copy.equalSlotVariables), new LinkedList<>(copy.equalFWAs), copy.type);
			this.unequalEquivalenceClasses.addAll(copy.unequalEquivalenceClasses);
		}

		public void merge(final EquivalenceClass other) {
			if (this.correspondingScope != other.correspondingScope) {
				throw new IllegalArgumentException("Only equivalence classes of the same scope can be merged!");
			}
			if (this == other)
				return;
			other.factVariables.forEach(this::add);
			other.equalSlotVariables.forEach(this::add);
			other.equalFWAs.forEach(this::add);
			for (final EquivalenceClass ec : other.unequalEquivalenceClasses) {
				ec.unequalEquivalenceClasses.remove(other);
				addNegatedEdge(ec);
			}
			if (null == this.type)
				this.type = other.type;
			else if (null != other.type && other.type != this.type)
				throw new IllegalArgumentException("Only equivalence classes of equal types can be merged!");
			other.factVariables.forEach(fv -> fv.setEqual(this));
			other.equalSlotVariables.forEach(sv -> {
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
			if (this.equalSlotVariables.contains(sv)) {
				throw new IllegalArgumentException(
						"Tried to add a SingleSlotVariable to an EquivalenceClass that already contained it!");
			}
			this.equalSlotVariables.add(sv);
		}

		public void add(final FunctionWithArguments<SymbolLeaf> fwa) {
			if (null == type)
				type = fwa.getReturnType();
			if (fwa.getReturnType() != type) {
				throw new IllegalArgumentException("Tried to add a FunctionWithArguments of type "
						+ fwa.getReturnType() + " to an EquivalenceClass of type " + type + "!");
			}
			if (this.equalFWAs.contains(fwa)) {
				throw new IllegalArgumentException(
						"Tried to add a FunctionWithArguments to an EquivalenceClass that already contained it!");
			}
			this.equalFWAs.add(fwa);
		}

		public static void addEqualParentEquivalenceClassRelation(final EquivalenceClass a, final EquivalenceClass b) {
			if (a.correspondingScope.isParentOf(b.correspondingScope)) {
				b.addEqualParentEquivalenceClass(a);
			} else if (b.correspondingScope.isParentOf(a.correspondingScope)) {
				a.addEqualParentEquivalenceClass(b);
			} else {
				throw new IllegalArgumentException(
						"The given equivalence classes are not in any child-parent relationship!");
			}
		}

		public void addEqualParentEquivalenceClass(final EquivalenceClass ec) {
			if (!ec.correspondingScope.isParentOf(this.correspondingScope)) {
				throw new IllegalArgumentException(
						"Given equivalence class is not part of a parenting scope w.r.t. this equivalence class!");
			}
			this.equalParentEquivalenceClasses.add(ec);
		}

		public static void addUnequalEquivalenceClassRelation(final EquivalenceClass a, final EquivalenceClass b) {
			if (a.correspondingScope.isParentOf(b.correspondingScope)) {
				b.addNegatedArc(a);
			} else if (b.correspondingScope.isParentOf(a.correspondingScope)) {
				a.addNegatedArc(b);
			} else {
				a.addNegatedEdge(b);
			}
		}

		private void addNegatedArc(final EquivalenceClass ec) {
			if (this == ec) {
				throw new IllegalArgumentException("Tried to insert a negated arc as a loop!");
			}
			if (null == type)
				type = ec.type;
			if (null != ec.type && type != ec.type) {
				throw new IllegalArgumentException(
						"Tried to add a negated arc between EquivalenceClasses of different types (left=" + type
								+ ", right=" + ec.type + ")!");
			}
			this.unequalEquivalenceClasses.add(ec);
		}

		private void addNegatedEdge(final EquivalenceClass ec) {
			if (this == ec) {
				throw new IllegalArgumentException("Tried to insert a negated edge as a loop!");
			}
			if (null == type)
				type = ec.type;
			if (null != ec.type && type != ec.type) {
				throw new IllegalArgumentException(
						"Tried to add a negated edge between EquivalenceClasses of different types (left=" + type
								+ ", right=" + ec.type + ")!");
			}
			ec.unequalEquivalenceClasses.add(this);
			this.unequalEquivalenceClasses.add(ec);
		}

		public void removeNegatedEdge(final EquivalenceClass ec) {
			assert this.unequalEquivalenceClasses.contains(ec) || ec.unequalEquivalenceClasses.contains(this);
			ec.unequalEquivalenceClasses.remove(this);
			this.unequalEquivalenceClasses.remove(ec);
		}

		public void replace(final Map<EquivalenceClass, EquivalenceClass> map) {
			// iterate over the map as the set can't be modified during iteration
			map.forEach((o, n) -> {
				if (this.unequalEquivalenceClasses.remove(o))
					this.unequalEquivalenceClasses.add(n);
			});
		}

		public PathLeaf getPathLeaf(final Map<EquivalenceClass, Path> ec2Path, final SingleSlotVariable sv) {
			if (!factVariables.isEmpty()) {
				return Optional.ofNullable(ec2Path.get(factVariables.getFirst().getEqual()))
						.map(path -> new PathLeaf(path, (SlotAddress) null)).get();
			}
			return Optional.ofNullable(sv).map(var -> var.getPathLeaf(ec2Path)).get();
		}

		public PathLeaf getPathLeaf(final Map<EquivalenceClass, Path> ec2Path) {
			return getPathLeaf(ec2Path, equalSlotVariables.peekFirst());
		}

		public boolean isNonTrivial() {
			return (this.factVariables.isEmpty() ? 0 : 1) + this.equalSlotVariables.size() > 1;
		}
	}
}
