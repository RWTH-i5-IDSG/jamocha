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
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.IllegalParameterException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;

/**
 * @author Josef Alexander Hahn
 * 
 */
public class Timestamp2Datetime implements Function, Serializable {

	private static final long serialVersionUID = 1L;

	public static final String NAME = "timestamp2datetime";

	/**
	 * 
	 */
	public Timestamp2Datetime() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see woolfel.engine.rete.Function#getReturnType()
	 */
	public JamochaType getReturnType() {
		return JamochaType.DATETIME;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		if (params != null) {
			if (params.length == 1) {
				
				long p1 = params[0]  .getValue(engine).getLongValue();
				GregorianCalendar foo = new GregorianCalendar();
				foo.setTimeZone(TimeZone.getTimeZone("UTC"));
				foo.setTimeInMillis(p1);
				return JamochaValue.newDate(foo);
				
			}
		}
		throw new IllegalParameterException(1, false);
	}

	public String getName() {
		return NAME;
	}

	public String toPPString(Parameter[] params, int indents) {
		if (params != null && params.length == 3) {
			StringBuffer buf = new StringBuffer();
			buf.append("(timestamp2datetime");
			for (int idx = 0; idx < params.length; idx++) {
				buf.append(" ").append(params[idx].getExpressionString());
			}
			buf.append(")");
			return buf.toString();
		} else {
			return "(timestamp2datetime (<long> | <binding>) )\n" 
					+ "Function description:\n"
					+ "\t Returns the DateTime-Object from the"
					+ "given Timestamp";
		}
	}
}
