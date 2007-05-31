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
 * Replaces specific values in a list with a given replacement. Arguments given
 * as list are replaced in correct order and coherent. The arguments are
 * processed in the order they are given. Have a look at the examples to
 * understand the behaviour. Returns a new list the given values replaced by the
 * replacement.
 */
public class ReplaceMember$ implements Function, Serializable {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Replaces specific values in a list with a given replacement. Arguments given as list are replaced in correct order and coherent. The arguments are processed in the order they are given. Have a look at the examples to understand the behaviour. Returns a new list the given values replaced by the replacement.";
		}

		public int getParameterCount() {
			return 3;
		}

		public String getParameterDescription(int parameter) {
			switch (parameter) {
			case 0:
				return "The List to delete items from.";
			case 1:
				return "Replacement for the searchValues in the List.";
			case 2:
				return "Value to search for and replace in the List.";
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