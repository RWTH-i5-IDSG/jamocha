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
 * Returns the first value of a list. Nil if the List is empty.
 */
public class First$ implements Function, Serializable {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Returns the first item of a list. Nil if the list is empty.";
		}

		public int getParameterCount() {
			return 1;
		}

		public String getParameterDescription(int parameter) {
			return "List to get the first item from.";
		}

		public String getParameterName(int parameter) {
			return "someList";
		}

		public JamochaType[] getParameterTypes(int parameter) {
			return JamochaType.LISTS;
		}

		public JamochaType[] getReturnType() {
			return JamochaType.ANY;
		}

		public boolean isParameterCountFixed() {
			return true;
		}

		public boolean isParameterOptional(int parameter) {
			return false;
		}

		public String getExample() {
			return "(first$ (create$ cheese milk eggs bread))";
		}
	}

	private static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "first$";

	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	public String getName() {
		return NAME;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		if (params != null && params.length == 1) {
			JamochaValue list = params[0].getValue(engine);
			if (list.is(JamochaType.LIST)) {
				if (list.getListCount() > 0) {
					return list.getListValue(0);
				} else {
					return JamochaValue.NIL;
				}
			} else {
				throw new IllegalTypeException(JamochaType.LISTS, list
						.getType());
			}
		}
		throw new IllegalParameterException(1, false);
	}
}
