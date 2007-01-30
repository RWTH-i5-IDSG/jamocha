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
public class LoadFunctionGroupFunction implements Function, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String LOAD_FGROUP = "load-function-group";
	
	/**
	 * 
	 */
	public LoadFunctionGroupFunction() {
		super();
	}

	public JamochaType getReturnType() {
		return JamochaType.BOOLEAN;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params) throws EvaluationException {
		JamochaValue result = JamochaValue.FALSE;
		if (params != null && params.length > 0) {
			for (int idx=0; idx < params.length; idx++) {
				String func = params[idx].getValue(engine).getStringValue();
				engine.declareFunctionGroup(func);
				result = JamochaValue.TRUE;
			}
		}
		return result;
	}

	public String getName() {
		return LOAD_FGROUP;
	}

	public Class[] getParameter() {
		return new Class[]{ValueParam.class};
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(load-function-group [classname])";
	}

}
