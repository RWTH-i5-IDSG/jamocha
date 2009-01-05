/*
 * Copyright 2002-2008 The Jamocha Team
 * 
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

package org.jamocha.engine.functions.ruleengine;

import org.jamocha.engine.AssertException;
import org.jamocha.engine.Engine;
import org.jamocha.engine.Parameter;
import org.jamocha.engine.RetractException;
import org.jamocha.engine.configurations.ModifyConfiguration;
import org.jamocha.engine.functions.AbstractFunction;
import org.jamocha.engine.functions.FunctionDescription;
import org.jamocha.engine.workingmemory.elements.Fact;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;

/**
 * @author Peter Lin
 * 
 * The modify action allows the user to modify template facts on the fact-list.
 * Only one fact may be modified with a single modify statement. The
 * modification of a fact is equivalent to retracting the present fact and
 * asserting the modified fact. Returns true on success.
 */
public class Modify extends AbstractFunction {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Allows the user to modify template facts on the fact-list. Only one fact may be modified "
					+ "with a single modify statement. The modification of a fact is equivalent to retracting "
					+ "the present fact and asserting the modified fact. Returns true on success.";
		}

		public int getParameterCount() {
			return 2;
		}

		public String getParameterDescription(int parameter) {
			if (parameter == 0)
				return "Fact to modify.";
			else
				return "Slot and new value which is changed in the given fact.";
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
				return JamochaType.FACT_IDS;
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
			return parameter > 1 && parameter < 0;
		}

		public String getExample() {
			return "(deftemplate car (slot color)(slot speed))\n"
					+ "(assert (car (color \"red\")(speed 200)))\n"
					+ "(assert (car (color \"blue\")(speed 150)))\n"
					+ "(assert (car (color \"green\")(speed 100)))\n"
					+ "(bind ?fact (fact-id 2))\n" + "(echo ?fact)\n"
					+ "(modify ?fact (speed 500))\n" + "(facts)";
		}

		public boolean isResultAutoGeneratable() {
			return true;
		}

		public Object getExpectedResult() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "modify";

	@Override
	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public JamochaValue executeFunction(Engine engine, Parameter[] params)
			throws EvaluationException {
		JamochaValue result = JamochaValue.FALSE;
		if (engine != null && params != null && params.length > 0) {
			Fact fact = null;
			// grather all infos:
			try {
				// modificonfiguration
				if (params[0] instanceof ModifyConfiguration) {
					ModifyConfiguration mc = (ModifyConfiguration) params[0];
					fact = ((JamochaValue)mc.getFactBinding()).getFactValue(engine);
					engine.modifyFact(fact, mc);
					result = JamochaValue.TRUE;
				}

			} catch (RetractException e) {
				throw new EvaluationException(e);
			} catch (AssertException e) {
				throw new EvaluationException(e);
			}
		}

		return result;
	}
}