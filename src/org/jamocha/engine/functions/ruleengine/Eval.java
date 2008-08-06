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
import org.jamocha.communication.logging.Logging;
import org.jamocha.engine.BoundParam;
import org.jamocha.engine.Parameter;
import org.jamocha.engine.Engine;
import org.jamocha.engine.functions.AbstractFunction;
import org.jamocha.engine.functions.FunctionDescription;

/**
 * @author Sebastian Reinartz, Alexander Wilden
 * 
 * Evaluates the string given in the first argument as though it was entered at
 * the command prompt and returns the last result of the expression(s) (if any).
 * An optional binding as second argument can be used to catch an exception and
 * hold the error message.
 */
public class Eval extends AbstractFunction {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Evaluates the string given in the first argument as though it was entered at the command "
					+ "prompt and returns the last result of the expression(s) (if any). An optional binding as "
					+ "second argument can be used to catch an exception and hold the error message.";
		}

		public int getParameterCount() {
			return 2;
		}

		public String getParameterDescription(int parameter) {
			switch (parameter) {
			case 0:
				return "One or more commands in one string.";
			case 1:
				return "Optional binding to hold the error message if an exception occurres.";
			}
			return "";
		}

		public String getParameterName(int parameter) {
			switch (parameter) {
			case 0:
				return "command";
			case 1:
				return "errorBinding";
			}
			return "";
		}

		public JamochaType[] getParameterTypes(int parameter) {
			switch (parameter) {
			case 0:
				return JamochaType.STRINGS;
			case 1:
				return JamochaType.IDENTIFIERS;
			}
			return JamochaType.NONE;
		}

		public JamochaType[] getReturnType() {
			return JamochaType.ANY;
		}

		public boolean isParameterCountFixed() {
			return false;
		}

		public boolean isParameterOptional(int parameter) {
			switch (parameter) {
			case 0:
				return false;
			case 1:
				return true;
			}
			return true;
		}

		public String getExample() {
			return "(eval \"(printout t BlackJack)\" ?catchErr)\n"
					+ "(eval \"(printout t \\\"This is BlackJack.\\\")\")\n\n"
					+ "(bind ?x \"(+ 17 4)\")\n" + "(eval ?x)";
		}

		public boolean isResultAutoGeneratable() {
			return true;
		}

		public Object getExpectedResult() {
			return 21;
		}
	}

	public static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "eval";

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
		JamochaValue result = JamochaValue.NIL;
		if (params != null && params.length >= 1) {
			String command = params[0].getValue(engine).getStringValue();
			Logging.logger(this.getClass()).debug("evaluating '"+command+"'");
			String bindName = null;
			// if an additionall Binding is provided we reset it
			if (params.length > 1 && params[1] instanceof BoundParam) {
				bindName = ((BoundParam) params[1]).getVariableName();
				engine.setBinding(bindName, JamochaValue.NIL);
			}
			try {
				result = eval(engine, command);
				Logging.logger(this.getClass()).debug("evaluated successfully'");
			} catch (EvaluationException e) {
				Logging.logger(this.getClass()).debug("evaluation error: "+e.getMessage());
				// if an additionall Binding is provided we set the error
				// message in it.
				if (bindName != null)
					engine.setBinding(bindName, JamochaValue.newString(e
							.getMessage()));
				else
					throw e;
			}
		} else
			throw new IllegalParameterException(1);
		return result;
	}

	public JamochaValue eval(Engine engine, String command)
			throws EvaluationException {
		JamochaValue result = null;
		try {
			Parser parser = ParserFactory.getParser(new StringReader(command));
			Expression expr = null;
			while ((expr = parser.nextExpression()) != null)
				result = expr.getValue(engine);
		} catch (ParseException e) {
			throw new EvaluationException(e);
		}
		return result;
	}
}