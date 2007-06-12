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
import org.jamocha.parser.IllegalParameterException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Deffact;
import org.jamocha.rete.Deftemplate;
import org.jamocha.rete.Fact;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.functions.FunctionDescription;

/**
 * @author Peter Lin
 * 
 * Allows the user to add a temporal fact to the fact-list. If the fact-list is being watched, 
 * an inform message is printed each time a fact is asserted.
 */
public class AssertTemporal implements Function, Serializable {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Allows the user to add a temporal fact to the fact-list. If the fact-list is being watched, " +
					"an inform message is printed each time a fact is asserted.";
		}

		public int getParameterCount() {
			return 1;
		}

		public String getParameterDescription(int parameter) {
			return "Temporal fact(s) to be asserted.";
		}

		public String getParameterName(int parameter) {
			return "temporalFact";
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
			// TODO Auto-generated method stub
			return null;
		}
	}

	private static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "assert-temporal";

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

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		JamochaValue result = JamochaValue.FALSE;
		if (params.length > 0) {
			Deffact fact = null;
			JamochaValue firstParam = params[0].getValue(engine);
			if (firstParam.getType().equals(JamochaType.FACT)) {
				fact = (Deffact) firstParam.getFactValue();
			} else {
				Deftemplate tmpl = (Deftemplate) engine.getCurrentFocus()
						.getTemplate(firstParam.getIdentifierValue());
				// before we create the fact, we need to remove the four
				// slots for temporal facts
				fact = (Deffact) tmpl.createTemporalFact((Object[]) params[1]
						.getValue(engine).getObjectValue(), -1, engine);
			}
			if (fact.hasBinding()) {
				fact.resolveValues(engine, this.triggerFacts);
				fact = fact.cloneFact(engine);
			}
			try {
				Fact assertedFact = engine.assertFact(fact);
				// if the asserted fact is another object than the fact
				// we tried to assert, an equal fact was already asserted
				if (assertedFact == fact) {
					result = JamochaValue.newFactId(fact.getFactId());
				}
			} catch (AssertException e) {
				// we should log this and output an error
				throw new EvaluationException(e);
			}
		} else {
			throw new IllegalParameterException(1, true);
		}
		return result;
	}
}
