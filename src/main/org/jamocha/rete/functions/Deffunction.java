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
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;

/**
 * Deffunction is used for functions that are declared in the shell. It is
 * different than a function written in java. Deffunction run interpreted and
 * are mapped to existing functions.
 * 
 * @author Peter Lin
 */
public class Deffunction implements Function {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected String name = null;

	protected String ppString = null;

	protected Expression[] parameters = null;

	protected List functions = null;

	protected Class[] functionParams = null;

	protected JamochaType returnType;

	/**
	 * 
	 */
	public Deffunction() {
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		JamochaValue result = JamochaValue.FALSE;
		if (engine.findFunction(this.name) == null) {
			// first we get the actual function from the shell function
			Function[] functions = new Function[this.functions.size()];
			Parameter[][] parameters = new Parameter[this.functions.size()][];
			for (int i = 0; i < functions.length; ++i) {
				ShellFunction sf = (ShellFunction) this.functions.get(i);
				functions[i] = engine.findFunction(sf.getName());
				parameters[i] = sf.getParameters();
			}
			InterpretedFunction intrfunc = new InterpretedFunction(this.name,
					this.parameters, functions, parameters);
			intrfunc.configureFunction(engine);
			engine.declareFunction(intrfunc);
			result = JamochaValue.TRUE;
		}
		return result;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public Class[] getParameter() {
		return this.functionParams;
	}

	public JamochaType getReturnType() {
		return this.returnType;
	}

	public void setPPString(String text) {
		this.ppString = text;
	}

	public String toPPString(Parameter[] params, int indents) {
		return this.ppString;
	}

	public List getFunctions() {
		return functions;
	}

	public void setFunctions(List functions) {
		this.functions = functions;
	}

	public Expression[] getParameters() {
		return parameters;
	}

	public void setParameters(Expression[] parameters) {
		this.parameters = parameters;
	}
}
