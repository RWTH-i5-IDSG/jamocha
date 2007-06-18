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
package org.jamocha.rete.functions.ruleengine;

import java.io.Serializable;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.BoundParam;
import org.jamocha.rete.Fact;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.configurations.ModifyConfiguration;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.exception.RetractException;
import org.jamocha.rete.functions.FunctionDescription;

/**
 * @author Peter Lin
 * 
 * The modify action allows the user to modify template facts on the fact-list.
 * Only one fact may be modified with a single modify statement. The
 * modification of a fact is equivalent to retracting the present fact and
 * asserting the modified fact. Returns true on success.
 */
public class Modify implements Function, Serializable {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "The modify action allows the user to modify template facts on the fact-list. Only one fact may be modified with a single modify statement. The modification of a fact is equivalent to retracting the present fact and asserting the modified fact. Returns true on success.";
		}

		public int getParameterCount() {
			return 2;
		}

		public String getParameterDescription(int parameter) {
			if (parameter == 0)
				return "Fact to modify.";
			else
				return "Slot to change in the given Fact.";
		}

		public String getParameterName(int parameter) {
			if (parameter == 0)
				return "fact";
			else
				return "slot";
		}

		public JamochaType[] getParameterTypes(int parameter) {
			switch (parameter) {
			case 0:
				return JamochaType.FACTS;
			case 1:
				return JamochaType.SLOTS;
			}
			return JamochaType.NONE;
		}

		public JamochaType[] getReturnType() {
			return JamochaType.BOOLEANS;
		}

		public boolean isParameterCountFixed() {
			return false;
		}

		public boolean isParameterOptional(int parameter) {
			return (parameter > 1 && parameter < 0);
		}

		public String getExample() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	private static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "modify";

	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	public String getName() {
		return NAME;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		JamochaValue result = JamochaValue.FALSE;
		if (engine != null && params != null && params.length > 0) {
			BoundParam bp = null;
			Fact fact = null;
			// grather all infos:
			try {
				// modificonfiguration
				if (params[0] instanceof ModifyConfiguration) {
					ModifyConfiguration mc = (ModifyConfiguration) params[0];
					bp = mc.getFactBinding();

					fact = bp.getFact();
					if (fact == null) {
						JamochaValue engineBinding = engine.getBinding(bp
								.getVariableName());
						if (engineBinding != null) {
							if (engineBinding.is(JamochaType.FACT_ID)) {
								fact = engine.getFactById(engineBinding
										.getFactIdValue());
							} else if (engineBinding.is(JamochaType.FACT)) {
								fact = engineBinding.getFactValue();
							}
						}
					}

					engine.modifyFact(fact, mc);
					result = JamochaValue.TRUE;
				}

			} catch (RetractException e) {
				engine.writeMessage(e.getMessage());
			} catch (AssertException e) {
				engine.writeMessage(e.getMessage());
			}
		}

		return result;
	}
}