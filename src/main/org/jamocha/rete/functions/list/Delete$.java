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
 * Deletes a specified number of items from a list and returns the remaining values in a new list. 
 * The first integer defines the index of the first item to be removed and 
 * the second integer defines the index of the last item to be removed. 
 * <p>
 * Attention: Lists in Jamocha start with index 1.
 * </p>
 */
public class Delete$ extends AbstractFunction {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Deletes a specified number of items from a list and returns the remaining values in a new list. The first integer defines the index of the first item to be removed and the second integer defines the index of the last item to be removed. Attention: Lists in Jamocha start with index 1.";
		}

		public int getParameterCount() {
			return 3;
		}

		public String getParameterDescription(int parameter) {
			switch (parameter) {
			case 0:
				return "List to delete items from.";
			case 1:
				return "First item to delete from the List. Has to be smaller or equal to endIndex and within the bounds of the list.";
			case 2:
				return "Last item to delete from the List. Has to be greater or equal to startIndex and within the bounds of list.";
			}
			return "";
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
			return "";
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
			return JamochaType.NONE;
		}

		public JamochaType[] getReturnType() {
			return JamochaType.LISTS;
		}

		public boolean isParameterCountFixed() {
			return true;
		}

		public boolean isParameterOptional(int parameter) {
			return false;
		}

		public String getExample() {
			return "(delete$ (create$ cheese milk eggs bread sausages) 2 4)";
		}

		public boolean isResultAutoGeneratable() {
			return true;
		}
	}

	public static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "delete$";

	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	public String getName() {
		return NAME;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		if (params != null && params.length == 3) {
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
					JamochaValue[] tmp = new JamochaValue[subject.getListCount()
							- ((endIndex - startIndex) + 1)];
					int count = 0;
					for (int i = 1; i <= subject.getListCount(); ++i) {
						if (i < startIndex || i > endIndex) {
							tmp[count++] = subject.getListValue(i - 1);
						}
					}
					return JamochaValue.newList(tmp);
				}
			} else {
				throw new IllegalTypeException(JamochaType.LISTS, subject
						.getType());
			}
		}
		throw new IllegalParameterException(3, false);
	}
}