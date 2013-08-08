/*
 * Copyright 2002-2013 The Jamocha Team
 * 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.jamocha.org/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.jamocha.filter;

public class FunctionWithArgumentsComposite extends FunctionWithArguments {

	final Function function;
	final FunctionWithArguments args[];

	public FunctionWithArgumentsComposite(final Function function,
			final FunctionWithArguments[] args) {
		super();
		this.function = function;
		this.args = args;
	}

	@Override
	public SlotType returnType() {
		return function.returnType();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
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
		for (int i = 0; i < args.length; ++i) {
			evaluatedArgs[i] = args[i].evaluate((Object[]) null);
		}
		return function.evaluate(evaluatedArgs);
	}

}
