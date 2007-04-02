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
import org.jamocha.parser.IllegalTypeException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Deffact;
import org.jamocha.rete.Deftemplate;
import org.jamocha.rete.Fact;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ValueParam;

/**
 * @author Peter Lin
 * 
 */
public class AssertFunction implements Function, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String ASSERT = "assert";

	protected Fact[] triggerFacts = null;

	public JamochaType getReturnType() {
		return JamochaType.FACT_ID;
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
			if (firstParam.getType().equals(JamochaType.IDENTIFIER)) {
				JamochaValue secondParam = params[1].getValue(engine);
				Deftemplate tmpl = (Deftemplate) engine.getCurrentFocus()
						.getTemplate(firstParam.getIdentifierValue());
				fact = (Deffact) tmpl.createFact((Object[]) secondParam
						.getObjectValue(), -1, engine);
			} else if (firstParam.getType().equals(JamochaType.FACT)) {
				fact = (Deffact) firstParam.getFactValue();
			} else {
				throw new IllegalTypeException(new JamochaType[] {
						JamochaType.FACT, JamochaType.IDENTIFIER }, firstParam
						.getType());
			}
			if (fact.hasBinding()) {
				fact.resolveValues(engine, this.triggerFacts);
				fact = fact.cloneFact(engine);
			}
			engine.assertFact(fact);
			// if the fact id is still -1, it means it wasn't asserted
			// if it was asserted, we return the fact id, otherwise
			// we return "false".
			if (fact.getFactId() > 0) {
				result = JamochaValue.newFactId(fact.getFactId());
			}
		} else {
			throw new IllegalParameterException(1);
		}
		return result;
	}

	public String getName() {
		return ASSERT;
	}

	/**
	 * The expected parameter is a deffact instance. According to CLIPS beginner
	 * guide, assert only takes facts and returns the id of the fact. For
	 * objects, there's (assert-object ?binding).
	 */
	public Class[] getParameter() {
		return new Class[] { ValueParam.class };
	}

	public String toPPString(Parameter[] params, int indents) {
		if (params != null && params.length > 0) {
			StringBuffer buf = new StringBuffer();
			buf.append("(assert ");
			for (int idx = 0; idx < params.length; idx++) {
				// the parameter should be a deffact
				buf.append(params[idx].getExpressionString());
			}
			buf.append(" )");
			return buf.toString();
		} else {
			return "(assert deffact])";
		}
	}
}
