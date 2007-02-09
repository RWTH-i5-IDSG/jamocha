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
import org.jamocha.rete.ValueParam;

/**
 * @author Peter Lin
 * 
 */
public class DefclassFunction implements Function, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	 
	public static final String DEFCLASS = "defclass";

	public DefclassFunction() {
		super();
	}

	public JamochaType getReturnType() {
		return JamochaType.BOOLEAN;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		JamochaValue result = JamochaValue.FALSE;
		if (params.length >= 0) {
			String clazz = params[0].getValue(engine).implicitCast(
					JamochaType.IDENTIFIER).getIdentifierValue();
			String template = null;
			if (params[1] != null) {
				template = params[1].getValue(engine).implicitCast(
						JamochaType.IDENTIFIER).getIdentifierValue();
			}
			String parent = null;
			if (params.length == 3) {
				parent = params[2].getValue(engine).implicitCast(
						JamochaType.IDENTIFIER).getIdentifierValue();
			}
			try {
				engine.declareObject(clazz, template, parent);
				result = JamochaValue.TRUE;
			} catch (ClassNotFoundException e) {
			}
		} else {
			throw new IllegalParameterException(1);
		}
		return result;
	}

	public String getName() {
		return DEFCLASS;
	}

	/**
	 * defclass function expects 3 parameters. (defclass classname,
	 * templatename, parenttemplate) parent template name is optional.
	 */
	public Class[] getParameter() {
		return new Class[] { ValueParam.class, ValueParam.class,
				ValueParam.class };
	}

	public String toPPString(Parameter[] params, int indents) {
		if (params != null && params.length > 0) {
			StringBuffer buf = new StringBuffer();
			buf.append("(defclass");
			for (int idx = 0; idx < params.length; idx++) {
				buf.append(" ").append(params[idx].getExpressionString());
			}
			buf.append(")");
			return buf.toString();
		} else {
			return "(defclass [new classname] [template] [parent template])";
		}
	}
}
