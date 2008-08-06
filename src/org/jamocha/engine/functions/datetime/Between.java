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

package org.jamocha.engine.functions.datetime;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.IllegalParameterException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.engine.Parameter;
import org.jamocha.engine.Engine;
import org.jamocha.engine.functions.AbstractFunction;
import org.jamocha.engine.functions.FunctionDescription;

/**
 * @author Josef Alexander Hahn
 * 
 * Returns TRUE if the given dates are in increasing chronological order.
 */
public class Between extends AbstractFunction {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Returns true if the given DateTime arguments are in increasing chronological order.";
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
			return parameter > 0;
		}

		public String getExample() {
			return "(between 2007-06-04 16:01:21 2007-07-07 19:07:49 (now))";
		}

		public boolean isResultAutoGeneratable() {
			return true;
		}

		public Object getExpectedResult() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	private static final long serialVersionUID = 1L;

	public static final FunctionDescription DESCRIPTION = new Description();

	public static final String NAME = "between";

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
		if (params != null)
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
		throw new IllegalParameterException(2, true);
	}
}