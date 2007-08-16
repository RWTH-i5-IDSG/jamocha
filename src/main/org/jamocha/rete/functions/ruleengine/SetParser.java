/*
 * Copyright 2007 Alexander Wilden
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

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.parser.ModeNotFoundException;
import org.jamocha.parser.ParserFactory;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.functions.AbstractFunction;
import org.jamocha.rete.functions.FunctionDescription;

/**
 * @author Alexander Wilden
 * 
 * Sets the default parser to the one given as first argument or prints
 * out the current default parser, if no argument is given.
 * <p>
 * Note: Changing the parser during runtime only makes sense for StringChannels.
 * StreamChannels must be reinitalized after changing the parser.
 * </p>
 */
public class SetParser extends AbstractFunction {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Sets the default parser to the one given as first argument or prints out the current default " +
					"parser, if no argument is given.\n" +
					"Note: Changing the parser during runtime only makes sense for StringChannels. " +
					"StreamChannels must be reinitalized after changing the parser.";
		}

		public int getParameterCount() {
			return 1;
		}

		public String getParameterDescription(int parameter) {
			return "New default parser to use.";
		}

		public String getParameterName(int parameter) {
			return "parser";
		}

		public JamochaType[] getParameterTypes(int parameter) {
			return JamochaType.STRINGS;
		}

		public JamochaType[] getReturnType() {
			return JamochaType.STRINGS;
		}

		public boolean isParameterCountFixed() {
			return false;
		}

		public boolean isParameterOptional(int parameter) {
			return true;
		}

		public String getExample() {
			return "(set-parser)";
		}

		public boolean isResultAutoGeneratable() {
			// TODO Auto-generated method stub
			return false;
		}
	}

	private static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "set-parser";

	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	public String getName() {
		return NAME;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		if (params != null && params.length == 1) {
			String defaultMode = params[0].getValue(engine).getStringValue();
			try {
				ParserFactory.setDefaultMode(defaultMode);
			} catch (ModeNotFoundException e) {
				throw new EvaluationException(
						"Error while setting the default parser to "
								+ defaultMode + ":\n" + e.getMessage(), e);
			}
		}
		return JamochaValue.newString(ParserFactory.getDefaultParser());
	}
}
