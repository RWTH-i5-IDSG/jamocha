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
import java.util.stream.IntStream;

import lombok.AllArgsConstructor;
import lombok.Getter;

import org.jamocha.dn.memory.SlotType;
import org.jamocha.function.Function;

/**
 * Class representing a bind operation.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@AllArgsConstructor
public class Bind<L extends ExchangeableLeaf<L>> implements FunctionWithArguments<L> {
	public static final String inClips = "bind";

	@Getter
	private final FunctionWithArguments<L> args[];
	@Getter(lazy = true, onMethod = @__(@Override))
	private final SlotType[] paramTypes = calculateParamTypes();

	private SlotType[] calculateParamTypes() {
		return GenericWithArgumentsComposite.calculateParamTypes(args);
	}

	@Override
	public <V extends FunctionWithArgumentsVisitor<L>> V accept(final V visitor) {
		visitor.visit(this);
		return visitor;
	}

	@Override
	public SlotType getReturnType() {
		return args.length == 1 ? SlotType.NIL : args[0].getReturnType();
	}

	@Override
	public Function<?> lazyEvaluate(final Function<?>... params) {
		return GenericWithArgumentsComposite.staticLazyEvaluate(
				((final Function<?>[] args) -> evaluate(Arrays.stream(args).map(f -> f.evaluate()).toArray())),
				inClips, args, params);
	}

	@Override
	public Object evaluate(final Object... params) {
		assert args.length > 0;
		final VariableLeaf leaf = ((VariableLeaf) args[0]);
		switch (args.length) {
		case 1:
			return leaf.reset();
		case 2:
			return leaf.set(args[1].evaluate());
		default:
			return leaf.set(IntStream.range(1, params.length).mapToObj(i -> args[i].evaluate()).toArray());
		}
	}

	@Override
	public int hashPositionIsIrrelevant() {
		// hashing unnecessary since it only occurs on the RHS
		return 0;
	}
}
