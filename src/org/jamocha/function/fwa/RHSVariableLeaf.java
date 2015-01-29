/*
 * Copyright 2002-2015 The Jamocha Team
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
package org.jamocha.function.fwa;

import lombok.AllArgsConstructor;

import org.jamocha.dn.memory.SlotType;
import org.jamocha.function.Function;
import org.jamocha.languages.common.ScopeStack.Symbol;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@AllArgsConstructor
public class RHSVariableLeaf implements ExchangeableLeaf<RHSVariableLeaf>, VariableLeaf {
	final VariableValueContext context;
	final Symbol key;
	final SlotType type;

	@Override
	public Object evaluate(final Object... params) {
		return context.get(key);
	}

	@Override
	public <V extends FunctionWithArgumentsVisitor<RHSVariableLeaf>> V accept(final V visitor) {
		visitor.visit(this);
		return visitor;
	}

	@Override
	public SlotType[] getParamTypes() {
		return SlotType.empty;
	}

	@Override
	public SlotType getReturnType() {
		return type;
	}

	@Override
	public Function<?> lazyEvaluate(final Function<?>... params) {
		return new GenericWithArgumentsComposite.LazyObject<>(evaluate());
	}

	@Override
	public int hashPositionIsIrrelevant() {
		return 0;
	}

	@Override
	public ExchangeableLeaf<RHSVariableLeaf> copy() {
		throw new UnsupportedOperationException("Can't copy stateful leafs!");
	}

	@Override
	public Object reset() {
		context.put(key, null);
		return null;
	};

	@Override
	public Object set(final Object value) {
		final Object correct;
		if (type.isArrayType()) {
			final Object[] array = SlotType.newArrayInstance(type, 1);
			array[0] = value;
			correct = array;
		} else {
			correct = value;
		}
		context.put(key, correct);
		return value;
	};

	@Override
	public Object set(final Object[] values) {
		context.put(key, values);
		return values;
	};
}
