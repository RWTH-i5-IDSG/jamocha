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
import java.io.StringReader;

import org.jamocha.messagerouter.CLIPSInterpreter;
import org.jamocha.parser.clips.CLIPSParser;
import org.jamocha.parser.clips.ParseException;
import org.jamocha.rete.BoundParam;
import org.jamocha.rete.Constants;
import org.jamocha.rete.Function;
import org.jamocha.rete.FunctionParam2;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;
import org.jamocha.rete.ValueParam;

/**
 * @author Sebastian Reinartz
 * 
 * Functional equivalent of (eval "(+ 1 3)") in CLIPS and JESS.
 */
public class EvalFunction implements Function, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String EVAL = "eval";

	/**
	 * 
	 */
	public EvalFunction() {
		super();
	}

	public int getReturnType() {
		return Constants.OBJECT_TYPE;
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		ReturnVector result = null;
		if (params != null && params.length > 0) {
			String command = null;
			if (params[0] instanceof ValueParam) {
				ValueParam n = (ValueParam) params[0];
				command = n.getStringValue();
			} else if (params[0] instanceof BoundParam) {
				BoundParam bp = (BoundParam) params[0];
				command = engine.getBinding(bp.getVariableName()).toString();
			} else if (params[0] instanceof FunctionParam2) {
				FunctionParam2 n = (FunctionParam2) params[0];
				n.setEngine(engine);
				n.lookUpFunction();
				ReturnVector rval = (ReturnVector) n.getValue();
				command = rval.firstReturnValue().getStringValue();
			}
			if (command != null) {
				result = eval(engine, command);
			}
		}
		return result;
	}

	public ReturnVector eval(Rete engine, String command) {
		ReturnVector result = null;
		try {
			CLIPSParser parser = new CLIPSParser(engine, new StringReader(
					command));
			CLIPSInterpreter interpreter = new CLIPSInterpreter(engine);
			Object expr = null;
			while ((expr = parser.basicExpr()) != null) {
				result = interpreter.executeCommand(expr);
			}
		} catch (ParseException e) {
			// we should report the error
			e.printStackTrace();
		}
		return result;
	}

	public String getName() {
		return EVAL;
	}

	public Class[] getParameter() {
		return new Class[] { ValueParam.class };
	}

	public String toPPString(Parameter[] params, int indents) {
		if (params != null && params.length > 0) {
			StringBuffer buf = new StringBuffer();
			buf.append("(eval");
			for (int idx = 0; idx < params.length; idx++) {
				if (params[idx] instanceof BoundParam) {
					BoundParam bp = (BoundParam) params[idx];
					buf.append(" ?" + bp.getVariableName());
				} else if (params[idx] instanceof ValueParam) {
					buf.append(" \"" + params[idx].getStringValue() + "\"");
				}
			}
			buf.append(")");
			return buf.toString();
		} else {
			return "(eval <string expressions>)\n" + "Command description:\n"
					+ "\tEvaluates the content of a string.";
		}
	}
}
