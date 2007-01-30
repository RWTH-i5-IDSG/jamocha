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
import org.jamocha.rete.ValueParam;

/**
 * @author Nikolaus Koemm
 * 
 */
public class Const implements Function, Serializable {

	private static final long serialVersionUID = 1L;

	public static final String CONST = "const";

	/**
	 * 
	 */
	public Const() {
		super();
	}

	public JamochaType getReturnType() {
		return JamochaType.DOUBLE;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		if (params[0] != null) {
			if (params.length == 1) {

				String val = params[0].getValue(engine).getStringValue();
				if (val.compareToIgnoreCase("pi") == 0) {
					return new JamochaValue(JamochaType.DOUBLE,
							java.lang.Math.PI);
				} else if (val.compareToIgnoreCase("e") == 0) {
					return new JamochaValue(JamochaType.DOUBLE,
							java.lang.Math.E);
				}
			}
		}
		throw new IllegalParameterException(1);
	}

	public String getName() {
		return CONST;
	}

	public Class[] getParameter() {
		return new Class[] { ValueParam.class, ValueParam.class };
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(const e|pi)\n" + "Function description:\n"
				+ "\te  return the value of the Euler constant,\n"
				+ "\tpi returns the value of Pi.";
	}

}
