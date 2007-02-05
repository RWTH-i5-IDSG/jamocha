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
import org.jamocha.rete.ValueParam;
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

	public static final String ASSERT_TEMPORAL = "assert-temporal";

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
                        (Object[])params[1].getValue(engine).getObjectValue(),-1);
			}
			if (fact.hasBinding()) {
				fact.resolveValues(engine, this.triggerFacts);
				fact = fact.cloneFact(engine);
			}
			try {
				engine.assertFact(fact);
				// if the fact id is still -1, it means it wasn't asserted
				// if it was asserted, we return the fact id, otherwise
				// we return "false".
				if (fact.getFactId() > 0) {
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
		return ASSERT_TEMPORAL;
	}

	/**
	 * The expected parameter is a deffact instance. According to CLIPS
	 * beginner guide, assert only takes facts and returns the id of the
	 * fact. For objects, there's (assert-object ?binding).
	 */
	public Class[] getParameter() {
		return new Class[] { ValueParam.class };
	}

	public String toPPString(Parameter[] params, int indents) {
		if (params != null && params.length > 0) {
			StringBuffer buf = new StringBuffer();
			buf.append("(assert-temporal ");
			for (int idx = 0; idx < params.length; idx++) {
				// the parameter should be a deffact
				buf.append(params[idx].getParameterString());
			}
			buf.append(" )");
			return buf.toString();
		} else {
			return "(assert-temporal [deffact])";
		}
	}
}
