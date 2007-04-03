/*
 * Copyright 2002-2007 Christoph Emonds Sebastian Reinartz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.jamocha.org/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.jamocha.rete.functions.list;

import java.io.Serializable;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.IllegalParameterException;
import org.jamocha.parser.IllegalTypeException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.BoundParam;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;

/**
 * @author Christoph Emonds, Sebastian Reinartz
 * 
 * Evaluates expressions for all items in a list.
 */
public class Foreach implements Function, Serializable {

    private static final long serialVersionUID = 1L;

    public static final String NAME = "foreach";

    /**
         * 
         */
    public Foreach() {
	super();
    }

    public JamochaType getReturnType() {
	return JamochaType.UNDEFINED;
    }

    public JamochaValue executeFunction(Rete engine, Parameter[] params) throws EvaluationException {
	if (params != null && params.length >= 2) {
	    if (params[0] instanceof BoundParam) {
		BoundParam variable = (BoundParam) params[0];
		JamochaValue list = params[1].getValue(engine);
		if (list.is(JamochaType.LIST)) {
		    JamochaValue result = JamochaValue.NIL;
		    for (int j = 0; j < list.getListCount(); ++j) {
			engine.setBinding(variable.getVariableName(), list.getListValue(j));
			for (int i = 2; i < params.length; ++i) {
			    result = params[i].getValue(engine);
			}
		    }
		    return result;
		} else {
		    throw new IllegalTypeException(JamochaType.LISTS, list.getType());
		}
	    } else {
		throw new EvaluationException("First parameter must be a binding.");
	    }
	}
	throw new IllegalParameterException(2, true);
    }

    public String getName() {
	return NAME;
    }

    public String toPPString(Parameter[] params, int indents) {
	if (params != null && params.length > 0) {
	    StringBuffer buf = new StringBuffer();
	    buf.append("(" + NAME);
	    for (int idx = 0; idx < params.length; idx++) {
		buf.append(" " + params[idx].getExpressionString());
	    }
	    buf.append(")");
	    return buf.toString();
	} else {
	    return "(" + NAME + " <variable> <list> <expression>*)\n" + "Function description:\n"
		    + "\t Iterates over a list.";
	}
    }
}
