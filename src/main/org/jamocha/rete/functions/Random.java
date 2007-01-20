/*
 * Copyright 2006 Christian Ebert 
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
 * @author Christian Ebert
 * 
 * Returns a double value with a positive sign, greater than or equal to 0.0 and less 
 * than 1.0. Returned values are chosen pseudorandomly with (approximately)
 * uniform distribution from that range.
 */
public class Random implements Function, Serializable {

	public static final String RANDOM = "random";

	/**
	 * 
	 */
	public Random() {
		super();
	}

	public int getReturnType() {
		return Constants.DOUBLE_PRIM_TYPE;
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		double dval = java.lang.Math.random();	
		DefaultReturnVector ret = new DefaultReturnVector();
		DefaultReturnValue rv = new DefaultReturnValue(Constants.DOUBLE_PRIM_TYPE,
				dval); 
		ret.addReturnValue(rv);
		return ret;
	}

	public String getName() {
		return RANDOM;
	}

	public Class[] getParameter() {
		return new Class[] {};
	}

	public String toPPString(Parameter[] params, int indents) {
		if (params != null && params.length >= 0) {
			StringBuffer buf = new StringBuffer();
			buf.append("(random");
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
			return "(random)\n" +
			"Function description:\n" +
			"\tReturns a random value between 0.0 and 1.0.";
		}
	}
}
