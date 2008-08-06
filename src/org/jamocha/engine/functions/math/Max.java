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

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.IllegalParameterException;
import org.jamocha.parser.IllegalTypeException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.engine.Parameter;
import org.jamocha.engine.Engine;
import org.jamocha.engine.functions.AbstractFunction;
import org.jamocha.engine.functions.FunctionDescription;

/**
 * @author Nikolaus Koemm
 * @author Christoph Emonds
 * 
 * Max returns the greatest of one or more values.
 */
public class Max extends AbstractFunction {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Returns the value of its greatest numeric argument. Each argument has to be a numeric expression. The return type is either an integer or float depending on the type of the greatest argument.";
			// "When necessary, integers are temporarily converted to floats for
			// comparison. ";
		}

		public int getParameterCount() {
			return 1;
		}

		public String getParameterDescription(int parameter) {
			return "Numeric values (at least one) to get the maximum from.";
		}

		public String getParameterName(int parameter) {
			return "number";
		}

		public JamochaType[] getParameterTypes(int parameter) {
			return JamochaType.NUMBERS;
		}

		public JamochaType[] getReturnType() {
			return JamochaType.NUMBERS;
		}

		public boolean isParameterCountFixed() {
			return false;
		}

		public boolean isParameterOptional(int parameter) {
			return false;
		}

		public String getExample() {
			return "(max 87 -.3 777 445 9023 -75555)";
		}

		public boolean isResultAutoGeneratable() {
			return true;
		}

		public Object getExpectedResult() {
			return 9023;
		}
	}

	public static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "max";

	@Override
	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public JamochaValue executeFunction(Engine engine, Parameter[] params)
			throws EvaluationException {
		if (params != null && params.length > 0) {
			JamochaValue result = params[0].getValue(engine);
			for (int idx = 1; idx < params.length; idx++) {
				JamochaValue value = params[idx].getValue(engine);
				if (result.is(JamochaType.DOUBLE)) {
					if (result.getDoubleValue() < value.implicitCast(
							JamochaType.DOUBLE).getDoubleValue())
						result = value;
				} else if (result.is(JamochaType.LONG)) {
					if (result.getLongValue() < value.implicitCast(
							JamochaType.LONG).getLongValue())
						result = value;
				} else
					throw new IllegalTypeException(JamochaType.NUMBERS, result
							.getType());
			}
			return result;
		}
		throw new IllegalParameterException(1, true);
	}
}