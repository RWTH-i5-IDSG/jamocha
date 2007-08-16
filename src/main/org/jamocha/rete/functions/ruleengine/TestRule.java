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
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Deffact;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.exception.ExecuteException;
import org.jamocha.rete.functions.Function;
import org.jamocha.rete.functions.FunctionDescription;
import org.jamocha.rule.Defrule;
import org.jamocha.rule.util.GenerateFacts;

/**
 * @author Peter Lin
 * 
 * Generates the facts for a rule, asserts them and calls (fire).
 */
public class TestRule extends Function {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Generates the facts for a rule, asserts them and calls (fire).";
		}

		public int getParameterCount() {
			return 1;
		}

		public String getParameterDescription(int parameter) {
			return "Rule that will be tested.";
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

	public static final String NAME = "test-rule";

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
			String rlz = params[0].getValue(engine).getStringValue();
			Defrule r = (Defrule) engine.getCurrentFocus().findRule(rlz);
			ArrayList facts = GenerateFacts.generateFacts(r, engine);
			if (facts.size() > 0) {
				try {
					engine.setWatch(Rete.WATCH_ALL);
					Iterator itr = facts.iterator();
					while (itr.hasNext()) {
						Object data = itr.next();
						if (data instanceof Deffact) {
							engine.assertFact((Deffact) data);
						} else {
							engine.assertObject(data, null, false, true);
						}
					}
					engine.fire();
					engine.setUnWatch(Rete.WATCH_ALL);
					result = JamochaValue.TRUE;
				} catch (AssertException e) {
					throw new EvaluationException("Error during assert. ",e);
				} catch (ExecuteException e) {
					throw new EvaluationException("Error during fire. ",e);
				}
			}
		}
		return result;
	}
}
