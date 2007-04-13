/*
 * Copyright 2006 Christian Ebert 
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
import org.jamocha.parser.ParserFactory;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;

/**
 * @author Christian Ebert
 * 
 * Returns the trigonometric arc cosine of its only argument (which should be an
 * angle as numeric expression). The return value will double.
 */
public class Acos implements Function, Serializable {

	private static final class AcosDescription implements FunctionDescription {

		public String getDescription() {
			return "Returns the trigonometric arc cosine of its only argument (which should be an angle as numeric expression). The return value will double.";
		}

		public int getParameterCount() {
			return 1;
		}

		public String getParameterDescription(int parameter) {
			return "Angle, whose trigonometric arc cosine will be returned.";
		}

		public String getParameterName(int parameter) {
			return "number";
		}

		public JamochaType[] getParameterTypes(int parameter) {
			return JamochaType.NUMBERS;
		}

		public JamochaType[] getReturnType() {
			return JamochaType.DOUBLES;
		}

		public boolean isParameterCountFixed() {
			return true;
		}

		public boolean isParameterOptional(int parameter) {
			return false;
		}
	}

	private static final FunctionDescription DESCRIPTION = new AcosDescription();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "acos";

	public Acos() {
		super();
	}

	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	public String getName() {
		return NAME;
	}


	public JamochaType getReturnType() {
		return JamochaType.DOUBLE;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		if (params != null) {
			if (params.length == 1) {
				JamochaValue value = params[0].getValue(engine);
				if (!value.getType().equals(JamochaType.DOUBLE)
						&& !value.getType().equals(JamochaType.LONG)) {
					value = value.implicitCast(JamochaType.DOUBLE);
				}
				if (value.getType().equals(JamochaType.DOUBLE)) {
					return JamochaValue.newDouble(Math.acos(value
							.getDoubleValue()));
				} else if (value.getType().equals(JamochaType.LONG)) {
					return JamochaValue.newDouble(Math.acos(value
							.getLongValue()));
				}
			}
		}
		throw new IllegalParameterException(1);
	}
}
