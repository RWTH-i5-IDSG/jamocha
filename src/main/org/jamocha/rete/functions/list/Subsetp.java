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
package org.jamocha.rete.functions.list;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.IllegalParameterException;
import org.jamocha.parser.IllegalTypeException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.functions.Function;
import org.jamocha.rete.functions.FunctionDescription;

/**
 * @author Alexander Wilden
 * 
 * Returns true if the first list is a subset of the second list, false
 * otherwise. The order of the lists is not considered.
 */
public class Subsetp extends Function {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Returns true if the first list is a subset of the second list, false otherwise. The order of the lists is not considered.";
		}

		public int getParameterCount() {
			return 2;
		}

		public String getParameterDescription(int parameter) {
			switch (parameter) {
			case 0:
				return "Subset to test for.";
			case 1:
				return "List to search in.";
			}
			return "";
		}

		public String getParameterName(int parameter) {
			switch (parameter) {
			case 0:
				return "listOne";
			case 1:
				return "listTwo";
			}
			return "";
		}

		public JamochaType[] getParameterTypes(int parameter) {
			switch (parameter) {
			case 0:
				return JamochaType.LISTS;
			case 1:
				return JamochaType.LISTS;
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
			return "(subsetp (create$ 3 4) (create$ 1 2 3 4 5 6))";
		}

		public boolean isResultAutoGeneratable() {
			return true;
		}
	}

	public static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "subsetp";

	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	public String getName() {
		return NAME;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		if (params != null && params.length == 2) {
			JamochaValue first = params[0].getValue(engine);
			JamochaValue second = params[1].getValue(engine);
			if (!first.getType().equals(JamochaType.LIST)) {
				throw new IllegalTypeException(JamochaType.LISTS, first
						.getType());
			}
			if (!second.getType().equals(JamochaType.LIST)) {
				throw new IllegalTypeException(JamochaType.LISTS, second
						.getType());
			}
			if (first.equals(JamochaValue.EMPTY_LIST))
				return JamochaValue.TRUE;
			if (second.getListCount() >= first.getListCount()) {
				outer: for (int i = 0; i < first.getListCount(); i++) {
					for (int j = 0; j < second.getListCount(); j++) {
						if (first.getListValue(i)
								.equals(second.getListValue(j)))
							continue outer;
					}
					return JamochaValue.FALSE;
				}
				return JamochaValue.TRUE;
			} else {
				return JamochaValue.FALSE;
			}
		}
		throw new IllegalParameterException(2, false);
	}
}