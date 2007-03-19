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
package org.jamocha.rete.functions;

import java.io.Serializable;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.parser.ParserFactory;
import org.jamocha.parser.ParserNotFoundException;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ValueParam;

/**
 * This function either sets the default arser to the given one or prints out
 * the current default parser.
 * 
 * @author Alexander Wilden
 */
public class SetParserFunction implements Function, Serializable {

	private static final long serialVersionUID = 1L;

	public static final String SETPARSER = "set-parser";

	/**
	 * 
	 */
	public SetParserFunction() {
		super();
	}

	public JamochaType getReturnType() {
		return JamochaType.STRING;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		if (params != null && params.length == 1) {
			String defaultParser = params[0].getValue(engine).getStringValue();
			try {
				ParserFactory.setDefaultParser(defaultParser);
			} catch (ParserNotFoundException e) {
				throw new EvaluationException(
						"Error while setting the default parser to "
								+ defaultParser + ":\n" + e.getMessage(), e);
			}
		}
		return JamochaValue.newString(ParserFactory.getDefaultParser());
	}

	public String getName() {
		return SETPARSER;
	}

	public Class[] getParameter() {
		return new Class[] { ValueParam[].class };
	}

	public String toPPString(Parameter[] params, int indents) {
		if (params != null && params.length >= 0) {
			StringBuffer buf = new StringBuffer();

			buf.append("(set-parser ");
			int idx = 0;
			buf.append(" ").append(params[idx].getExpressionString());
			buf.append(")");
			return buf.toString();
		} else {
			return "(set-parser <parser-name>)\n"
					+ "Function description:\n"
					+ "\tIf <parser-name> name is given we try to make it the default parser.\n"
					+ "\tOtherwise the name of the current default parser is returned.\n"
					+ "\tNote: Changing the parser during runtime only makes sense for StringChannels.\n"
					+ "\t      StreamChannels must be reinitalized after changing the parser.";
		}
	}
}
