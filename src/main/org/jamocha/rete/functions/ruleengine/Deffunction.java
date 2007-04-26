/*
 * Copyright 2002-2007 Peter Lin, 2007 Alexander Wilden
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
package org.jamocha.rete.functions.ruleengine;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.ExpressionSequence;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.configurations.DeffunctionConfiguration;
import org.jamocha.rete.functions.FunctionDescription;
import org.jamocha.rete.functions.InterpretedFunction;

/**
 * @author Peter Lin
 * 
 * Deffunction is used for functions that are declared in the shell. It is
 * different than a function written in java. Deffunctions run interpreted and
 * are mapped to existing functions. Returns true if the Function could be
 * declared and false if not or if it already existed.
 */
public class Deffunction implements Function {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Deffunction is used for functions that are declared in the shell. It is different than a function written in java. Deffunctions run interpreted and are mapped to existing functions. Returns true if the Function could be declared and false if not or if it already existed.";
		}

		public int getParameterCount() {
			return 4;
		}

		public String getParameterDescription(int parameter) {
			switch (parameter) {
			case 0:
				return "Name of the new function.";
			case 1:
				return "Optional Description of the new Function.";
			case 2:
				return "Parameters for the Function.";
			case 3:
				return "Actions for the Function.";
			}
			return "";
		}

		public String getParameterName(int parameter) {
			switch (parameter) {
			case 0:
				return "functionName";
			case 1:
				return "description";
			case 2:
				return "parameters";
			case 3:
				return "actions";
			}
			return "";
		}

		public JamochaType[] getParameterTypes(int parameter) {
			switch (parameter) {
			case 0:
				return JamochaType.IDENTIFIERS;
			case 1:
				return JamochaType.STRINGS;
			case 2:
				return JamochaType.IDENTIFIERS;
			case 3:
				return JamochaType.ANY;
			}
			return JamochaType.NONE;
		}

		public JamochaType[] getReturnType() {
			return JamochaType.BOOLEANS;
		}

		public boolean isParameterCountFixed() {
			return false;
		}

		public boolean isParameterOptional(int parameter) {
			switch (parameter) {
			case 0:
			case 2:
			case 3:
				return false;
			}
			return true;

		}
	}

	private static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "deffunction";

	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	public String getName() {
		return NAME;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		JamochaValue result = JamochaValue.FALSE;

		String functionName = null;
		String description = "";
		Parameter[] functionParameters = null;
		ExpressionSequence functionList = null;

		// get all the Deffunction configuration from params
		if (params[0] instanceof DeffunctionConfiguration) {
			DeffunctionConfiguration dc = (DeffunctionConfiguration) params[0];
			functionName = dc.getFunctionName();
			description = dc.getFunctionDescription();
			functionParameters = dc.getParams();
			functionList = dc.getActions();
		} else {

			JamochaValue firstParam = params[0].getValue(engine);
			String name = firstParam.getIdentifierValue();
			if (engine.findFunction(name) == null) {
				int paramIndex = 1;
				description = "";
				JamochaValue secondParam = params[1].getValue(engine);
				// If the second parameter is a String we have a description for
				// the
				// Deffunction.
				if (secondParam.getType().equals(JamochaType.STRING)) {
					paramIndex++;
					description = secondParam.getStringValue();
					secondParam = params[paramIndex].getValue(engine);
				}
				functionParameters = (Parameter[]) secondParam.getObjectValue();
				paramIndex++;
				if (params[paramIndex] instanceof ExpressionSequence) {
					functionList = (ExpressionSequence) params[2];
				}
			}
		}
		InterpretedFunction intrfunc = new InterpretedFunction(functionName,
				description, functionParameters, functionList);
		engine.declareFunction(intrfunc);
		result = JamochaValue.TRUE;

		return result;
	}
}