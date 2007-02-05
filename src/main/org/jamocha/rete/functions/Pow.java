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
 * Returns the value of the first argument raised to the power of the following
 * arguments.
 */
public class Pow implements Function, Serializable {

	private static final long serialVersionUID = 1L;

	public static final String POW = "pow";

	/**
	 * 
	 */
	public Pow() {
		super();
	}

	public JamochaType getReturnType() {
		return JamochaType.DOUBLE;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		if (params != null) {
			if (params.length > 0) {
				boolean isDouble = false;
				for (int idx = 0; idx < params.length; idx++) {
					if (params[idx].getValue(engine).getType().equals(
							JamochaType.DOUBLE)) {
						isDouble = true;
						break;
					}
				}
				double result = params[0].getValue(engine).implicitCast(
						JamochaType.DOUBLE).getDoubleValue();
				if (isDouble) {
					for (int i = 1; i < params.length; ++i) {
						result = Math.pow(result, params[i].getValue(engine)
								.implicitCast(JamochaType.DOUBLE)
								.getDoubleValue());
					}
				} else {
					for (int i = 1; i < params.length; ++i) {
						result = Math.pow(result, params[i].getValue(engine)
								.implicitCast(JamochaType.LONG).getLongValue());
					}
				}
				return JamochaValue.newDouble(result);
			}
		}
		throw new IllegalParameterException(1, true);
	}

	public String getName() {
		return POW;
	}

	public Class[] getParameter() {
		return new Class[] { ValueParam[].class };
	}

	public String toPPString(Parameter[] params, int indents) {
		if (params != null && params.length >= 0) {
			StringBuffer buf = new StringBuffer();
			buf.append("(pow");
			int idx = 0;
			if (params[idx] instanceof BoundParam) {
				BoundParam bp = (BoundParam) params[idx];
				buf.append(" ?" + bp.getVariableName());
			} else if (params[idx] instanceof ValueParam) {
				buf.append(" " + params[idx].getParameterString());
			} else {
				buf.append(" " + params[idx].getParameterString());
			}
			buf.append(")");
			return buf.toString();
		} else {
			return "(pow (<literal> | <binding>)+)\n"
					+ "Function description:\n"
					+ "\tRaises its first argument to the power of the\n"
					+ "\tfollowing arguments.";
		}
	}
}
