/*
 * Copyright 2002-2008 The Jamocha Team
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

package org.jamocha.engine.functions.math;

import org.jamocha.engine.Engine;
import org.jamocha.engine.Parameter;
import org.jamocha.engine.functions.AbstractFunction;
import org.jamocha.engine.functions.FunctionDescription;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.IllegalParameterException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;

/**
 * @author Nikolaus Koemm, Christian Ebert
 * 
 * Returns the value of the first argument raised to the power of the following
 * arguments.
 */
public class Pow extends AbstractFunction {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Returns the value of the first mumeric argument raised to the power of the following numeric arguments.";
		}

		public int getParameterCount() {
			return 2;
		}

		public String getParameterDescription(int parameter) {
			if (parameter == 0)
				return "Numeric value, base for the calculation.";
			else if (parameter == 1)
				return "Numeric values, exponent(s) for the calculation";
			return null;
		}

		public String getParameterName(int parameter) {
			if (parameter == 0)
				return "base";
			else if (parameter == 1)
				return "exponent(s)";
			return null;
		}

		public JamochaType[] getParameterTypes(int parameter) {
			return JamochaType.NUMBERS;
		}

		public JamochaType[] getReturnType() {
			return JamochaType.DOUBLES;
		}

		public boolean isParameterCountFixed() {
			return false;
		}

		public boolean isParameterOptional(int parameter) {
			return parameter > 1;
		}

		public String getExample() {
			return "(pow 2 3 2 0.5)";
		}

		public boolean isResultAutoGeneratable() {
			return true;
		}

		public Object getExpectedResult() {
			return Math.pow(2, 3 * 2 * 0.5);
		}
	}

	public static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "pow";

	public Pow() {
		aliases.add("**");
	}

	@Override
	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	@Override
	public String getName() {
		return NAME;
	}

	public JamochaType getReturnType() {
		return JamochaType.DOUBLE;
	}

	@Override
	public JamochaValue executeFunction(Engine engine, Parameter[] params)
			throws EvaluationException {
		if (params != null)
			if (params.length > 0) {
				boolean isDouble = false;
				for (int idx = 0; idx < params.length; idx++)
					if (params[idx].getValue(engine).getType().equals(
							JamochaType.DOUBLE)) {
						isDouble = true;
						break;
					}
				double result = params[0].getValue(engine).implicitCast(
						JamochaType.DOUBLE).getDoubleValue();
				if (isDouble)
					for (int i = 1; i < params.length; ++i)
						result = Math.pow(result, params[i].getValue(engine)
								.implicitCast(JamochaType.DOUBLE)
								.getDoubleValue());
				else
					for (int i = 1; i < params.length; ++i)
						result = Math.pow(result, params[i].getValue(engine)
								.implicitCast(JamochaType.LONG).getLongValue());
				return JamochaValue.newDouble(result);
			}
		throw new IllegalParameterException(1, true);
	}
}
