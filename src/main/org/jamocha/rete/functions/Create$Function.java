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
import java.util.ArrayList;
import java.util.List;

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
 * Creates a list of the given parameter values.
 */
public class Create$Function implements Function, Serializable {

    private static final long serialVersionUID = 1L;

    public static final String NAME = "create$";

    /**
         * 
         */
    public Create$Function() {
	super();
    }

    public JamochaType getReturnType() {
	return JamochaType.LIST;
    }

    public JamochaValue executeFunction(Rete engine, Parameter[] params) throws EvaluationException {
	if (params != null) {
	    List<JamochaValue> newListValues = new ArrayList<JamochaValue>();
	    for(int i=0;i<params.length;++i) {
		    JamochaValue value = params[i].getValue(engine);
		    if (value.equals(JamochaType.LIST)) {
			for(int j=0;j<value.getListCount();++j) {
			    newListValues.add(value.getListValue(j));
			}
		    } else {
			newListValues.add(value);
		    }
		
	    }
	    JamochaValue[] values = new JamochaValue[newListValues.size()];
	    newListValues.toArray(values);
	    return JamochaValue.newList(values);
	}
	throw new IllegalParameterException(0, true);
    }

    public String getName() {
	return NAME;
    }

    public Class[] getParameter() {
	return new Class[] { };
    }

    public String toPPString(Parameter[] params, int indents) {
	if (params != null && params.length > 0) {
	    StringBuffer buf = new StringBuffer();
	    buf.append("(");
	    for (int idx = 0; idx < params.length; idx++) {
		buf.append(" " + params[idx].getExpressionString());
	    }
	    buf.append(")");
	    return buf.toString();
	} else {
	    return "("+NAME+" <value>*)\n" + "Function description:\n"
		    + "\t Returns the a list of the given parameters.";
	}
    }
}
