/*
 * Copyright 2002-2006 Peter Lin, 2007 Uta Christoph
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://ruleml-dev.sourceforge.net/
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
 * LessOrEqual will compare 2 or more numeric values and return true if the
 * (n-1)th value is less or equal to the nth value.
 */
public class LessOrEqual implements Function, Serializable {

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
	}

	private static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "lessOrEqual";

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
				JamochaValue current, previous = params[0].getValue(engine);
				boolean isDouble = (previous.is(JamochaType.DOUBLE));
				for (int i = 1; i < params.length; ++i) {
					current = params[i].getValue(engine);
					if (current.is(JamochaType.DOUBLE)) {
						isDouble = true;
					}

					if (isDouble) {
						if (current.getDoubleValue() < previous
								.getDoubleValue()) {
							return JamochaValue.FALSE;
						}
					} else {
						if (current.getLongValue() < previous.getLongValue()) {
							return JamochaValue.FALSE;
						}
					}
					previous = current;
				}
				return JamochaValue.TRUE;
			}
		}
		throw new IllegalParameterException(1, true);
	}
}
