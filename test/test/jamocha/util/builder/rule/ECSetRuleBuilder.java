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

package test.jamocha.util.builder.rule;

import com.google.common.collect.HashBiMap;
import lombok.Getter;
import org.jamocha.dn.ConstructCache;
import org.jamocha.dn.compiler.ecblocks.CEToECTranslator;
import org.jamocha.dn.memory.Template;
import org.jamocha.filter.ECCollector;
import org.jamocha.filter.ECFilterSet;
import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.languages.common.RuleCondition;
import org.jamocha.languages.common.ScopeStack;
import org.jamocha.languages.common.SingleFactVariable;

import java.io.Closeable;
import java.io.IOException;
import java.util.Set;
import java.util.Stack;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class ECSetRuleBuilder extends AbstractConditionProxy {

	public class ExistentialConditionProxy extends AbstractConditionProxy implements Closeable {
		final boolean positive;
		boolean open = true;

		private ExistentialConditionProxy(final AbstractConditionProxy parent, final ScopeStack scopeStack,
				final boolean positive) {
			super(parent, scopeStack);
			this.positive = positive;
			// create the new scope
			this.scopeStack.openScope();
		}

		@Override
		public void close() throws IOException {
			if (!this.open) throw new UnsupportedOperationException("Scope of ConditionProxy already closed!");
			if (this != ECSetRuleBuilder.this.stack.peek()) {
				throw new IllegalStateException("Close was called on a scope that is not on top of the stack!");
			}
			this.equivalenceClasses.addAll(this.constantToEquivalenceClass.values());
			final ECFilterSet.ECExistentialSet ecExistentialSet = CEToECTranslator.NoORsTranslator
					.toEcExistentialSet(ECSetRuleBuilder.this.initialFactVariable, this.positive, this.factVariableSet,
							this.equivalenceClasses, this.condition);
			this.parent.condition.add(ecExistentialSet);
			this.scopeStack.closeScope();
			ECSetRuleBuilder.this.stack.pop();
			this.open = false;
		}
	}

	final String ruleName;
	final Stack<AbstractConditionProxy> stack = new Stack<>();
	@Getter
	final SingleFactVariable initialFactVariable;

	public ECSetRuleBuilder(final Template initialFactTemplate, final String ruleName) {
		super(null, new ScopeStack());
		this.ruleName = ruleName;
		this.initialFactVariable = super.scopeStack.createDummyFactVariable(initialFactTemplate, null);
		this.stack.push(this);
	}

	public ExistentialConditionProxy newExistentialScope(final boolean positive) {
		final AbstractConditionProxy stackTop = this.stack.peek();
		final ExistentialConditionProxy existentialConditionProxy =
				new ExistentialConditionProxy(stackTop, super.scopeStack, positive);
		this.stack.add(existentialConditionProxy);
		return existentialConditionProxy;
	}

	public ConstructCache.Defrule.ECSetRule build() {
		if (1 != this.stack.size()) {
			throw new IllegalStateException("Rule can only be constructed if all existential scopes are closed!");
		}
		final AbstractConditionProxy conditionProxy = this.stack.pop();
		final Set<ECFilterSet> condition = conditionProxy.condition;
		final Set<SingleFactVariable> factVariableSet = conditionProxy.factVariableSet;
		final Set<RuleCondition.EquivalenceClass> equivalenceClasses = conditionProxy.equivalenceClasses;
		equivalenceClasses.addAll(this.constantToEquivalenceClass.values());
		final Set<RuleCondition.EquivalenceClass> usedECs = ECCollector.collect(condition);
		if (usedECs.contains(this.initialFactVariable.getEqual())) {
			factVariableSet.add(this.initialFactVariable);
			equivalenceClasses.add(this.initialFactVariable.getEqual());
		}
		final ConstructCache.Defrule defrule =
				new ConstructCache.Defrule(this.ruleName, "", 0, null, new FunctionWithArguments[]{});
		final ConstructCache.Defrule.ECSetRule ecSetRule =
				defrule.newECSetRule(condition, factVariableSet, equivalenceClasses, HashBiMap.create(0), 0);
		return ecSetRule;
	}
}

