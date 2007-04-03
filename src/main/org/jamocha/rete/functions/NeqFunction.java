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
 */
public class NeqFunction implements Function, Serializable {

	private static final long serialVersionUID = 1L;

	public static final String NAME = "neq";

	/**
	 * 
	 */
	public NeqFunction() {
		super();
	}

	public JamochaType getReturnType() {
		return JamochaType.BOOLEAN;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		JamochaValue result = JamochaValue.TRUE;
		if (params != null && params.length > 1) {
			JamochaValue first = params[0].getValue(engine);
			for (int idx = 1; idx < params.length; idx++) {
				JamochaValue right = params[idx].getValue(engine);
				if (first.equals(right)) {
					result = JamochaValue.FALSE;
					break;
				}
			}
		} else {
			throw new IllegalParameterException(1, true);
		}
		return result;
	}

	public String getName() {
		return NAME;
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(neq (<literal> | <binding>)+)\n"
				+ "Function description:\n"
				+ "\tCompares a literal value against one or more"
				+ "bindings. \n\tIf all of the bindings are equal to the constant value,"
				+ "\n\tthe function returns true.";
	}

}
