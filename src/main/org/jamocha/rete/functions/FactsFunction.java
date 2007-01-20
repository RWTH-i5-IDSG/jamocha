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
import java.util.List;

import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Fact;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;
import org.jamocha.rete.util.FactUtils;


/**
 * @author Peter Lin
 * 
 * Facts function will printout all the facts, not including any
 * initial facts which are internal to the rule engine.
 */
public class FactsFunction implements Function, Serializable {

	public static final String FACTS = "facts";

	/**
	 * 
	 */
	public FactsFunction() {
		super();
	}

	public int getReturnType() {
		return Constants.RETURN_VOID_TYPE;
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		List facts = engine.getAllFacts();
		Object[] sorted = FactUtils.sortFacts(facts);
		for (int idx = 0; idx < sorted.length; idx++) {
			Fact ft = (Fact) sorted[idx];
			engine.writeMessage(ft.toFactString() + Constants.LINEBREAK);
		}
		engine.writeMessage("for a total of " + sorted.length +
				Constants.LINEBREAK,Constants.DEFAULT_OUTPUT);
		return new DefaultReturnVector();
	}

	public String getName() {
		return FACTS;
	}

	public Class[] getParameter() {
		return new Class[0];
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(facts)\n" +
			"Function description:\n" +
			"\tPrints all facts except the initial facts.";
	}
}
