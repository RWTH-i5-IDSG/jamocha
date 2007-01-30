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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ValueParam;

/**
 * @author Peter Lin
 * 
 * The purpose of spool function is to capture the output to a file, and make it
 * easier to record what happens. This is inspired by Oracle SqlPlus spool
 * function.
 */
public class SpoolFunction implements Function, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String SPOOL = "spool";

	/**
	 * 
	 */
	public SpoolFunction() {
		super();
	}

	public JamochaType getReturnType() {
		return JamochaType.BOOLEAN;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		JamochaValue result = JamochaValue.FALSE;
		if (params != null && params.length >= 2) {
			String name = params[0].getValue(engine).getStringValue();
			String file = params[1].getValue(engine).getStringValue();
			if (name.equals("off")) {
				// turn off spooling
				PrintWriter writer = engine.removePrintWriter(file);
				if (writer != null) {
					writer.flush();
					writer.close();
				}
			} else {
				// turn on spooling
				// we expected a file name
				try {
					File nfile = new File(file);
					nfile.createNewFile();
					FileOutputStream fos = new FileOutputStream(nfile);
					PrintWriter writer = new PrintWriter(fos);
					engine.addPrintWriter(name, writer);
					result = JamochaValue.TRUE;
				} catch (IOException e) {
					throw new EvaluationException(e);
				}
			}
		}
		return result;
	}

	public String getName() {
		return SPOOL;
	}

	public Class[] getParameter() {
		return new Class[] { ValueParam.class };
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(spool <name> <file>| off <name>)";
	}

}
