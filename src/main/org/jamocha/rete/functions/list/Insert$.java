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
 * Inserts one or more items into an existing list at a specific position and returns the extended list.
 * If a list of items is inserted each item of it is inserted separately, so there are no nested lists.
 * <p>
 * Attention: Lists in Jamocha start with index 1.
 * </p> 
 */
public class Insert$ extends AbstractFunction {

	public static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Inserts one or more items into an existing list at a specific position and returns the extended list. If a list of items is inserted each item of it is inserted separately, so there are no nested lists.\n" +
					"Attention: Lists in Jamocha start with index 1.";
		}

		public int getParameterCount() {
			return 3;
		}

		public String getParameterDescription(int parameter) {
			switch (parameter) {
			case 0:
				return "List to insert one or more items into.";
			case 1:
				return "Position where the new item(s) should be added at.";
			}
			return "Item(s) to insert into the List.";
		}

		public String getParameterName(int parameter) {
			switch (parameter) {
			case 0:
				return "someList";
			case 1:
				return "index";
			}
			return "item";
		}

		public JamochaType[] getParameterTypes(int parameter) {
			switch (parameter) {
			case 0:
				return JamochaType.LISTS;
			case 1:
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
			return (parameter > 2);
		}

		public String getExample() {
			return "(insert$ (create$ cheese milk bread sausages) 3 eggs)";
		}

		public boolean isResultAutoGeneratable() {
			return true;
		}
	}

	private static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "insert$";

	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	public String getName() {
		return NAME;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		if (params != null && params.length > 2) {
			JamochaValue list = params[0].getValue(engine);
			if (list == null) {
				list = JamochaValue.newList(new JamochaValue[0]);
			}
			int index = (int) params[1].getValue(engine).getLongValue();
			if (list.is(JamochaType.LIST)) {
				if (index < 1 && list.getListCount() > 0) {
					throw new EvaluationException("Index " + index
							+ " is out of bounds (1 - " + (list.getListCount()+1)
							+ ").");
				} else {
					List<JamochaValue> newList = new LinkedList<JamochaValue>();

					// add old entries before replacement start
					index--;
					for (int i = 0; i < index && i < list.getListCount(); ++i) {
						newList.add(list.getListValue(i));
					}

					// add new entries
					for (int i = 2; i < params.length; ++i) {
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
					for (int i = index; i < list.getListCount(); ++i) {
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
		throw new IllegalParameterException(3, false);
	}
}