/*
 * Copyright 2007 Alexander Wilden
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
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.functions.FunctionDescription;

/**
 * @author Alexander Wilden
 * 
 * Prints out an example for this function and executes it in the rule engine.
 * Note: Don't use this function in your production environment as templates,
 * rules and facts might be added, removed or modified.
 */
public class Example implements Function, Serializable {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Prints out an example for this function and executes it in the rule engine. Note: Don't use this function in your production environment as templates, rules and facts might be added, removed or modified.";
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
			// TODO Auto-generated method stub
			return null;
		}
	}

	private static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "example";

	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	public String getName() {
		return NAME;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		if (params != null && params.length == 1) {
			JamochaValue firstParam = params[0].getValue(engine);
			String function = firstParam.getStringValue();
			Function aFunction = engine.getFunctionMemory().findFunction(function);
			if (aFunction != null) {
				String example = aFunction.getDescription().getExample();
				if (example != null) {
					engine
							.writeMessage("Showing example for " + function
									+ ":");
					engine.writeMessage("Code: " + example);
					engine
							.writeMessage("-------- START OF EXAMPLE --------------------------");
					engine.writeMessage(" ");
					List<JamochaValue> result = eval(engine, example);
					if (!result.isEmpty()) {
						for (JamochaValue value : result) {
							engine
									.writeMessage(ParserFactory.getFormatter(
											true).formatExpression(value));
							engine.writeMessage(" ");
						}
					}
					engine
							.writeMessage("-------- END OF EXAMPLE ----------------------------");
					engine.writeMessage("Example done!");
				} else {
					engine.writeMessage("Sorry, but " + function
							+ " provides no example.");
				}
			} else {
				engine.writeMessage("The Function " + function
						+ " doesn't exist.");
			}
		} else
			throw new IllegalParameterException(1);
		return JamochaValue.NIL;
	}

	public List<JamochaValue> eval(Rete engine, String command)
			throws EvaluationException {
		List<JamochaValue> result = new LinkedList<JamochaValue>();
		try {
			Parser parser = ParserFactory.getParser(new StringReader(command));
			Expression expr = null;
			while ((expr = parser.nextExpression()) != null) {
				result.add(expr.getValue(engine));
			}
		} catch (ParseException e) {
			throw new EvaluationException(e);
		}
		return result;
	}
}