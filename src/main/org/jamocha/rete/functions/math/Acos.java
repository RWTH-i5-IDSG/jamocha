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

import java.io.Serializable;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.IllegalParameterException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.functions.FunctionDescription;

/**
 * @author Christian Ebert
 * 
 * Returns the trigonometric arc cosine of its only argument (which should be an
 * angle as numeric expression). The return value will double.
 */
public class Acos implements Function, Serializable {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Returns the arc cosine of its numeric argument. The return value is an angle expressed in radians in the range of 0.0 through pi (3.141592653589793..)";
		}

		public int getParameterCount() {
			return 1;
		}

		public String getParameterDescription(int parameter) {
			return "Numeric value in the range of -1.0 through 1.0.";	
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
			return "(acos -0.7563654)";
		}
	}

	private static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "acos";

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
					return JamochaValue.newDouble(Math.acos(value
							.getDoubleValue()));
				} else if (value.getType().equals(JamochaType.LONG)) {
					return JamochaValue.newDouble(Math.acos(value
							.getLongValue()));
				}
			}
		}
		throw new IllegalParameterException(1);
	}
}
