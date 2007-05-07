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
import java.util.ArrayList;
import java.util.List;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.IllegalParameterException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.functions.FunctionDescription;

/**
 * @author Christoph Emonds, Alexander Wilden
 * 
 * Creates a list of the given parameter values.
 */
public class Create$ implements Function, Serializable {
	
	private static final class CreateDescription implements
			FunctionDescription {

		public String getDescription() {
			return "Creates a list of the given parameter values.";
		}

		public int getParameterCount() {
			return 1;
		}

		public String getParameterDescription(int parameter) {
			return "Any value that should be put into the List.";
		}

		public String getParameterName(int parameter) {
			return "someValue";
		}

		public JamochaType[] getParameterTypes(int parameter) {
			return JamochaType.ANY;
		}

		public JamochaType[] getReturnType() {
			return JamochaType.LISTS;
		}

		public boolean isParameterCountFixed() {
			return false;
		}

		public boolean isParameterOptional(int parameter) {
			return true;
		}

		public String getExample() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	private static final FunctionDescription DESCRIPTION = new CreateDescription();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "create$";

	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	public String getName() {
		return NAME;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		if (params != null) {
			List<JamochaValue> newListValues = new ArrayList<JamochaValue>();
			for (int i = 0; i < params.length; ++i) {
				JamochaValue value = params[i].getValue(engine);
				if (value.is(JamochaType.LIST)) {
					for (int j = 0; j < value.getListCount(); ++j) {
						newListValues.add(value.getListValue(j));
					}
				} else {
					newListValues.add(value);
				}

			}
			JamochaValue[] values = new JamochaValue[newListValues.size()];
			newListValues.toArray(values);
			return JamochaValue.newList(values);
		}
		throw new IllegalParameterException(0, true);
	}
}
