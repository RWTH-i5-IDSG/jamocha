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
		JamochaValue result = JamochaValue.NIL;
		if (params != null) {
			if (params.length > 0) {
				JamochaType type = JamochaType.LONG;
				for (int idx = 0; idx < params.length; idx++) {
					if (params[idx].getValue(engine).getType().equals(
							JamochaType.DOUBLE)) {
						type = JamochaType.DOUBLE;
						break;
					}
				}
				result = new JamochaValue(type, 0);
				for (int idx = 0; idx < params.length; idx++) {
					JamochaValue value = params[idx].getValue(engine);
					if (value.getType().equals(JamochaType.DOUBLE)) {
						result = new JamochaValue(type, (result
								.getDoubleValue() + value.getDoubleValue()));
					} else if (value.getType().equals(JamochaType.LONG)) {
						if (type.equals(JamochaType.LONG)) {
							result = new JamochaValue(type, (result
									.getLongValue() + value.getLongValue()));
						} else {
							result = new JamochaValue(type, (result
									.getDoubleValue() + value.getLongValue()));
						}
					} else {
						if (type.equals(JamochaType.LONG)) {
							result = new JamochaValue(type, (result
									.getLongValue() + value.implicitCast(type).getLongValue()));
						} else {
							result = new JamochaValue(type, (result
									.getDoubleValue() + value.implicitCast(type).getDoubleValue()));
						}
					}
				}
				return result;
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
					buf.append(" " + params[idx].getParameterString());
				} else {
					buf.append(" " + params[idx].getParameterString());
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
