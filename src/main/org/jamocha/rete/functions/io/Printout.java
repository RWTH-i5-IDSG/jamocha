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
package org.jamocha.rete.functions.io;

import java.io.Serializable;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Constants;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.functions.FunctionDescription;

/**
 * @author Peter Lin
 * 
 * Writes any number and type of arguments to the channel specified in the first argument. 
 * 't' indicates to send the output to the standard output device of the computer. Generally,
 * this is the terminal. Printout concatenates all arguments after the channel without separation marks.
 * A string, enclosed in double quotes, is handled as one single argument.
 */
public class Printout implements Function, Serializable {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Writes any number and type of arguments to the channel specified in the first argument. " +
					"'t' indicates to send the output to the standard output device of the computer. Generally, " +
					"this is the terminal. Printout concatenates all arguments after the channel without separation marks. " +
					"A string, enclosed in double quotes, is handled as one single argument.";
		}

		public int getParameterCount() {
			return 1;
		}

		public String getParameterDescription(int parameter) {
			return "Any value to print out.";
		}

		public String getParameterName(int parameter) {
			return "value";
		}

		public JamochaType[] getParameterTypes(int parameter) {
			return JamochaType.ANY;
		}

		public JamochaType[] getReturnType() {
			return JamochaType.NONE;
		}

		public boolean isParameterCountFixed() {
			return false;
		}

		public boolean isParameterOptional(int parameter) {
			return (parameter > 0);
		}

		public String getExample() {			
			return "(printout t \"Jamocha rul\" e z !)";
		}
	}

	private static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "printout";

	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	public String getName() {
		return NAME;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		JamochaValue result = JamochaValue.NIL;
		// print out some stuff
		if (params.length > 0) {
			String outputType = params[0].getValue(engine).getStringValue();
			JamochaValue value = null;
			StringBuilder outputString = new StringBuilder();
			String str;
			for (int idx = 1; idx < params.length; idx++) {
				value = params[idx].getValue(engine);
				if (value != null) {
					str = value.toString();
					if (str.equalsIgnoreCase(Constants.CRLF)) {
						outputString.append(Constants.LINEBREAK);
					} else
						outputString.append(str);
				} else {
					outputString.append(Constants.NIL_STRING);
				}
			}
			engine.writeMessage(outputString.toString(), outputType);
		}
		return result;
	}

}
