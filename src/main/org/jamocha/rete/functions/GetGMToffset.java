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
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.IllegalParameterException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;

/**
 * @author Josef Alexander Hahn
 * 
 */
public class GetGMToffset implements Function, Serializable {

    private static final long serialVersionUID = 1L;

    public static final String NAME = "getgmtoffset";

    /**
         * 
         */
    public GetGMToffset() {
	super();
    }

    /*
         * (non-Javadoc)
         * 
         * @see woolfel.engine.rete.Function#getReturnType()
         */
    public JamochaType getReturnType() {
	return JamochaType.DATETIME;
    }

    public JamochaValue executeFunction(Rete engine, Parameter[] params) throws EvaluationException {
	if (params != null) {
	    if (params.length == 1) {

		GregorianCalendar p1 = params[0].getValue(engine).getDateValue();
		return JamochaValue.newLong(p1.get(Calendar.ZONE_OFFSET) / (1000 * 60 * 60));
	    }
	}
	throw new IllegalParameterException(1, false);
    }

    public String getName() {
	return NAME;
    }

    public String toPPString(Parameter[] params, int indents) {
	if (params != null && params.length == 3) {
	    StringBuffer buf = new StringBuffer();
	    buf.append("(getgmtoffset");
	    for (int idx = 0; idx < params.length; idx++) {
		buf.append(" " + params[idx].getExpressionString());
	    }
	    buf.append(")");
	    return buf.toString();
	} else {
	    return "(getgmtoffset (<datetime> | <binding>) )\n" + "Function description:\n"
		    + "\t Returns the GMT-Offset from the given" + "DateTime-Object";
	}
    }
}
