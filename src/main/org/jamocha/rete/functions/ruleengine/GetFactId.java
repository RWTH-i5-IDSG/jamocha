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
package org.jamocha.rete.functions.ruleengine;

import java.io.Serializable;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.IllegalParameterException;
import org.jamocha.parser.IllegalTypeException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Fact;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.functions.FunctionDescription;

/**
 * @author Alexander Wilden
 * 
 * Returns the Fact-ID of a given Fact.
 */
public class GetFactId implements Function, Serializable {

	private static final class FindFactByFactDescription implements
			FunctionDescription {

		public String getDescription() {
			return "Returns the Fact-ID of a given Fact.";
		}

		public int getParameterCount() {
			return 1;
		}

		public String getParameterDescription(int parameter) {
			return "Fact to return the ID of.";
		}

		public String getParameterName(int parameter) {
			return "fact";
		}

		public JamochaType[] getParameterTypes(int parameter) {
			return JamochaType.FACTS;
		}

		public JamochaType[] getReturnType() {
			return JamochaType.FACT_IDS;
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

	private static final FunctionDescription DESCRIPTION = new FindFactByFactDescription();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "get-fact-id";

	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	public String getName() {
		return NAME;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {

		if (params != null && params.length == 1) {
			JamochaValue param = params[0].getValue(engine);
			if (param.is(JamochaType.FACT)) {
				Fact fact = param.getFactValue();
				if (fact == null) {
					return JamochaValue.newFactId(0);
				} else {
					return JamochaValue.newFactId(fact.getFactId());
				}
			} else {
				throw new IllegalTypeException(
						DESCRIPTION.getParameterTypes(0), param.getType());
			}
		} else {
			throw new IllegalParameterException(1);
		}
	}
}