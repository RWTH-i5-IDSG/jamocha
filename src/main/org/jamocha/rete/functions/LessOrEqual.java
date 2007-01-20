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
import org.jamocha.rete.FunctionParam2;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;
import org.jamocha.rete.ValueParam;


/**
 * @author Peter Lin
 * 
 * LessOrEqual will compare 2 or more numeric values and return true if the (n-1)th
 * value is less or equal to the nth value.
 */
public class LessOrEqual implements Function, Serializable {

	public static final String LESSOREQUAL = "lessOrEqual";

	/**
	 * 
	 */
	public LessOrEqual() {
		super();
	}

	public int getReturnType() {
		return Constants.BOOLEAN_OBJECT;
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		boolean eval = false;
		double left = 0;
		double right = 0;
		if ((params != null) && (params.length >= 2)) {
			if (params[0] instanceof ValueParam) {
				left = ((ValueParam) params[0]).getDoubleValue();
			} else if (params[0] instanceof BoundParam) {
				left = ((BoundParam) params[0]).getDoubleValue();
			} else if (params[0] instanceof FunctionParam2) {
				FunctionParam2 n = (FunctionParam2) params[0];
				n.setEngine(engine);
				n.lookUpFunction();
				ReturnVector rval = (ReturnVector) n.getValue();
				left = rval.firstReturnValue().getDoubleValue();
			}
			for (int idx = 1; idx < params.length; idx++) {
				if (params[idx] instanceof ValueParam) {
					right = ((ValueParam) params[idx]).getDoubleValue();
				} else if (params[idx] instanceof BoundParam) {
					right = ((BoundParam) params[idx]).getDoubleValue();
				} else if (params[idx] instanceof FunctionParam2) {
					FunctionParam2 n = (FunctionParam2) params[1];
					n.setEngine(engine);
					n.lookUpFunction();
					ReturnVector rval = (ReturnVector) n.getValue();
					right = rval.firstReturnValue().getDoubleValue();
				}
				if (left <= right){
					eval = true;
					left = right;
				} else {
					eval = false;
					DefaultReturnVector ret = new DefaultReturnVector();
					DefaultReturnValue rv = new DefaultReturnValue(
							Constants.BOOLEAN_OBJECT, new Boolean(eval));
					ret.addReturnValue(rv);
					return ret;
				}
			}
		}
		DefaultReturnVector ret = new DefaultReturnVector();
		DefaultReturnValue rv = new DefaultReturnValue(
				Constants.BOOLEAN_OBJECT, new Boolean(eval));
		ret.addReturnValue(rv);
		return ret;
	}

	public String getName() {
		return LESSOREQUAL;
	}

	public Class[] getParameter() {
		return new Class[] { ValueParam.class, ValueParam.class };
	}

	public String toPPString(Parameter[] params, int indents) {
		if (params != null && params.length > 0) {
			StringBuffer buf = new StringBuffer();
			buf.append("(<=");
			for (int idx = 0; idx < params.length; idx++) {
				if (params[idx] instanceof BoundParam) {
					BoundParam bp = (BoundParam) params[idx];
					buf.append(" ?" + bp.getVariableName());
				} else if (params[idx] instanceof ValueParam) {
					buf.append(" " + params[idx].getStringValue());
				} else {
					buf.append(" " + params[idx].getStringValue());
				}
			}
			buf.append(")");
			return buf.toString();
		} else {
			return "(<= (<literal> | <binding>)+)\n" +
					"Function description:\n" +
					"\t Returns the symbol TRUE if for all its arguments, " +
					"argument \n \t n-1 is less or equal than argument n";
		}
	}
}
