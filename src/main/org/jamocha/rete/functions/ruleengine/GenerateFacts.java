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

import java.util.ArrayList;
import java.util.Iterator;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.IllegalParameterException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Deffact;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.functions.AbstractFunction;
import org.jamocha.rete.functions.FunctionDescription;
import org.jamocha.rule.Defrule;

/**
 * @author Peter Lin
 * 
 * Generate facts will call the utility class with the Rule object and return an
 * Object[] array of facts. Depending on the rule, there should be one or more
 * deffacts or object instances. The way to use this is to bind the result or
 * add it to a list.
 */
public class GenerateFacts extends AbstractFunction {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Generate facts will call the utility class with the Rule object and return an Object[] " +
					"array of facts. Depending on the rule, there should be one or more deffacts or object " +
					"instances. The way to use this is to bind the result or add it to a list.";
		}

		public int getParameterCount() {
			return 2;
		}

		public String getParameterDescription(int parameter) {
			switch (parameter) {
			case 0:
				return "Name of the rule to generate facts from.";
			case 1:
				return "If true the results are printed out.";
			}
			return "";
		}

		public String getParameterName(int parameter) {
			switch (parameter) {
			case 0:
				return "ruleName";
			case 1:
				return "echo";
			}
			return "";
		}

		public JamochaType[] getParameterTypes(int parameter) {
			switch (parameter) {
			case 0:
				return JamochaType.IDENTIFIERS;
			case 1:
				return JamochaType.BOOLEANS;
			}
			return JamochaType.NONE;
		}

		public JamochaType[] getReturnType() {
			return JamochaType.OBJECTS;
		}

		public boolean isParameterCountFixed() {
			return false;
		}

		public boolean isParameterOptional(int parameter) {
			if (parameter == 0)
				return false;
			else
				return true;
		}

		public String getExample() {
			// TODO Auto-generated method stub
			return null;
		}

		public boolean isResultAutoGeneratable() {
			// TODO Auto-generated method stub
			return false;
		}
	}

	public static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "generate-facts";

	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	public String getName() {
		return NAME;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		JamochaValue result = JamochaValue.FALSE;
		boolean echo = false;
		ArrayList facts = null;
		if (params != null && params.length >= 1) {
			JamochaValue firstParam = params[0].getValue(engine);
			Defrule r = (Defrule) engine.getCurrentFocus().findRule(
					firstParam.getIdentifierValue());
			if (params.length == 2) {
				if (params[1].getValue(engine).getBooleanValue()) {
					echo = true;
				}
			}
			facts = org.jamocha.rule.util.GenerateFacts
					.generateFacts(r, engine);
			if (facts.size() > 0) {
				if (echo) {
					Iterator itr = facts.iterator();
					while (itr.hasNext()) {
						Object data = itr.next();
						if (data instanceof Deffact) {
							Deffact f = (Deffact) data;
							engine.writeMessage(f.toFactString());
						} else {
							engine.writeMessage(data.toString());
						}
					}
				}
				result = JamochaValue.newObject(facts.toArray());
			}
		} else {
			throw new IllegalParameterException(1);
		}
		return result;
	}
}