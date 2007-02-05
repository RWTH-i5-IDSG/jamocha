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
public class StringLengthFunction implements Function, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String STRING_LENGTH = "str-length";
	
	/**
	 * 
	 */
	public StringLengthFunction() {
		super();
	}

	public JamochaType getReturnType() {
		return JamochaType.LONG;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params) throws EvaluationException {
		int len;
		if (params != null && params.length == 1) {
			String txt = params[0].getValue(engine).getStringValue();
			len = txt.length();
		} else {
			throw new IllegalParameterException(1);
		}
		return JamochaValue.newLong(len);
	}

	public String getName() {
		return STRING_LENGTH;
	}

	public Class[] getParameter() {
		return new Class[]{ValueParam.class};
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(str-length [string])";
	}

}
