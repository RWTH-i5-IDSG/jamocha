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
import org.jamocha.rete.ValueParam;


/**
 * @author Peter Lin
 *
 */
public class StringCompareFunction implements Function, Serializable {

	public static final String STRING_COMPARE = "str-compare";
	
	/**
	 * 
	 */
	public StringCompareFunction() {
		super();
	}

	public int getReturnType() {
		return Constants.INTEGER_OBJECT;
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		int eq = -1;
		if (params != null && params.length == 2) {
			String val = params[0].getStringValue();
			String val2 = params[1].getStringValue();
			eq = val.compareTo(val2);
		}
		DefaultReturnVector ret = new DefaultReturnVector();
		DefaultReturnValue rv = new DefaultReturnValue(
				Constants.INTEGER_OBJECT, new Integer(eq));
		ret.addReturnValue(rv);
		return ret;
	}

	public String getName() {
		return STRING_COMPARE;
	}

	public Class[] getParameter() {
		return new Class[]{ValueParam.class,ValueParam.class};
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(str-compare [string] [string])";
	}

}
