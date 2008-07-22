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

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.IllegalParameterException;
import org.jamocha.parser.IllegalTypeException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.engine.Parameter;
import org.jamocha.engine.Engine;
import org.jamocha.engine.functions.AbstractFunction;
import org.jamocha.engine.functions.FunctionDescription;

/**
 * @author Alexander Wilden
 * 
 * Returns the item of a list at the specified index.
 * <p>
 * Attention: Lists in Jamocha start with index 1.
 * </p>
 */
public class Nth$ extends AbstractFunction {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Returns the item of a list at the specified index. Attention: Lists in Jamocha start with index 1.";
		}

		public int getParameterCount() {
			return 2;
		}

		public String getParameterDescription(int parameter) {
			switch (parameter) {
			case 0:
				return "Index of the item in the list to return. Needs to be in the bounds of someList.";
			case 1:
				return "List to return the specified item from.";
			}
			return "";
		}

		public String getParameterName(int parameter) {
			switch (parameter) {
			case 0:
				return "index";
			case 1:
				return "someList";
			}
			return "";
		}

		public JamochaType[] getParameterTypes(int parameter) {
			switch (parameter) {
			case 0:
				return JamochaType.LONGS;
			case 1:
				return JamochaType.LISTS;
			}
			return JamochaType.NONE;
		}

		public JamochaType[] getReturnType() {
			return JamochaType.ANY;
		}

		public boolean isParameterCountFixed() {
			return true;
		}

		public boolean isParameterOptional(int parameter) {
			return false;
		}

		public String getExample() {
			return "(nth$ 3 (create$ cheese milk eggs bread sausages))";
		}

		public boolean isResultAutoGeneratable() {
			return true;
		}

		public Object getExpectedResult() {
			return "eggs";
		}
	}

	public static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "nth$";

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
		if (params != null && params.length == 2) {
			int index = (int) params[0].getValue(engine).getLongValue();
			JamochaValue list = params[1].getValue(engine);
			if (list.is(JamochaType.LIST)) {
				if (index < 1 || index > list.getListCount())
					throw new EvaluationException("Index " + index
							+ " is out of bounds (1 - " + list.getListCount()
							+ ").");
				else
					return list.getListValue(index - 1);
			} else
				throw new IllegalTypeException(JamochaType.LISTS, list
						.getType());
		}
		throw new IllegalParameterException(2, false);
	}
}