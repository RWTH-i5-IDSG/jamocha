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
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;

/**
 * @author Peter Lin Any equal is used to compare a literal value against one or
 *         more bindings. If any of the bindings is equal to the constant value,
 *         the function returns true.
 */
public class AnyEqFunction implements Function, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String ANYEQUAL = "any-eq";

	/**
	 * 
	 */
	public AnyEqFunction() {
		super();
	}

	public JamochaType getReturnType() {
		return JamochaType.BOOLEAN;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		JamochaValue result = JamochaValue.FALSE;
		if (params != null && params.length > 1) {
			JamochaValue constant = params[0].getValue(engine);
			for (int idx = 1; idx < params.length; idx++) {
				if (constant.equals(params[idx].getValue(engine))) {
					result = JamochaValue.FALSE;
					break;
				}
			}
		} else {
			throw new IllegalParameterException(1);
		}
		return result;
	}

	public String getName() {
		return ANYEQUAL;
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(any-eq (<literal> | <binding>)+)\n"
				+ "Function description:\n"
				+ "\tCompares a literal value against one or more"
				+ "bindings. \n\tIf any of the bindings is equal to the constant value,"
				+ "\n\tthe function returns true.";
	}

}
