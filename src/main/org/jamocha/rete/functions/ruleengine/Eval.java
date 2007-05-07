/*
 * Copyright 2007 Sebastian Reinartz, Alexander Wilden
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

import java.io.Serializable;
import java.io.StringReader;

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
 * @author Sebastian Reinartz, Alexander Wilden
 * 
 * The eval function evaluates the string as though it were entered at the
 * command prompt and returns the last result of the Expression(s) (if any).
 */
public class Eval implements Function, Serializable {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "The eval function evaluates the string as though it were entered at the command prompt and returns the last result of the Expression(s) (if any).";
		}

		public int getParameterCount() {
			return 1;
		}

		public String getParameterDescription(int parameter) {
			return "One or more Commands as one single String.";
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
			// TODO Auto-generated method stub
			return null;
		}
	}

	private static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "eval";

	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	public String getName() {
		return NAME;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		JamochaValue result;
		if (params != null && params.length == 1) {
			String command = params[0].getValue(engine).getStringValue();
			result = eval(engine, command);
		} else {
			throw new IllegalParameterException(1);
		}
		return result;
	}

	public JamochaValue eval(Rete engine, String command)
			throws EvaluationException {
		JamochaValue result = null;
		try {
			Parser parser = ParserFactory.getParser(new StringReader(command));
			Expression expr = null;
			while ((expr = parser.nextExpression()) != null) {
				result = expr.getValue(engine);
			}
		} catch (ParseException e) {
			throw new EvaluationException(e);
		}
		return result;
	}
}