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
import java.util.HashMap;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;


/**
 * @author Peter Lin
 * 
 * MatchesFunction will print out all partial matches including alpha and 
 * beta nodes.
 */
public class MatchesFunction implements Function, Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String MATCHES = "matches";
    
	/**
	 * 
	 */
	public MatchesFunction() {
		super();
	}

	public JamochaType getReturnType() {
        return JamochaType.NIL;
	}

	/**
	 * If the function is called without any parameters, it prints out
	 * all the memories. if parameters are passed, the output will be
	 * filtered.
	 */
	public JamochaValue executeFunction(Rete engine, Parameter[] params) throws EvaluationException {
		HashMap<String,Object> filter = new HashMap<String,Object>();
		if (params != null && params.length > 0) {
			// now we populate the filter
			for (int idx=0; idx < params.length; idx++) {
				filter.put( params[idx].getValue(engine).getStringValue(),null);
			}
		}
		engine.getWorkingMemory().printWorkingMemory(filter);
		return JamochaValue.NIL;
	}

	public String getName() {
		return MATCHES;
	}

	public Class[] getParameter() {
		return new Class[] {String[].class};
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(matches)\n" +
			"Function description:\n" +
			"\tPrints out all partial matches including alpha and beta nodes.";
	}
}
