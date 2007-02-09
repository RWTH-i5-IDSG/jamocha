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

import java.util.List;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.Expression;
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
	
	private String description = null;

	protected Expression[] inputParams = null;

	private List<ShellFunction> actions = null;

	/**
	 * 
	 */
	public InterpretedFunction(String name, String description, Expression[] params,
			List<ShellFunction> actions) {
		this.name = name;
		this.description = description;
		this.inputParams = params;
		this.actions = actions;
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
				for (int i = 0; i < this.actions.size(); ++i) {
					ShellFunction sf = this.actions.get(i);
					sf.lookUpFunction(engine);
					result = sf.executeFunction(engine, sf.getParameters());
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
		return description;
	}

	public Expression[] getInputParameters() {
		return inputParams;
	}
}
