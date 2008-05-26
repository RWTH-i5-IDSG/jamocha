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

package org.jamocha.engine.functions.ruleengine;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.IllegalParameterException;
import org.jamocha.parser.IllegalTypeException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.engine.BoundParam;
import org.jamocha.engine.Parameter;
import org.jamocha.engine.Engine;
import org.jamocha.engine.functions.AbstractFunction;
import org.jamocha.engine.functions.FunctionDescription;

/**
 * @author Peter Lin
 * 
 * Binds any value to a variable visible in the scope the bind takes place.
 */
public class Bind extends AbstractFunction {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Binds any value to a variable visible in the scope the bind takes place.";
		}

		public int getParameterCount() {
			return 2;
		}

		public String getParameterDescription(int parameter) {
			switch (parameter) {
			case 0:
				return "Variable to bind the value to. Name should start with a '?': ?variableName .";
			case 1:
				return "Value to bind to the variable.";
			}
			return "";
		}

		public String getParameterName(int parameter) {
			switch (parameter) {
			case 0:
				return "variable";
			case 1:
				return "value";
			}
			return "";
		}

		public JamochaType[] getParameterTypes(int parameter) {
			switch (parameter) {
			case 0:
				return JamochaType.IDENTIFIERS;
			case 1:
				return JamochaType.ANY;
			}
			return JamochaType.NONE;
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
			return "(bind ?helo 'Jamocha rulez!') (printout t ?helo)";
		}

		public boolean isResultAutoGeneratable() {
			return true;
		}

		public Object getExpectedResult() {
			return null;
		}
	}

	public static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "bind";

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
		if (params.length == 2) {
			String variableName = null;

			if (params[0] instanceof JamochaValue) {
				JamochaValue identifier = params[0].getValue(engine);
				if (!identifier.getType().equals(JamochaType.IDENTIFIER))
					throw new IllegalTypeException(JamochaType.IDENTIFIERS,
							identifier.getType());
				variableName = identifier.getIdentifierValue();
			} else if (params[0] instanceof BoundParam)
				variableName = ((BoundParam) params[0]).getVariableName();
			JamochaValue value = params[1].getValue(engine);
			if (value != null) {
				engine.setBinding(variableName, value);
				result = JamochaValue.TRUE;
			}
		} else
			throw new IllegalParameterException(2);
		return result;
	}
}