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
import java.util.Arrays;
import java.util.Collection;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import org.jamocha.dn.memory.SlotType;
import org.jamocha.dn.nodes.SlotInFactAddress;

/**
 * This class is the composite of the {@link FunctionWithArguments} hierarchy. It stores a
 * {@link Function function} and its parameters as an array of {@link FunctionWithArguments}. This
 * way it can recursively represent any combination of {@link Function functions} and their
 * arguments. On evaluation, the given parameters are split into chunks and passed to the
 * corresponding arguments. The returning values are passed to the stored function evaluating the
 * result.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @see Function
 * @see FunctionWithArguments
 */
@EqualsAndHashCode
public class GenericWithArgumentsComposite<R, F extends Function<? extends R>> implements
		FunctionWithArguments {

	final F function;
	final FunctionWithArguments args[];
	final SlotType[] paramTypes;

	public GenericWithArgumentsComposite(final F function, final FunctionWithArguments... args) {
		super();
		this.function = function;
		this.args = args;
		this.paramTypes = calculateParamTypes(args);
	}

	private static SlotType[] calculateParamTypes(final FunctionWithArguments args[]) {
		final ArrayList<SlotType> types = new ArrayList<>();
		for (final FunctionWithArguments fwa : args) {
			for (final SlotType type : fwa.getParamTypes()) {
				types.add(type);
			}
		}
		return types.toArray(new SlotType[types.size()]);
	}

	@Override
	public SlotType[] getParamTypes() {
		return this.paramTypes;
	}

	@Override
	public SlotType getReturnType() {
		return function.getReturnType();
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(function.toString());
		sb.append("(");
		if (args.length > 0) {
			sb.append(args[0].toString());
		}
		for (int i = 1; i < args.length; ++i) {
			sb.append(", ");
			sb.append(args[i].toString());
		}
		sb.append(")");
		return sb.toString();
	}

	@RequiredArgsConstructor
	@ToString
	public static class LazyObject implements Function<Object> {
		final Object value;

		@Override
		public SlotType[] getParamTypes() {
			return SlotType.empty;
		}

		@Override
		public SlotType getReturnType() {
			throw new UnsupportedOperationException(
					"Type checking can not be done during lazy evaluation!");
		}

		@Override
		public Object evaluate(final Function<?>... params) {
			return this.value;
		}

	}

	@Override
	public Function<R> lazyEvaluate(final Function<?>... params) {
		final F function = this.function;
		return new Function<R>() {
			@Override
			public SlotType[] getParamTypes() {
				return SlotType.empty;
			}

			@Override
			public SlotType getReturnType() {
				throw new UnsupportedOperationException(
						"Type checking can not be done during lazy evaluation!");
			}

			@Override
			public R evaluate(final Function<?>... innerParams) {
				final Function<?> evaluatableArgs[] = new Function<?>[args.length];
				int k = 0;
				for (int i = 0; i < args.length; i++) {
					final FunctionWithArguments fwa = args[i];
					final SlotType[] types = fwa.getParamTypes();
					evaluatableArgs[i] =
							fwa.lazyEvaluate(Arrays.copyOfRange(params, k, k + types.length));
					k += types.length;
				}
				return function.evaluate(evaluatableArgs);
			}
		};
	}

	@Override
	public R evaluate(final Object... params) {
		final int len = params.length;
		final LazyObject[] lazyParams = new LazyObject[len];
		for (int i = 0; i < len; ++i) {
			lazyParams[i] = new LazyObject(params[i]);
		}
		return lazyEvaluate(lazyParams).evaluate();
	}

	@Override
	public FunctionWithArguments translatePath(final ArrayList<SlotInFactAddress> addressesInTarget) {
		return new GenericWithArgumentsComposite<R, F>(this.function,
				translatePathHelper(addressesInTarget));
	}

	protected FunctionWithArguments[] translatePathHelper(
			final ArrayList<SlotInFactAddress> addressesInTarget) {
		final FunctionWithArguments[] args = new FunctionWithArguments[this.args.length];
		for (int i = 0; i < this.args.length; ++i) {
			args[i] = this.args[i].translatePath(addressesInTarget);
		}
		return args;
	}

	@Override
	public <T extends Collection<Path>> T gatherPaths(final T paths) {
		for (final FunctionWithArguments fwa : args) {
			fwa.gatherPaths(paths);
		}
		return paths;
	}

	@Override
	public <T extends Collection<SlotInFactAddress>> T gatherCurrentAddresses(final T paths) {
		for (final FunctionWithArguments fwa : args) {
			fwa.gatherCurrentAddresses(paths);
		}
		return paths;
	}

	@Override
	public boolean equalsInFunction(final FunctionWithArguments function) {
		if (function == this)
			return true;
		if (!(function instanceof GenericWithArgumentsComposite))
			return false;
		final GenericWithArgumentsComposite<?, ?> other =
				(GenericWithArgumentsComposite<?, ?>) function;
		if (!other.canEqual(this))
			return false;
		if (this.args.length != other.args.length)
			return false;
		for (int i = 0; i < this.args.length; ++i) {
			final FunctionWithArguments arg1 = this.args[i];
			final FunctionWithArguments arg2 = other.args[i];
			if (arg1 == null ? arg2 != null : !arg1.equalsInFunction(arg2))
				return false;
		}
		return true;
	}

	public GenericWithArgumentsComposite<R, F> withFunction(final F function) {
		return new GenericWithArgumentsComposite<R, F>(function, args);
	}

	public FunctionWithArguments[] reverseArguments() {
		final int length = this.args.length;
		final FunctionWithArguments[] rev = new FunctionWithArguments[length];
		for (int i = 0; i < length; ++i) {
			rev[length - i - 1] = this.args[i];
		}
		return rev;
	}

	public GenericWithArgumentsComposite<R, F> withInverseArgs() {
		return new GenericWithArgumentsComposite<R, F>(this.function, reverseArguments());
	}
}
