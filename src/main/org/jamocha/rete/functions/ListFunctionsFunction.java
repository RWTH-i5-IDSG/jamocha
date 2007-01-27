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
import java.util.Iterator;
import java.util.List;

import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Function;
import org.jamocha.rete.FunctionGroup;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;


public class ListFunctionsFunction implements Function, Serializable {

	public static final String LIST_FUNCTIONS = "list-deffunctions";

	public ListFunctionsFunction() {
		super();
	}

	public int getReturnType() {
		return Constants.RETURN_VOID_TYPE;
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		List fgroups = engine.getFunctionGroups();
		Iterator itr = fgroups.iterator();
		int counter = 0;
		while (itr.hasNext()) {
			// we iterate over the function groups and print out the
			// functions in each group
			FunctionGroup fg = (FunctionGroup) itr.next();
			engine.writeMessage("++++ " + fg.getName() + " ++++" + Constants.LINEBREAK, "t");
			Iterator listitr = fg.listFunctions().iterator();
			while (listitr.hasNext()) {
				Function f = (Function) listitr.next();
				engine.writeMessage("  " + f.getName() + Constants.LINEBREAK,
						"t");
				counter++;
			}
		}
		engine.writeMessage(counter + " functions" + Constants.LINEBREAK, "t");
		DefaultReturnVector ret = new DefaultReturnVector();
		return ret;
	}

	public String getName() {
		return LIST_FUNCTIONS;
	}

	public Class[] getParameter() {
		return new Class[0];
	}

	/**
	 * for now, just return the simple form. need to implement the method
	 * completely.
	 */
	public String toPPString(Parameter[] params, int indents) {
		if (indents > 0) {
			StringBuffer buf = new StringBuffer();
			for (int idx = 0; idx < indents; idx++) {
				buf.append(" ");
			}
			buf.append("(list-deffunctions)");
			return buf.toString();
		} else {
			return "(list-deffunctions)";
		}
	}

}
