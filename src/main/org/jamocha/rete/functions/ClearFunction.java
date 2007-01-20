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
import org.jamocha.rete.DefaultReturnValue;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;


/**
 * @author Peter Lin
 *
 * ClearFunction will call Rete.clear()
 */
public class ClearFunction implements Function, Serializable {

	public static final String CLEAR = "clear";

	/**
	 * 
	 */
	public ClearFunction() {
		super();
	}

	public int getReturnType() {
		return Constants.BOOLEAN_OBJECT;
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		DefaultReturnVector ret = new DefaultReturnVector();
		if (params != null && params.length == 1) {
			if (params[0].getStringValue().equals("objects")) {
				engine.clearObjects();
			} else if (params[0].getStringValue().equals("deffacts")) {
				engine.clearFacts();
			}
		} else {
			engine.clearAll();
		}
		DefaultReturnValue rv = new DefaultReturnValue(
				Constants.BOOLEAN_OBJECT, new Boolean(true));
		ret.addReturnValue(rv);
		return ret;
	}

	public String getName() {
		return CLEAR;
	}

	/**
	 * The function does not take any parameters
	 */
	public Class[] getParameter() {
		return new Class[0];
	}

	public String toPPString(Parameter[] params, int indents) {
		if (indents > 0) {
			StringBuffer buf = new StringBuffer();
			for (int idx = 0; idx < indents; idx++) {
				buf.append(" ");
			}
			buf.append("(clear)");
			return buf.toString();
		} else {
			return "(clear [objects | deffacts])\n" +
			"Function description:\n" +
			"\tRemoves all the facts from memory and resets the fact index\n" +
			"\tif no argument is provided.\n" + 
			"\tThe argument \"objects\" removes all the facts and\n" + 
			"\tthe argument \"deffacts\" clears all the defined facts.\n"; 
		}
	}
}
