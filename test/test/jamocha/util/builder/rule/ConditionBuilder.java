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

import lombok.RequiredArgsConstructor;
import org.jamocha.dn.memory.SlotAddress;
import org.jamocha.dn.memory.SlotType;
import org.jamocha.dn.memory.Template;
import org.jamocha.filter.ECFilter;
import org.jamocha.filter.ECFilterSet;
import org.jamocha.function.Function;
import org.jamocha.function.Predicate;
import org.jamocha.function.fwa.ECLeaf;
import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.function.fwa.PredicateWithArguments;
import org.jamocha.languages.common.RuleCondition;
import org.jamocha.languages.common.SingleFactVariable;
import test.jamocha.util.builder.fwa.ECFunctionBuilder;
import test.jamocha.util.builder.fwa.ECPredicateBuilder;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public interface ConditionBuilder {
	SingleFactVariable newFactVariable(final Template template);

	SingleFactVariable.SingleSlotVariable newSlotVariable(final SingleFactVariable fv, final SlotAddress slot);

	default SingleFactVariable.SingleSlotVariable newSlotVariable(final SingleFactVariable fv, final String slot) {
		return newSlotVariable(fv, fv.getTemplate().getSlotAddress(slot));
	}

	default SingleFactVariable.SingleSlotVariable newSlotVariable(final SingleFactVariable fv,
			final Template.Slot slot) {
		return newSlotVariable(fv, slot.getName());
	}

	default void add(final PredicateWithArguments<ECLeaf> predicate) {
		add(new ECFilter(predicate));
	}

	void add(final ECFilterSet filter);

	PredicateBuilder newPredicateBuilder(final Predicate predicate);

	ECFunctionBuilder newFunctionBuilder(final Function<?> function);

	@RequiredArgsConstructor
	class PredicateBuilder {
		final ECPredicateBuilder delegate;
		final ConditionBuilder target;

		public void build() {
			target.add(delegate.build());
		}

		public PredicateBuilder addBoolean(final boolean value) {
			delegate.addBoolean(value);
			return this;
		}

		public PredicateBuilder addFunction(final FunctionWithArguments<ECLeaf> function) {
			delegate.addFunction(function);
			return this;
		}

		public PredicateBuilder addDouble(final double value) {
			delegate.addDouble(value);
			return this;
		}

		public PredicateBuilder addConstant(final Object value, final SlotType type) {
			delegate.addConstant(value, type);
			return this;
		}

		public PredicateBuilder addEC(final RuleCondition.EquivalenceClass equivalenceClass) {
			delegate.addEC(equivalenceClass);
			return this;
		}

		public PredicateBuilder addLong(final long value) {
			delegate.addLong(value);
			return this;
		}

		public PredicateBuilder addString(final String value) {
			delegate.addString(value);
			return this;
		}
	}
}
