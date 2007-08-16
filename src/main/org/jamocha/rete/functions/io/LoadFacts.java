/*
 * Copyright 2007 Alexander Wilden, Uta Christoph
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.jamocha.messagerouter.StringChannel;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.AbstractFunction;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.functions.FunctionDescription;

/**
 * @author Alexander Wilden
 * 
 * Reads a given URL and surrounds the content with (assert ... ). So a list of
 * facts will be assert in the rule engine via just one call to AssertFunction.
 * The result will be TRUE if any of the urls could be parsed successfully and
 * FALSE otherwise. On failure an exception is thrown.
 */
public class LoadFacts extends AbstractFunction {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Reads a file from the given location and surrounds the content with (assert ... ). So a list "
					+ "of facts can be asserted into the rule engine with one single call. The result is true if "
					+ "any of the given files could be parsed successfully."
					+ // and false otherwise. On failure an exception is
						// thrown.";
					"Attention a corresponding Jamocha template must be defined in order to actually import the"
					+ "facts into the engine.";
		}

		public int getParameterCount() {
			return 1;
		}

		public String getParameterDescription(int parameter) {
			return "Path(s) to one or more file(s) containing facts without assert-call to be asserted.";
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
			return "(deftemplate transaction\n"
					+ "  (slot accountId (type STRING))\n"
					+ "  (slot countryCode (type STRING))\n"
					+ "  (slot cusip (type INTEGER))\n"
					+ "  (slot issuer (type STRING))\n"
					+ "  (slot total (type DOUBLE))\n" + ")\n"
					+ "(load-facts samples/data.clp)\n";
		}

		public boolean isResultAutoGeneratable() {
			// TODO Auto-generated method stub
			return false;
		}
	}

	private static final long serialVersionUID = 1L;

	public static final FunctionDescription DESCRIPTION = new Description();

	public static final String NAME = "load-facts";

	private StringChannel loadFactsChannel = null;

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
			String input;
			for (int idx = 0; idx < params.length; idx++) {
				input = params[idx].getValue(engine).getStringValue();
				try {
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
					result = this.assertFacts(engine, new BufferedReader(
							new InputStreamReader(inStream)));
					inStream.close();
				} catch (FileNotFoundException e) {
				} catch (IOException e) {
					throw new EvaluationException(
							"Error while loading facts from: " + input, e);
				}
			}
		}
		return result;
	}

	public JamochaValue assertFacts(Rete engine, BufferedReader reader)
			throws IOException {
		if (loadFactsChannel == null) {
			loadFactsChannel = engine.getMessageRouter().openChannel(
					"loadFactsChannel");
		}
		StringBuilder buffer = new StringBuilder("(assert ");
		while (reader.ready()) {
			buffer.append(reader.readLine());
		}
		buffer.append(")");
		loadFactsChannel.executeCommand(buffer.toString());
		return JamochaValue.TRUE;
	}
}
