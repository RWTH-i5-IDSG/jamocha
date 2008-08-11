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

package org.jamocha.engine.functions.ruleengine;

import org.jamocha.engine.Engine;
import org.jamocha.engine.ExpressionSequence;
import org.jamocha.engine.Parameter;
import org.jamocha.engine.configurations.DeffunctionConfiguration;
import org.jamocha.engine.functions.AbstractFunction;
import org.jamocha.engine.functions.FunctionDescription;
import org.jamocha.engine.functions.InterpretedFunction;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;

/**
 * @author Peter Lin
 * 
 * Used to declare new functions in the shell. It is different from a function
 * written in Java. Deffunctions run interpreted and are mapped to existing
 * functions. Returns true if the function could be declared. Returns false if
 * the function could not be declared or already existed.
 */
public class Deffunction extends AbstractFunction {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Used to declare new functions in the shell. Deffunctions run interpreted and are mapped to "
					+ "existing functions. Returns true if the function could be declared. Returns false if the function"
					+ "could not be declared or already existed."
					+ "Deffunction is used for functions that are declared in the shell. It is different than a function written in java. Deffunctions run interpreted and are mapped to existing functions. Returns true if the Function could be declared and false if not or if it already existed.";
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

		public String getExample() {
			return "(deffunction minute-hand \"Returns the minutes of the actual time.\" () (printout t \"The minute hand is pointing to \" (getminutes (now)) \" right now.\"))\n"
					+ "(minute-hand)\n\n"
					+ "(deffunction is-hello (?x1) (eq \"hello\" ?x1))";
		}

		public boolean isResultAutoGeneratable() {
			return true;
		}

		public Object getExpectedResult() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "deffunction";

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

			InterpretedFunction intrfunc = new InterpretedFunction(
					functionName, description, functionParameters, functionList);
			if (dc.definesFunctionGroup())
				engine.getFunctionMemory().declareFunction(intrfunc,
						dc.getFunctionGroup());
			else
				engine.getFunctionMemory().declareFunction(intrfunc);
			result = JamochaValue.TRUE;

		} else
			throw new EvaluationException();

		return result;
	}
}