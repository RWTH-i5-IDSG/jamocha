/*
 * Copyright 2002-2006 Peter Lin, 2007 Alexander Wilden
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
package org.jamocha.rete.functions.compare;

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
 * Less will compare 2 or more numeric values and return true if the (n-1)th
 * value is less than the nth.
 */
public class Less implements Function, Serializable {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Less will compare 2 or more numeric values and return true if the (n-1)th value is less than the nth.";
		}

		public int getParameterCount() {
			return 2;
		}

		public String getParameterDescription(int parameter) {
			return "Number that will be compared to the other Parameters.";
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
	}

	private static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "less";

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
					double left = params[0].getValue(engine).implicitCast(
							JamochaType.DOUBLE).getDoubleValue();
					double right;
					for (int i = 1; i < params.length; ++i) {
						right = params[i].getValue(engine).implicitCast(
								JamochaType.DOUBLE).getDoubleValue();
						if (right <= left) {
							return JamochaValue.newBoolean(false);
						}
						left = right;
					}
				} else {
					long left = params[0].getValue(engine).implicitCast(
							JamochaType.LONG).getLongValue();
					long right;
					for (int i = 1; i < params.length; ++i) {
						right = params[i].getValue(engine).implicitCast(
								JamochaType.LONG).getLongValue();
						if (right <= left) {
							return JamochaValue.newBoolean(false);
						}
						left = right;
					}
				}
				return JamochaValue.newBoolean(true);
			}
		}
		throw new IllegalParameterException(1, true);
	}
}
