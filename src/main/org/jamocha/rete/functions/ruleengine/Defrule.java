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
import org.jamocha.parser.IllegalParameterException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.parser.RuleException;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.configurations.DefruleConfiguration;
import org.jamocha.rete.functions.FunctionDescription;

/**
 * @author Peter Lin
 * 
 * Defines a new rule in the currently focused module of the engine.
 */
public class Defrule implements Function, Serializable {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Defines a new rule in the currently focused module of the engine.\n" +
					"defrule has the following syntax:\n" +
					"(defrule rule_name \"optional_comment\"\n" +
					"	(pattern_1)     ; Left-Hand Side (LHS)\n" +
					"	(pattern_2)     ; of the rule consisting of elements\n" +
					"		.           ; before the \"=>\"\n" +
					"		.\n" +
					"		.\n" +
					"	(pattern_N)\n" +
					"=>                 ; THEN arrow\n" +
					"	(action_1)      ; Right-Hand Side (RHS)\n" +
					"	(action_2)      ; of the rule consisting of elements\n" +
					"		.           ; after the \"=>\"\n" +
					"		.\n" +
					"	(action_M)\n" +
					")                  ; close defrule";
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
			return "(deftemplate customer\n" +
					"  (slot first)\n" +
					"  (slot last)\n" +
					"  (slot title)\n" +
					"  (slot address)\n" +
					")\n" +
					"(defrule rule\n" +
					"  (customer\n" +
					"    (first \"john\")\n" +
					"  )\n" +
					"  =>\n" +
					"  (printout t \"rule0 was fired\" )\n" +
					")\n" +
					"(assert (customer (first \"john\")(last \"doe\")(address \"moon\") ) )\n" +
					"(fire)";
		}

		public boolean isResultAutoGeneratable() {
			// TODO Auto-generated method stub
			return false;
		}
	}

	private static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "defrule";

	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	public String getName() {
		return NAME;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		JamochaValue result = JamochaValue.FALSE;

		if (params != null && params.length == 1) {
			org.jamocha.rule.Defrule defrule = null;
			if (params[0] instanceof JamochaValue) {
				// get defrule from first parameter:
				JamochaValue firstParam = params[0].getValue(engine);
				if (firstParam.getObjectValue() instanceof org.jamocha.rule.Defrule) {
					defrule = (org.jamocha.rule.Defrule) firstParam
							.getObjectValue();
				}
			}
			// create new defrule from DefruleConfiguration:
			else if (params[0] instanceof DefruleConfiguration) {
				defrule = new org.jamocha.rule.Defrule(
						(DefruleConfiguration) params[0], engine);
			} else {
				throw new RuleException(
						"Parameter 1 is no Defrule Configuration.");
			}
			// compile Defrule:
			if (!engine.getCurrentFocus().containsRule(defrule)) {
				if (engine.getRuleCompiler().addRule(defrule)) {
					result = JamochaValue.TRUE;
					engine.writeMessage("added rule with complexity "
							+ defrule.getTotalComplexity());
				}
			}
		} else {
			throw new IllegalParameterException(1);
		}
		return result;
	}

}
