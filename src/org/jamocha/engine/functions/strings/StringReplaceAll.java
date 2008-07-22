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
 * @author Alexander Wilden
 * 
 * Replaces the all substrings of the target string, given as first argument,
 * that match the regular expression given as second argument with the
 * replacement from the third argument.
 */
public class StringReplaceAll extends AbstractFunction {
	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Replaces the all substrings of the target string, given as first argument, "
					+ "that match the regular expression given as second argument with the replacement "
					+ "from the third argument.";
		}

		public int getParameterCount() {
			return 3;
		}

		public String getParameterDescription(int parameter) {
			switch (parameter) {
			case 0:
				return "String where the replacement takes place.";
			case 1:
				return "Substring to search for (as regular expression).";
			case 2:
				return "Replacement string.";
			}
			return "";
		}

		public String getParameterName(int parameter) {
			switch (parameter) {
			case 0:
				return "targetString";
			case 1:
				return "searchString";
			case 2:
				return "replString";
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
			return "(str-replace-all \"Jamocha\" \"m[^io]ch\" \"maic\")";
		}

		public boolean isResultAutoGeneratable() {
			return true;
		}

		public Object getExpectedResult() {
			return "Jamocha";
		}
	}

	public static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "str-replace-all";

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
		String retstr = null;
		if (params != null && params.length == 3) {
			String txt = params[0].getValue(engine).getStringValue();
			String regx = params[1].getValue(engine).getStringValue();
			String repl = params[2].getValue(engine).getStringValue();
			retstr = txt.replaceAll(regx, repl);
		} else
			throw new IllegalParameterException(3);
		return JamochaValue.newString(retstr);
	}
}
