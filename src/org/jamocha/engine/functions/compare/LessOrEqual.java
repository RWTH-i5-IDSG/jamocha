/*
 * Copyright 2002-2008 Peter Lin & The Jamocha Team
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

package org.jamocha.engine.functions.compare;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.IllegalParameterException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.engine.Parameter;
import org.jamocha.engine.Engine;
import org.jamocha.engine.functions.AbstractFunction;
import org.jamocha.engine.functions.FunctionDescription;

/**
 * @author Peter Lin
 * 
 * LessOrEqual will compare 2 or more numeric values and return true if the
 * (n-1)th value is less or equal to the nth value.
 */
public class LessOrEqual extends AbstractFunction {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "LessOrEqual will compare two or more numeric values and return true, if the (n-1)th value is less than or equal to the nth value.";
		}

		public int getParameterCount() {
			return 2;
		}

		public String getParameterDescription(int parameter) {
			return "Number that will be compared to the other parameters.";
		}

		public String getParameterName(int parameter) {
			return "number";
		}

		public JamochaType[] getParameterTypes(int parameter) {
			return JamochaType.NUMBERS;
		}

		public JamochaType[] getReturnType() {
			return JamochaType.BOOLEANS;
		}

		public boolean isParameterCountFixed() {
			return false;
		}

		public boolean isParameterOptional(int parameter) {
			if (parameter > 0)
				return true;
			else
				return false;
		}

		public String getExample() {
			return "(lessOrEqual 1 2 2 84)";
		}

		public boolean isResultAutoGeneratable() {
			return true;
		}

		public Object getExpectedResult() {
			return JamochaValue.TRUE;
		}
	}

	private static final long serialVersionUID = 1L;

	public static final FunctionDescription DESCRIPTION = new Description();

	public static final String NAME = "lessOrEqual";

	public LessOrEqual() {
		aliases.add("<=");
	}

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
		if (params != null)
			if (params.length > 0) {
				JamochaValue current, previous = params[0].getValue(engine);
				boolean isDouble = previous.is(JamochaType.DOUBLE);
				for (int i = 1; i < params.length; ++i) {
					current = params[i].getValue(engine);
					if (current.is(JamochaType.DOUBLE))
						isDouble = true;

					if (isDouble) {
						if (current.getDoubleValue() < previous
								.getDoubleValue())
							return JamochaValue.FALSE;
					} else if (current.getLongValue() < previous.getLongValue())
						return JamochaValue.FALSE;
					previous = current;
				}
				return JamochaValue.TRUE;
			}
		throw new IllegalParameterException(1, true);
	}
}
