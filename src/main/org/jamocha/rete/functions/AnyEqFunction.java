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

import org.jamocha.rete.BoundParam;
import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnValue;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;
import org.jamocha.rete.ValueParam;


/**
 * @author Peter Lin
 * Any equal is used to compare a literal value against one or more
 * bindings. If any of the bindings is equal to the constant value,
 * the function returns true.
 */
public class AnyEqFunction implements Function, Serializable {

	public static final String ANYEQUAL = "any-eq";
	
	/**
	 * 
	 */
	public AnyEqFunction() {
		super();
	}

	public int getReturnType() {
		return Constants.BOOLEAN_OBJECT;
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		DefaultReturnVector ret = new DefaultReturnVector();
		boolean eq = false;
		if (params != null && params.length > 1) {
			Object constant = params[0].getValue();
			for (int idx=1; idx < params.length; idx++) {
				if (constant.equals(params[idx].getValue())) {
					eq = true;
					break;
				}
			}
		}
		DefaultReturnValue rv = new DefaultReturnValue(Constants.BOOLEAN_OBJECT,
				new Boolean(eq));
		ret.addReturnValue(rv);
		return ret;
	}

	public String getName() {
		return ANYEQUAL;
	}

	public Class[] getParameter() {
		return new Class[]{ValueParam.class,BoundParam.class};
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(any-eq (<literal> | <binding>)+)\n" +
			"Function description:\n" +
			"\tCompares a literal value against one or more" +
			"bindings. \n\tIf any of the bindings is equal to the constant value," +
			"\n\tthe function returns true.";
	}

}
