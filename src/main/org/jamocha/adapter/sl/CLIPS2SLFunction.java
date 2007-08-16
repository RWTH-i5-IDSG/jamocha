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
package org.jamocha.adapter.sl;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.IllegalParameterException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.functions.AbstractFunction;
import org.jamocha.rete.functions.FunctionDescription;

/**
 * Translates CLIPS-Code resp. JamochaValues to SL.
 * 
 * @author Alexander Wilden
 */
public class CLIPS2SLFunction extends AbstractFunction {

	private static final class CLIPS2SLFunctionDescription implements
			FunctionDescription {

		public String getDescription() {
			return "translates CLIPS-Code resp. JamochaValues to SL which then will be returned as a String.";
		}

		public int getParameterCount() {
			return 1;
		}

		public String getParameterDescription(int parameter) {
			return "Jamochavalue that should be translated to SL.";
		}

		public String getParameterName(int parameter) {
			return "string";
		}

		public JamochaType[] getParameterTypes(int parameter) {
			return JamochaType.ANY;
		}

		public JamochaType[] getReturnType() {
			return JamochaType.STRINGS;
		}

		public boolean isParameterCountFixed() {
			return true;
		}

		public boolean isParameterOptional(int parameter) {
			return false;
		}

		public String getExample() {
			// TODO Auto-generated method stub
			return null;
		}

		public boolean isResultAutoGeneratable() {
			// TODO Auto-generated method stub
			return false;
		}
	}

	private static final long serialVersionUID = 1L;

	public static final FunctionDescription DESCRIPTION = new CLIPS2SLFunctionDescription();

	public static final String NAME = "clips2sl";

	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	public String getName() {
		return NAME;
	}

	public JamochaType getReturnType() {
		return JamochaType.STRING;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		JamochaValue result = JamochaValue.newString("");
		if (params != null && params.length == 1) {
			JamochaValue value = params[0].getValue(engine);
			String slCode = CLIPS2SL.getSL(value, engine);
			result = JamochaValue.newString(slCode);
		} else {
			throw new IllegalParameterException(1);
		}
		return result;
	}
}