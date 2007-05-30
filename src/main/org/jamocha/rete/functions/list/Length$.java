/*
 * Copyright 2007 Christoph Emonds, Alexander Wilden
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
 * @author Christoph Emonds
 * 
 * Returns the length of a list.
 */
public class Length$ implements Function, Serializable {
	private static final class LengthDescription implements FunctionDescription {

		public String getDescription() {
			return "Returns the length of a list.";
		}

		public int getParameterCount() {
			return 1;
		}

		public String getParameterDescription(int parameter) {
			return "A List whose length should be returned.";
		}

		public String getParameterName(int parameter) {
			return "list";
		}

		public JamochaType[] getParameterTypes(int parameter) {
			return JamochaType.LISTS;
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
			return "(bind ?x (create$ cheese milk eggs bread))"
					+ "(length$ ?x)";
		}
	}

	private static final FunctionDescription DESCRIPTION = new LengthDescription();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "length$";

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
				return JamochaValue.newLong(list.getListCount());
			} else {
				throw new IllegalTypeException(JamochaType.LISTS, list
						.getType());
			}
		}
		throw new IllegalParameterException(1, false);
	}
}
