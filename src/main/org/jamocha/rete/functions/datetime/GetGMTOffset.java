/*
 * Copyright 2007 Josef Alexander Hahn, Alexander Wilden, Uta Christoph
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

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.IllegalParameterException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.functions.AbstractFunction;
import org.jamocha.rete.functions.FunctionDescription;

/**
 * @author Josef Alexander Hahn
 * 
 * Returns the GMT-Offset from the given DateTime-Object.
 */
public class GetGMTOffset extends AbstractFunction {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Returns the GMT Offset-field from the given DateTime argument.";
		}

		public int getParameterCount() {
			return 1;
		}

		public String getParameterDescription(int parameter) {
			return "DateTime to get the GMT Offset from.";
		}

		public String getParameterName(int parameter) {
			return "datetime";
		}

		public JamochaType[] getParameterTypes(int parameter) {
			return JamochaType.DATETIMES;
		}

		public JamochaType[] getReturnType() {
			return JamochaType.LONGS;
		}

		public boolean isParameterCountFixed() {
			return true;
		}

		public boolean isParameterOptional(int parameter) {
			return false;
		}

		public String getExample() {
			return "(getgmtoffset 2007-06-04 17:14:06+5)";
		}

		public boolean isResultAutoGeneratable() {
			return true;
		}
	}

	private static final long serialVersionUID = 1L;

	public static final FunctionDescription DESCRIPTION = new Description();

	public static final String NAME = "getgmtoffset";

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
				GregorianCalendar p1 = params[0].getValue(engine)
						.getDateValue();
				return JamochaValue.newLong(p1.get(Calendar.ZONE_OFFSET)
						/ (1000 * 60 * 60));
			}
		}
		throw new IllegalParameterException(1, false);
	}
}
