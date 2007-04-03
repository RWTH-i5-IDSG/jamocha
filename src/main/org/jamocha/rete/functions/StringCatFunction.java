/*
 * Copyright 2007 Alexander Wilden
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://jamocha.sourceforge.net/
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
import org.jamocha.rete.Constants;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;

/**
 * This function concatenates two or more Strings.
 * 
 * @author Alexander Wilden
 * 
 */
public class StringCatFunction implements Function, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String NAME = "str-cat";

	/**
	 * 
	 */
	public StringCatFunction() {
		super();
	}

	public JamochaType getReturnType() {
		return JamochaType.STRING;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		StringBuilder txt = new StringBuilder();
		if (params != null) {
			for (Parameter param : params) {
				JamochaValue value = param.getValue(engine);
				if (value.getType().equals(JamochaType.IDENTIFIER)
						&& value.getIdentifierValue().equals(Constants.CRLF)) {
					txt.append("\n");
				} else if (value.getType().equals(JamochaType.STRING)) {
					txt.append(value.getStringValue());
				} else {
					txt.append(value.toString());
				}
			}
		} else {
			throw new IllegalParameterException(1);
		}
		return JamochaValue.newString(txt.toString());
	}

	public String getName() {
		return NAME;
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(str-cat [string]*)\n" + "Function description:\n"
				+ "\tConcatenates all its argument Strings.";
	}

}
