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

import java.io.StringReader;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.Expression;
import org.jamocha.parser.IllegalParameterException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.parser.ParseException;
import org.jamocha.parser.Parser;
import org.jamocha.parser.ParserFactory;
import org.jamocha.engine.Parameter;
import org.jamocha.engine.Engine;
import org.jamocha.engine.configurations.Signature;
import org.jamocha.engine.functions.AbstractFunction;
import org.jamocha.engine.functions.FunctionDescription;
import org.jamocha.engine.functions.FunctionNotFoundException;

/**
 * @author Alexander Wilden
 * 
 * This funcion takes a CLIPS string as argument and uses the default parser to
 * parse it. If the first statement is a function call, the FunctionMemory is
 * checked wether a Function with this name exists. If it does it returns true
 * otherwise (or if the first statement is no function call) it returns false.
 */
public class FunctionExists extends AbstractFunction {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Takes a CLIPS string as argument and uses the default parser to parse it. If the first statement "
					+ "is a function call, the FunctionMemory is checked wether a function with this name exists. If it "
					+ "does it returns true otherwise (or if the first statement is no function call) it returns false.";
		}

		public int getParameterCount() {
			return 1;
		}

		public String getParameterDescription(int parameter) {
			return "One command as one single string.";
		}

		public String getParameterName(int parameter) {
			return "command";
		}

		public JamochaType[] getParameterTypes(int parameter) {
			return JamochaType.STRINGS;
		}

		public JamochaType[] getReturnType() {
			return JamochaType.ANY;
		}

		public boolean isParameterCountFixed() {
			return true;
		}

		public boolean isParameterOptional(int parameter) {
			return false;
		}

		public String getExample() {
			return "(function-exists \"(cos)\")\n"
					+ "(function-exists \"(i-dont-exist)\")";
		}

		public boolean isResultAutoGeneratable() {
			return true;
		}

		public Object getExpectedResult() {
			return JamochaValue.FALSE;
		}
	}

	public static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "function-exists";

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
		JamochaValue result;
		if (params != null && params.length == 1) {
			String command = params[0].getValue(engine).getStringValue();
			result = checkFunctionCall(engine, command);
		} else
			throw new IllegalParameterException(1);
		return result;
	}

	public JamochaValue checkFunctionCall(Engine engine, String command)
			throws EvaluationException {
		JamochaValue result = JamochaValue.FALSE;
		try {
			Parser parser = ParserFactory.getParser(new StringReader(command));
			Expression expr = parser.nextExpression();
			if (expr instanceof Signature) {
				Signature sig = (Signature) expr;
				try {
					engine.getFunctionMemory().findFunction(
							sig.getSignatureName());
					result = JamochaValue.TRUE;
				} catch (FunctionNotFoundException e) {
				}
			}
			// let the Parser parse the rest to let it come to an end (is it
			// neccessary??)
			while ((expr = parser.nextExpression()) != null) {
			}
		} catch (ParseException e) {
			throw new EvaluationException(e);
		}
		return result;
	}
}
