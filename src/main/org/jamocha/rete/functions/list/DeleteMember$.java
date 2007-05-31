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
 * Deletes specific values from a list. Arguments given as list are removed in
 * correct order and coherent. The arguments are processed in the order they are
 * given. Have a look at the examples to understand the behaviour. Returns a new
 * list without the given values.
 */
public class DeleteMember$ implements Function, Serializable {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Deletes specific values from a list. Arguments given as list are removed in correct order and coherent. The arguments are processed in the order they are given. Have a look at the examples to understand the behaviour. Returns a new list without the given values.";
		}

		public int getParameterCount() {
			return 2;
		}

		public String getParameterDescription(int parameter) {
			switch (parameter) {
			case 0:
				return "The List to delete items from.";
			case 1:
				return "Item to delete in the List.";
			}
			return "";
		}

		public String getParameterName(int parameter) {
			switch (parameter) {
			case 0:
				return "someList";
			case 1:
				return "deleteItem";
			}
			return "";
		}

		public JamochaType[] getParameterTypes(int parameter) {
			switch (parameter) {
			case 0:
				return JamochaType.LISTS;
			case 1:
				return JamochaType.ANY;
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
			return (parameter > 1);
		}

		public String getExample() {
			return "(delete-member$ (create$ 1 2 3 4 5) 2)\n"
					+ "(delete-member$ (create$ 1 2 3 4 5) (create$ 3 4) 1)\n"
					+ "(delete-member$ (create$ 1 2 3 4 5) (create$ 3 4) (create$ 2 5))";
		}
	}

	private static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "delete-member$";

	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	public String getName() {
		return NAME;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		if (params != null && params.length > 1) {
			JamochaValue subject = params[0].getValue(engine);
			if (subject.is(JamochaType.LIST)) {
				List<JamochaValue> tmpList = new LinkedList<JamochaValue>();
				List<JamochaValue> searchValues = new LinkedList<JamochaValue>();
				// preparsing to prevent possible multiple functioncalls for
				// searchvalues
				for (int i = 1; i < params.length; ++i) {
					searchValues.add(params[i].getValue(engine));
				}
				int startIndex = 0;
				int end = subject.getListCount();
				boolean found;
				JamochaValue current;
				while (startIndex < end) {
					found = false;
					current = subject.getListValue(startIndex);
					searchFor: for (JamochaValue searchValue : searchValues) {
						if (searchValue.is(JamochaType.LIST)) {
							if (searchValue.getListCount() > (end - startIndex)) {
								continue searchFor;
							} else {
								for (int i = 0; i < searchValue.getListCount(); ++i) {
									if (!subject.getListValue(startIndex + i)
											.equals(searchValue.getListValue(i))) {
										continue searchFor;
									}
								}
								startIndex += searchValue.getListCount();
								found = true;
								break searchFor;
							}
						} else {
							// found something. ignore the value
							if (current.equals(searchValue)) {
								found = true;
								++startIndex;
								break searchFor;
							}
						}
					}
					// no match found so keep the old value
					if (!found) {
						tmpList.add(current);
						++startIndex;
					}
				}

				JamochaValue[] res = new JamochaValue[tmpList.size()];
				for (int i = 0; i < res.length; ++i) {
					res[i] = tmpList.get(i);
				}
				return JamochaValue.newList(res);
			} else {
				throw new IllegalTypeException(JamochaType.LISTS, subject
						.getType());
			}
		}
		throw new IllegalParameterException(2, true);
	}
}