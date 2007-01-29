/*
 * Copyright 2006 Nikolaus Koemm, Christian Ebert 
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
 * @author Nikolaus Koemm, Christian Ebert
 * 
 * Returns the absolute value of a double value.
 */
public class IfFunction implements Function, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String IF = "if";

	/**
	 * 
	 */
	public IfFunction() {
		super();
	}

	public JamochaType getReturnType() {
		return Constants.OBJECT_TYPE;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params) throws EvaluationException {
		Object result = null;
		if (params != null) {
			if (params.length >= 3) {
				boolean conditionValue = false;
				if (params[0] instanceof ValueParam) {
					ValueParam n = (ValueParam) params[0];
					conditionValue = n.getBooleanValue();
				} else if (params[0] instanceof BoundParam) {
					BoundParam bp = (BoundParam) params[0];
					conditionValue = (Boolean) engine.getBinding(bp
							.getVariableName());
				} else if (params[0] instanceof FunctionParam2) {
					FunctionParam2 n = (FunctionParam2) params[0];
					n.setEngine(engine);
					n.lookUpFunction();
					ReturnVector rval = (ReturnVector) n.getValue();
					conditionValue = rval.firstReturnValue().getBooleanValue();
				}
				if (params[1] instanceof ValueParam
						&& "then".equals(params[1].getStringValue())) {
					boolean elseExpressions = false;
					for (int i = 2; i < params.length; ++i) {
						if (params[i] instanceof ValueParam
								&& "else".equals(params[i].getStringValue())) {
							elseExpressions = true;
						} else {
							if ((conditionValue && !elseExpressions)
									|| (!conditionValue && elseExpressions)) {
								if (params[i] instanceof ValueParam) {
									ValueParam n = (ValueParam) params[i];
									result = n.getValue();
								} else if (params[i] instanceof BoundParam) {
									BoundParam bp = (BoundParam) params[i];
									result = engine.getBinding(bp
											.getVariableName());
								} else if (params[i] instanceof FunctionParam2) {
									FunctionParam2 n = (FunctionParam2) params[i];
									n.setEngine(engine);
									n.lookUpFunction();
									ReturnVector rval = (ReturnVector) n
											.getValue();
									if (rval.size() > 0) {
										result = rval.firstReturnValue()
												.getValue();
									}
								}
							}
						}
					}
				}
			}
		}
		DefaultReturnVector ret = new DefaultReturnVector();
		DefaultReturnValue rv = new DefaultReturnValue(Constants.OBJECT_TYPE,
				result);
		ret.addReturnValue(rv);
		return ret;
	}

	public String getName() {
		return IF;
	}

	public Class[] getParameter() {
		return new Class[] { ValueParam[].class };
	}

	public String toPPString(Parameter[] params, int indents) {
		if (params != null && params.length >= 0) {
			StringBuffer buf = new StringBuffer();
			buf.append("(if");
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
			return "(if <boolean expression> then <expression>+ [else <expression>+])\n"
					+ "Function description:\n"
					+ "\tExecutes the expressions after then if the boolean expressions evaluates to true, otherwise it executes the expressions after the optional else.";
		}
	}
}
