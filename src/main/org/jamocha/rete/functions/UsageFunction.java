/*
 * Copyright 2006 Karl-Heinz Krempels
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

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ValueParam;

/**
 * @author Karl-Heinz Krempels
 * 
 * @return a short usage for a function name passed as argument.
 * 
 * @param the
 *            name of a function.
 */
public class UsageFunction implements Function, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String USAGE = "usage";

	/**
	 * 
	 */
	public UsageFunction() {
		super();
	}

	public JamochaType getReturnType() {
		return JamochaType.STRING;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		JamochaValue result = JamochaValue.newString(this
				.toPPString(null, 0));
		if (params != null && params.length == 1) {
			JamochaValue firstParam = params[0].getValue(engine);
			String function = firstParam.getStringValue();
			Function aFunction = engine.findFunction(function);
			if (aFunction != null) {
				result = JamochaValue.newString(aFunction
						.toPPString(null, 0));
			}
		}
		return result;
	}

	public String getName() {
		return USAGE;
	}

	public Class[] getParameter() {
		return new Class[] { ValueParam[].class };
	}

	public String toPPString(Parameter[] params, int indents) {
		if (params != null && params.length >= 0) {
			StringBuffer buf = new StringBuffer();

			buf.append("(usage ");
			int idx = 0;
			buf.append(" ").append(params[idx].getParameterString());
			buf.append(")");
			return buf.toString();
		} else {
			return "(usage <function-name>)\n"
					+ "Function description:\n"
					+ "\tPrint a short description of <function-name>.\n"
					+ "\tPlease use the command \"functions\" to get a list of all functions.";
		}
	}
}
