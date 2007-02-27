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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.Expression;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.parser.clips.CLIPSParser;
import org.jamocha.parser.clips.ParseException;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ValueParam;

/**
 * @author Peter Lin
 * 
 * Functional equivalent of (batch file.clp) in CLIPS and JESS.
 */
public class BatchFunction implements Function, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String BATCH = "batch";

	/**
	 * 
	 */
	public BatchFunction() {
		super();
	}

	public JamochaType getReturnType() {
		return JamochaType.BOOLEAN;
	}

	/**
	 * method will attempt to load one or more files. If batch is called without
	 * any parameters, the function does nothing and just returns. TODO - finish
	 * implementing the method, once the parser wrapper is done I can finish
	 * this method
	 */
	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		JamochaValue result = JamochaValue.FALSE;
		if (params != null && params.length > 0) {
			for (int idx = 0; idx < params.length; idx++) {
				try {
					String input = params[idx].getValue(engine).getStringValue();
					InputStream inStream;
					if (input.startsWith("http://")) {
						URL url = new URL(input);
						inStream = url.openConnection().getInputStream();
					} else {
						inStream = new FileInputStream(new File(input));
					}
					result = this.parse(engine, inStream);
					inStream.close();
				} catch (FileNotFoundException e) {
				} catch (IOException e) {
					throw new EvaluationException(e);
				}
			}
		}
		return result;
	}

	/**
	 * method does the actual work of creating a CLIPSParser and parsing the
	 * file.
	 * 
	 * @param engine
	 * @param ins
	 * @param rv
	 * @throws EvaluationException
	 */
	public JamochaValue parse(Rete engine, InputStream ins)
			throws EvaluationException {
		try {
			CLIPSParser parser = new CLIPSParser(ins);
			Expression expr = null;
			while ((expr = parser.nextExpression()) != null) {
				expr.getValue(engine);
			}
			return JamochaValue.TRUE;
		} catch (ParseException e) {
			// we should report the error
			e.printStackTrace();
		}
		return JamochaValue.FALSE;
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
				buf.append(" ").append(params[idx].getExpressionString());
			}
			buf.append(")");
			return buf.toString();
		} else {
			return "(batch <filename>)\n" + "Command description:\n"
					+ "\tLoads and executes the file <filename>.";
		}
	}
}
