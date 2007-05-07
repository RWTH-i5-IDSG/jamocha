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
import java.util.GregorianCalendar;
import java.util.TimeZone;

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
 * Returns the DateTime-Object from the given Timestamp.
 */
public class Timestamp2Datetime implements Function, Serializable {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Returns the DateTime-Object from the given Timestamp.";
		}

		public int getParameterCount() {
			return 1;
		}

		public String getParameterDescription(int parameter) {
			return "Timestamp to generate the Datetime-Object from.";
		}

		public String getParameterName(int parameter) {
			return "timestamp";
		}

		public JamochaType[] getParameterTypes(int parameter) {
			return JamochaType.LONGS;
		}

		public JamochaType[] getReturnType() {
			return JamochaType.DATETIMES;
		}

		public boolean isParameterCountFixed() {
			return true;
		}

		public boolean isParameterOptional(int parameter) {
			return false;
		}

		public String getExample() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	private static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "timestamp2datetime";

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

				long p1 = params[0].getValue(engine).getLongValue();
				GregorianCalendar foo = new GregorianCalendar();
				foo.setTimeZone(TimeZone.getTimeZone("UTC"));
				foo.setTimeInMillis(p1);
				return JamochaValue.newDate(foo);

			}
		}
		throw new IllegalParameterException(1, false);
	}
}
