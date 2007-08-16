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

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.IllegalParameterException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.functions.AbstractFunction;
import org.jamocha.rete.functions.FunctionDescription;

/**
 * @author Peter Lin
 * 
 * The + function returns the sum of its arguments. Each of its arguments should
 * be a numeric expression. Addition is performed using the type of the
 * arguments provided unless mixed mode arguments (long and double) are used. In
 * this case, the function return value and long arguments are converted to
 * doubles after the first double argument has been encountered. This function
 * returns a double if any of its arguments is a double, otherwise it returns an
 * long.
 */
public class Add extends AbstractFunction {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Returns the sum of its numeric arguments. This function can be called with the identifier 'add' or '+'. Each argument has to be a numeric expression. The return type depends on the types of the arguments, it returns a double if any of its arguments is a double, otherwise it returns a long.";
		}

		public int getParameterCount() {
			return 1;
		}

		public String getParameterDescription(int parameter) {
			return "Numeric values (at least one) to be added up.";
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
			return "(+ 7 8.56 -4.3)";
		}

		public boolean isResultAutoGeneratable() {
			return true;
		}
	}

	private static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "add";

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
					double result = 0.0;
					for (int i = 0; i < params.length; ++i) {
						result += params[i].getValue(engine).implicitCast(
								JamochaType.DOUBLE).getDoubleValue();
					}
					return JamochaValue.newDouble(result);
				} else {
					long result = 0;
					for (int i = 0; i < params.length; ++i) {
						result += params[i].getValue(engine).implicitCast(
								JamochaType.LONG).getLongValue();
					}
					return JamochaValue.newLong(result);
				}
			}
		}
		throw new IllegalParameterException(1, true);
	}
}
