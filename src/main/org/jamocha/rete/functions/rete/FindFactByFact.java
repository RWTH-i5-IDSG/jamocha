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
package org.jamocha.rete.functions.rete;

import java.io.Serializable;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.IllegalTypeException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Fact;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ValueParam;

/**
 * @author Peter Lin
 * 
 */
public class FindFactByFact implements Function, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String NAME = "find-fact-by-fact";

	protected Fact[] triggerFacts = null;

	public JamochaType getReturnType() {
		return JamochaType.FACT_ID;
	}

	public void setTriggerFacts(Fact[] facts) {
		this.triggerFacts = facts;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		JamochaValue factValue = params[0].getValue(engine);
		if (factValue.is(JamochaType.FACT))  {
		    Fact templateFact =factValue.getFactValue();
		    Fact existingFact = engine.getFact(templateFact);
		    if(existingFact == null) {
			return JamochaValue.NIL;
		    } else {
			return JamochaValue.newFactId(existingFact.getFactId());
		    }
		}
		throw new IllegalTypeException(JamochaType.FACTS, factValue.getType());
	}

	public String getName() {
		return NAME;
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
