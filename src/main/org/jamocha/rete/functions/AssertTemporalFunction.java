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
import java.util.List;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnValue;
import org.jamocha.rete.DefaultReturnVector;
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

	public static final String ASSERT_TEMPORAL = "assert-temporal";

	protected Fact[] triggerFacts = null;

	public JamochaType getReturnType() {
		return Constants.STRING_TYPE;
	}

	public void setTriggerFacts(Fact[] facts) {
		this.triggerFacts = facts;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params) throws EvaluationException {
		String asrt = "";
		if (params.length > 0) {
			Deffact fact = null;
			if (params[0].getValue() instanceof Deffact) {
				fact = (Deffact) params[0].getValue();
			} else {
				Deftemplate tmpl = (Deftemplate) engine.getCurrentFocus()
						.getTemplate(params[0].getStringValue());
                // before we create the fact, we need to remove the four
                // slots for temporal facts
				fact = (Deffact) tmpl.createTemporalFact(
                        (Object[])params[1].getValue(),-1);
			}
			if (fact.hasBinding()) {
				fact.resolveValues(engine, this.triggerFacts);
				fact = fact.cloneFact();
			}
			try {
				engine.assertFact(fact);
				// if the fact id is still -1, it means it wasn't asserted
				// if it was asserted, we return the fact id, otherwise
				// we return "false".
				if (fact.getFactId() > 0) {
					asrt = String.valueOf(fact.getFactId());
				} else {
					asrt = "false";
				}
			} catch (AssertException e) {
				// we should log this and output an error
				asrt = "false";
			}
		} else {
			asrt = "false";
		}
		DefaultReturnVector ret = new DefaultReturnVector();
		DefaultReturnValue rv = new DefaultReturnValue(Constants.STRING_TYPE,
				asrt);
		ret.addReturnValue(rv);
		return ret;
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
				Deffact fact = (Deffact) params[idx].getValue();
				buf.append(fact.toPPString());
			}
			buf.append(" )");
			return buf.toString();
		} else {
			return "(assert-temporal [deffact])";
		}
	}
}
