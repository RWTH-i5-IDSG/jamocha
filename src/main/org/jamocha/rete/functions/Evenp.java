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
 * If its only argument is even, Evenp returns true.
 */
public class Evenp implements Function, Serializable {

    private static final long serialVersionUID = 1L;

    public static final String NAME = "evenp";

    /**
         * 
         */
    public Evenp() {
	super();
    }

    public JamochaType getReturnType() {
	return JamochaType.BOOLEAN;
    }

    public JamochaValue executeFunction(Rete engine, Parameter[] params) throws EvaluationException {
	if (params != null) {
	    if (params.length == 1) {
		JamochaValue value = params[0].getValue(engine);
		if (!value.getType().equals(JamochaType.DOUBLE) && !value.getType().equals(JamochaType.LONG)) {
		    value = value.implicitCast(JamochaType.DOUBLE);
		}
		if (value.getType().equals(JamochaType.DOUBLE)) {
		    return JamochaValue.newBoolean(((value.getDoubleValue() % 2) == 0.0));
		}
		if (value.getType().equals(JamochaType.LONG)) {
		    return JamochaValue.newBoolean(((value.getLongValue() % 2) == 0));
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
	    buf.append("(evenp");
	    int idx = 0;
	    buf.append(" ").append(params[idx].getExpressionString());
	    buf.append(")");
	    return buf.toString();
	} else {
	    return "(evenp <expression>)\n" + "Function description:\n"
		    + "\tReturns true, if its only argument is even.";
	}
    }
}
