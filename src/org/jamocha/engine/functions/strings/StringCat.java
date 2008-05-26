/*
 * Copyright 2002-2008 Peter Lin & The Jamocha Team
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

import org.jamocha.Constants;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.IllegalParameterException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.engine.Parameter;
import org.jamocha.engine.Engine;
import org.jamocha.engine.functions.AbstractFunction;
import org.jamocha.engine.functions.FunctionDescription;

/**
 * @author Alexander Wilden
 * 
 * This function concatenates two or more strings into one string.
 */
public class StringCat extends AbstractFunction {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Concatenates two or more strings into one string and returns the new string.";
		}

		public int getParameterCount() {
			return 1;
		}

		public String getParameterDescription(int parameter) {
			return "String to be concatenated with the strings from the other arguments.";
		}

		public String getParameterName(int parameter) {
			return "string";
		}

		public JamochaType[] getParameterTypes(int parameter) {
			return JamochaType.STRINGS;
		}

		public JamochaType[] getReturnType() {
			return JamochaType.STRINGS;
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
			return "(str-cat \"Jamocha \" \"rulez\" \"!!!\")";
		}

		public boolean isResultAutoGeneratable() {
			return true;
		}

		public Object getExpectedResult() {
			return "Jamocha rulez!!!";
		}
	}

	public static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "str-cat";

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
		StringBuilder txt = new StringBuilder();
		if (params != null)
			for (Parameter param : params) {
				JamochaValue value = param.getValue(engine);
				if (value.getType().equals(JamochaType.IDENTIFIER)
						&& value.getIdentifierValue().equals(Constants.CRLF))
					txt.append("\n");
				else if (value.getType().equals(JamochaType.STRING))
					txt.append(value.getStringValue());
				else
					txt.append(value.toString());
			}
		else
			throw new IllegalParameterException(1, true);
		return JamochaValue.newString(txt.toString());
	}
}
