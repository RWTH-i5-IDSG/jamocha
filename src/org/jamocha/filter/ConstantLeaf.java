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
package org.jamocha.filter;

import java.util.ArrayList;
import java.util.Collection;

import lombok.EqualsAndHashCode;

import org.jamocha.dn.memory.SlotType;
import org.jamocha.dn.nodes.SlotInFactAddress;

/**
 * A parameter of a {@link Function} may be a constant value specified in the parsed representation
 * of the rule. This constant value is then stored in this class together with its type.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@EqualsAndHashCode
public class ConstantLeaf implements FunctionWithArguments, Function<Object> {
	final Object value;
	final SlotType type;

	public ConstantLeaf(final Object value, final SlotType type) {
		super();
		this.value = value;
		this.type = type;
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
	public String toString() {
		return value.toString();
	}

	@Override
	public Object evaluate(final Function<?>... params) {
		return value;
	}

	@Override
	public Function<?> lazyEvaluate(final Function<?>... params) {
		return this;
	}

	@Override
	public Object evaluate(final Object... params) {
		return value;
	}

	@Override
	public FunctionWithArguments translatePath(final ArrayList<SlotInFactAddress> addressesInTarget) {
		return this;
	}

	@Override
	public <T extends Collection<Path>> T gatherPaths(final T paths) {
		return paths;
	}

	@Override
	public <T extends Collection<SlotInFactAddress>> T gatherCurrentAddresses(final T paths) {
		return paths;
	}

	@Override
	public boolean equalsInFunction(final FunctionWithArguments function) {
		if (!(function instanceof ConstantLeaf))
			return false;
		final ConstantLeaf other = (ConstantLeaf) function;
		if (!other.canEqual(this))
			return false;
		if (this.type == null ? other.type != null : !this.type.equals(other.type))
			return false;
		if (this.value == null ? other.value != null : !this.value.equals(other.value))
			return false;
		return true;
	}

}
