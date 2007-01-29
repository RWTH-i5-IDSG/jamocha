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

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
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
 * Divide will divide one or more numbers and return a Double value
 */
public class Divide implements Function, Serializable {

	public static final String DIVIDE = "divide";

	/**
	 * 
	 */
	public Divide() {
		super();
	}

	public JamochaType getReturnType() {
		return Constants.BIG_DECIMAL;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params) throws EvaluationException {
		BigDecimal bdval = null;
		if (params != null) {
			if (params[0] instanceof ValueParam) {
				bdval = params[0].getBigDecimalValue();
			} else if (params[0] instanceof BoundParam) {
				BoundParam bp = (BoundParam) params[0];
				bdval = (BigDecimal) engine.getBinding(bp.getVariableName());
			} else if (params[0] instanceof FunctionParam2) {
				FunctionParam2 n = (FunctionParam2) params[0];
				n.setEngine(engine);
				n.lookUpFunction();
				ReturnVector rval = (ReturnVector) n.getValue();
				bdval = rval.firstReturnValue().getBigDecimalValue();
			}
			for (int idx = 1; idx < params.length; idx++) {
				if (params[idx] instanceof ValueParam) {
					ValueParam n = (ValueParam) params[idx];
					BigDecimal bd = n.getBigDecimalValue();
					bdval = secureDivide(bdval, bd);
				} else if (params[idx] instanceof FunctionParam2) {
					FunctionParam2 n = (FunctionParam2) params[idx];
					n.setEngine(engine);
					n.lookUpFunction();
					ReturnVector rval = (ReturnVector) n.getValue();
					BigDecimal bd = rval.firstReturnValue().getBigDecimalValue();
					// bdval = bdval.divide(bd);
					if (idx == 0) {
						bdval = bd;
					} else {
						bdval = secureDivide(bdval, bd);
					}
				}
			}
		}
		DefaultReturnVector ret = new DefaultReturnVector();
		DefaultReturnValue rv = new DefaultReturnValue(
				Constants.DOUBLE_PRIM_TYPE, bdval);
		ret.addReturnValue(rv);
		return ret;
	}

	public String getName() {
		return DIVIDE;
	}

	public Class[] getParameter() {
		return new Class[] { ValueParam[].class };
	}

	public String toPPString(Parameter[] params, int indents) {
		if (params != null && params.length > 0) {
			StringBuffer buf = new StringBuffer();
			buf.append("(/");
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
			return "(/ (<literal> | <binding>)+)\n" +
					"Function description:\n" +
					"\t Returns the value of the first argument divided by " +
					"each of the subsequent arguments.";
		}
	}
	
	private BigDecimal secureDivide(BigDecimal dividend, BigDecimal divisor) {
	    try {
		dividend = dividend.divide( divisor );
	    }
	    catch( ArithmeticException e ) {
		dividend = new BigDecimal( dividend.doubleValue() / divisor.doubleValue() );
	    }
	    
	    return dividend;
	}
}
