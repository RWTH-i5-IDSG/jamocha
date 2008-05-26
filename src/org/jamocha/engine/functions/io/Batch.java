/*
 * Copyright 2002-2008 Peter Lin & The Jamocha Team
 * 
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

package org.jamocha.engine.functions.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.Expression;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.parser.ParseException;
import org.jamocha.parser.Parser;
import org.jamocha.parser.ParserFactory;
import org.jamocha.engine.Parameter;
import org.jamocha.engine.Engine;
import org.jamocha.engine.functions.AbstractFunction;
import org.jamocha.engine.functions.FunctionDescription;

/**
 * @author Peter Lin
 * 
 * Loads and executes one or more given files. Files can be on a local drive or
 * on a remote machine accessible via http or some other protocol. Returns true
 * on success.
 */
public class Batch extends AbstractFunction {

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
			return parameter > 0;
		}

		public String getExample() {
			return "(batch samples/view-test.clp)";
		}

		public boolean isResultAutoGeneratable() {
			// TODO Auto-generated method stub
			return false;
		}

		public Object getExpectedResult() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	private static final long serialVersionUID = 1L;

	public static final FunctionDescription DESCRIPTION = new Description();

	public static final String NAME = "batch";

	@Override
	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public JamochaValue executeFunction(Engine engine, Parameter[] params)
			throws EvaluationException {
		JamochaValue result = JamochaValue.FALSE;
		if (params != null && params.length > 0)
			for (int idx = 0; idx < params.length; idx++) {
				String input = params[idx].getValue(engine).getStringValue();
				try {
					InputStream inStream;
					// Check for a protocol indicator at the beginning of the
					// String. If we have one use a URL.
					if (input.matches("^[a-zA-Z]+://.*")) {
						URL url = new URL(input);
						if (url.getProtocol().equals("internal"))
							inStream = org.jamocha.Constants.class
									.getResourceAsStream(url.getPath());
						else
							inStream = url.openConnection().getInputStream();
					} else {
						File file = new File(input);
						if (!file.exists()) {
							String[] paths = Engine.getJamochaSearchPaths();
							for (String path : paths) {
								file = new File(path + input);
								if (file.exists())
									break;
							}
						}
						inStream = new FileInputStream(new File(input));
					}
					result = parse(engine, inStream);
					inStream.close();
				} catch (FileNotFoundException e) {
					throw new EvaluationException("File not found: " + input, e);
				} catch (IOException e) {
					throw new EvaluationException("Error reading file: "
							+ input, e);
				} catch (Exception e) {
					throw new EvaluationException("Error while parsing file: "
							+ input, e);
				}
			}
		return result;
	}

	public JamochaValue parse(Engine engine, InputStream ins)
			throws EvaluationException {
		try {
			Parser parser = ParserFactory.getParser(ins);
			Expression expr = null;
			while ((expr = parser.nextExpression()) != null)
				expr.getValue(engine);
			return JamochaValue.TRUE;
		} catch (ParseException e) {
			return JamochaValue.newString(e.getMessage());
		}
	}
}
