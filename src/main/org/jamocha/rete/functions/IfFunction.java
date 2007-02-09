/*
 * Copyright 2006 Nikolaus Koemm, Christian Ebert 
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
import org.jamocha.parser.IllegalParameterException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ValueParam;

/**
 * @author Christoph Emonds, Alexander Wilden, Sebastian Reinartz
 * 
 * A conditional
 */
public class IfFunction implements Function, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String IF = "if";

	/**
	 * 
	 */
	public IfFunction() {
		super();
	}

	public JamochaType getReturnType() {
		return JamochaType.UNDEFINED;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		JamochaValue result = null;
		if (params != null && params.length >= 3) {
			JamochaValue condition = params[0].getValue(engine);
			boolean conditionValue = condition.getBooleanValue();
			if (!params[1].getExpressionString().equals("then")) {
				throw new EvaluationException("Error, expected then, found "
						+ params[1].getExpressionString());
			}
			boolean elseExpressions = false;
			for (int i = 2; i < params.length; ++i) {
				if (params[i].getExpressionString().equals("else")) {
					elseExpressions = true;
				} else {
					if ((conditionValue && !elseExpressions)
							|| (!conditionValue && elseExpressions)) {
						result = params[i].getValue(engine);
					}
				}
			}
		} else {
			throw new IllegalParameterException(3, true);
		}
		return result;
	}

	public String getName() {
		return IF;
	}

	public Class[] getParameter() {
		return new Class[] { ValueParam[].class };
	}

	public String toPPString(Parameter[] params, int indents) {
		if (params != null && params.length >= 0) {
			StringBuffer buf = new StringBuffer();
			buf.append("(if");
			int idx = 0;
				buf.append(" ").append(params[idx].getExpressionString());
			buf.append(")");
			return buf.toString();
		} else {
			return "(if <boolean expression> then <expression>+ [else <expression>+])\n"
					+ "Function description:\n"
					+ "\tExecutes the expressions after then if the boolean expressions evaluates to true, otherwise it executes the expressions after the optional else.";
		}
	}
}
