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
 * Replaces specific items in a list with a given replacement. Arguments can either 
 * be single items or lists of items.
 * If a list is given to be replaced, the items are only replaced if they appear in 
 * identical (correct and coherent) order in the first list.
 * The function walks through the first list and tries to match each position to any 
 * of the arguments that are to be replaced. If it finds a match it replaces the item(s)
 * at the current position. Afterwards it moves on to the next position after the 
 * replacement and tries to match this position anew to any of the arguments that are 
 * to replaced.   
 * Therefore the arguments are not necessarily processed in the order they are given, but 
 * replacements will not be matched recursively.
 * Returns a new list consisting of untouched and replaced items.
 */
public class ReplaceMember$ extends AbstractFunction {

	public static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Replaces specific items in a list with a given replacement. Arguments can either be single " +
					"items or lists of items.\n" +
					"If a list is given to be replaced, the items are only replaced if they appear in identical " +
					"(correct and coherent) order in the first list.\n" +
					"The function walks through the first list and tries to match each position to any of the " +
					"arguments that are to be replaced. If it finds a match it replaces the item(s) at the current " +
					"position. Afterwards it moves on to the next position after the replacement and tries to match " +
					"this position anew to any of the arguments that are to replaced.\n" +
					"Therefore the arguments are not necessarily processed in the order they are given, but " +
					"replacements will not be matched recursively.\n" +
					"Returns a new list consisting of untouched and replaced items.";					
		}

		public int getParameterCount() {
			return 3;
		}

		public String getParameterDescription(int parameter) {
			switch (parameter) {
			case 0:
				return "List to replace items in.";
			case 1:
				return "Replacement for the searchValue in the list.";
			case 2:
				return "Value to search for and replace in the list.";
			}
			return "";
		}

		public String getParameterName(int parameter) {
			switch (parameter) {
			case 0:
				return "someList";
			case 1:
				return "replacement";
			case 2:
				return "searchValue";
			}
			return "";
		}

		public JamochaType[] getParameterTypes(int parameter) {
			switch (parameter) {
			case 0:
				return JamochaType.LISTS;
			case 1:
				return JamochaType.ANY;
			case 2:
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
			return (parameter > 2);
		}

		public String getExample() {
			return "(replace-member$ (create$ a b a b) (create$ a b a) a b)\n"
					+ "(replace-member$ (create$ a b a b) (create$ a b a) (create$ a b))";
		}

		public boolean isResultAutoGeneratable() {
			return true;
		}
	}

	private static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "replace-member$";

	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	public String getName() {
		return NAME;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		if (params != null && params.length > 2) {
			JamochaValue subject = params[0].getValue(engine);
			JamochaValue replacement = params[1].getValue(engine);
			if (subject.is(JamochaType.LIST)) {
				List<JamochaValue> tmpList = new LinkedList<JamochaValue>();
				List<JamochaValue> searchValues = new LinkedList<JamochaValue>();
				// preparsing to prevent possible multiple functioncalls for
				// searchvalues
				for (int i = 2; i < params.length; ++i) {
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
									if (!subject
											.getListValue(startIndex + i)
											.equals(searchValue.getListValue(i))) {
										continue searchFor;
									}
								}
								addReplacement(replacement, tmpList);
								startIndex += searchValue.getListCount();
								found = true;
								break searchFor;
							}
						} else {
							// found something. add it to the temp list
							if (current.equals(searchValue)) {
								found = true;
								addReplacement(replacement, tmpList);
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
		throw new IllegalParameterException(3, true);
	}

	private void addReplacement(JamochaValue replacement,
			List<JamochaValue> tmpList) {
		if (replacement.is(JamochaType.LIST)) {
			for (int i = 0; i < replacement.getListCount(); ++i) {
				tmpList.add(replacement.getListValue(i));
			}
		} else
			tmpList.add(replacement);
	}

	// (replace-member$ (create$ a b a b) (create$ a b a) (create$ b a) a)
}