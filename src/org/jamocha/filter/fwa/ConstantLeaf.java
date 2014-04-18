/*
 * Copyright 2002-2013 The Jamocha Team
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
package org.jamocha.filter.fwa;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import org.jamocha.dn.memory.SlotType;
import org.jamocha.filter.Function;
import org.jamocha.filter.fwa.GenericWithArgumentsComposite.LazyObject;

/**
 * A parameter of a {@link Function} may be a constant value specified in the parsed representation
 * of the rule. This constant value is then stored in this class together with its type.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Getter
@EqualsAndHashCode
public class ConstantLeaf implements FunctionWithArguments {
	final Object value;
	final SlotType type;
	final LazyObject lazyObject;

	public ConstantLeaf(final Object value, final SlotType type) {
		super();
		this.value = value;
		this.type = type;
		this.lazyObject = new LazyObject(value);
	}

	@Override
	public SlotType[] getParamTypes() {
		return SlotType.empty;
	}

	@Override
	public SlotType getReturnType() {
		return this.type;
	}

	@Override
	public String toString() {
		return this.value.toString();
	}

	@Override
	public Function<?> lazyEvaluate(final Function<?>... params) {
		return this.lazyObject;
	}

	@Override
	public Object evaluate(final Object... params) {
		return this.value;
	}

	@Override
	public <T extends FunctionWithArgumentsVisitor> T accept(final T visitor) {
		visitor.visit(this);
		return visitor;
	}

	@Override
	public int hashPositionIsIrrelevant() {
		return value.hashCode();
	}
}
