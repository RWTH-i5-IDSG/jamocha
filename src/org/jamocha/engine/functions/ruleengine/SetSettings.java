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

package org.jamocha.engine.functions.ruleengine;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.engine.Parameter;
import org.jamocha.engine.Engine;
import org.jamocha.engine.functions.AbstractFunction;
import org.jamocha.engine.functions.FunctionDescription;
import org.jamocha.settings.JamochaSettings;

/**
 * set the setting value for given setting name
 * 
 * @author Sebastian Reinartz
 */
public class SetSettings extends AbstractFunction {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Jamocha settings can be set by this function.";
		}

		public int getParameterCount() {
			return 2;
		}

		public String getParameterDescription(int parameter) {
			if (parameter == 0)
				return "Name of property to be set";
			return "Property value";
		}

		public String getParameterName(int parameter) {
			if (parameter == 0)
				return "property name";
			return "property value";
		}

		public JamochaType[] getParameterTypes(int parameter) {
			return JamochaType.STRINGS;
		}

		public JamochaType[] getReturnType() {
			return JamochaType.STRINGS;
		}

		public boolean isParameterCountFixed() {
			return true;
		}

		public boolean isParameterOptional(int parameter) {
			return false;
		}

		public String getExample() {
			return "(set watch-activations true)";
		}

		public boolean isResultAutoGeneratable() {
			return false;
		}

		public Object getExpectedResult() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "set-settings";

	public SetSettings() {
		aliases.add("set");
	}

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
		JamochaSettings prefs = JamochaSettings.getInstance();
		// do we want to set specific setting?
		if (params != null && params.length == 2) {
			String property = params[0].getValue(engine).getStringValue();
			// test: TODO: implement mappin jamochatype ->settings type:
			String value = params[1].getValue(engine).implicitCast(
					JamochaType.STRING).toString();

			boolean result = prefs.set(property, value);
			return result ? JamochaValue.TRUE : JamochaValue.FALSE;
		}
		// no params: list all settings:
		else if (params != null && params.length == 0) {
			engine.writeMessage(prefs.getSettingsTable());
			return JamochaValue.TRUE;
		}

		return JamochaValue.FALSE;
	}
}
