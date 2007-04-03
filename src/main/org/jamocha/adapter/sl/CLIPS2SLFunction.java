/*
 * Copyright 2007 Alexander Wilden
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://jamocha.sourceforge.net/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.jamocha.adapter.sl;

import java.io.Serializable;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.IllegalParameterException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;

/**
 * Translates CLIPS-Code resp. JamochaValues to SL.
 * 
 * @author Alexander Wilden
 */
public class CLIPS2SLFunction implements Function, Serializable {

    private static final long serialVersionUID = 1L;

    public static final String CLIPS_2_SL = "clips2sl";

    /**
         * 
         */
    public CLIPS2SLFunction() {
	super();
    }

    public JamochaType getReturnType() {
	return JamochaType.STRING;
    }

    public JamochaValue executeFunction(Rete engine, Parameter[] params) throws EvaluationException {
	JamochaValue result = JamochaValue.newString("");
	if (params != null && params.length == 1) {
	    JamochaValue value = params[0].getValue(engine);
	    String slCode = CLIPS2SL.getSL(value);
	    result = JamochaValue.newString(slCode);
	} else {
	    throw new IllegalParameterException(1);
	}
	return result;
    }

    public String getName() {
	return CLIPS_2_SL;
    }

    public String toPPString(Parameter[] params, int indents) {
	if (params != null && params.length > 0) {
	    StringBuffer buf = new StringBuffer();
	    buf.append("(clips2sl");
	    for (int idx = 0; idx < params.length; idx++) {
		buf.append(" ").append(params[idx].getExpressionString());
	    }
	    buf.append(")");
	    return buf.toString();
	} else {
	    return "(clips2sl <string expression>)\n" + "Command description:\n"
		    + "\tTranslates a string in CLIPS to SL.";
	}
    }
}