/*
 * Copyright 2007 Christoph Emonds, Alexander Wilden, Uta Christoph
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
import org.jamocha.parser.IllegalParameterException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Fact;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.functions.AbstractFunction;
import org.jamocha.rete.functions.FunctionDescription;

/**
 * @author Christoph Emonds
 * 
 * Returns the value of a slot of a specific fact.
 */
public class FactSlotValue extends AbstractFunction {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Returns the value of a slot of a specific fact.";
		}

		public int getParameterCount() {
			return 2;
		}

		public String getParameterDescription(int parameter) {
			switch (parameter) {
			case 0:
				return "Fact to return the slot value from.";
			case 1:
				return "Name of the slot to get the value from.";
			}
			return "";
		}

		public String getParameterName(int parameter) {
			switch (parameter) {
			case 0:
				return "fact";
			case 1:
				return "slotName";
			}
			return "";
		}

		public JamochaType[] getParameterTypes(int parameter) {
			switch (parameter) {
			case 0:
				JamochaType[] paramTypes = new JamochaType[JamochaType.FACTS.length
						+ JamochaType.FACT_IDS.length];
				int count = 0;
				for (int i = 0; i < JamochaType.FACTS.length; ++i)
					paramTypes[count++] = JamochaType.FACTS[i];
				for (int i = 0; i < JamochaType.FACT_IDS.length; ++i)
					paramTypes[count++] = JamochaType.FACT_IDS[i];
				return paramTypes;
			case 1:
				return JamochaType.IDENTIFIERS;
			}
			return JamochaType.NONE;
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
			return "(deftemplate car (slot color)(slot speed))\n" +
					"(assert (car (color \"red\")(speed 200)))\n" +
					"(assert (car (color \"blue\")(speed 150)))\n" +
					"(assert (car (color \"green\")(speed 100)))\n" +
					"(fact-slot-value 1 color)\n" +
					"(fact-slot-value 3 speed)";
		}

		public boolean isResultAutoGeneratable() {
			return true;
		}
	}

	public static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "fact-slot-value";

	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	public String getName() {
		return NAME;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		if (params != null && params.length == 2) {
			JamochaValue factId = params[0].getValue(engine);
			JamochaValue slotName = params[1].getValue(engine);
			Fact fact;
			if (!factId.is(JamochaType.FACT)) {
				fact = engine.getFactById(factId.getFactIdValue());
			} else {
				fact = factId.getFactValue();
			}
			int slotId = fact.getSlotId(slotName.getIdentifierValue());
			if (slotId < 0) {
				throw new EvaluationException("Error no slot " + slotName);
			}
			return fact.getSlotValue(slotId);
		} else {
			throw new IllegalParameterException(2);
		}
	}
}