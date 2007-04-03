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
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;

/**
 * @author Nikolaus Koemm
 * 
 * Mod returns
 */
public class Mod implements Function, Serializable {

	private static final long serialVersionUID = 1L;

	public static final String NAME = "mod";

	/**
	 * 
	 */
	public Mod() {
		super();
	}

	public JamochaType getReturnType() {
		return JamochaType.UNDEFINED;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		if (params != null) {
			if (params.length == 2) {
				boolean isDouble = false;
				for (int idx = 0; idx < params.length; idx++) {
					if (params[idx].getValue(engine).getType().equals(
							JamochaType.DOUBLE)) {
						isDouble = true;
						break;
					}
				}
				if (isDouble) {
					double first = params[0].getValue(engine).implicitCast(
							JamochaType.DOUBLE).getDoubleValue();
					double second = params[1].getValue(engine).implicitCast(
							JamochaType.DOUBLE).getDoubleValue();
					return JamochaValue.newDouble((first % second));
				} else {
					long first = params[0].getValue(engine).implicitCast(
							JamochaType.LONG).getLongValue();
					long second = params[1].getValue(engine).implicitCast(
							JamochaType.LONG).getLongValue();
					return JamochaValue.newLong((first % second));
				}
			}
		}
		throw new IllegalParameterException(1, true);
	}

	public String getName() {
		return NAME;
	}

	public String toPPString(Parameter[] params, int indents) {
		if (params != null && params.length > 0) {
			StringBuffer buf = new StringBuffer();
			buf.append("(mod");
			for (int idx = 0; idx < params.length; idx++) {
				buf.append(" " + params[idx].getExpressionString());

			}
			buf.append(")");
			return buf.toString();
		} else {
			return "(mod (<literal> | <binding>) (<literal> | <binding>))\n"
					+ "Function description:\n"
					+ "\tReturns the remainder of the result of dividing\n"
					+ "\tits first argument by its second argument.";
		}
	}
}
