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
 * the specific language governing permissions and limitations under the License.
 */

package test.jamocha.util.builder.rule;

import com.google.common.collect.Sets;
import lombok.RequiredArgsConstructor;
import org.jamocha.dn.memory.SlotAddress;
import org.jamocha.dn.memory.SlotType;
import org.jamocha.dn.memory.Template;
import org.jamocha.filter.ECFilterSet;
import org.jamocha.function.Function;
import org.jamocha.function.Predicate;
import org.jamocha.function.fwa.ConstantLeaf;
import org.jamocha.function.fwa.ECLeaf;
import org.jamocha.languages.common.RuleCondition;
import org.jamocha.languages.common.ScopeStack;
import org.jamocha.languages.common.SingleFactVariable;
import test.jamocha.util.builder.fwa.ECFunctionBuilder;
import test.jamocha.util.builder.fwa.ECPredicateBuilder;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor
public abstract class AbstractConditionProxy implements ConditionBuilder {
    private static final Consumer<SingleFactVariable.SingleSlotVariable> NULL_CONSUMER = (a) -> {
    };

    final AbstractConditionProxy parent;
    final ScopeStack scopeStack;
    final Set<ECFilterSet> condition = Sets.newIdentityHashSet();
    final Set<SingleFactVariable> factVariableSet = Sets.newIdentityHashSet();
    final Set<RuleCondition.EquivalenceClass> equivalenceClasses = Sets.newIdentityHashSet();
    final HashMap<ConstantLeaf<ECLeaf>, RuleCondition.EquivalenceClass> constantToEquivalenceClass = new HashMap<>();

    @Override
    public SingleFactVariable newFactVariable(final Template template) {
        final SingleFactVariable factVariable = this.scopeStack.createDummyFactVariable(template, null);
        this.factVariableSet.add(factVariable);
        this.equivalenceClasses.add(factVariable.getEqual());
        return factVariable;
    }

    @Override
    public SingleFactVariable.SingleSlotVariable newSlotVariable(final SingleFactVariable fv, final SlotAddress slot) {
        final ScopeStack.VariableSymbol variableSymbol =
                this.scopeStack.createDummySlotVariable(fv, slot, null, NULL_CONSUMER);
        final LinkedList<SingleFactVariable.SingleSlotVariable> slotVariables =
                variableSymbol.getEqual().getSlotVariables();
        this.equivalenceClasses.add(variableSymbol.getEqual());
        return slotVariables.getLast();
    }

    @Override
    public RuleCondition.EquivalenceClass getECFromConstant(final Object value, final SlotType type) {
        return this.constantToEquivalenceClass.computeIfAbsent(new ConstantLeaf<>(value, type),
                c -> RuleCondition.EquivalenceClass.newECFromConstantExpression(this.scopeStack.getCurrentScope(), c));
    }

    @Override
    public void add(final ECFilterSet filter) {
        this.condition.add(filter);
    }

    @Override
    public PredicateBuilder newPredicateBuilder(final Predicate predicate) {
        return new PredicateBuilder(
                new ECPredicateBuilder(predicate, this.scopeStack.getCurrentScope(), constantToEquivalenceClass), this);
    }

    @Override
    public FunctionBuilder newFunctionBuilder(final Function<?> function) {
        return new FunctionBuilder(
                new ECFunctionBuilder(function, this.scopeStack.getCurrentScope(), constantToEquivalenceClass));
    }
}
