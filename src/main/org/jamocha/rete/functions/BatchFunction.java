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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.IOException;
import java.io.Serializable;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.parser.clips.CLIPSParser;
import org.jamocha.parser.clips.ParseException;
import org.jamocha.rete.BoundParam;
import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnValue;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Deftemplate;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ValueParam;
import org.jamocha.rete.exception.CompileRuleException;
import org.jamocha.rule.*;


/**
 * @author Peter Lin
 *
 * Functional equivalent of (batch file.clp) in CLIPS and JESS.
 */
public class BatchFunction implements Function, Serializable {

	public static final String BATCH = "batch";

	/**
	 * 
	 */
	public BatchFunction() {
		super();
	}

	public JamochaType getReturnType() {
		return Constants.BOOLEAN_OBJECT;
	}

	/**
	 * method will attempt to load one or more files. If batch is called without
	 * any parameters, the function does nothing and just returns.
	 * TODO - finish implementing the method, once the parser wrapper is done
	 * I can finish this method
	 */
	public JamochaValue executeFunction(Rete engine, Parameter[] params) throws EvaluationException {
		DefaultReturnVector rv = new DefaultReturnVector();
		if (params != null && params.length > 0) {
			for (int idx = 0; idx < params.length; idx++) {
				try {
					InputStream ins = new FileInputStream(params[idx]
							.getStringValue());
					this.parse(engine, ins, rv);
                    ins.close();
				} catch (FileNotFoundException e) {
					// we should report the error
					rv.addReturnValue(new DefaultReturnValue(
							Constants.BOOLEAN_OBJECT, new Boolean(false)));
                } catch (IOException e) {
                    
				}
			}
		}
		return rv;
	}

	/**
	 * method does the actual work of creating a CLIPSParser and parsing
	 * the file.
	 * @param engine
	 * @param ins
	 * @param rv
	 */
	public void parse(Rete engine, InputStream ins, DefaultReturnVector rv) {
		try {
			CLIPSParser parser = new CLIPSParser(engine, ins);
			Object expr = null;
			while ((expr = parser.basicExpr()) != null) {
				if (expr instanceof Defrule) {
					Defrule rl = (Defrule) expr;
					engine.getRuleCompiler().addRule(rl);
				} else if (expr instanceof Deftemplate) {
					Deftemplate dft = (Deftemplate) expr;
					engine.getCurrentFocus().addTemplate(dft, engine,
							engine.getWorkingMemory());
				} else if (expr instanceof Function) {
					Function fnc = (Function) expr;
					fnc.executeFunction(engine, null);
				}
			}
			if (rv != null) {
				rv.addReturnValue(new DefaultReturnValue(
						Constants.BOOLEAN_OBJECT, new Boolean(true)));
			}
		} catch (ParseException e) {
			// we should report the error
            e.printStackTrace();
			if (rv != null) {
				rv.addReturnValue(new DefaultReturnValue(
						Constants.BOOLEAN_OBJECT, new Boolean(false)));
			}
		}
	}

	public String getName() {
		return BATCH;
	}

	public Class[] getParameter() {
		return new Class[] { ValueParam.class };
	}

	public String toPPString(Parameter[] params, int indents) {
		if (params != null && params.length > 0) {
			StringBuffer buf = new StringBuffer();
			buf.append("(batch");
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
			return "(batch <filename>)\n" +
					"Command description:\n" +
					"\tLoads and executes the file <filename>.";
		}
	}
}
