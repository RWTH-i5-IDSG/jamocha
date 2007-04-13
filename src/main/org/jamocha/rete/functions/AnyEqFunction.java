/*
 * Copyright 2002-2006 Peter Lin
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

/**
 * @author Peter Lin
 * 
 * Any equal is used to compare a literal value against one or more bindings. If
 * any of the bindings is equal to the constant value, the function returns
 * true.
 */
public class AnyEqFunction implements Function, Serializable {

	private static final class AnyEqDescription implements FunctionDescription {

		public String getDescription() {
			return "Any equal is used to compare a literal value against one or more bindings. If any of the bindings is equal to the constant value, the function returns true.";
		}

		public int getParameterCount() {
			return 2;
		}

		public String getParameterDescription(int parameter) {
			switch (parameter) {
			case 0:
				return "Literal value that should be compared to the other parameters.";
			default:
				return "Value that should be compared to the first parameter.";
			}
		}

		public String getParameterName(int parameter) {
			return "number";
		}

		public JamochaType[] getParameterTypes(int parameter) {
			return JamochaType.ANY;
		}

		public JamochaType[] getReturnType() {
			return JamochaType.BOOLEANS;
		}

		public boolean isParameterCountFixed() {
			return false;
		}

		public boolean isParameterOptional(int parameter) {
			if (parameter > 1)
				return true;
			else
				return false;
		}
	}

	private static final FunctionDescription DESCRIPTION = new AnyEqDescription();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "any-eq";
	
	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	public String getName() {
		return NAME;
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

}
