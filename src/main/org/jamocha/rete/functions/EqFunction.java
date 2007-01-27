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
import java.math.BigDecimal;

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
 */
public class EqFunction implements Function, Serializable {

	public static final String EQUAL = "eq";

	/**
	 * 
	 */
	public EqFunction() {
		super();
	}

	public int getReturnType() {
		return Constants.BOOLEAN_OBJECT;
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		DefaultReturnVector ret = new DefaultReturnVector();
		boolean eq = false;
		if (params != null && params.length > 1) {
			Object first = null;
			if (params[0] instanceof ValueParam) {
				ValueParam n = (ValueParam) params[0];
				first = n.getValue();
			} else if (params[0] instanceof BoundParam) {
				BoundParam bp = (BoundParam) params[0];
				first = (BigDecimal) engine.getBinding(bp.getVariableName());
			} else if (params[0] instanceof FunctionParam2) {
				FunctionParam2 n = (FunctionParam2) params[0];
				n.setEngine(engine);
				n.lookUpFunction();
				ReturnVector rval = (ReturnVector) n.getValue();
				first = rval.firstReturnValue().getValue();
			}
            boolean eval = true;
			for (int idx = 1; idx < params.length; idx++) {
				Object right = null;
				if (params[idx] instanceof ValueParam) {
					ValueParam n = (ValueParam) params[idx];
                    right = n.getValue();
				} else if (params[idx] instanceof BoundParam) {
					BoundParam bp = (BoundParam) params[idx];
                    right = engine
							.getBinding(bp.getVariableName());
				} else if (params[idx] instanceof FunctionParam2) {
					FunctionParam2 n = (FunctionParam2) params[idx];
					n.setEngine(engine);
					n.lookUpFunction();
					ReturnVector rval = (ReturnVector) n.getValue();
                    right = rval.firstReturnValue().getValue();
				}
				if (first == null && right != null) {
                    eval = false;
                    break;
                } else if (first != null && !first.equals(right)) {
					eval = false;
					break;
				}
			}
            eq = eval;
		}
		DefaultReturnValue rv = new DefaultReturnValue(
				Constants.BOOLEAN_OBJECT, new Boolean(eq));
		ret.addReturnValue(rv);
		return ret;
	}

	public String getName() {
		return EQUAL;
	}

	public Class[] getParameter() {
		return new Class[] { ValueParam[].class };
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(eq (<literal> | <binding>)+)\n" +
			"Function description:\n" +
			"\tCompares a literal value against one or more" +
			"bindings. \n\tIf all of the bindings are equal to the constant value," +
			"\n\tthe function returns true.";
	}

}
