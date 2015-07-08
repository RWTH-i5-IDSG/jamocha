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

import java.util.Arrays;

import lombok.Data;
import lombok.Getter;

import org.jamocha.dn.memory.SlotType;
import org.jamocha.function.Function;
import org.jamocha.languages.common.RuleCondition.EquivalenceClass;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Data
public class ECLeaf implements ExchangeableLeaf<ECLeaf> {

	final EquivalenceClass ec;
	@Getter(lazy = true)
	private final int hashCode = initHashCode();

	private int initHashCode() {
		return FunctionWithArguments.hash(Arrays.asList(this.ec).stream().mapToInt(java.util.Objects::hashCode)
				.toArray(), FunctionWithArguments.positionIsIrrelevant);
	}

	@Override
	public SlotType[] getParamTypes() {
		return SlotType.empty;
	}

	@Override
	public SlotType getReturnType() {
		return ec.getType();
	}

	@Override
	public Function<?> lazyEvaluate(final Function<?>... params) {
		throw new UnsupportedOperationException("Evaluate not allowed for ECLeafs!");
	}

	@Override
	public Object evaluate(final Object... params) {
		throw new UnsupportedOperationException("Evaluate not allowed for ECLeafs!");
	}

	@Override
	public int hashPositionIsIrrelevant() {
		return getHashCode();
	}

	@Override
	public <V extends FunctionWithArgumentsVisitor<ECLeaf>> V accept(final V visitor) {
		visitor.visit(this);
		return visitor;
	}

	@Override
	public ExchangeableLeaf<ECLeaf> copy() {
		return new ECLeaf(ec);
	}

}
