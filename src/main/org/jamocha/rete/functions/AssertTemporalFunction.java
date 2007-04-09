/*
 * Copyright 2002-2006 Peter Lin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://ruleml-dev.sourceforge.net/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.jamocha.rete.functions;

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


/**
 * @author Peter Lin
 *
 */
public class AssertTemporalFunction implements Function, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String NAME = "assert-temporal";

	protected Fact[] triggerFacts = null;

	public JamochaType getReturnType() {
		return JamochaType.UNDEFINED;
	}

	public void setTriggerFacts(Fact[] facts) {
		this.triggerFacts = facts;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params) throws EvaluationException {
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
				fact = (Deffact) tmpl.createTemporalFact(
                        (Object[])params[1].getValue(engine).getObjectValue(),-1, engine);
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

	public String getName() {
		return NAME;
	}

	public String toPPString(Parameter[] params, int indents) {
		if (params != null && params.length > 0) {
			StringBuffer buf = new StringBuffer();
			buf.append("(assert-temporal ");
			for (int idx = 0; idx < params.length; idx++) {
				// the parameter should be a deffact
				buf.append(params[idx].getExpressionString());
			}
			buf.append(" )");
			return buf.toString();
		} else {
			return "(assert-temporal [deffact])";
		}
	}
}
