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
import org.jamocha.rete.BoundParam;
import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnValue;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ValueParam;


/**
 * @author Peter Lin
 * Any equal is used to compare a literal value against one or more
 * bindings. If any of the bindings is equal to the constant value,
 * the function returns true.
 */
public class MemberTestFunction implements Function, Serializable {

	public static final String MEMBERTEST = "member$";
	
	/**
	 * 
	 */
	public MemberTestFunction() {
		super();
	}

	public JamochaType getReturnType() {
		return Constants.INTEGER_OBJECT;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params) throws EvaluationException {
		DefaultReturnVector ret = new DefaultReturnVector();
		int index = -1;
		if (params != null && params.length == 2) {
			Object first = params[0].getValue();
            Object[] second = (Object[])params[1].getValue();
			for (int idx=0; idx < second.length; idx++) {
				if (first.equals(second[idx])) {
					index = idx;
					break;
				}
			}
		}
        if (index > -1) {
            DefaultReturnValue rv = new DefaultReturnValue(Constants.INTEGER_OBJECT,
                    new Integer(index));
            ret.addReturnValue(rv);
        } else {
            DefaultReturnValue rv = new DefaultReturnValue(Constants.BOOLEAN_OBJECT,
                    new Boolean(false));
            ret.addReturnValue(rv);
        }
		return ret;
	}

	public String getName() {
		return MEMBERTEST;
	}

	public Class[] getParameter() {
		return new Class[]{BoundParam.class,BoundParam.class};
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(member$ <expression> <multifield-expression>)\n" +
			"Function description:\n" +
			"\tCompares an expression against a multifield-expression." +
			"\n\tIf the single expression is in the second expression it," +
			"\n\treturns the integer position.";
	}

}
