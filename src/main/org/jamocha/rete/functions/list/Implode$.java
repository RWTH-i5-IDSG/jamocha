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
 * Creates and returns a string of a given list. Optionally a separator to put in-between the items can be provided. 
 * Default separator is the space character.
 */
public class Implode$ extends AbstractFunction {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Creates and returns a string of a given list. Optionally a separator to put in-between the items" +
					"can be provided. Default separator is the space character.";
		}

		public int getParameterCount() {
			return 2;
		}

		public String getParameterDescription(int parameter) {
			switch (parameter) {
			case 0:
				return "List to be imploded.";
			case 1:
				return "Optional separator, default: space character.";
			}
			return "";
		}

		public String getParameterName(int parameter) {
			switch (parameter) {
			case 0:
				return "list";
			case 1:
				return "separator";
			}
			return "";
		}

		public JamochaType[] getParameterTypes(int parameter) {
			switch (parameter) {
			case 0:
				return JamochaType.LISTS;
			case 1:
				return JamochaType.STRINGS;
			}
			return JamochaType.NONE;
		}

		public JamochaType[] getReturnType() {
			return JamochaType.STRINGS;
		}

		public boolean isParameterCountFixed() {
			return false;
		}

		public boolean isParameterOptional(int parameter) {
			switch (parameter) {
			case 0:
				return false;
			case 1:
				return true;
			}
			return false;
		}

		public String getExample() {
			return "(implode$ (create$ We need beer))\n(implode$ (create$ We need beer) \" -STOP- \")";
		}

		public boolean isResultAutoGeneratable() {
			return true;
		}
	}

	public static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "implode$";

	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	public String getName() {
		return NAME;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		if (params != null && params.length >= 1) {
			JamochaValue list = params[0].getValue(engine);
			if (!list.getType().equals(JamochaType.LIST)) {
				throw new IllegalTypeException(JamochaType.LISTS, list
						.getType());
			}
			if (list.equals(JamochaValue.EMPTY_LIST))
				return JamochaValue.newString("");
			String separator = " ";
			// In contrast to Jess or CLIPS we allow separators specified by the
			// user.
			if (params.length > 1)
				separator = params[1].getValue(engine).getStringValue();
			StringBuilder res = new StringBuilder();
			for (int i = 0; i < list.getListCount(); i++) {
				if (i > 0)
					res.append(separator);
				res.append(list.getListValue(i));
			}
			return JamochaValue.newString(res.toString());
		}
		throw new IllegalParameterException(1, true);
	}
}