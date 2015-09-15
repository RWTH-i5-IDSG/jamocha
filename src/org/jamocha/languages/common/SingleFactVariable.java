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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

import org.jamocha.dn.memory.SlotAddress;
import org.jamocha.dn.memory.SlotType;
import org.jamocha.dn.memory.Template;
import org.jamocha.filter.Path;
import org.jamocha.function.fwa.PathLeaf;
import org.jamocha.languages.common.RuleCondition.EquivalenceClass;
import org.jamocha.languages.common.ScopeStack.VariableSymbol;

/**
 * Gathers relevant information about a variable.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Getter
@EqualsAndHashCode
@ToString(of = { "template" })
public class SingleFactVariable {
	@NonNull
	final Template template;
	@NonNull
	@Setter
	EquivalenceClass equal;
	final HashMap<SlotAddress, SingleSlotVariable> slots = new HashMap<>();

	public SingleFactVariable(final Template template, final VariableSymbol symbol) {
		this.template = template;
		assert null != symbol.equal;
		this.equal = symbol.equal;
		this.equal.add(this);
	}

	public SingleSlotVariable newSingleSlotVariable(final SlotAddress slot, final VariableSymbol symbol) {
		final SingleSlotVariable instance = slots.computeIfAbsent(slot, SingleSlotVariable::new);
		assert null != symbol.equal;
		instance.equalSet.add(symbol.equal);
		symbol.equal.add(instance);
		return instance;
	}

	public Collection<SingleSlotVariable> getSlotVariables() {
		return slots.values();
	}

	public PathLeaf getPathLeaf(final Map<EquivalenceClass, Path> ec2Path) {
		return new PathLeaf(ec2Path.get(equal), (SlotAddress) null);
	}

	public PathLeaf toPathLeaf(final Map<SingleFactVariable, Path> fv2Path) {
		return new PathLeaf(fv2Path.get(this), (SlotAddress) null);
	}

	@Getter
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	public class SingleSlotVariable {
		@NonNull
		private final SlotAddress slot;
		@NonNull
		private final Set<EquivalenceClass> equalSet = new HashSet<>();

		public SlotType getType() {
			return template.getSlotType(slot);
		}

		public SingleFactVariable getFactVariable() {
			return SingleFactVariable.this;
		}

		public PathLeaf getPathLeaf(final Map<EquivalenceClass, Path> ec2Path) {
			final Path path = ec2Path.get(getFactVariable().getEqual());
			return null == path ? null : new PathLeaf(path, slot);
		}

		public PathLeaf toPathLeaf(final Map<SingleFactVariable, Path> fv2Path) {
			final Path path = fv2Path.get(getFactVariable());
			return null == path ? null : new PathLeaf(path, slot);
		}

		public EquivalenceClass getEqual() {
			if (equalSet.size() > 1) {
				throw new UnsupportedOperationException("Only to be called after merging!");
			}
			return equalSet.iterator().next();
		}

		@Override
		public String toString() {
			return "SingleSlotVariable(" + template.getName() + "::" + template.getSlotName(slot) + ")";
		}
	}
}
