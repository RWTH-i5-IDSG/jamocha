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
import java.util.Calendar;

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
 * @author Josef Alexander Hahn
 * 
 */
public class Between implements Function, Serializable {

	private static final long serialVersionUID = 1L;

	public static final String BETWEEN = "between";

	/**
	 * 
	 */
	public Between() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see woolfel.engine.rete.Function#getReturnType()
	 */
	public JamochaType getReturnType() {
		return JamochaType.BOOLEAN;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		if (params != null) {
			if (params.length > 2) {
				long p1 = params[0].getValue(engine).getDateValue().getTimeInMillis();
				long p2 = params[1].getValue(engine).getDateValue().getTimeInMillis();
				long p3 = params[2].getValue(engine).getDateValue().getTimeInMillis();
				if (p1 <= p2 && p2 <= p3) return JamochaValue.newBoolean(true);
				return JamochaValue.newBoolean(false);
			}
		}
		throw new IllegalParameterException(3, false);
	}

	public String getName() {
		return BETWEEN;
	}

	public Class[] getParameter() {
		return new Class[] { ValueParam.class, ValueParam.class, ValueParam.class };
	}

	public String toPPString(Parameter[] params, int indents) {
		if (params != null && params.length > 0) {
			StringBuffer buf = new StringBuffer();
			buf.append("(>");
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
			return "(> (<literal> | <binding>)+)\n" + "Function description:\n"
					+ "\t Returns the symbol TRUE if the three given dates are" +
					"in chronological order";
		}
	}
}
