/*
 * Copyright 2007 Christoph Emonds, Alexander Wilden
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
package org.jamocha.rete.functions.ruleengine;

import java.io.Serializable;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.IllegalParameterException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.configurations.Signature;
import org.jamocha.rete.functions.FunctionDescription;

/**
 * @author Christoph Emonds
 * 
 * Applies a given function to one or more given parameters.
 */
public class Apply implements Function, Serializable {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Applies a given function to one or more given parameters.";
		}

		public int getParameterCount() {
			return 2;
		}

		public String getParameterDescription(int parameter) {
			if (parameter > 0)
				return "Optional Parameters for the Function";
			else
				return "Name of the Function to apply";
		}

		public String getParameterName(int parameter) {
			if (parameter > 0)
				return "functionParameter";
			else
				return "functionName";
		}

		public JamochaType[] getParameterTypes(int parameter) {
			if (parameter > 0)
				return JamochaType.ANY;
			else
				return JamochaType.IDENTIFIERS;
		}

		public JamochaType[] getReturnType() {
			return JamochaType.ANY;
		}

		public boolean isParameterCountFixed() {
			return false;
		}

		public boolean isParameterOptional(int parameter) {
			if (parameter > 0)
				return true;
			else
				return false;
		}
	}

	private static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "apply";

	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	public String getName() {
		return NAME;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		JamochaValue result;
		if (params != null && params.length >= 1) {
			String functionName = params[0].getValue(engine).getStringValue();
			Signature func = new Signature(functionName);
			Parameter[] functionParams = new Parameter[params.length - 1];
			System.arraycopy(params, 1, functionParams, 0,
					functionParams.length);
			func.setParameters(functionParams);
			result = func.getValue(engine);
		} else {
			throw new IllegalParameterException(1);
		}
		return result;
	}
}