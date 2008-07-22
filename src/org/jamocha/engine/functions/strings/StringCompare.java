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
 * Compares two strings lexicographically. The comparison is based on the
 * Unicode value of each character in the strings. The character sequence of the
 * first argument is compared to the character sequence of the second argument.
 * Returns a negative integer if the first string lexicographically precedes the
 * second string. Returns a positive integer if the first string
 * lexicographically follows the second string. Returns 0 if the strings are
 * equal.
 * 
 * (Definition of lexicographic ordering: Two strings are different, when they
 * have either different characters at some index, or their lengths differ, or
 * both. If they have different characters at one or more index positions, then
 * the string whose character at the smallest differing index position k has the
 * smaller value, as determined by using the < operator, lexicographically
 * precedes the other string. If there is no index position at which they
 * differ, then the shorter string lexicographically precedes the longer
 * string.)
 */
public class StringCompare extends AbstractFunction {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Compares two strings lexicographically. The comparison is based on the Unicode value of each "
					+ "character in the strings. "
					+ "The character sequence of the first argument is compared to the character sequence of the"
					+ "second argument. Returns a negative integer if the first string lexicographically precedes "
					+ "the second string. Returns a positive integer if the first string lexicographically follows "
					+ "the second string. Returns 0 if the strings are equal.\n\n"
					+ "(Definition of lexicographic ordering: Two strings are different, when they have either"
					+ "different characters at some index, or their lengths differ, or both. If they have different "
					+ "characters at one or more index positions, then the string whose character at the smallest "
					+ "differing index position k has the smaller value, as determined by using the < operator, "
					+ "lexicographically precedes the other string. If there is no index position at which they "
					+ "differ, then the shorter string lexicographically precedes the longer string.) ";
		}

		public int getParameterCount() {
			return 2;
		}

		public String getParameterDescription(int parameter) {
			switch (parameter) {
			case 0:
				return "String to compare to the second string.";
			case 1:
				return "String to compare to the first string.";
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
			return JamochaType.LONGS;
		}

		public boolean isParameterCountFixed() {
			return true;
		}

		public boolean isParameterOptional(int parameter) {
			return false;
		}

		public String getExample() {
			return "(str-compare \"Jamocha\" \"Jamocha\")\n"
					+ "(str-compare \"Jamocha\" \"Jamocho\")\n"
					+ "(str-compare \"Jamocha\" \"Jamicha\")\n"
					+ "(str-compare \"Jamocha\" \"Jamochaaaa\")\n"
					+ "(str-compare \"Jamochaaaa\" \"Jamocha\")";
		}

		public boolean isResultAutoGeneratable() {
			return true;
		}

		public Object getExpectedResult() {
			return "Jamochaaaa".compareTo("Jamocha");
		}
	}

	public static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "str-compare";

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
		int eq = -1;
		if (params != null && params.length == 2) {
			String val = params[0].getValue(engine).getStringValue();
			String val2 = params[1].getValue(engine).getStringValue();
			eq = val.compareTo(val2);
		} else
			throw new IllegalParameterException(2);
		return JamochaValue.newLong(eq);
	}

}
