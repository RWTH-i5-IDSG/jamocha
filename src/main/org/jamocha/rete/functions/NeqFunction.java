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
 */
public class NeqFunction implements Function, Serializable {

	public static final String NEQUAL = "neq";

	/**
	 * 
	 */
	public NeqFunction() {
		super();
	}

	public int getReturnType() {
		return Constants.BOOLEAN_OBJECT;
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		DefaultReturnVector ret = new DefaultReturnVector();
		boolean eq = true;
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
			for (int idx = 1; idx < params.length; idx++) {
				Object other = null;
				if (params[idx] instanceof ValueParam) {
					ValueParam n = (ValueParam) params[idx];
					other = n.getValue();
				} else if (params[idx] instanceof BoundParam) {
					BoundParam bp = (BoundParam) params[idx];
					other = (BigDecimal) engine
							.getBinding(bp.getVariableName());
				} else if (params[idx] instanceof FunctionParam2) {
					FunctionParam2 n = (FunctionParam2) params[idx];
					n.setEngine(engine);
					n.lookUpFunction();
					ReturnVector rval = (ReturnVector) n.getValue();
					other = rval.firstReturnValue().getValue();
				}
				if ( (  (first == null && other == null)     ||  
						(first != null && first.equals(other))  ) ) {
					eq = false;
					break;
				}
			}
		}
		DefaultReturnValue rv = new DefaultReturnValue(
				Constants.BOOLEAN_OBJECT, new Boolean(eq));
		ret.addReturnValue(rv);
		return ret;
	}

	public String getName() {
		return NEQUAL;
	}

	public Class[] getParameter() {
		return new Class[] { ValueParam[].class };
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(neq (<literal> | <binding>)+)\n" +
			"Function description:\n" +
			"\tCompares a literal value against one or more" +
			"bindings. \n\tIf all of the bindings are equal to the constant value," +
			"\n\tthe function returns true.";
	}

}
