/*
 * Copyright 2006 Nikolaus Koemm, Christoph Emonds
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
import org.jamocha.parser.IllegalTypeException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.functions.FunctionDescription;

/**
 * @author Nikolaus Koemm
 * @author Christoph Emonds
 * 
 * Min returns the smallest of one or more values.
 */
public class Min implements Function, Serializable {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "The min function returns the value of its smallest numeric argument. Each of its arguments should be a numeric expression. When necessary, integers are temporarily converted to floats for comparison. The return value will either be integer or float (depending upon the type of the smallest argument).";
		}

		public int getParameterCount() {
			return 1;
		}

		public String getParameterDescription(int parameter) {
			return "Numbers (at least one) whose minimum will be returned.";
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
	}

	private static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "min";

	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	public String getName() {
		return NAME;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		if (params != null && params.length > 0) {
			JamochaValue result = params[0].getValue(engine);
			for (int idx = 1; idx < params.length; idx++) {
				JamochaValue value = params[idx].getValue(engine);
				if (result.is(JamochaType.DOUBLE)) {
					if (result.getDoubleValue() > value.implicitCast(
							JamochaType.DOUBLE).getDoubleValue()) {
						result = value;
					}
				} else if (result.is(JamochaType.LONG)) {
					if (result.getLongValue() > value.implicitCast(
							JamochaType.LONG).getLongValue()) {
						result = value;
					}
				} else {
					throw new IllegalTypeException(JamochaType.NUMBERS, result
							.getType());
				}
			}
			return result;
		}
		throw new IllegalParameterException(1, true);
	}
}
