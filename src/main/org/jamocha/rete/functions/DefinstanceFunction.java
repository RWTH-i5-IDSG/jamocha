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
import org.jamocha.rete.Fact;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;
import org.jamocha.rete.ValueParam;
import org.jamocha.rete.exception.AssertException;


/**
 * @author Peter Lin
 *
 * Definstance will assert an object instance using Rete.assert(Object).
 */
public class DefinstanceFunction implements Function, Serializable {

	public static final String DEFINSTANCE = "definstance";

	public DefinstanceFunction() {
		super();
	}

	public int getReturnType() {
		return Constants.STRING_TYPE;
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		String asrt = "";
		if (params.length >= 1 && params[0].getValue() != null) {
			Object obj = params[0].getValue();
			String template = "";
			if (params.length == 2 && params[1].getStringValue() != null) {
				template = params[1].getStringValue();
			}
			try {
				engine.assertObject(obj, template, false, true);
				asrt = "true";
			} catch (AssertException e) {
				// we should log this and output an error
				asrt = "false";
			}
		} else {
			asrt = "false";
		}
		DefaultReturnVector ret = new DefaultReturnVector();
		DefaultReturnValue rv = new DefaultReturnValue(Constants.STRING_TYPE,
				asrt);
		ret.addReturnValue(rv);
		return ret;
	}

	public String getName() {
		return DEFINSTANCE;
	}

	/**
	 * The function expects a single BoundParam that is an object binding
	 */
	public Class[] getParameter() {
		return new Class[] { BoundParam.class, ValueParam.class };
	}

	public String toPPString(Parameter[] params, int indents) {
		if (params != null && params.length > 0) {
			StringBuffer buf = new StringBuffer();
			return buf.toString();
		} else {
			return "(definstance )";
		}
	}
}
