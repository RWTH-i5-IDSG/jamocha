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
 * @author Peter Lin
 * 
 * Add will add one or more numbers and return the result.
 */
public class Add implements Function, Serializable {

	private static final long serialVersionUID = 1L;

	public static final String ADD = "add";

	/**
	 * 
	 */
	public Add() {
		super();
	}

	public JamochaType getReturnType() {
		return JamochaType.UNDEFINED;
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
				if(isDouble) {
					double result = 0.0;
					for (int i=0; i<params.length; ++i) {
						result += params[i].getValue(engine).implicitCast(JamochaType.DOUBLE).getDoubleValue();
					}
					return JamochaValue.newDouble(result);
				}
				else {
					long result = 0;
					for (int i=0; i<params.length; ++i) {
						result += params[i].getValue(engine).implicitCast(JamochaType.LONG).getLongValue();
					}
					return JamochaValue.newLong(result);
				}
			}
		}
		throw new IllegalParameterException(1,true);
	}

	public String getName() {
		return ADD;
	}

	public Class[] getParameter() {
		return new Class[] { ValueParam[].class };
	}

	public String toPPString(Parameter[] params, int indents) {
		if (params != null && params.length > 0) {
			StringBuffer buf = new StringBuffer();
			buf.append("(+");
			for (int idx = 0; idx < params.length; idx++) {
				if (params[idx] instanceof BoundParam) {
					BoundParam bp = (BoundParam) params[idx];
					buf.append(" ?" + bp.getVariableName());
				} else if (params[idx] instanceof ValueParam) {
					buf.append(" " + params[idx].getExpressionString());
				} else {
					buf.append(" " + params[idx].getExpressionString());
				}
			}
			buf.append(")");
			return buf.toString();
		} else {
			return "(+ (<literal> | <binding>)+)\n" + "Function description:\n"
					+ "\tCalculates the sum of the arguments.";
		}
	}
}
