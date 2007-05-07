/*
 * Copyright 2002-2006 Peter Lin, 2007 Alexander Wilden
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
 * The sub-string function will retrieve a portion of a string from another
 * string.
 */
public class SubString implements Function, Serializable {

	private static final class Description implements
			FunctionDescription {

		public String getDescription() {
			return "The sub-string function will retrieve a portion of a string from another string.";
		}

		public int getParameterCount() {
			return 3;
		}

		public String getParameterDescription(int parameter) {
			switch (parameter) {
			case 0:
				return "Start index of the Substring.";
			case 1:
				return "End index of the Substring.";
			case 2:
				return "The String to work on.";
			}
			return "";
		}

		public String getParameterName(int parameter) {
			switch (parameter) {
			case 0:
				return "beginIndex";
			case 1:
				return "endIndex";
			case 2:
				return "string";
			}
			return "";
		}

		public JamochaType[] getParameterTypes(int parameter) {
			switch (parameter) {
			case 0:
			case 1:
				return JamochaType.LONGS;
			case 2:
				return JamochaType.STRINGS;
			}
			return null;
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
			// TODO Auto-generated method stub
			return null;
		}
	}

	private static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "sub-string";

	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	public String getName() {
		return NAME;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		String sub = null;
		if (params != null && params.length == 3) {
			long begin = params[0].getValue(engine).getLongValue();
			long end = params[1].getValue(engine).getLongValue();
			String txt = params[2].getValue(engine).getStringValue();
			sub = txt.substring((int) begin, (int) end);
		} else {
			throw new IllegalParameterException(3);
		}
		return JamochaValue.newString(sub);
	}

}