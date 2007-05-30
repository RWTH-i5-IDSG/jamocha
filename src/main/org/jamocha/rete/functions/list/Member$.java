/*
 * Copyright 2002-2006 Peter Lin, 2007 Alexander Wilden
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
import org.jamocha.parser.IllegalTypeException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.functions.FunctionDescription;

/**
 * @author Peter Lin
 * 
 * Compares an expression against a multifield-expression. If the single
 * expression is in the second expression it, returns the integer position. Else
 * -1 is returned.
 */
public class Member$ implements Function, Serializable {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Compares an expression against a multifield-expression. If the single expression is in the second expression it, returns the integer position. Else -1 is returned.";
		}

		public int getParameterCount() {
			return 2;
		}

		public String getParameterDescription(int parameter) {
			switch(parameter) {
			case 0:
				return "Value to search for in the List.";
			case 1:
				return "List to search in.";
			}
			return "";
		}

		public String getParameterName(int parameter) {
			switch(parameter) {
			case 0:
				return "value";
			case 1:
				return "list";
			}
			return "";
		}

		public JamochaType[] getParameterTypes(int parameter) {
			switch(parameter) {
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
			// TODO Auto-generated method stub
			return null;
		}
	}

	private static final FunctionDescription DESCRIPTION = new Description();

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
		JamochaValue result = new JamochaValue(JamochaType.LONG, -1);
		if (params != null && params.length == 2) {
			JamochaValue first = params[0].getValue(engine);
			JamochaValue second = params[1].getValue(engine);
			if (!second.getType().equals(JamochaType.LIST)) {
				throw new IllegalTypeException(JamochaType.LISTS, second
						.getType());
			}
			for (int idx = 0; idx < second.getListCount(); idx++) {
				if (first.equals(second.getListValue(idx))) {
					result = JamochaValue.newLong(++idx);
					break;
				}
			}
		}
		return result;
	}

}