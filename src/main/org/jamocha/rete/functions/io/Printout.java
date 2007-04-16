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
package org.jamocha.rete.functions.io;

import java.io.Serializable;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Constants;
import org.jamocha.rete.Fact;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.functions.FunctionDescription;

/**
 * @author Peter Lin
 * 
 * Prints out any number and type of parameters. Returns nothing.
 */
public class Printout implements Function, Serializable {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Prints out any number and type of parameters. Returns nothing.";
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
		// print out some stuff
		if (params.length > 0) {
			String output = params[0].getValue(engine).getStringValue();
			for (int idx = 1; idx < params.length; idx++) {
				JamochaValue value = params[idx].getValue(engine);
				if (value.getType().equals(JamochaType.IDENTIFIER)
						&& value.getIdentifierValue().equals(Constants.CRLF)) {
					engine.writeMessage(Constants.LINEBREAK, output);
				} else {
					engine.writeMessage(value.toString(), output);
				}
			}
		}
		// there's nothing to return, so just return a new DefaultReturnVector
		return JamochaValue.NIL;
	}

	public void writeArray(Object[] arry, Rete engine, String output,
			boolean linebreak) {
		for (int idz = 0; idz < arry.length; idz++) {
			Object val = arry[idz];
			if (val instanceof Fact) {
				Fact f = (Fact) val;
				engine.writeMessage(f.toFactString() + " ", output);
			} else {
				engine.writeMessage(arry[idz].toString() + " ", output);
			}
			if (linebreak) {
				engine.writeMessage(Constants.LINEBREAK, output);
			}
		}
	}
}
