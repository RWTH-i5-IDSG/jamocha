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
public class FunctionWithArgumentsComposite implements FunctionWithArguments {

	final Function function;
	final FunctionWithArguments args[];

	public FunctionWithArgumentsComposite(final Function function,
			final FunctionWithArguments... args) {
		super();
		this.function = function;
		this.args = args;
	}

	@Override
	public SlotType[] getParamTypes() {
		final ArrayList<SlotType> types = new ArrayList<>();
		for (FunctionWithArguments fwa : args) {
			for (SlotType type : fwa.getParamTypes()) {
				types.add(type);
			}
		}
		return types.toArray(new SlotType[types.size()]);
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

	@Override
	public Object evaluate(final Object... params) {
		final Object evaluatedArgs[] = new Object[args.length];
		int k = 0;
		for (int i = 0; i < args.length; i++) {
			final FunctionWithArguments fwa = args[i];
			final SlotType[] types = fwa.getParamTypes();
			evaluatedArgs[i] = fwa.evaluate(Arrays.copyOfRange(params, k, k + types.length));
			k += types.length;
		}
		return function.evaluate(evaluatedArgs);
	}

	@Override
	public FunctionWithArguments translatePath(final ArrayList<SlotInFactAddress> addressesInTarget) {
		for (int i = 0; i < this.args.length; ++i) {
			args[i] = args[i].translatePath(addressesInTarget);
		}
		return this;
	}

	@Override
	public <T extends Collection<Path>> T gatherPaths(final T paths) {
		for (FunctionWithArguments fwa : args) {
			fwa.gatherPaths(paths);
		}
		return paths;
	}

	@Override
	public boolean equalsInFunction(final FunctionWithArguments function) {
		// FIXME implement equalsInFunction
		return false;
	}

}
