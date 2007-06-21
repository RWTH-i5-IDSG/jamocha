/*
 * Copyright 2002-2006 Peter Lin, 2007 Alexander Wilden, Uta Christoph
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
import org.jamocha.rete.Deffact;
import org.jamocha.rete.Fact;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.configurations.AssertConfiguration;
import org.jamocha.rete.configurations.SlotConfiguration;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.functions.FunctionDescription;

/**
 * @author Peter Lin
 * 
 * Returns the ID of the given Fact or NIL if it wasn't found.
 */
public class FindFactByFact implements Function, Serializable {

	private static final class FindFactByFactDescription implements FunctionDescription {

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
			return "(deftemplate car (slot color)(slot speed))\n" +
					"(assert (car (color \"red\")(speed 200)))\n" +
					"(assert (car (color \"blue\")(speed 150)))\n" +
					"(assert (car (color \"green\")(speed 100)))\n" +					
					"(find-fact-by-fact (car (color \"green\") (speed 100)))";
		}
	}

	private static final FunctionDescription DESCRIPTION = new FindFactByFactDescription();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "find-fact-by-fact";

	protected Fact[] triggerFacts = null;

	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	public String getName() {
		return NAME;
	}

	public void setTriggerFacts(Fact[] facts) {
		this.triggerFacts = facts;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params) throws EvaluationException {
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
				org.jamocha.rete.Deftemplate template = (org.jamocha.rete.Deftemplate) engine.getCurrentFocus().getTemplate(templateName);
				if (template == null) {
					throw new AssertException("Template " + template.getName() + "could not be found");
				} else {

					// get the slot configurations
					scArray = ac.getSlots();

					// create the fact
					fact = (Deffact) template.createFact(scArray, engine);
					if (fact.hasBinding()) {
						fact.resolveValues(engine, this.triggerFacts);
						fact = fact.cloneFact(engine);
					}
					Fact existingFact = engine.getFact(fact);
					if (existingFact == null) {
						result = JamochaValue.NIL;
					} else {
						result = JamochaValue.newFactId(existingFact.getFactId());
					}
				}
			}
		}
		return result;
	}

}
