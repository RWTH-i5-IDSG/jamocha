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

package org.jamocha.engine.functions.help;

import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

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
import org.jamocha.engine.functions.AbstractFunction;
import org.jamocha.engine.functions.Function;
import org.jamocha.engine.functions.FunctionDescription;
import org.jamocha.engine.functions.FunctionNotFoundException;

/**
 * @author Alexander Wilden
 * 
 * Prints out an example for the given function and executes it in the rule
 * engine. Note: Don't use this function in your production environment as
 * templates, rules and facts might be added, removed or modified.
 */
public class Example extends AbstractFunction {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Prints out an example for the function given as argument and executes it in the rule engine. Note: Don't use this function in your production environment as templates, rules and facts might be added, removed or modified.";
		}

		public int getParameterCount() {
			return 1;
		}

		public String getParameterDescription(int parameter) {
			return "Name of the function to show an example for.";
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
			return "(example add)";
		}

		public boolean isResultAutoGeneratable() {
			return true;
		}

		public Object getExpectedResult() {
			return null;
		}
	}

	private static final long serialVersionUID = 1L;

	public static final FunctionDescription DESCRIPTION = new Description();

	public static final String NAME = "example";

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
		if (params != null && params.length == 1) {
			JamochaValue firstParam = params[0].getValue(engine);
			String function = firstParam.getStringValue();
			Function aFunction;
			try {
				aFunction = engine.getFunctionMemory().findFunction(function);
			} catch (FunctionNotFoundException e) {
				return JamochaValue.NIL;
			}
			if (aFunction != null) {
				String example = aFunction.getDescription().getExample();
				if (example != null) {
					engine
							.writeMessage("Showing example for " + function
									+ ":");
					engine.writeMessage("Code:\n" + example);
					engine
							.writeMessage("-------- START OF EXAMPLE --------------------------");
					engine.writeMessage(" ");
					List<JamochaValue> result = eval(engine, example);
					if (!result.isEmpty())
						for (JamochaValue value : result) {
							engine.writeMessage(ParserFactory
									.getFormatter(true).visit(value));
							engine.writeMessage(" ");
						}
					engine
							.writeMessage("-------- END OF EXAMPLE ----------------------------");
					engine.writeMessage("Example done!");
				} else
					engine.writeMessage("Sorry, but " + function
							+ " provides no example.");
			} else
				engine.writeMessage("The Function " + function
						+ " doesn't exist.");
		} else
			throw new IllegalParameterException(1);
		return JamochaValue.NIL;
	}

	public List<JamochaValue> eval(Engine engine, String command)
			throws EvaluationException {
		List<JamochaValue> result = new LinkedList<JamochaValue>();
		try {
			Parser parser = ParserFactory.getParser(new StringReader(command));
			Expression expr = null;
			while ((expr = parser.nextExpression()) != null)
				result.add(expr.getValue(engine));
		} catch (ParseException e) {
			throw new EvaluationException(e);
		}
		return result;
	}
}