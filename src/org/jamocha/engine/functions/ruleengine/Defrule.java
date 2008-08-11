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

import org.jamocha.communication.logging.Logging;
import org.jamocha.engine.Engine;
import org.jamocha.engine.Parameter;
import org.jamocha.engine.configurations.DefruleConfiguration;
import org.jamocha.engine.functions.AbstractFunction;
import org.jamocha.engine.functions.FunctionDescription;
import org.jamocha.engine.rules.rulecompiler.CompileRuleException;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.IllegalParameterException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.parser.RuleException;

/**
 * @author Peter Lin
 * 
 * Defines a new rule in the currently focused module of the engine.
 */
public class Defrule extends AbstractFunction {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Defines a new rule in the currently focused module of the engine.\n"
					+ "defrule has the following syntax:\n"
					+ "(defrule rule_name \"optional_comment\"\n"
					+ "	(pattern_1)     ; Left-Hand Side (LHS)\n"
					+ "	(pattern_2)     ; of the rule consisting of elements\n"
					+ "		.           ; before the \"=>\"\n"
					+ "		.\n"
					+ "		.\n"
					+ "	(pattern_N)\n"
					+ "=>                 ; THEN arrow\n"
					+ "	(action_1)      ; Right-Hand Side (RHS)\n"
					+ "	(action_2)      ; of the rule consisting of elements\n"
					+ "		.           ; after the \"=>\"\n"
					+ "		.\n"
					+ "	(action_M)\n" + ")                  ; close defrule";
		}

		public int getParameterCount() {
			return 1;
		}

		public String getParameterDescription(int parameter) {
			return "Rule to be defined.";
		}

		public String getParameterName(int parameter) {
			return "rule";
		}

		public JamochaType[] getParameterTypes(int parameter) {
			return JamochaType.OBJECTS;
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
					+ "  =>\n"
					+ "  (printout t \"rule0 was fired\" )\n"
					+ ")\n"
					+ "(assert (customer (first \"john\")(last \"doe\")(address \"moon\") ) )\n"
					+ "(fire)";
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

	public static final String NAME = "defrule";

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

		if (params != null && params.length == 1) {
			org.jamocha.rules.Defrule defrule = null;
			if (params[0] instanceof JamochaValue) {
				// get defrule from first parameter:
				JamochaValue firstParam = params[0].getValue(engine);
				if (firstParam.getObjectValue() instanceof org.jamocha.rules.Defrule)
					defrule = (org.jamocha.rules.Defrule) firstParam
							.getObjectValue();
			}
			// create new defrule from DefruleConfiguration:
			else if (params[0] instanceof DefruleConfiguration)
				// TODO this is wrong! we have to lookup the in-rule-name-coded
				// module
				defrule = new org.jamocha.rules.Defrule(engine
						.getCurrentFocus(), (DefruleConfiguration) params[0],
						engine);
			else
				throw new RuleException(
						"Parameter 1 is no Defrule Configuration.");
			// compile Defrule:
			try {
				if (engine.addRule(defrule)) {
					result = JamochaValue.TRUE;
					engine.writeMessage("added rule with complexity "
							+ defrule.getComplexity());
				}
			} catch (CompileRuleException e) {
				Logging.logger(this.getClass()).warn(e);
			}
		} else
			throw new IllegalParameterException(1);
		return result;
	}

}
