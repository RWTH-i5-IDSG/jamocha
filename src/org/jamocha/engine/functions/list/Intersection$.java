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

package org.jamocha.engine.functions.list;

import java.util.LinkedList;
import java.util.List;

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
 * Returns the intersection of its arguments without duplicates. The order of
 * returned items corresponds to the one in the first list.
 */
public class Intersection$ extends AbstractFunction {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Returns the intersection of its two arguments without duplicates. The order of returned items corresponds to the one in the first list. ";
		}

		public int getParameterCount() {
			return 2;
		}

		public String getParameterDescription(int parameter) {
			switch (parameter) {
			case 0:
				return "First list for intersection.";
			case 1:
				return "Second list for intersection.";
			}
			return "";
		}

		public String getParameterName(int parameter) {

			switch (parameter) {
			case 0:
				return "firstList";
			case 1:
				return "secondList";
			}
			return "";
		}

		public JamochaType[] getParameterTypes(int parameter) {
			return JamochaType.LISTS;
		}

		public JamochaType[] getReturnType() {
			return JamochaType.LISTS;
		}

		public boolean isParameterCountFixed() {
			return false;
		}

		public boolean isParameterOptional(int parameter) {
			return false;
		}

		public String getExample() {
			return "(intersection$ (create$ 3 a b 1 c) (create$ e 1 d a f c))";
		}

		public boolean isResultAutoGeneratable() {
			return true;
		}

		public Object getExpectedResult() {
			return "[a, 1, c]";
		}
	}

	public static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "intersection$";

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
			JamochaValue firstList = params[0].getValue(engine);
			JamochaValue secondList = params[1].getValue(engine);
			JamochaValue current;
			if (!firstList.is(JamochaType.LIST))
				throw new IllegalTypeException(JamochaType.LISTS, firstList
						.getType());
			if (!secondList.is(JamochaType.LIST))
				throw new IllegalTypeException(JamochaType.LISTS, secondList
						.getType());
			List<JamochaValue> tmpList = new LinkedList<JamochaValue>();
			outer: for (int i = 0; i < firstList.getListCount(); ++i) {
				current = firstList.getListValue(i);
				if (tmpList.contains(current))
					continue outer;
				for (int j = 0; j < secondList.getListCount(); ++j)
					if (current.equals(secondList.getListValue(j))) {
						tmpList.add(current);
						continue outer;
					}
			}
			JamochaValue[] res = new JamochaValue[tmpList.size()];
			for (int i = 0; i < res.length; ++i)
				res[i] = tmpList.get(i);
			return JamochaValue.newList(res);
		}
		throw new IllegalParameterException(1, false);
	}
}
