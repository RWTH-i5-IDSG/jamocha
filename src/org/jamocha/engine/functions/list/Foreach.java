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

package org.jamocha.engine.functions.list;

import org.jamocha.engine.BoundParam;
import org.jamocha.engine.Engine;
import org.jamocha.engine.Parameter;
import org.jamocha.engine.functions.AbstractFunction;
import org.jamocha.engine.functions.FunctionDescription;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.IllegalParameterException;
import org.jamocha.parser.IllegalTypeException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;

/**
 * @author Christoph Emonds, Sebastian Reinartz
 * 
 * Evaluates expressions for all items in a list.
 */
public class Foreach extends AbstractFunction {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Evaluates expressions for all items in a list.";
		}

		public int getParameterCount() {
			return 2;
		}

		public String getParameterDescription(int parameter) {
			switch (parameter) {
			case 0:
				return "Name of the variable for each list item.";
			case 1:
				return "List to work on.";
			}
			return "An expression that is evaluated for each item of the list.";
		}

		public String getParameterName(int parameter) {
			switch (parameter) {
			case 0:
				return "variableName";
			case 1:
				return "list";
			}
			return "someExpression";
		}

		public JamochaType[] getParameterTypes(int parameter) {
			switch (parameter) {
			case 0:
				return JamochaType.IDENTIFIERS;
			case 1:
				return JamochaType.LISTS;
			}
			return JamochaType.ANY;
		}

		public JamochaType[] getReturnType() {
			return JamochaType.ANY;
		}

		public boolean isParameterCountFixed() {
			return false;
		}

		public boolean isParameterOptional(int parameter) {
			switch (parameter) {
			case 0:
			case 1:
				return false;
			}
			return true;
		}

		public String getExample() {
			return "(bind ?x (create$ cheese milk eggs bread))\n"
					+ "(bind ?res \"We need: \")\n"
					+ "(foreach ?item ?x (bind ?res (str-cat ?res ?item \", \")))\n"
					+ "(return ?res)";
		}

		public boolean isResultAutoGeneratable() {
			return true;
		}

		public Object getExpectedResult() {
			return "We need: cheese, milk, eggs, bread, ";
		}
	}

	public static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "foreach";

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
		if (params != null && params.length >= 2)
			if (params[0] instanceof BoundParam) {
				BoundParam variable = (BoundParam) params[0];
				JamochaValue list = params[1].getValue(engine);
				if (list.is(JamochaType.LIST)) {
					JamochaValue result = JamochaValue.NIL;
					for (int j = 0; j < list.getListCount(); ++j) {
						engine.setBinding(variable.getVariableName(), list
								.getListValue(j));
						for (int i = 2; i < params.length; ++i)
							result = params[i].getValue(engine);
					}
					return result;
				} else
					throw new IllegalTypeException(JamochaType.LISTS, list
							.getType());
			} else
				throw new EvaluationException(
						"First parameter must be a binding.");
		throw new IllegalParameterException(2, true);
	}
}
