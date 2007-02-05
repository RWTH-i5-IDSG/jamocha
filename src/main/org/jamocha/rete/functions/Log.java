/*
 * Copyright 2006 Nikolaus Koemm
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
 * @author Nikolaus Koemm
 * 
 * Log returns the natural logarithm of a double value.
 */
public class Log implements Function, Serializable {

	private static final long serialVersionUID = 1L;

	public static final String LOG = "log";

	/**
	 * 
	 */
	public Log() {
		super();
	}

	public JamochaType getReturnType() {
		return JamochaType.DOUBLE;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		if (params != null) {
			if (params.length == 1) {
				JamochaValue value = params[0].getValue(engine);
				if (!value.getType().equals(JamochaType.DOUBLE)
						&& !value.getType().equals(JamochaType.LONG)) {
					value = value.implicitCast(JamochaType.DOUBLE);
				}
				if (value.getType().equals(JamochaType.DOUBLE)) {
					return JamochaValue.newDouble(Math.log(value
							.getDoubleValue()));
				} else if (value.getType().equals(JamochaType.LONG)) {
					return JamochaValue.newDouble(Math.log(value
							.getLongValue()));
				}
			}
		}
		throw new IllegalParameterException(1);
	}

	public String getName() {
		return LOG;
	}

	public Class[] getParameter() {
		return new Class[] { ValueParam[].class };
	}

	public String toPPString(Parameter[] params, int indents) {
		if (params != null && params.length >= 0) {
			StringBuffer buf = new StringBuffer();
			buf.append("(log");
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
			return "(log <expression>)\n" + "Function description:\n"
					+ "\tCalculates the natural logarithm of its argument.";
		}
	}
}
