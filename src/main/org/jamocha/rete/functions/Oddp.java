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
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

import java.lang.Math;

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
 * If its ony argument is odd, Oddp returns true.
 */
public class Oddp implements Function, Serializable {

	public static final String ODDP = "oddp";

	/**
	 * 
	 */
	public Oddp() {
		super();
	}

	public int getReturnType() {
		return Constants.BIG_DECIMAL;
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		BigDecimal bdval = new BigDecimal(0);
		boolean eval = false;
		if (params.length == 1) {
            bdval = (BigDecimal)params[0].getValue(engine, Constants.BIG_DECIMAL);
			double bdh = bdval.doubleValue();
			if (bdh % 2 == 1){
				eval = true;
			}

		}
		DefaultReturnVector ret = new DefaultReturnVector();
		DefaultReturnValue rv = new DefaultReturnValue(
				Constants.BOOLEAN_OBJECT, new Boolean(eval));
		ret.addReturnValue(rv);
		return ret;
	}

	public String getName() {
		return ODDP;
	}

	public Class[] getParameter() {
		return new Class[] { ValueParam[].class };
	}

	public String toPPString(Parameter[] params, int indents) {
		if (params != null && params.length >= 0) {
			StringBuffer buf = new StringBuffer();
			buf.append("(oddp");
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
			return "(oddp <expression>)\n" +
			"Function description:\n" +
			"\tReturns true, if its only argument is odd.";
		}
	}
}
