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
 * Returns a new list containing all elements of the second list that are not also elements of the first list.
 */
public class Complement$ extends AbstractFunction {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Returns a new list containing all elements of the second list that are not also elements of the first list.";			
		}

		public int getParameterCount() {
			return 2;
		}

		public String getParameterDescription(int parameter) {
			switch (parameter) {
			case 0:
				return "List acting as a filter for list two.";
			case 1:
				return "List that is filtered by list one.";
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
			return JamochaType.LISTS;
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
			return "(complement$  (create$ sausage milk eggs) (create$ cheese milk eggs bread))";
		}

		public boolean isResultAutoGeneratable() {
			return true;
		}
	}

	private static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "complement$";

	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	public String getName() {
		return NAME;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		if (params != null && params.length == 2) {
			JamochaValue listOne = params[0].getValue(engine);
			JamochaValue listTwo = params[1].getValue(engine);
			if (listOne.is(JamochaType.LIST)) {
				if (listTwo.is(JamochaType.LIST)) {
					if (listTwo.getListCount() < 1) {
						return JamochaValue.EMPTY_LIST;
					} else {
						if (listOne.getListCount() < 1) {
							// Because List one is empty we clone the second
							// list.
							JamochaValue[] res = new JamochaValue[listTwo
									.getListCount()];
							for (int i = 0; i < res.length; ++i) {
								res[i] = listTwo.getListValue(i);
							}
							return JamochaValue.newList(res);
						} else {
							int countOne = listOne.getListCount();
							int countTwo = listTwo.getListCount();
							List<JamochaValue> tmp = new LinkedList<JamochaValue>();
							outerloop: for (int i = 0; i < countTwo; ++i) {
								for (int j = 0; j < countOne; ++j) {
									if (listOne.getListValue(j).equals(
											listTwo.getListValue(i))) {
										continue outerloop;
									}
								}
								tmp.add(listTwo.getListValue(i));
							}
							JamochaValue[] res = new JamochaValue[tmp.size()];
							for (int i = 0; i < tmp.size(); ++i) {
								res[i] = tmp.get(i);
							}
							return JamochaValue.newList(res);
						}
					}
				} else {
					throw new IllegalTypeException(JamochaType.LISTS, listTwo
							.getType());
				}
			} else {
				throw new IllegalTypeException(JamochaType.LISTS, listOne
						.getType());
			}
		}
		throw new IllegalParameterException(2, false);
	}
}