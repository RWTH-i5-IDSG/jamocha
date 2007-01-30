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


/**
 * @author Peter Lin
 *
 * TODO To change the template for this generated type comment go to
 */
public class ResetFactsFunction implements Function, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String RESET_FACTS = "reset-facts";
	
	/**
	 * 
	 */
	public ResetFactsFunction() {
		super();
	}

	/**
	 * the function does not return anything
	 */
	public JamochaType getReturnType() {
        return JamochaType.NIL;
	}

	/**
	 * current implementation will call Rete.resetAll. This means it
	 * will reset all objects and deffacts.
	 */
	public JamochaValue executeFunction(Rete engine, Parameter[] params) throws EvaluationException {
		engine.resetFacts();
		return JamochaValue.NIL;
	}

	public String getName() {
		return RESET_FACTS;
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
