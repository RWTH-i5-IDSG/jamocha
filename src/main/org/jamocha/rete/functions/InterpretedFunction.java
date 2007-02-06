/*
 * Copyright 2002-2007 Peter Lin
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

import java.util.HashMap;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.IllegalParameterException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.BoundParam;
import org.jamocha.rete.DefaultScope;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.Scope;

/**
 * 
 * @author Peter Lin
 */
public class InterpretedFunction implements Function {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String name = null;

	protected String ppString = null;

	protected Parameter[] inputParams = null;

	private Function[] internalFunctions = null;

	/**
	 * these are the functions we pass to the top level function. they may be
	 * different than the input parameters for the function.
	 */
	private Parameter[][] functionParams = null;

	private HashMap bindings = new HashMap();

	/**
	 * 
	 */
	public InterpretedFunction(String name, Parameter[] params,
			Function[] functions, Parameter[][] parameters) {
		this.name = name;
		this.inputParams = params;
		this.internalFunctions = functions;
		this.functionParams = parameters;
	}

	public void configureFunction(Rete engine) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jamocha.rete.Function#executeFunction(org.jamocha.rete.Rete,
	 *      org.jamocha.rete.Parameter[])
	 */
	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		// the first thing we do is set the values
		JamochaValue result = JamochaValue.NIL;
		if (params.length == this.inputParams.length) {
			Scope parameterValues = new DefaultScope();
			for (int idx = 0; idx < this.inputParams.length; idx++) {
				BoundParam bp = (BoundParam) this.inputParams[idx];
				parameterValues.setBindingValue(bp.getVariableName(),
						params[idx].getValue(engine));
			}
			engine.pushScope(parameterValues);
			try {
				for (int i = 0; i < this.internalFunctions.length; ++i) {
					result = this.internalFunctions[i].executeFunction(engine,
							this.functionParams[i]);
				}
			} finally {
				engine.popScope();
			}
		} else {
			throw new IllegalParameterException(this.inputParams.length);
		}
		return result;
	}

	public String getName() {
		return this.name;
	}

	public Class[] getParameter() {
		return new Class[] { BoundParam.class };
	}

	public JamochaType getReturnType() {
		return JamochaType.UNDEFINED;
	}

	public String toPPString(Parameter[] params, int indents) {
		return ppString;
	}

	public Parameter[] getInputParameters() {
		return inputParams;
	}

	public JamochaValue getBinding(String var) {
		return (JamochaValue) this.bindings.get(var);
	}
}
