/*
 * Copyright 2002-2008 The Jamocha Team
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

package org.jamocha.languages.sl.sl2clips_adapter;

import org.jamocha.engine.Engine;
import org.jamocha.engine.Parameter;
import org.jamocha.engine.functions.AbstractFunction;
import org.jamocha.engine.functions.FunctionDescription;
import org.jamocha.languages.sl.parser.ParseException;
import org.jamocha.languages.sl.parser.SLParser;
import org.jamocha.languages.sl.sl2clips_adapter.configurations.ContentSLConfiguration;
import org.jamocha.languages.sl.sl2clips_adapter.configurations.SLCompileType;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.IllegalParameterException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;

public class SLMessageCompare extends AbstractFunction {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Compares two messages in SL by translating them to CLIPS and thereby receiving a kind of semantic representation. Then the result of a simple String comparison is returned.";
		}

		public int getParameterCount() {
			return 2;
		}

		public String getParameterDescription(int parameter) {
			switch (parameter) {
			case 0:
				return "String to compare to the second argument.";
			case 1:
				return "String to compare to the first argument.";
			}
			return "";
		}

		public String getParameterName(int parameter) {
			switch (parameter) {
			case 0:
				return "stringOne";
			case 1:
				return "stringTwo";
			}
			return "";
		}

		public JamochaType[] getParameterTypes(int parameter) {
			switch (parameter) {
			case 0:
				return JamochaType.STRINGS;
			case 1:
				return JamochaType.STRINGS;
			}
			return null;
		}

		public JamochaType[] getReturnType() {
			return JamochaType.BOOLEANS;
		}

		public boolean isParameterCountFixed() {
			return true;
		}

		public boolean isParameterOptional(int parameter) {
			return false;
		}

		public String getExample() {
			return null;
		}

		public boolean isResultAutoGeneratable() {
			return true;
		}

		public Object getExpectedResult() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	private static final long serialVersionUID = 1L;

	public static final FunctionDescription DESCRIPTION = new Description();

	public static final String NAME = "SL-message-compare";

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
		if (params != null && params.length == 2) {
			String message1 = params[0].getValue(engine).getStringValue();
			String message2 = params[1].getValue(engine).getStringValue();

			try {
				ContentSLConfiguration contentConf1 = SLParser.parse(message1);
				ContentSLConfiguration contentConf2 = SLParser.parse(message2);
				message1 = contentConf1
						.compile(SLCompileType.ACTION_AND_ASSERT);
				message2 = contentConf2
						.compile(SLCompileType.ACTION_AND_ASSERT);
				if (message1.equalsIgnoreCase(message2))
					return JamochaValue.TRUE;
			} catch (ParseException e) {
				throw new EvaluationException(e);
			}

		} else
			throw new IllegalParameterException(1);
		return result;
	}
}
