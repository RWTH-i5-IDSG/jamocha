/*
 * Copyright 2006 Christian Ebert, 2007 Uta Christoph 
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
package org.jamocha.rete.functions.math;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.IllegalParameterException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.functions.AbstractFunction;
import org.jamocha.rete.functions.FunctionDescription;

/**
 * @author Christian Ebert
 * 
 * Returns the trigonometric arc tangent of an angle.
 */
public class Atan extends AbstractFunction {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Returns the arc tangent of its numeric argument. The return value is an angle expressed in radians in the range of -pi/2 through pi/2.";
		}

		public int getParameterCount() {
			return 1;
		}

		public String getParameterDescription(int parameter) {
			return "Numeric value to get the arc tangent from (no restrictions in the domain range).";
		}

		public String getParameterName(int parameter) {
			return "number";
		}

		public JamochaType[] getParameterTypes(int parameter) {
			return JamochaType.NUMBERS;
		}

		public JamochaType[] getReturnType() {
			return JamochaType.DOUBLES;
		}

		public boolean isParameterCountFixed() {
			return true;
		}

		public boolean isParameterOptional(int parameter) {
			return false;
		}

		public String getExample() {
			return "(atan -3.14)";
		}

		public boolean isResultAutoGeneratable() {
			return true;
		}
	}

	private static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "atan";

	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	public String getName() {
		return NAME;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		if (params != null) {
			if (params.length == 1) {
				JamochaValue value = params[0].getValue(engine);
				if (!value.getType().equals(JamochaType.DOUBLE)
						&& !value.getType().equals(JamochaType.LONG)) {
					value = value.implicitCast(JamochaType.DOUBLE);
				}
				if (value.getType().equals(JamochaType.DOUBLE)) {
					return JamochaValue.newDouble(Math.atan(value
							.getDoubleValue()));
				} else if (value.getType().equals(JamochaType.LONG)) {
					return JamochaValue.newDouble(Math.atan(value
							.getLongValue()));
				}
			}
		}
		throw new IllegalParameterException(1);
	}
}
