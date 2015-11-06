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
package org.jamocha.dn.compiler.ecblocks;

import java.util.Objects;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.jamocha.dn.memory.SlotType;
import org.jamocha.function.Function;
import org.jamocha.function.fwa.ExchangeableLeaf;
import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.function.fwa.FunctionWithArgumentsVisitor;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Getter
@RequiredArgsConstructor
public class ECOccurrenceLeaf implements ExchangeableLeaf<ECOccurrenceLeaf> {

	private final ECOccurrence ecOccurrence;

	@Getter(lazy = true)
	private final int hashCode = initHashCode();

	private int initHashCode() {
		return FunctionWithArguments.hash(new int[] { Objects.hashCode(this.ecOccurrence.getEc()) },
				FunctionWithArguments.positionIsIrrelevant);
	}

	@Override
	public SlotType[] getParamTypes() {
		return SlotType.empty;
	}

	@Override
	public SlotType getReturnType() {
		return ecOccurrence.getEc().getType();
	}

	@Override
	public Function<?> lazyEvaluate(final Function<?>... params) {
		throw new UnsupportedOperationException("Evaluate not allowed for ECOccurrenceLeafs!");
	}

	@Override
	public Object evaluate(final Object... params) {
		throw new UnsupportedOperationException("Evaluate not allowed for ECOccurrenceLeafs!");
	}

	@Override
	public int hashPositionIsIrrelevant() {
		return getHashCode();
	}

	@Override
	public <V extends FunctionWithArgumentsVisitor<ECOccurrenceLeaf>> V accept(final V visitor) {
		visitor.visit(this);
		return visitor;
	}

	@Override
	public ExchangeableLeaf<ECOccurrenceLeaf> copy() {
		return new ECOccurrenceLeaf(ecOccurrence);
	}
}
