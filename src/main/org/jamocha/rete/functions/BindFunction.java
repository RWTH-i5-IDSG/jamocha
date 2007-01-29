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
import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnValue;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Function;
import org.jamocha.rete.FunctionParam2;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ValueParam;


/**
 * @author Peter Lin
 * 
 * BindFunction is responsible for calling the appropriate method in Rete to
 * create the defglobal.
 */
public class BindFunction implements Function, Serializable {

	public static final String BIND = "bind";

	/**
	 * 
	 */
	public BindFunction() {
		super();
	}

	/**
	 * the return type is Boolean. If the function was successful, it returns
	 * true. Otherwise it returns false.
	 */
	public JamochaType getReturnType() {
		return Constants.BOOLEAN_OBJECT;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params) throws EvaluationException {
		boolean bound = false;
		if (params.length == 2) {
			String name = params[0].getStringValue();
			Object val = null;
			if (params[1] instanceof ValueParam) {
				val = params[1].getValue();
			} else if (params[1] instanceof FunctionParam2) {
				FunctionParam2 fp2 = (FunctionParam2) params[1];
				fp2.setEngine(engine);
				fp2.lookUpFunction();
				DefaultReturnVector drv = (DefaultReturnVector) fp2.getValue();
				val = drv.firstReturnValue().getValue();
			}
			engine.setBinding(name, val);
			bound = true;
		}
		DefaultReturnVector ret = new DefaultReturnVector();
		DefaultReturnValue rv = new DefaultReturnValue(
				Constants.BOOLEAN_OBJECT, new Boolean(bound));
		ret.addReturnValue(rv);
		return ret;
	}

	public String getName() {
		return BIND;
	}

	/**
	 * The function takes 2 parameters. The first is the name of the variable
	 * and the second is some value. At the moment, the function does not hand
	 */
	public Class[] getParameter() {
		return new Class[] { ValueParam.class, ValueParam.class };
	}

	public String toPPString(Parameter[] params, int indents) {
		if (params != null && params.length > 0) {
			StringBuffer buf = new StringBuffer();
			buf.append("(bind ?" + params[0].getStringValue());
			for (int idx = 1; idx < params.length; idx++) {
				if (params[idx] instanceof ValueParam) {
					buf.append(" " + params[idx].getStringValue());
				} else if (params[idx] instanceof FunctionParam2) {
					FunctionParam2 fp2 = (FunctionParam2) params[idx];
					buf.append(" " + fp2.toPPString());
				}
			}
			buf.append(" )");
			return buf.toString();
		} else {
			return "(bind ?<variable-name> <expression>)\n" +
			"Function description:\n" +
			"\tBinds the value of the argument <expression> to the \n" + 
			"\tvariable <variable-name>.";
		}
	}
}
