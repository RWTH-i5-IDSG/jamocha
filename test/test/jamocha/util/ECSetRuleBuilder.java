/*
 * Copyright 2002-2016 The Jamocha Team
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.jamocha.org/
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under
 * the License.
 */

package test.jamocha.util;

import com.google.common.collect.HashBiMap;
import com.google.common.collect.Sets;
import lombok.Getter;
import org.jamocha.dn.ConstructCache;
import org.jamocha.dn.compiler.ecblocks.CEToECTranslator;
import org.jamocha.dn.memory.SlotAddress;
import org.jamocha.dn.memory.Template;
import org.jamocha.filter.ECFilter;
import org.jamocha.filter.ECFilterSet;
import org.jamocha.function.fwa.ECLeaf;
import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.function.fwa.PredicateWithArguments;
import org.jamocha.languages.common.RuleCondition;
import org.jamocha.languages.common.ScopeStack;
import org.jamocha.languages.common.SingleFactVariable;

import java.io.Closeable;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Set;
import java.util.Stack;
import java.util.function.Consumer;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class ECSetRuleBuilder {

	static enum Existential {
		NO, POSITIVE, NEGATED;
	}

	private static final Consumer<SingleFactVariable.SingleSlotVariable> nullConsumer = (a) -> {
	};

	public class ConditionProxy implements Closeable {
		final ConditionProxy parent;
		final ScopeStack scopeStack;
		final Existential existential;
		final Set<ECFilterSet> condition = Sets.newIdentityHashSet();
		final Set<SingleFactVariable> factVariableSet = Sets.newIdentityHashSet();
		final Set<RuleCondition.EquivalenceClass> equivalenceClasses = Sets.newIdentityHashSet();
		boolean open = true;

		private ConditionProxy(final ConditionProxy parent, final ScopeStack scopeStack,
				final Existential existential) {
			this.parent = parent;
			this.scopeStack = scopeStack;
			this.existential = existential;
			// create the new scope
			this.scopeStack.openScope();
		}

		@Override
		public void close() throws IOException {
			if (Existential.NO == this.existential)
				throw new UnsupportedOperationException("Cannot close non-existential proxy!");
			if (!this.open) throw new UnsupportedOperationException("Scope of ConditionProxy already closed!");
			if (this != ECSetRuleBuilder.this.stack.peek()) {
				throw new IllegalStateException("Close was called on a scope that is not on top of the stack!");
			}
			final ECFilterSet.ECExistentialSet ecExistentialSet = CEToECTranslator.NoORsTranslator
					.toEcExistentialSet(initialFactVariable, this.existential == Existential.POSITIVE ? true : false,
							factVariableSet, equivalenceClasses, condition);
			this.parent.condition.add(ecExistentialSet);
			this.scopeStack.closeScope();
			ECSetRuleBuilder.this.stack.pop();
			this.open = false;
		}

		public SingleFactVariable newFactVariable(final Template template) {
			final SingleFactVariable factVariable = this.scopeStack.createDummyFactVariable(template, null);
			this.factVariableSet.add(factVariable);
			this.equivalenceClasses.add(factVariable.getEqual());
			return factVariable;
		}

		public SingleFactVariable.SingleSlotVariable newSlotVariable(final SingleFactVariable fv,
				final SlotAddress slot) {
			final ScopeStack.VariableSymbol variableSymbol =
					this.scopeStack.createDummySlotVariable(fv, slot, null, nullConsumer);
			final LinkedList<SingleFactVariable.SingleSlotVariable> slotVariables =
					variableSymbol.getEqual().getSlotVariables();
			this.equivalenceClasses.add(variableSymbol.getEqual());
			return slotVariables.getLast();
		}

		public SingleFactVariable.SingleSlotVariable newSlotVariable(final SingleFactVariable fv, final String slot) {
			return newSlotVariable(fv, fv.getTemplate().getSlotAddress(slot));
		}

		public SingleFactVariable.SingleSlotVariable newSlotVariable(final SingleFactVariable fv,
				final Template.Slot slot) {
			return newSlotVariable(fv, slot.getName());
		}

		public ConditionProxy add(final PredicateWithArguments<ECLeaf> predicate) {
			return add(new ECFilter(predicate));
		}

		public ConditionProxy add(final ECFilterSet filter) {
			condition.add(filter);
			return this;
		}
	}

	final String rulename;
	final Stack<ConditionProxy> stack = new Stack<>();
	@Getter
	final SingleFactVariable initialFactVariable;

	public ECSetRuleBuilder(final Template initialFactTemplate, final String rulename) {
		this.rulename = rulename;
		final ScopeStack scopeStack = new ScopeStack();
		initialFactVariable = scopeStack.createDummyFactVariable(initialFactTemplate, null);
		stack.add(new ConditionProxy(null, scopeStack, Existential.NO));
	}


	public ConditionProxy newExistentialScope(final boolean positive) {
		final ConditionProxy stackTop = stack.peek();
		final ConditionProxy conditionProxy = new ConditionProxy(stackTop, stackTop.scopeStack,
				positive ? Existential.POSITIVE : Existential.NEGATED);
		this.stack.add(conditionProxy);
		return conditionProxy;
	}

	public ConstructCache.Defrule.ECSetRule build() {
		if (1 != stack.size()) {
			throw new IllegalStateException("Rule can only be constructed if all existential scopes are closed!");
		}
		final ConditionProxy conditionProxy = stack.pop();
		final Set<ECFilterSet> condition = conditionProxy.condition;
		final Set<SingleFactVariable> factVariableSet = conditionProxy.factVariableSet;
		final Set<RuleCondition.EquivalenceClass> equivalenceClasses = conditionProxy.equivalenceClasses;
		final ConstructCache.Defrule defrule =
				new ConstructCache.Defrule(rulename, "", 0, null, new FunctionWithArguments[]{});
		final ConstructCache.Defrule.ECSetRule ecSetRule =
				defrule.newECSetRule(condition, factVariableSet, equivalenceClasses, HashBiMap.create(0), 0);
		return ecSetRule;
	}
}
