/*
 * Copyright 2002-2006 Peter Lin, 2007 Uta Christoph
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
 * @author Peter Lin
 * 
 * Divide will divide one or more numbers and return a Double value
 */
public class Divide implements Function, Serializable {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Returns the value of the first argument divided by each of the subsequent arguments. This function can be called with the identifier 'divide' or '/'.";
		}

		public int getParameterCount() {
			return 2;
		}

		public String getParameterDescription(int parameter) {
			if(parameter == 0) {
				return "Numeric value which is divided by the following arguments.";
			}
			return "Numeric values which divide the first argument or the result of the previous divisions respectively.";
		}

		public String getParameterName(int parameter) {
			if(parameter == 0) {
				return "dividend";
			}
			return "divisor";
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
			return "(/ 27 3 -4.5)";
		}

		public boolean isResultAutoGeneratable() {
			return true;
		}
	}

	private static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "divide";

	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	public String getName() {
		return NAME;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		if (params != null) {
			if (params.length > 0) {
				boolean isDouble = false;
				for (int idx = 0; idx < params.length; idx++) {
					if (params[idx].getValue(engine).getType().equals(
							JamochaType.DOUBLE)) {
						isDouble = true;
						break;
					}
				}
				if (isDouble) {
					double result = params[0].getValue(engine).implicitCast(
							JamochaType.DOUBLE).getDoubleValue();
					for (int i = 1; i < params.length; ++i) {
						result /= params[i].getValue(engine).implicitCast(
								JamochaType.DOUBLE).getDoubleValue();
					}
					return JamochaValue.newDouble(result);
				} else {
					long result = params[0].getValue(engine).implicitCast(
							JamochaType.LONG).getLongValue();
					for (int i = 1; i < params.length; ++i) {
						result /= params[i].getValue(engine).implicitCast(
								JamochaType.LONG).getLongValue();
					}
					return JamochaValue.newLong(result);
				}
			}
		}
		throw new IllegalParameterException(1, true);
	}
}
