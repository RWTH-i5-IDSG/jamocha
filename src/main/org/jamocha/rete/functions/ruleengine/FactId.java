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
package org.jamocha.rete.functions.ruleengine;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.IllegalConversionException;
import org.jamocha.parser.IllegalParameterException;
import org.jamocha.parser.IllegalTypeException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.functions.AbstractFunction;
import org.jamocha.rete.functions.FunctionDescription;
import org.jamocha.rete.wme.Fact;

/**
 * @author Alexander Wilden
 * 
 * Returns the value (fact) of the given fact-id or NIL if the given id doesn't
 * exist.
 */
public class FactId extends AbstractFunction {

	private static final class FindFactByFactDescription implements
			FunctionDescription {

		public String getDescription() {
			return "Returns the value (fact) of the given fact-id or NIL if the given id doesn't exist.";
		}

		public int getParameterCount() {
			return 1;
		}

		public String getParameterDescription(int parameter) {
			return "Fact-Id to get fact from.";
		}

		public String getParameterName(int parameter) {
			return "factId";
		}

		public JamochaType[] getParameterTypes(int parameter) {
			JamochaType[] paramTypes = new JamochaType[JamochaType.LONGS.length
					+ JamochaType.FACT_IDS.length];
			int count = 0;
			for (int i = 0; i < JamochaType.LONGS.length; ++i)
				paramTypes[count++] = JamochaType.LONGS[i];
			for (int i = 0; i < JamochaType.FACT_IDS.length; ++i)
				paramTypes[count++] = JamochaType.FACT_IDS[i];
			return paramTypes;
		}

		public JamochaType[] getReturnType() {
			return JamochaType.FACTS;
		}

		public boolean isParameterCountFixed() {
			return true;
		}

		public boolean isParameterOptional(int parameter) {
			return false;
		}

		public String getExample() {
			return "(deftemplate car (slot color)(slot speed))\n"
					+ "(assert (car (color \"red\")(speed 200)))\n"
					+ "(assert (car (color \"blue\")(speed 150)))\n"
					+ "(assert (car (color \"green\")(speed 100)))\n"
					+ "(fact-id 2)";
		}

		public boolean isResultAutoGeneratable() {
			return true;
		}
	}

	public static final FunctionDescription DESCRIPTION = new FindFactByFactDescription();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "fact-id";

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
			try {
				long factId;
				if (param.is(JamochaType.IDENTIFIER)
						&& param.getStringValue().length() > 2
						&& param.getStringValue().startsWith("f-")) {
					factId = Long
							.parseLong(param.getStringValue().substring(2));
				} else {
					factId = param.implicitCast(JamochaType.LONG)
							.getLongValue();
				}
				Fact fact = engine.getFactById(factId);
				if (fact == null) {
					return JamochaValue.NIL;
				} else {
					return JamochaValue.newFact(fact);
				}
			} catch (IllegalConversionException e) {
				throw new IllegalTypeException(
						DESCRIPTION.getParameterTypes(0), param.getType());
			}
		} else {
			throw new IllegalParameterException(1);
		}
	}
}