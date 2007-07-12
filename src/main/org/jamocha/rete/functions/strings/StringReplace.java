/*
 * Copyright 2002-2006 Peter Lin, 2007 Alexander Wilden, Uta Christoph
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
package org.jamocha.rete.functions.strings;

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
 * Replaces the first substring of the target string, given as first argument, 
 * that matches the regular expression given as second argument with the replacement 
 * from the third argument.
 */
public class StringReplace implements Function, Serializable {
	private static final class Description implements
			FunctionDescription {

		public String getDescription() {
			return "Replaces the first substring of the target string, given as first argument, " +
					"that matches the regular expression given as second argument with the replacement from the" +
					"third argument.";
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
			return "(str-replace \"Jamocha\" \"m[^io]ch\" \"maic\")";
		}

		public boolean isResultAutoGeneratable() {
			// TODO Auto-generated method stub
			return false;
		}
	}

	private static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "str-replace";

	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	public String getName() {
		return NAME;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		String retstr = null;
		if (params != null && params.length == 3) {
			String txt = params[0].getValue(engine).getStringValue();
			String regx = params[1].getValue(engine).getStringValue();
			String repl = params[2].getValue(engine).getStringValue();
			retstr = txt.replaceFirst(regx, repl);
		} else {
			throw new IllegalParameterException(3);
		}
		return JamochaValue.newString(retstr);
	}
}
