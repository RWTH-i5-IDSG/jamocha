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
import org.jamocha.rete.BoundParam;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ValueParam;

/**
 * Translates SL-Code to CLIPS-Code
 * 
 * @author Sebastian Reinartz
 */
public class SL2CLIPSFunction implements Function, Serializable {

	private static final long serialVersionUID = 1L;

	public static final String SL_2_CLIPS = "sl2clips";

	/**
	 * 
	 */
	public SL2CLIPSFunction() {
		super();
	}

	public JamochaType getReturnType() {
		return JamochaType.STRING;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		JamochaValue result = JamochaValue.newString("");
		if (params != null && params.length == 2) {
			long performative = params[0].getValue(engine).getLongValue();
			String slCode = params[1].getValue(engine).getStringValue();
			try {
				// TODO check if performative is a request
				String clipsCode = SL2CLIPS.getCLIPSFromRequest(slCode);
				result = JamochaValue.newString(clipsCode);
			} catch (AdapterTranslationException e) {
				throw new EvaluationException(
						"Error while translating from SL to CLIPS.", e);
			}
		} else {
			throw new IllegalParameterException(1);
		}
		return result;
	}

	public String getName() {
		return SL_2_CLIPS;
	}

	public Class[] getParameter() {
		return new Class[] { ValueParam.class };
	}

	public String toPPString(Parameter[] params, int indents) {
		if (params != null && params.length > 0) {
			StringBuffer buf = new StringBuffer();
			buf.append("(sl2clips");
			for (int idx = 0; idx < params.length; idx++) {
				if (params[idx] instanceof BoundParam) {
					BoundParam bp = (BoundParam) params[idx];
					buf.append(" ?").append(bp.getVariableName());
				} else if (params[idx] instanceof ValueParam) {
					buf.append(" \"").append(params[idx].getExpressionString())
							.append("\"");
				}
			}
			buf.append(")");
			return buf.toString();
		} else {
			return "(sl2clips <string expression>)\n"
					+ "Command description:\n"
					+ "\tTranslates a string in SL to CLIPS.";
		}
	}
}
