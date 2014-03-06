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

import java.util.ArrayList;
import java.util.Arrays;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import org.jamocha.dn.memory.SlotType;
import org.jamocha.filter.Function;

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
@Getter
public abstract class GenericWithArgumentsComposite<R, F extends Function<? extends R>> implements
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
		return this.function.getReturnType();
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(this.function.toString());
		sb.append("(");
		if (this.args.length > 0) {
			sb.append(this.args[0].toString());
		}
		for (int i = 1; i < this.args.length; ++i) {
			sb.append(", ");
			sb.append(this.args[i].toString());
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
				final Function<?> evaluatableArgs[] =
						new Function<?>[GenericWithArgumentsComposite.this.args.length];
				int k = 0;
				for (int i = 0; i < GenericWithArgumentsComposite.this.args.length; i++) {
					final FunctionWithArguments fwa = GenericWithArgumentsComposite.this.args[i];
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

}