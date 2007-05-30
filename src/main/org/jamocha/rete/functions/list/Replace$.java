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

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.IllegalParameterException;
import org.jamocha.parser.IllegalTypeException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.functions.FunctionDescription;

/**
 * @author Alexander Wilden
 * 
 * Replaces a specified range in a list with (a) given value(s) and returns it
 * as a new list. The first integer expression is the index of the first value
 * to replace and the second integer expression is the index of the last value
 * to replace. All following values will take the place of the values in the
 * replacement range. Lists are also possible values where each item is added
 * separately.
 * <p>
 * Attention: Lists in Jamocha start with index 1.
 */
public class Replace$ implements Function, Serializable {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Replaces a specified range in a list with (a) given value(s) and returns it as a new list. The first integer expression is the index of the first value to replace and the second integer expression is the index of the last value to replace. All following values will take the place of the values in the replacement range. Lists are also possible values where each item is added separately. Attention: Lists in Jamocha start with index 1.";
		}

		public int getParameterCount() {
			return 4;
		}

		public String getParameterDescription(int parameter) {
			switch (parameter) {
			case 0:
				return "The List to replace a specific range in.";
			case 1:
				return "First item to replace in the List. Has to be smaller or equal to endIndex.";
			case 2:
				return "Last item to replace in the List. Has to be greater or equal to startIndex.";
			}
			return "Value used as replacement.";
		}

		public String getParameterName(int parameter) {
			switch (parameter) {
			case 0:
				return "someList";
			case 1:
				return "startIndex";
			case 2:
				return "endIndex";
			}
			return "replacement";
		}

		public JamochaType[] getParameterTypes(int parameter) {
			switch (parameter) {
			case 0:
				return JamochaType.LISTS;
			case 1:
				return JamochaType.LONGS;
			case 2:
				return JamochaType.LONGS;
			}
			return JamochaType.ANY;
		}

		public JamochaType[] getReturnType() {
			return JamochaType.LISTS;
		}

		public boolean isParameterCountFixed() {
			return false;
		}

		public boolean isParameterOptional(int parameter) {
			return (parameter > 3);
		}

		public String getExample() {
			return "(replace$ (create$ 42 123 911 4711 1) 2 4 112)"
					+ "(replace$ (create$ cheese eggs milk sausages) 3 4 (create$ bread ham))";
		}
	}

	private static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "replace$";

	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	public String getName() {
		return NAME;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		if (params != null && params.length >= 4) {
			JamochaValue list = params[0].getValue(engine);
			int startIndex = (int) params[1].getValue(engine).getLongValue();
			int endIndex = (int) params[2].getValue(engine).getLongValue();
			if (list.is(JamochaType.LIST)) {
				if (startIndex > endIndex) {
					throw new EvaluationException("Start index " + startIndex
							+ " is greater than end index " + endIndex + ".");
				} else if (startIndex < 1 || startIndex > list.getListCount()) {
					throw new EvaluationException("Start index " + startIndex
							+ " is out of bounds (1 - " + list.getListCount()
							+ ").");
				} else if (endIndex < 1 || endIndex > list.getListCount()) {
					throw new EvaluationException("End index " + endIndex
							+ " is out of bounds (1 - " + list.getListCount()
							+ ").");
				} else {
					List<JamochaValue> newList = new LinkedList<JamochaValue>();

					// add old entries before replacement start
					startIndex--;
					for (int i = 0; i < startIndex; ++i) {
						newList.add(list.getListValue(i));
					}

					// add new entries
					for (int i = 3; i < params.length; ++i) {
						JamochaValue value = params[i].getValue(engine);
						if (!value.equals(JamochaValue.NIL)) {
							if (value.is(JamochaType.LIST)) {
								for (int j = 0; j < value.getListCount(); ++j) {
									newList.add(value.getListValue(j));
								}
							} else {
								newList.add(value);
							}
						}
					}

					// add old entries after replacement end
					for (int i = endIndex; i < list.getListCount(); ++i) {
						newList.add(list.getListValue(i));
					}
					JamochaValue[] tmp = new JamochaValue[newList.size()];
					for (int i = 0; i < newList.size(); ++i) {
						tmp[i] = newList.get(i);
					}
					return JamochaValue.newList(tmp);
				}
			} else {
				throw new IllegalTypeException(JamochaType.LISTS, list
						.getType());
			}
		}
		throw new IllegalParameterException(4, true);
	}
}