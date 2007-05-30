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
 * Deletes a specified number of items from a list and returns the resulting
 * values again in a new list. The first integer expression is the index of the
 * first value to remove and the second integer expression is the index of the
 * last value to remove.
 * <p>
 * Attention: Lists in Jamocha start with index 1.
 */
public class Delete$ implements Function, Serializable {

	private static final class CreateDescription implements FunctionDescription {

		public String getDescription() {
			return "Deletes a specified number of items from a list and returns the resulting values again in a new list. The first integer expression is the index of the first value to remove and the second integer expression is the index of the last value to remove. Attention: Lists in Jamocha start with index 1.";
		}

		public int getParameterCount() {
			return 3;
		}

		public String getParameterDescription(int parameter) {
			switch (parameter) {
			case 0:
				return "The List to delete items from.";
			case 1:
				return "First item to delete in the List. Has to be smaller or equal to endIndex.";
			case 2:
				return "Last item to delete in the List. Has to be greater or equal to startIndex.";
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
			return "(bind ?x (create$ cheese milk eggs bread sausages))"
					+ "(delete$ ?x 2 4)";
		}
	}

	private static final FunctionDescription DESCRIPTION = new CreateDescription();

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
					JamochaValue[] tmp = new JamochaValue[list.getListCount()
							- ((endIndex - startIndex) + 1)];
					int count = 0;
					for (int i = 1; i <= list.getListCount(); ++i) {
						if (i < startIndex || i > endIndex) {
							tmp[count++] = list.getListValue(i - 1);
						}
					}
					return JamochaValue.newList(tmp);
				}
			} else {
				throw new IllegalTypeException(JamochaType.LISTS, list
						.getType());
			}
		}
		throw new IllegalParameterException(3, false);
	}
}