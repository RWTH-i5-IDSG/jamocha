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

import org.jamocha.dn.memory.SlotType;
import org.jamocha.filter.Path;
import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.function.fwa.PathLeaf;
import org.jamocha.function.fwa.SymbolLeaf;
import org.jamocha.languages.common.ScopeStack.VariableSymbol;
import org.jamocha.languages.common.SingleFactVariable.SingleSlotVariable;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 */
@Getter
public class RuleCondition {

	private final List<ConditionalElement> conditionalElements = new ArrayList<>();
	private final Set<VariableSymbol> variableSymbols = new HashSet<>();

	public void addSymbol(final VariableSymbol symbol) {
		this.variableSymbols.add(symbol);
	}

	// private final Map<ConditionalElement, EquivalenceClasses> equivalenceClassesPerToplevelCE =
	// new HashMap<>();

	/**
	 * Equivalence class whose elements are equal to each other.
	 * 
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 */
	@Getter
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	public static class EquivalenceClass {
		Optional<SingleFactVariable> factVariable;
		final LinkedList<SingleSlotVariable> equalSlotVariables;
		final LinkedList<FunctionWithArguments<SymbolLeaf>> equalFWAs;
		final Set<EquivalenceClass> unequalEquivalenceClasses = new HashSet<>();
		SlotType type;

		public EquivalenceClass() {
			this(Optional.empty(), new LinkedList<>(), new LinkedList<>(), null);
		}

		public EquivalenceClass(final SlotType type) {
			this(Optional.empty(), new LinkedList<>(), new LinkedList<>(), type);
		}

		public EquivalenceClass(final FunctionWithArguments<SymbolLeaf> fwa) {
			this(Optional.empty(), new LinkedList<>(), new LinkedList<>(Collections.singletonList(fwa)), fwa
					.getReturnType());
		}

		public EquivalenceClass(final SingleFactVariable fv) {
			this(Optional.of(fv), new LinkedList<>(), new LinkedList<>(), SlotType.FACTADDRESS);
		}

		public EquivalenceClass(final SingleSlotVariable sv) {
			this(Optional.empty(), new LinkedList<>(Collections.singletonList(sv)), new LinkedList<>(), sv.getType());
		}

		public EquivalenceClass(final EquivalenceClass copy) {
			this(copy.factVariable, new LinkedList<>(copy.equalSlotVariables), new LinkedList<>(copy.equalFWAs),
					copy.type);
			this.unequalEquivalenceClasses.addAll(copy.unequalEquivalenceClasses);
		}

		public void setFactVariable(final Optional<SingleFactVariable> fv) {
			assert !this.factVariable.isPresent();
			this.factVariable = fv;
		}

		public void add(final EquivalenceClass other) {
			if (this == other)
				return;
			if (!this.factVariable.isPresent())
				this.factVariable = other.factVariable;
			else
				assert !other.factVariable.isPresent();
			this.equalSlotVariables.addAll(other.equalSlotVariables);
			this.equalFWAs.addAll(other.equalFWAs);
			for (final EquivalenceClass ec : other.unequalEquivalenceClasses) {
				ec.unequalEquivalenceClasses.remove(other);
				addNegatedEdge(ec);
			}
			if (null == this.type)
				this.type = other.type;
			else
				assert null == other.type || other.type == this.type;
		}

		public void add(final SingleFactVariable fv) {
			assert !this.factVariable.isPresent();
			if (null == type)
				type = SlotType.FACTADDRESS;
			assert SlotType.FACTADDRESS == type;
			this.factVariable = Optional.of(fv);
		}

		public void add(final SingleSlotVariable sv) {
			if (null == type)
				type = sv.getType();
			assert sv.getType() == type;
			assert !this.equalSlotVariables.contains(sv);
			this.equalSlotVariables.add(sv);
		}

		public void add(final FunctionWithArguments<SymbolLeaf> fwa) {
			if (null == type)
				type = fwa.getReturnType();
			assert fwa.getReturnType() == type;
			assert !this.equalFWAs.contains(fwa);
			this.equalFWAs.add(fwa);
		}

		public void addNegatedEdge(final EquivalenceClass ec) {
			assert this != ec;
			if (null == type)
				type = ec.type;
			assert null == ec.type || type == ec.type;
			ec.unequalEquivalenceClasses.add(this);
			this.unequalEquivalenceClasses.add(ec);
		}

		public void removeNegatedEdge(final EquivalenceClass ec) {
			assert this.unequalEquivalenceClasses.contains(ec);
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

		public PathLeaf getPathLeaf(final Map<SingleFactVariable, Path> pathMap, final SingleSlotVariable sv) {
			if (factVariable.isPresent()) {
				final Path path = pathMap.get(factVariable.get());
				return null == path ? null : new PathLeaf(path, null);
			}
			return null == sv ? null : sv.getPathLeaf(pathMap);
		}

		public PathLeaf getPathLeaf(final Map<SingleFactVariable, Path> pathMap) {
			return getPathLeaf(pathMap, (equalSlotVariables.isEmpty() ? null : equalSlotVariables.get(0)));
		}

		public void removeFactVariable() {
			this.factVariable = Optional.empty();
		}
	}
}
