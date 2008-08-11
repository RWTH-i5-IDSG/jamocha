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
import org.jamocha.engine.configurations.AssertConfiguration;
import org.jamocha.engine.configurations.SlotConfiguration;
import org.jamocha.engine.functions.AbstractFunction;
import org.jamocha.engine.functions.FunctionDescription;
import org.jamocha.engine.workingmemory.elements.Deffact;
import org.jamocha.engine.workingmemory.elements.Fact;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;

/**
 * @author Peter Lin
 * 
 * Returns the ID of the given Fact or NIL if it wasn't found.
 */
public class FindFactByFact extends AbstractFunction {

	private static final class FindFactByFactDescription implements
			FunctionDescription {

		public String getDescription() {
			return "Returns the ID of the given fact or NIL if it wasn't found.";
		}

		public int getParameterCount() {
			return 1;
		}

		public String getParameterDescription(int parameter) {
			return "Fact to search for.";
		}

		public String getParameterName(int parameter) {
			return "fact";
		}

		public JamochaType[] getParameterTypes(int parameter) {
			return JamochaType.FACTS;
		}

		public JamochaType[] getReturnType() {
			return JamochaType.FACTS;
		}

		public boolean isParameterCountFixed() {
			return true;
		}

		public boolean isParameterOptional(int parameter) {
			return false;
		}

		public String getExample() {
			return "(deftemplate car (slot color)(slot speed))\n"
					+ "(assert (car (color \"red\")(speed 200)))\n"
					+ "(assert (car (color \"blue\")(speed 150)))\n"
					+ "(assert (car (color \"green\")(speed 100)))\n"
					+ "(find-fact-by-fact (car (color \"green\") (speed 100)))";
		}

		public boolean isResultAutoGeneratable() {
			return true;
		}

		public Object getExpectedResult() {
			return "f-4";
		}
	}

	public static final FunctionDescription DESCRIPTION = new FindFactByFactDescription();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "find-fact-by-fact";

	protected Fact[] triggerFacts = null;

	@Override
	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	@Override
	public String getName() {
		return NAME;
	}

	public void setTriggerFacts(Fact[] facts) {
		triggerFacts = facts;
	}

	@Override
	public JamochaValue executeFunction(Engine engine, Parameter[] params)
			throws EvaluationException {
		JamochaValue result = JamochaValue.FALSE;
		if (params.length == 1) {
			Deffact fact = null;

			// get all the assert configuration from params
			if (params[0] instanceof AssertConfiguration) {

				AssertConfiguration ac = null;
				String templateName = null;
				SlotConfiguration[] scArray = null;
				ac = (AssertConfiguration) params[0];

				// get the template name
				templateName = ac.getTemplateName();

				// check if the needed template exists in the engine
				org.jamocha.engine.workingmemory.elements.Deftemplate template = (org.jamocha.engine.workingmemory.elements.Deftemplate) engine
						.getCurrentFocus().getTemplate(templateName);
				if (template == null)
					throw new AssertException("Template " + template.getName()
							+ "could not be found");
				else {

					// get the slot configurations
					scArray = ac.getSlotConfigurations();

					// create the fact
					fact = (Deffact) template.createFact(scArray, engine);
					if (fact.hasBinding()) {
						fact.resolveValues(engine, triggerFacts);
						fact = fact.cloneFact(engine);
					}
					Fact existingFact = engine.getModules().getFactByFact(fact);
					if (existingFact == null)
						result = JamochaValue.NIL;
					else
						result = JamochaValue.newFactId(existingFact
								.getFactId());
				}
			}
		}
		return result;
	}

}
