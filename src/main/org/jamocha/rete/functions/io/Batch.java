/*
 * Copyright 2002-2006 Peter Lin, 2007 Alexander Wilden, Uta Christoph
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
package org.jamocha.rete.functions.io;

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
import org.jamocha.parser.ParseException;
import org.jamocha.parser.Parser;
import org.jamocha.parser.ParserFactory;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.functions.FunctionDescription;

/**
 * @author Peter Lin
 * 
 * Loads and executes one or more given files. Files can be on a local drive or
 * on a remote machine accessible via http or some other protocol. Returns true
 * on success.
 */
public class Batch implements Function, Serializable {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Loads and executes one or more given files. Multiple arguments are separated by a blank. Files can be located on a local drive or on a remote machine accessible via http or some other protocol. Returns true on success.";
		}
		
		public int getParameterCount() {
			return 1;
		}

		public String getParameterDescription(int parameter) {
			return "File(s) to load and execute.";
		}

		public String getParameterName(int parameter) {
			return "fileName";
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
			return "(batch samples/view-test.clp)";
		}
	}

	private static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "batch";

	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	public String getName() {
		return NAME;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		JamochaValue result = JamochaValue.FALSE;
		if (params != null && params.length > 0) {
			for (int idx = 0; idx < params.length; idx++) {
				try {
					String input = params[idx].getValue(engine)
							.getStringValue();
					InputStream inStream;
					// Check for a protocol indicator at the beginning of the
					// String. If we have one use a URL.
					if (input.matches("^[a-zA-Z]+://.*")) {
						URL url = new URL(input);
						inStream = url.openConnection().getInputStream();
						// Otherwise treat it as normal file on the Filesystem
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

	public JamochaValue parse(Rete engine, InputStream ins)
			throws EvaluationException {
		try {
			Parser parser = ParserFactory.getParser(ins);
			Expression expr = null;
			while ((expr = parser.nextExpression()) != null) {
				expr.getValue(engine);
			}
			return JamochaValue.TRUE;
		} catch (ParseException e) {
			return new JamochaValue(JamochaType.STRING, e.getMessage());
		}
	}
}
