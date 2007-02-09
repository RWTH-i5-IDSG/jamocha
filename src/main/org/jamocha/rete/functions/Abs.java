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
import org.jamocha.rete.BoundParam;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ValueParam;

/**
 * @author Nikolaus Koemm, Christian Ebert
 * 
 * Returns the absolute value of a double value.
 */
public class Abs implements Function, Serializable {

	private static final long serialVersionUID = 1L;
	
	public static final String ABS = "abs";

	/**
	 * 
	 */
	public Abs() {
		super();
	}

	public JamochaType getReturnType() {
		return JamochaType.UNDEFINED;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		JamochaValue result = JamochaValue.NIL;
		if (params != null) {
			if (params.length == 1) {
				JamochaValue value = params[0].getValue(engine);
				if (!value.getType().equals(JamochaType.DOUBLE)
						&& !value.getType().equals(JamochaType.LONG)) {
					value = value.implicitCast(JamochaType.DOUBLE);
				}
				if (value.getType().equals(JamochaType.DOUBLE)) {
					result = JamochaValue.newDouble(Math
							.abs(value.getDoubleValue()));
				} else if (value.getType().equals(JamochaType.LONG)) {
					result = JamochaValue.newLong(Math.abs(value
							.getLongValue()));
				}
				return result;
			}
		}
		throw new IllegalParameterException(1);
	}

	public String getName() {
		return ABS;
	}

	public Class[] getParameter() {
		return new Class[] { ValueParam[].class };
	}

	public String toPPString(Parameter[] params, int indents) {
		if (params != null && params.length >= 0) {
			StringBuffer buf = new StringBuffer();
			buf.append("(abs");
			int idx = 0;
			if (params[idx] instanceof BoundParam) {
				BoundParam bp = (BoundParam) params[idx];
				buf.append(" ?" + bp.getVariableName());
			} else if (params[idx] instanceof ValueParam) {
				buf.append(" " + params[idx].getExpressionString());
			} else {
				buf.append(" " + params[idx].getExpressionString());
			}
			buf.append(")");
			return buf.toString();
		} else {
			return "(abs <literal> | <binding>)\n"
					+ "Function description:\n"
					+ "\tCalculates the absolute value of the numeric argument.";
		}
	}
}
