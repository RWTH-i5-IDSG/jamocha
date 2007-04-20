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
			return "Defines a new rule in the currently focused module of the engine.";
		}

		public int getParameterCount() {
			return 1;
		}

		public String getParameterDescription(int parameter) {
			return "Rule that should be defined.";
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
				throw new EvaluationException("Parameter 1 is no defrule.");
			}
			// compile Defrule:
			if (!engine.getCurrentFocus().containsRule(defrule)) {
				if (engine.getRuleCompiler().addRule(defrule)) {
					result = JamochaValue.TRUE;
				}
			}
		} else {
			throw new IllegalParameterException(1);
		}
		return result;
	}

}
