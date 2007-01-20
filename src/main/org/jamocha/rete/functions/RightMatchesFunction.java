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

import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;


/**
 * @author Peter Lin
 * The purpose of RightMatches is to print out the facts in the right
 * side of BetaNodes. It isn't the same as matches function. Unlike
 * matches, RightMatches prints out all the facts on the right side
 * and doesn't show which facts it matches on the left.
 */
public class RightMatchesFunction implements Function, Serializable {

	public static final String RIGHT_MATCHES = "right-matches";
	
	/**
	 * 
	 */
	public RightMatchesFunction() {
		super();
	}

	public int getReturnType() {
		return Constants.RETURN_VOID_TYPE;
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		engine.getWorkingMemory().printWorkingMemoryBetaRight();
		return new DefaultReturnVector();
	}

	public String getName() {
		return RIGHT_MATCHES;
	}

	public Class[] getParameter() {
		return new Class[0];
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(right-matches)\n" +
				"Function description:\n" +
				"\tPrints out the facts in the right side of BetaNodes,\n" +
				"\tand does not show which facts it matches on the left.";
	}

}
