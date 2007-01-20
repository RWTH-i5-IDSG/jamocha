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

import org.jamocha.rete.BoundParam;
import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;
import org.jamocha.rete.ValueParam;


/**
 * @author Peter Lin
 * 
 * MatchesFunction will print out all partial matches including alpha and 
 * beta nodes.
 */
public class MatchesFunction implements Function, Serializable {

    public static final String MATCHES = "matches";
    
	/**
	 * 
	 */
	public MatchesFunction() {
		super();
	}

	public int getReturnType() {
        return Constants.RETURN_VOID_TYPE;
	}

	/**
	 * If the function is called without any parameters, it prints out
	 * all the memories. if parameters are passed, the output will be
	 * filtered.
	 */
	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		HashMap filter = new HashMap();
		if (params != null && params.length > 0) {
			// now we populate the filter
			for (int idx=0; idx < params.length; idx++) {
				if (params[idx] instanceof ValueParam) {
					filter.put( ((ValueParam)params[idx]).getStringValue(),null);
				} else if (params[idx] instanceof BoundParam) {
					// for now, BoundParam is not supported
				}
			}
		}
		engine.getWorkingMemory().printWorkingMemory(filter);
		return new DefaultReturnVector();
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
