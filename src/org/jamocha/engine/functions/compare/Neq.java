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
 * @author Nikolaus Koemm
 * 
 * Neq is used to compare a literal value against one or more other values. If
 * all of the values are not equal, the function returns true.
 */
public class Neq extends AbstractFunction {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Neq is used to compare a literal value against one or more other values. If all of the other values are not equal, the function returns true. Neq should be used if the type of its arguments is not known in adavance.";
		}

		public int getParameterCount() {
			return 2;
		}

		public String getParameterDescription(int parameter) {
			switch (parameter) {
			case 0:
				return "Literal value to be compared to all other parameters.";
			default:
				return "Value to be compared to the first parameter.";
			}
		}

		public String getParameterName(int parameter) {
			return "value";
		}

		public JamochaType[] getParameterTypes(int parameter) {
			return JamochaType.ANY;
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
			return "(neq 47 11)";
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

	public static final String NAME = "neq";

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
		JamochaValue result = JamochaValue.TRUE;
		if (params != null && params.length > 1) {
			JamochaValue first = params[0].getValue(engine);
			for (int idx = 1; idx < params.length; idx++) {
				JamochaValue right = params[idx].getValue(engine);
				if (first.equals(right)) {
					result = JamochaValue.FALSE;
					break;
				}
			}
		} else
			throw new IllegalParameterException(1, true);
		return result;
	}
}