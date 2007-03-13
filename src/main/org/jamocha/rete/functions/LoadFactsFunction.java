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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.parser.clips.CLIPSParser;
import org.jamocha.parser.clips.ParseException;
import org.jamocha.rete.Deffact;
import org.jamocha.rete.Deftemplate;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ValueParam;
import org.jamocha.rete.exception.AssertException;

/**
 * @author Peter Lin LoadFunction will create a new instance of CLIPSParser and
 *         load the facts in the data file.
 */
public class LoadFactsFunction implements Function, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String LOAD = "load-facts";

	/**
	 * 
	 */
	public LoadFactsFunction() {
		super();
	}

	public JamochaType getReturnType() {
		return JamochaType.BOOLEAN;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		JamochaValue result = JamochaValue.FALSE;
		if (params != null && params.length > 0) {
			for (int idx = 0; idx < params.length; idx++) {
				String input = params[idx].getValue(engine).getStringValue();

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
					CLIPSParser parser = new CLIPSParser(inStream);
					List data = parser.loadExpr();
					Iterator itr = data.iterator();
					while (itr.hasNext()) {
						Object val = itr.next();
						ValueParam[] vp = (ValueParam[]) val;
						Deftemplate tmpl = (Deftemplate) engine
								.getCurrentFocus()
								.getTemplate(
										vp[0].getValue(engine).getStringValue());
						Deffact fact = (Deffact) tmpl.createFact(
								(Object[]) vp[1].getValue(engine)
										.getObjectValue(), -1);

						engine.assertFact(fact);
					}
					inStream.close();
					result = JamochaValue.TRUE;
				} catch (FileNotFoundException e) {
					engine.writeMessage(e.getMessage(), "t");
				} catch (ParseException e) {
					engine.writeMessage(e.getMessage(), "t");
				} catch (AssertException e) {
					engine.writeMessage(e.getMessage(), "t");
				} catch (MalformedURLException e) {
					engine.writeMessage(e.getMessage(), "t");
				} catch (IOException e) {
					engine.writeMessage(e.getMessage(), "t");
				}
			}
		}
		return result;
	}

	public String getName() {
		return LOAD;
	}

	public Class[] getParameter() {
		return new Class[] { ValueParam.class };
	}

	public String toPPString(Parameter[] params, int indents) {
		if (params != null && params.length > 0) {
			StringBuffer buf = new StringBuffer();
			buf.append("(load-facts");
			for (int idx = 0; idx < params.length; idx++) {
				buf.append(" ").append(params[idx].getExpressionString());
			}
			buf.append(")");
			return buf.toString();
		} else {
			return "(load <filename>)\n" + "Command description:\n"
					+ "\tLoad the file <filename>.";
		}
	}
}
