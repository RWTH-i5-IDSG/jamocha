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

import org.jamocha.engine.Engine;
import org.jamocha.engine.Parameter;
import org.jamocha.engine.configurations.AssertConfiguration;
import org.jamocha.engine.functions.AbstractFunction;
import org.jamocha.engine.functions.FunctionDescription;
import org.jamocha.engine.workingmemory.elements.Deffact;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;

/**
 * @author Peter Lin
 * 
 * Allows the user to add a fact to the fact-list. Multiple facts may be
 * asserted with each call. If the fact-list is being watched, then an inform
 * message will be printed each time a fact is asserted.
 * <p>
 * Attention: In Jamocha there has to exist a corresponding template in order to
 * assert a fact.
 * </p>
 */
public class Assert extends AbstractFunction {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Allows the user to add a fact to the fact-list. Multiple facts may be asserted with each call. "
					+ "If the fact-list is being watched, an inform message is printed each time a "
					+ "fact is asserted.\n"
					+ "Attention: In Jamocha there has to exist a corresponding template in order to assert a fact. ";
		}

		public int getParameterCount() {
			return 1;
		}

		public String getParameterDescription(int parameter) {
			return "Fact(s) to be asserted.";
		}

		public String getParameterName(int parameter) {
			return "fact";
		}

		public JamochaType[] getParameterTypes(int parameter) {
			return JamochaType.ANY;
		}

		public JamochaType[] getReturnType() {
			return JamochaType.FACT_IDS;
		}

		public boolean isParameterCountFixed() {
			return false;
		}

		public boolean isParameterOptional(int parameter) {
			return false;
		}

		public String getExample() {
			return "(deftemplate car (slot color)(slot speed)) \n (assert (car (color \"red\")(speed 200))) \n (facts)";
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

	public static final String NAME = "assert";

	// protected Fact[] triggerFacts = null;

	@Override
	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	@Override
	public String getName() {
		return NAME;
	}

	// public void setTriggerFacts(Fact[] facts) {
	// this.triggerFacts = facts;
	// }

	@Override
	public JamochaValue executeFunction(Engine engine, Parameter[] params)
			throws EvaluationException {
		JamochaValue result = JamochaValue.FALSE;
		if (params.length > 0) {
			Deffact fact = null;

			AssertConfiguration ac = null;

			for (int i = 0; i < params.length; i++) {
				ac = (AssertConfiguration) params[i];
				fact = (Deffact) engine.getModules().createFact(ac);
				engine.assertFact(fact);
				result = JamochaValue.newFactId(fact.getFactId());
			}
		}
		return result;
	}
}