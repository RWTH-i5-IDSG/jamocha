/*
 * Copyright 2002-2008 Peter Lin & The Jamocha Team
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

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rules.Defrule;
import org.jamocha.engine.Parameter;
import org.jamocha.engine.Engine;
import org.jamocha.engine.functions.AbstractFunction;
import org.jamocha.engine.functions.FunctionDescription;

/**
 * @author Peter Lin
 * 
 * Removes a rule in the engine. Returns true on success.
 */
public class UnDefrule extends AbstractFunction {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Removes a rule in the engine. Returns true on success.";
		}

		public int getParameterCount() {
			return 1;
		}

		public String getParameterDescription(int parameter) {
			return "Rule that will be removed.";
		}

		public String getParameterName(int parameter) {
			return "ruleName";
		}

		public JamochaType[] getParameterTypes(int parameter) {
			return JamochaType.STRINGS;
		}

		public JamochaType[] getReturnType() {
			return JamochaType.BOOLEANS;
		}

		public boolean isParameterCountFixed() {
			return true;
		}

		public boolean isParameterOptional(int parameter) {
			return false;
		}

		public String getExample() {
			return "(deftemplate customer\n"
					+ "  (slot first)\n"
					+ "  (slot last)\n"
					+ "  (slot title)\n"
					+ "  (slot address)\n"
					+ ")\n"
					+ "(defrule rule\n"
					+ "  (customer\n"
					+ "    (first \"john\")\n"
					+ "  )\n"
					+ "=>\n"
					+ "  (printout t \"rule was fired\" )\n"
					+ ")\n"
					+ "(assert (customer (first \"john\")(last \"doe\")(address \"moon\") ) )\n"
					+ "(fire)\n" + "(rules)\n" + "(undefrule rule)\n"
					+ "(rules)";
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

	public static final String NAME = "undefrule";

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
		if (params.length == 1) {
			String rl = params[0].getValue(engine).getStringValue();
			Defrule defrl = (Defrule) engine.getCurrentFocus().findRule(rl);
			if (defrl != null) {
				engine.getCurrentFocus().removeRule(defrl);
				result = JamochaValue.TRUE;
			}
		}
		return result;
	}
}