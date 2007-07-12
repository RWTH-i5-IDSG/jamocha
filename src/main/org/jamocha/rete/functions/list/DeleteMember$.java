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
 * Deletes specific items from a list. Arguments can either be single items or lists of items.
 * In case of a list to be deleted, items are only removed if they appear in identical (correct and coherent)
 * order in the first list. The function walks through the first list and tries to match each position to any 
 * of the arguments that are to be deleted. After deleting items from the list, it starts anew to walk through 
 * the remaining list and tries again to find a match to any of the arguments that are to be deleted. Therefore 
 * the arguments are not necessarily processed in the order they are given and also deleted repeatedly if the 
 * remaining list can be matched again.
 * Returns a new list consisting of the remaining items.
 */
public class DeleteMember$ implements Function, Serializable {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Deletes specific items from a list. Arguments can either be single items or lists of " +
					"items.\n" +
					"In case of a list to be deleted, items are only removed if they appear in identical (correct and " +
					"coherent) order in the first list. The function walks through the first list and tries to match each " +
					"position to any of the arguments that are to be deleted. After deleting items from the list, it " +
					"starts anew to walk through the remaining list and tries again to find a match to any of the " +
					"arguments that are to be deleted. Therefore the arguments are not necessarily processed in the " +
					"order they are given and also deleted repeatedly if the remaining list can be matched again.\n" +
					"Returns a new list consisting of the remaining items.";					
		}

		public int getParameterCount() {
			return 2;
		}

		public String getParameterDescription(int parameter) {
			switch (parameter) {
			case 0:
				return "List to delete items from.";
			case 1:
				return "Item(s) to delete from the list.";
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

		public boolean isResultAutoGeneratable() {
			// TODO Auto-generated method stub
			return false;
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

				// preparsing the subject list
				for (int i = 0; i < subject.getListCount(); ++i) {
					tmpList.add(subject.getListValue(i));
				}
				// preparsing to prevent possible multiple functioncalls for
				// searchvalues
				for (int i = 1; i < params.length; ++i) {
					searchValues.add(params[i].getValue(engine));
				}
				int startIndex = 0;
				boolean found;
				while (startIndex < tmpList.size()) {
					found = false;
					searchFor: for (JamochaValue searchValue : searchValues) {
						if (searchValue.is(JamochaType.LIST)) {
							if (searchValue.getListCount() > (tmpList.size() - startIndex)) {
								continue searchFor;
							} else {
								for (int i = 0; i < searchValue.getListCount(); ++i) {
									if (!tmpList.get(startIndex + i)
											.equals(searchValue.getListValue(i))) {
										continue searchFor;
									}
								}
								for (int i = 0; i < searchValue.getListCount(); ++i) {
									tmpList.remove(startIndex);
								}
								found = true;
								break searchFor;
							}
						} else {
							// found something. ignore the value
							if (tmpList.get(startIndex).equals(searchValue)) {
								found = true;
								tmpList.remove(startIndex);
								break searchFor;
							}
						}
					}
					// no match found so raise the startIndex
					if (!found) {
						++startIndex;
					}
					// match found so reset the startIndex
					else {
						startIndex = 0;
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