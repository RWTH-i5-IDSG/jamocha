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

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.IllegalParameterException;
import org.jamocha.parser.IllegalTypeException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Deffact;
import org.jamocha.rete.Fact;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.Template;
import org.jamocha.rete.configurations.AssertConfiguration;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.functions.Function;
import org.jamocha.rete.functions.FunctionDescription;

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
public class Assert extends Function {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Allows the user to add a fact to the fact-list. Multiple facts may be asserted with each call. " + "If the fact-list is being watched, an inform message is printed each time a "
					+ "fact is asserted.\n" + "Attention: In Jamocha there has to exist a corresponding template in order to assert a fact. ";
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
	}

	public static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "assert";

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
		if (params.length > 0) {
			Deffact fact = null;

			// get all the assert configuration from params
			if (params[0] instanceof AssertConfiguration) {

				AssertConfiguration ac = null;
				String templateName = null;

				for (int i = 0; i < params.length; i++) {
					ac = (AssertConfiguration) params[i];

					// get the template name
					templateName = ac.getTemplateName();
					fact = (Deffact) engine.getModules().createFact(ac.getSlots(), templateName);
					//TODO: check if binding handling can be moved to create fact
					if (fact.hasBinding()) {
						fact.resolveValues(engine, this.triggerFacts);
						fact = fact.cloneFact(engine);
					}
					Fact assertedFact = engine.assertFact(fact);
					// if the fact id is still -1, it means it wasn't
					// asserted
					// if it was asserted, we return the fact id, otherwise
					// we return "false".
					if (assertedFact.getFactId() > 0) {
						result = JamochaValue.newFactId(assertedFact.getFactId());
					} else {
						throw new AssertException("Fact could not be asserted for an unknown reason.");
					}
				}

				// no assert configuration:
			} else {
				assert (false);
				JamochaValue firstParam = params[0].getValue(engine);

				if (firstParam.getType().equals(JamochaType.IDENTIFIER)) {
					JamochaValue secondParam = params[1].getValue(engine);
					Template tmpl = (Template) engine.getCurrentFocus().getTemplate(firstParam.getIdentifierValue());
					fact = (Deffact) tmpl.createFact((Object[]) secondParam.getObjectValue(), engine);
				} else if (firstParam.getType().equals(JamochaType.FACT)) {
					fact = (Deffact) firstParam.getFactValue();
				} else {
					throw new IllegalTypeException(new JamochaType[] { JamochaType.FACT, JamochaType.IDENTIFIER }, firstParam.getType());
				}

				if (fact.hasBinding()) {
					fact.resolveValues(engine, this.triggerFacts);
					fact = fact.cloneFact(engine);
				}

				Fact assertedFact = engine.assertFact(fact);
				// if the fact id is still -1, it means it wasn't asserted
				// if it was asserted, we return the fact id, otherwise
				// we return "false".
				if (assertedFact == fact) {
					result = JamochaValue.newFactId(fact.getFactId());
				} else {
					throw new IllegalParameterException(1);
				}
			}
		}
		return result;
	}
}