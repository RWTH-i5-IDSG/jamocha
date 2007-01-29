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
 * Definstance will assert an object instance using Rete.assert(Object).
 */
public class DefinstanceFunction implements Function, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String DEFINSTANCE = "definstance";

	public DefinstanceFunction() {
		super();
	}

	public JamochaType getReturnType() {
		return JamochaType.BOOLEAN;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		JamochaValue result = JamochaValue.FALSE;
		if (params != null && params.length >= 1) {

			JamochaValue firstParam = params[0].getValue(engine);
			String template = "";
			if (params.length == 2) {
				template = params[1].getValue(engine).implicitCast(
						JamochaType.IDENTIFIER).getIdentifierValue();
			}
			engine.assertObject(firstParam.getObjectValue(), template, false,
					true);
			result = JamochaValue.TRUE;
		} else {
			throw new IllegalParameterException(1, true);
		}
		return result;
	}

	public String getName() {
		return DEFINSTANCE;
	}

	/**
	 * The function expects a single BoundParam that is an object binding
	 */
	public Class[] getParameter() {
		return new Class[] { BoundParam.class, ValueParam.class };
	}

	public String toPPString(Parameter[] params, int indents) {
		if (params != null && params.length > 0) {
			StringBuffer buf = new StringBuffer();
			return buf.toString();
		} else {
			return "(definstance )";
		}
	}
}
