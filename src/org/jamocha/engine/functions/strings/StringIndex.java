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

package org.jamocha.engine.functions.strings;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.IllegalParameterException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.engine.Parameter;
import org.jamocha.engine.Engine;
import org.jamocha.engine.functions.AbstractFunction;
import org.jamocha.engine.functions.FunctionDescription;

/**
 * @author Peter Lin
 * 
 * Returns the position index of the first occurrence of the second string
 * inside the first string. This function is case sensitive. Returns -1 if no
 * such substring is found in the first argument.
 * 
 * Index positions in a string start with 0.
 */
public class StringIndex extends AbstractFunction {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Returns the position index of the first occurrence of the second string inside the first string. "
					+ "This function is case sensitive. Returns -1 if no such substring is found in the first argument.\n"
					+ "Index positions in a string start with 0.";
		}

		public int getParameterCount() {
			return 2;
		}

		public String getParameterDescription(int parameter) {
			switch (parameter) {
			case 0:
				return "String to search in.";
			case 1:
				return "Substring to search for.";
			}
			return "";
		}

		public String getParameterName(int parameter) {
			switch (parameter) {
			case 0:
				return "firstString";
			case 1:
				return "secondString";
			}
			return "";
		}

		public JamochaType[] getParameterTypes(int parameter) {
			return JamochaType.STRINGS;
		}

		public JamochaType[] getReturnType() {
			return JamochaType.STRINGS;
		}

		public boolean isParameterCountFixed() {
			return true;
		}

		public boolean isParameterOptional(int parameter) {
			return false;
		}

		public String getExample() {
			return "(str-index \"Jamocha\" \"Jam\")\n"
					+ "(str-index \"Jamocha\" \"Mocha\")\n"
					+ "(str-index \"Jamocha\" \"mocha\")\n";
		}

		public boolean isResultAutoGeneratable() {
			return true;
		}

		public Object getExpectedResult() {
			return 2;
		}

	}

	public static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "str-index";

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
		int index = -1;
		if (params != null && params.length == 2) {
			String val = params[0].getValue(engine).getStringValue();
			String pt = params[1].getValue(engine).getStringValue();
			index = val.indexOf(pt);
		} else
			throw new IllegalParameterException(2);
		return JamochaValue.newLong(index);
	}
}
