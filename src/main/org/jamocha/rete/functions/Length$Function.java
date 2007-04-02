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
import org.jamocha.parser.IllegalParameterException;
import org.jamocha.parser.IllegalTypeException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ValueParam;

/**
 * @author Christoph Emonds
 * 
 * Return the length of a list.
 */
public class Length$Function implements Function, Serializable {

    private static final long serialVersionUID = 1L;

    public static final String NAME = "length$";

    /**
         * 
         */
    public Length$Function() {
	super();
    }

    public JamochaType getReturnType() {
	return JamochaType.LONG;
    }

    public JamochaValue executeFunction(Rete engine, Parameter[] params) throws EvaluationException {
	if (params != null && params.length == 1) {
	    JamochaValue list = params[0].getValue(engine);
	    if (list.equals(JamochaType.LIST)) {
		return JamochaValue.newLong(list.getListCount());
	    } else {
		throw new IllegalTypeException(JamochaType.LISTS, list.getType());
	    }
	}
	throw new IllegalParameterException(1, false);
    }

    public String getName() {
	return NAME;
    }

    public Class[] getParameter() {
	return new Class[] { ValueParam.class, ValueParam.class };
    }

    public String toPPString(Parameter[] params, int indents) {
	if (params != null && params.length > 0) {
	    StringBuffer buf = new StringBuffer();
	    buf.append("(length$ ");
	    for (int idx = 0; idx < params.length; idx++) {
		buf.append(" " + params[idx].getExpressionString());
	    }
	    buf.append(")");
	    return buf.toString();
	} else {
	    return "("+NAME+" <list-value>)\n" + "Function description:\n"
		    + "\t Returns the length of a list.";
	}
    }
}
