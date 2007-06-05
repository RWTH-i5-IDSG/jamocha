/*
 * Copyright 2006 Nikolaus Koemm, 2007 Uta Christoph
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
 * @author Nikolaus Koemm
 * 
 * Mod returns
 */
public class Mod implements Function, Serializable {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Returns the remainder of an integer division.";
		}

		public int getParameterCount() {
			return 2;
		}
	
		public String getParameterDescription(int parameter) {
			if(parameter == 0) {
				return "Numeric value which is divided by the following argument.";
			}
			return "Numeric value which divides the first argument.";
		}

		public String getParameterName(int parameter) {
			if (parameter == 0) {
				return "dividend";
			} else if (parameter == 1) {
				return "divisor";
			}
			return null;
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
			return "(mod 17 3)";
		}
	}

	private static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "mod";

	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	public String getName() {
		return NAME;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		if (params != null) {
			if (params.length == 2) {
				boolean isDouble = false;
				for (int idx = 0; idx < params.length; idx++) {
					if (params[idx].getValue(engine).getType().equals(
							JamochaType.DOUBLE)) {
						isDouble = true;
						break;
					}
				}
				if (isDouble) {
					double first = params[0].getValue(engine).implicitCast(
							JamochaType.DOUBLE).getDoubleValue();
					double second = params[1].getValue(engine).implicitCast(
							JamochaType.DOUBLE).getDoubleValue();
					return JamochaValue.newDouble((first % second));
				} else {
					long first = params[0].getValue(engine).implicitCast(
							JamochaType.LONG).getLongValue();
					long second = params[1].getValue(engine).implicitCast(
							JamochaType.LONG).getLongValue();
					return JamochaValue.newLong((first % second));
				}
			}
		}
		throw new IllegalParameterException(1, true);
	}
}
