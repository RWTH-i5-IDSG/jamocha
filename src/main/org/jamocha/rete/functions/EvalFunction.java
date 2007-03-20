/*
 * Copyright 2002-2006 Peter Lin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://ruleml-dev.sourceforge.net/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.jamocha.rete.functions;

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
import org.jamocha.rete.BoundParam;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ValueParam;

/**
 * @author Sebastian Reinartz
 * 
 * Functional equivalent of (eval "(+ 1 3)") in CLIPS and JESS.
 */
public class EvalFunction implements Function, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String EVAL = "eval";

	/**
	 * 
	 */
	public EvalFunction() {
		super();
	}

	public JamochaType getReturnType() {
		return JamochaType.UNDEFINED;
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

	public String getName() {
		return EVAL;
	}

	public Class[] getParameter() {
		return new Class[] { ValueParam.class };
	}

	public String toPPString(Parameter[] params, int indents) {
		if (params != null && params.length > 0) {
			StringBuffer buf = new StringBuffer();
			buf.append("(eval");
			for (int idx = 0; idx < params.length; idx++) {
				if (params[idx] instanceof BoundParam) {
					BoundParam bp = (BoundParam) params[idx];
					buf.append(" ?").append(bp.getVariableName());
				} else if (params[idx] instanceof ValueParam) {
					buf.append(" \"").append(params[idx].getExpressionString())
							.append("\"");
				}
			}
			buf.append(")");
			return buf.toString();
		} else {
			return "(eval <string expressions>)\n" + "Command description:\n"
					+ "\tEvaluates the content of a string.";
		}
	}
}
