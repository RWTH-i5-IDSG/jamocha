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

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.IllegalParameterException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;

/**
 * @author Nikolaus Koemm
 * 
 * Ceil returns the smallest integer bigger or equal to a value.
 */
public class Ceil implements Function, Serializable {

    private static final long serialVersionUID = 1L;

    public static final String NAME = "ceil";

    /**
         * 
         */
    public Ceil() {
	super();
    }

    public JamochaType getReturnType() {
	return JamochaType.DOUBLE;
    }

    public JamochaValue executeFunction(Rete engine, Parameter[] params) throws EvaluationException {
	if (params != null) {
	    if (params.length == 1) {
		JamochaValue value = params[0].getValue(engine);
		if (!value.getType().equals(JamochaType.DOUBLE) && !value.getType().equals(JamochaType.LONG)) {
		    value = value.implicitCast(JamochaType.DOUBLE);
		}
		if (value.getType().equals(JamochaType.DOUBLE)) {
		    return JamochaValue.newDouble(Math.ceil(value.getDoubleValue()));
		} else if (value.getType().equals(JamochaType.LONG)) {
		    return JamochaValue.newDouble(Math.ceil(value.getLongValue()));
		}
	    }
	}
	throw new IllegalParameterException(1);
    }

    public String getName() {
	return NAME;
    }

    public String toPPString(Parameter[] params, int indents) {
	if (params != null && params.length >= 0) {
	    StringBuffer buf = new StringBuffer();
	    buf.append("(ceil");
	    int idx = 0;
	    buf.append(" ").append(params[idx].getExpressionString());
	    buf.append(")");
	    return buf.toString();
	} else {
	    return "(ceil <expression>)\n" + "Function description:\n"
		    + "\tReturns the smallest integer bigger or equal to the numerical value \n"
		    + "\treturned by <expression>.";
	}
    }
}
