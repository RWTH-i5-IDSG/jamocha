/*
 * Copyright 2002-2006 Peter Lin, 2007 Alexander Wilden
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
package org.jamocha.rete.functions.ruleengine;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.functions.AbstractFunction;
import org.jamocha.rete.functions.FunctionDescription;

/**
 * @author Peter Lin
 * 
 * Captures the output to a file, and makes it easier to record what happens. 
 * This is inspired by Oracle SqlPlus spool function.
 */
public class Spool extends AbstractFunction {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Captures the output to a file, and makes it easier to record what happens. This is " +
					"inspired by Oracle SqlPlus spool function.";
		}

		public int getParameterCount() {
			return 2;
		}

		public String getParameterDescription(int parameter) {
			switch (parameter) {
			case 0:
				return "Use \"on\" or \"off\" for spooling.";
			case 1:
				return "Filename to spool to, if switch is \"on\".";
			}
			return "";
		}

		public String getParameterName(int parameter) {
			switch (parameter) {
			case 0:
				return "switch";
			case 1:
				return "fileName";
			}
			return "";
		}

		public JamochaType[] getParameterTypes(int parameter) {
			return JamochaType.STRINGS;
		}

		public JamochaType[] getReturnType() {
			return JamochaType.BOOLEANS;
		}

		public boolean isParameterCountFixed() {
			return false;
		}

		public boolean isParameterOptional(int parameter) {
			return (parameter > 0);
		}

		public String getExample() {
			return "(spool on /var/tmp/spool.clp)";
		}

		public boolean isResultAutoGeneratable() {
			// TODO Auto-generated method stub
			return false;
		}
	}

	private static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "spool";

	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	public String getName() {
		return NAME;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		JamochaValue result = JamochaValue.FALSE;
		if (params != null && params.length >= 2) {
			String name = params[0].getValue(engine).getStringValue();
			String file = params[1].getValue(engine).getStringValue();
			if (name.equalsIgnoreCase("off")) {
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
}