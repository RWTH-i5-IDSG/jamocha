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
package org.jamocha.function.fwa;

import org.jamocha.dn.memory.SlotType;
import org.jamocha.function.Function;
import org.jamocha.function.fwa.GenericWithArgumentsComposite.LazyObject;
import org.jamocha.languages.common.GlobalVariable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class GlobalVariableLeaf<L extends ExchangeableLeaf<L>> implements FunctionWithArguments<L>, VariableLeaf {
	final GlobalVariable variable;

	@Override
	public <V extends FunctionWithArgumentsVisitor<L>> V accept(final V visitor) {
		visitor.visit(this);
		return visitor;
	}

	@Override
	public SlotType[] getParamTypes() {
		return SlotType.empty;
	}

	@Override
	public SlotType getReturnType() {
		return variable.getType();
	}

	@Override
	public Function<?> lazyEvaluate(final Function<?>... params) {
		return new LazyObject<>(variable.getValue());
	}

	@Override
	public Object evaluate(final Object... params) {
		return variable.getValue();
	}

	@Override
	public int hashPositionIsIrrelevant() {
		return variable.hashCode();
	}

	@Override
	public String toString() {
		return variable.getSymbol().toString() + '[' + variable.getValue() + ']';
	}

	@Override
	public Object reset() {
		variable.reset();
		return variable.getValue();
	}

	@Override
	public Object set(final Object value) {
		variable.setValue(value);
		return value;
	}
}
