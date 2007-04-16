/*
 * Copyright 2007 Josef Alexander Hahn, Alexander Wilden
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
package org.jamocha.rete.functions.datetime;

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
 * @author Josef Alexander Hahn
 * 
 * Returns TRUE if the given dates are in increasing chronological order.
 */
public class Between implements Function, Serializable {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Returns TRUE if the given dates are in increasing chronological order.";
		}

		public int getParameterCount() {
			return 1;
		}

		public String getParameterDescription(int parameter) {
			return "DateTime to compare to the other parameters.";
		}

		public String getParameterName(int parameter) {
			return "datetime";
		}

		public JamochaType[] getParameterTypes(int parameter) {
			return JamochaType.DATETIMES;
		}

		public JamochaType[] getReturnType() {
			return JamochaType.BOOLEANS;
		}

		public boolean isParameterCountFixed() {
			return false;
		}

		public boolean isParameterOptional(int parameter) {
			return (parameter > 0);
		}
	}

	private static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "between";

	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	public String getName() {
		return NAME;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		if (params != null) {
			if (params.length >= 1) {

				for (int i = 0; i < params.length - 1; i++) {
					long p1 = params[i].getValue(engine).getDateValue()
							.getTimeInMillis();
					long p2 = params[i + 1].getValue(engine).getDateValue()
							.getTimeInMillis();
					if (p1 > p2)
						return JamochaValue.FALSE;
				}
				return JamochaValue.TRUE;
			}
		}
		throw new IllegalParameterException(2, true);
	}
}