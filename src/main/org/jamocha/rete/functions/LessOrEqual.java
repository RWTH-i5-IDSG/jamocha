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
 * LessOrEqual will compare 2 or more numeric values and return true if the
 * (n-1)th value is less or equal to the nth value.
 */
public class LessOrEqual implements Function, Serializable {

	private static final long serialVersionUID = 1L;

	public static final String LESSOREQUAL = "lessOrEqual";

	/**
	 * 
	 */
	public LessOrEqual() {
		super();
	}

	public JamochaType getReturnType() {
		return JamochaType.BOOLEAN;
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
				if (isDouble) {
					double left = params[0].getValue(engine).implicitCast(
							JamochaType.DOUBLE).getDoubleValue();
					double right;
					for (int i = 1; i < params.length; ++i) {
						right = params[i].getValue(engine).implicitCast(
								JamochaType.DOUBLE).getDoubleValue();
						if (right < left) {
							return new JamochaValue(JamochaType.BOOLEAN, false);
						}
						left = right;
					}
				} else {
					long left = params[0].getValue(engine).implicitCast(
							JamochaType.LONG).getLongValue();
					long right;
					for (int i = 1; i < params.length; ++i) {
						right = params[i].getValue(engine).implicitCast(
								JamochaType.LONG).getLongValue();
						if (right < left) {
							return new JamochaValue(JamochaType.BOOLEAN, false);
						}
						left = right;
					}
				}
				return new JamochaValue(JamochaType.BOOLEAN, true);
			}
		}
		throw new IllegalParameterException(1, true);
	}

	public String getName() {
		return LESSOREQUAL;
	}

	public Class[] getParameter() {
		return new Class[] { ValueParam.class, ValueParam.class };
	}

	public String toPPString(Parameter[] params, int indents) {
		if (params != null && params.length > 0) {
			StringBuffer buf = new StringBuffer();
			buf.append("(<=");
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
			return "(<= (<literal> | <binding>)+)\n"
					+ "Function description:\n"
					+ "\t Returns the symbol TRUE if for all its arguments, "
					+ "argument \n \t n-1 is less or equal than argument n";
		}
	}
}
