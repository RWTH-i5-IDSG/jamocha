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
package org.jamocha.filter.fwa;

import static org.jamocha.util.ToArray.toArray;

import java.util.Arrays;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.jamocha.dn.Network;
import org.jamocha.dn.memory.FactIdentifier;
import org.jamocha.dn.memory.SlotType;
import org.jamocha.filter.Function;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor
public class Retract implements FunctionWithArguments {
	@Getter
	final Network network;
	@Getter(onMethod = @__(@Override))
	final SlotType[] paramTypes;

	@Override
	public SlotType getReturnType() {
		// TBD should we really use nil here for void?
		// could use boolean - true for success
		return SlotType.NIL;
	}

	private static FactIdentifier toFactIdentifier(final Function<?> param) {
		if (SlotType.LONG == param.getReturnType()) {
			final int id = ((Long) param.evaluate()).intValue();
			return new FactIdentifier(id);
		}
		assert param.getReturnType() == SlotType.FACTADDRESS;
		assert FactIdentifier.class == SlotType.FACTADDRESS.getJavaClass();
		return ((FactIdentifier) param.evaluate());
	}

	private static final GenericWithArgumentsComposite.LazyObject nullLazyObject =
			new GenericWithArgumentsComposite.LazyObject(null);

	@Override
	public Function<?> lazyEvaluate(final Function<?>... params) {
		this.network.retractFacts(toArray(Arrays.stream(params).map(Retract::toFactIdentifier),
				FactIdentifier[]::new));
		return nullLazyObject;
	}

	@Override
	public Object evaluate(final Object... params) {
		return GenericWithArgumentsComposite.staticEvaluate(this::lazyEvaluate, params);
	}

	@Override
	public <T extends FunctionWithArgumentsVisitor> T accept(final T visitor) {
		visitor.visit(this);
		return visitor;
	}

	@Override
	public int hashPositionIsIrrelevant() {
		// TODO Auto-generated method stub
		return 0;
	}
}
