/*
 * Copyright 2006 Nikolaus Koemm
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
import org.jamocha.rete.FunctionParam2;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;
import org.jamocha.rete.ValueParam;

/**
 * @author Nikolaus Koemm
 * 
 * Min returns the smallest of two or more values.
 */
public class NotFunction implements Function, Serializable {

	public static final String NOT = "NOT";

	/**
	 * 
	 */
	public NotFunction() {
		super();
	}

	public int getReturnType() {
		return Constants.BOOLEAN_OBJECT;
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		boolean boolVal = true;
		if (params != null) {
			if(params.length==1) {
				if (params[0] instanceof ValueParam) {
					ValueParam n = (ValueParam) params[0];
					boolVal = n.getBooleanValue();
				} else if (params[0] instanceof BoundParam) {
					BoundParam bp = (BoundParam) params[0];
					boolVal = (Boolean)engine.getBinding(bp.getVariableName());
				} else if (params[0] instanceof FunctionParam2) {
					FunctionParam2 n = (FunctionParam2) params[0];
					n.setEngine(engine);
					n.lookUpFunction();
					ReturnVector rval = (ReturnVector) n.getValue();
					boolVal = rval.firstReturnValue().getBooleanValue();
				}
				boolVal = (boolVal == false);
			}
		}
		DefaultReturnVector ret = new DefaultReturnVector();
		DefaultReturnValue rv = new DefaultReturnValue(Constants.BOOLEAN_OBJECT,
				new Boolean(boolVal));
		ret.addReturnValue(rv);
		return ret;
	}

	public String getName() {
		return NOT;
	}

	public Class[] getParameter() {
		return new Class[] { ValueParam[].class };
	}

	public String toPPString(Parameter[] params, int indents) {
		if (params != null && params.length >= 0) {
			StringBuffer buf = new StringBuffer();
			buf.append("("+NOT);
				int idx = 0;
				if (params[idx] instanceof BoundParam) {
					BoundParam bp = (BoundParam) params[idx];
					buf.append(" ?" + bp.getVariableName());
				} else if (params[idx] instanceof ValueParam) {
					buf.append(" " + params[idx].getStringValue());
				} else {
					buf.append(" " + params[idx].getStringValue());
				}
			buf.append(")");
			return buf.toString();
		} else {
			return "(NOT <booleanexpression>)\n" +
			"Function description:\n" +
			"\tReturns the boolean not of any boolean expression argument.";
		}
	}
}
