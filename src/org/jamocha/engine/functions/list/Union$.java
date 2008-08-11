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

import java.util.LinkedList;
import java.util.List;

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
 * @author Alexander Wilden
 * 
 * Returns the union of its arguments without duplicates.
 */
public class Union$ extends AbstractFunction {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Returns the union of its arguments without duplicates.";
		}

		public int getParameterCount() {
			return 1;
		}

		public String getParameterDescription(int parameter) {
			return "List to unify with the other arguments.";
		}

		public String getParameterName(int parameter) {
			return "list";
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
			return parameter > 0;
		}

		public String getExample() {
			return "(union$ (create$ a b c) (create$ e d a f) (create$ 1 2 d))";
		}

		public boolean isResultAutoGeneratable() {
			return true;
		}

		public Object getExpectedResult() {
			return "[a, b, c, e, d, f, 1, 2]";
		}
	}

	public static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "union$";

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
		if (params != null && params.length > 0) {
			JamochaValue list;
			JamochaValue current;
			List<JamochaValue> tmpList = new LinkedList<JamochaValue>();
			for (int i = 0; i < params.length; ++i) {
				list = params[i].getValue(engine);
				if (list.is(JamochaType.LIST))
					for (int j = 0; j < list.getListCount(); ++j) {
						current = list.getListValue(j);
						if (!tmpList.contains(current))
							tmpList.add(current);
					}
				else if (list.is(JamochaType.NIL)) {
					// nothing to do here
				} else
					throw new IllegalTypeException(JamochaType.LISTS, list
							.getType());
			}
			JamochaValue[] res = new JamochaValue[tmpList.size()];
			for (int i = 0; i < res.length; ++i)
				res[i] = tmpList.get(i);
			return JamochaValue.newList(res);
		}
		throw new IllegalParameterException(1, false);
	}
}
