/*
 * Copyright 2007 Christoph Emonds
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.jamocha.org/
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
import org.jamocha.rete.ValueParam;

/**
 * @author Christoph Emonds
 * 
 * Functional equivalent of (apply <function name> <function parameters>*) in JESS.
 */
public class ApplyFunction implements Function, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String NAME = "apply";

	/**
	 * 
	 */
	public ApplyFunction() {
		super();
	}

	public JamochaType getReturnType() {
		return JamochaType.UNDEFINED;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		JamochaValue result;
		if (params != null && params.length >= 1) {
			String functionName = params[0].getValue(engine).getStringValue();
			Function function = engine.findFunction(functionName);
			if(function == null) {
				throw new EvaluationException("Error function "+functionName+" could not be found.");
			}
			Parameter[] functionParams = new Parameter[params.length-1];
			System.arraycopy(params, 1, functionParams, 0, functionParams.length);
			result = function.executeFunction(engine, functionParams);
		} else {
			throw new IllegalParameterException(1);
		}
		return result;
	}

	public String getName() {
		return NAME;
	}

	public Class[] getParameter() {
		return new Class[] { ValueParam.class };
	}

	public String toPPString(Parameter[] params, int indents) {
		if (params != null && params.length > 0) {
			StringBuffer buf = new StringBuffer();
			buf.append("(apply");
			for (int idx = 0; idx < params.length; idx++) {
				buf.append(' ').append(params[idx].getParameterString());
			}
			buf.append(")");
			return buf.toString();
		} else {
			return "(apply <function name> <function parameter>*)\n" + "Command description:\n"
					+ "\tCalls the function <function name> with the <function parameters> .";
		}
	}
}
