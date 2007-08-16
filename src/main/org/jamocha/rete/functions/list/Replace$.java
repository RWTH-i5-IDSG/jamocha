/*
 * Copyright 2007 Alexander Wilden, Uta Christoph
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

import java.util.LinkedList;
import java.util.List;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.IllegalParameterException;
import org.jamocha.parser.IllegalTypeException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.functions.AbstractFunction;
import org.jamocha.rete.functions.FunctionDescription;

/**
 * @author Alexander Wilden
 * 
 * Replaces a specified range in a list with (a) given value(s) and returns 
 * the modified list.  
 * The first integer defines the index of the first item to replace and 
 * the second integer defines the index of the last item to replace.
 * The following arguments are inserted into the list, starting at the index of the first 
 * replaced item. If a list is given as replacement each item is inserted separately.
 * <p>
 * Attention: Lists in Jamocha start with index 1.
 * </p>
 */
public class Replace$ extends AbstractFunction {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Replaces a specified range in a list with (a) given value(s) and returns the modified list. " +
					"The first integer defines the index of the first item to replace and the second integer defines " +
					"the index of the last item to replace. The following arguments are inserted into the list, " +
					"starting at the index of the first replaced item. If a list is given as replacement each item " +
					"is inserted separately.\n" +
					"Attention: Lists in Jamocha start with index 1.";			
		}

		public int getParameterCount() {
			return 4;
		}

		public String getParameterDescription(int parameter) {
			switch (parameter) {
			case 0:
				return "List to replace a specific range in.";
			case 1:
				return "First item to replace in the List. Has to be smaller or equal to endIndex and within the bounds of the list.";
			case 2:
				return "Last item to replace in the List. Has to be greater or equal to startIndex and within the bounds of the list.";
			}
			return "Value(s) used as replacement.";
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
			return "(replace$ (create$ 42 123 911 4711 1) 2 4 112)\n"
					+ "(replace$ (create$ cheese eggs milk sausages) 3 4 (create$ bread ham))";
		}

		public boolean isResultAutoGeneratable() {
			return true;
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
			JamochaValue subject = params[0].getValue(engine);
			int startIndex = (int) params[1].getValue(engine).getLongValue();
			int endIndex = (int) params[2].getValue(engine).getLongValue();
			if (subject.is(JamochaType.LIST)) {
				if (startIndex > endIndex) {
					throw new EvaluationException("Start index " + startIndex
							+ " is greater than end index " + endIndex + ".");
				} else if (startIndex < 1 || startIndex > subject.getListCount()) {
					throw new EvaluationException("Start index " + startIndex
							+ " is out of bounds (1 - " + subject.getListCount()
							+ ").");
				} else if (endIndex < 1 || endIndex > subject.getListCount()) {
					throw new EvaluationException("End index " + endIndex
							+ " is out of bounds (1 - " + subject.getListCount()
							+ ").");
				} else {
					List<JamochaValue> newList = new LinkedList<JamochaValue>();

					// add old entries before replacement start
					startIndex--;
					for (int i = 0; i < startIndex; ++i) {
						newList.add(subject.getListValue(i));
					}

					// add new entries
					for (int i = 3; i < params.length; ++i) {
						JamochaValue replacement = params[i].getValue(engine);
						if (!replacement.equals(JamochaValue.NIL)) {
							if (replacement.is(JamochaType.LIST)) {
								for (int j = 0; j < replacement.getListCount(); ++j) {
									newList.add(replacement.getListValue(j));
								}
							} else {
								newList.add(replacement);
							}
						}
					}

					// add old entries after replacement end
					for (int i = endIndex; i < subject.getListCount(); ++i) {
						newList.add(subject.getListValue(i));
					}
					JamochaValue[] tmp = new JamochaValue[newList.size()];
					for (int i = 0; i < newList.size(); ++i) {
						tmp[i] = newList.get(i);
					}
					return JamochaValue.newList(tmp);
				}
			} else {
				throw new IllegalTypeException(JamochaType.LISTS, subject
						.getType());
			}
		}
		throw new IllegalParameterException(4, true);
	}
}