/*
 * Copyright 2002-2006 Peter Lin, 2007 Alexander Wilden, Uta Christoph
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
import org.jamocha.rete.functions.Function;
import org.jamocha.rete.functions.FunctionDescription;

/**
 * @author Peter Lin
 * 
 * Compares if the first argument is a member of the list given in the second argument.
 * If the the first argument is a list it is compared if there exists a subset of the second argument 
 * which is in the same order. 
 * Returns the position index of the element or the starting index of the subset in the second argument, 
 * if the first argument is a member.
 * Otherwise returns -1.
 */
public class Member$ extends Function {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Compares if the first argument is a member of the list given in the second argument. " +
					"If the the first argument is a list it is compared if there exists a subset of the second " +
					"argument which is in the same order.\n" +
					"Returns the position index of the element or the starting index of the subset in the second " +
					"argument, if the first argument is a member.\n" +
					"Otherwise returns -1."; 
					//"Compares an expression against a multifield-expression. If the expression is in the second expression or the expression is a list and a subset of the second expression in same order, integer position is returned. For lists the index of the first matching element is returned. Else -1 is returned.";
		}

		public int getParameterCount() {
			return 2;
		}

		public String getParameterDescription(int parameter) {
			switch (parameter) {
			case 0:
				return "Value to search for in the list.";
			case 1:
				return "List to search in.";
			}
			return "";
		}

		public String getParameterName(int parameter) {
			switch (parameter) {
			case 0:
				return "value";
			case 1:
				return "list";
			}
			return "";
		}

		public JamochaType[] getParameterTypes(int parameter) {
			switch (parameter) {
			case 0:
				return JamochaType.ANY;
			case 1:
				return JamochaType.LISTS;
			}
			return JamochaType.NONE;
		}

		public JamochaType[] getReturnType() {
			return JamochaType.LONGS;
		}

		public boolean isParameterCountFixed() {
			return true;
		}

		public boolean isParameterOptional(int parameter) {
			return false;
		}

		public String getExample() {
			return "(member$ 5 (create$ 1 2 3 4 5 6))\n"
					+ "(member$ (create$ 3 4) (create$ 1 2 3 4 5 6))";
		}

		public boolean isResultAutoGeneratable() {
			return true;
		}
	}

	public static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "member$";

	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	public String getName() {
		return NAME;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		if (params != null && params.length == 2) {
			JamochaValue result = JamochaValue.newLong(-1);
			JamochaValue first = params[0].getValue(engine);
			JamochaValue second = params[1].getValue(engine);
			if (!second.getType().equals(JamochaType.LIST)) {
				throw new IllegalTypeException(JamochaType.LISTS, second
						.getType());
			}
			// For lists we check if the first list is a subset of the second
			// list and then return the index. This behaviour is adopted from
			// clips. Jess doesn't support this.
			if (first.is(JamochaType.LIST)) {
				if (second.getListCount() >= first.getListCount()) {
					outer: for (int i = 0; i < second.getListCount(); i++) {
						if ((second.getListCount() - i) < first.getListCount()) {
							break outer;
						}
						for (int j = 0; j < first.getListCount(); j++) {
							if (!first.getListValue(j).equals(
									second.getListValue(i + j)))
								continue outer;
						}
						result = JamochaValue.newLong(i + 1);
					}
				}
			} else {
				if (first.is(JamochaType.FACT)){
					first = JamochaValue.newFactId(first.getFactValue().getFactId());
				}
				for (int i = 0; i < second.getListCount(); i++) {
					if (first.equals(second.getListValue(i))) {
						result = JamochaValue.newLong(i + 1);
						break;
					}
				}
			}
			return result;
		}
		throw new IllegalParameterException(2, false);
	}
}