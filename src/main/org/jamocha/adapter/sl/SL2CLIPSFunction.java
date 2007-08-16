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

import java.io.Serializable;

import org.jamocha.adapter.AdapterTranslationException;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.IllegalParameterException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.functions.FunctionDescription;

/**
 * Translates SL-Code to CLIPS-Code
 * 
 * @author Alexander Wilden
 */
public class SL2CLIPSFunction implements Function, Serializable {

	public static final class Description implements
			FunctionDescription {

		public String getDescription() {
			return "Translates SL-Code to CLIPS-Code which then will be returned as a String.";
		}

		public int getParameterCount() {
			return 2;
		}

		public String getParameterDescription(int parameter) {
			switch (parameter) {
			case 0:
				return "Performative that is used.";
			case 1:
				return "String that should be translated to CLIPS-Code.";
			}
			return "";
		}

		public String getParameterName(int parameter) {
			switch (parameter) {
			case 0:
				return "performative";
			case 1:
				return "string";
			}
			return "";
		}

		public JamochaType[] getParameterTypes(int parameter) {
			switch (parameter) {
			case 0:
				return JamochaType.STRINGS;
			case 1:
				return JamochaType.STRINGS;
			}
			return null;
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

	private static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "sl2clips";

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
		String clipsCode = "";
		if (params != null && params.length == 2) {
			String performative = params[0].getValue(engine).getStringValue();
			String slCode = params[1].getValue(engine).getStringValue();
			try {
				clipsCode = SL2CLIPS.getCLIPS(performative, slCode);
			} catch (AdapterTranslationException e) {
				throw new EvaluationException(
						"Error while translating from SL to CLIPS.", e);
			}
		} else {
			throw new IllegalParameterException(2);
		}
		return JamochaValue.newString(clipsCode);
	}
}
