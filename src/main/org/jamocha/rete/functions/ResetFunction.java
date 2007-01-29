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
import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;


/**
 * @author Peter Lin
 *
 * Reset will reset all deffacts and objects. To reset just the objects,
 * call reset-objects
 */
public class ResetFunction implements Function, Serializable {

	public static final String RESET = "reset";
	
	/**
	 * 
	 */
	public ResetFunction() {
		super();
	}

	/**
	 * the function does not return anything
	 */
	public JamochaType getReturnType() {
        return Constants.RETURN_VOID_TYPE;
	}

	/**
	 * current implementation will call Rete.resetAll. This means it
	 * will reset all objects and deffacts.
	 */
	public JamochaValue executeFunction(Rete engine, Parameter[] params) throws EvaluationException {
		engine.resetAll();
		return new DefaultReturnVector();
	}

	public String getName() {
		return RESET;
	}

	/**
	 * reset does not take any parameters
	 */
	public Class[] getParameter() {
		return new Class[0];
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(reset)";
	}
}
