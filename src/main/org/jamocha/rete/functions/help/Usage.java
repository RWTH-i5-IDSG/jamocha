/*
 * Copyright 2006 Karl-Heinz Krempels, 2007 Alexander Wilden
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
package org.jamocha.rete.functions.help;

import java.io.Serializable;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.parser.ParserFactory;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.functions.FunctionDescription;

/**
 * @author Karl-Heinz Krempels
 * 
 * Prints out a short usage for a function name passed as argument. If no
 * argument is passed the usage of this function itself is printed.
 */
public class Usage implements Function, Serializable {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Prints out a short usage for a function name passed as argument. If no argument is passed the usage of this usage function itself is printed.";
		}

		public int getParameterCount() {
			return 1;
		}

		public String getParameterDescription(int parameter) {
			return "Name of the function to show the usage of.";
		}

		public String getParameterName(int parameter) {
			return "function";
		}

		public JamochaType[] getParameterTypes(int parameter) {
			return JamochaType.IDENTIFIERS;
		}

		public JamochaType[] getReturnType() {
			return JamochaType.NONE;
		}

		public boolean isParameterCountFixed() {
			return false;
		}

		public boolean isParameterOptional(int parameter) {
			return true;
		}

		public String getExample() {
			return "(usage)\n\n" +
					"(usage member$)";
		}

		public boolean isResultAutoGeneratable() {
			return true;
		}
	}

	private static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "usage";

	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	public String getName() {
		return NAME;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		JamochaValue result = JamochaValue.newString(ParserFactory
				.getFormatter().formatFunction(this));
		if (params != null && params.length == 1) {
			JamochaValue firstParam = params[0].getValue(engine);
			String function = firstParam.getStringValue();
			Function aFunction = engine.getFunctionMemory().findFunction(function);
			if (aFunction != null) {
				result = JamochaValue.newString(ParserFactory.getFormatter(true)
						.formatFunction(aFunction));
			}
		}
		return result;
	}
}