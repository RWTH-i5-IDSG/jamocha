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
import java.util.ArrayList;
import java.util.Iterator;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Deffact;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ValueParam;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rule.Defrule;
import org.jamocha.rule.util.GenerateFacts;

/**
 * @author Peter Lin
 *
 * ClearFunction will call Rete.clear()
 */
public class TestRuleFunction implements Function, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String TESTRULE = "test-rule";

	/**
	 * 
	 */
	public TestRuleFunction() {
		super();
	}

	public JamochaType getReturnType() {
		return JamochaType.BOOLEAN;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params) throws EvaluationException {
		JamochaValue result = JamochaValue.FALSE;
		if (params != null && params.length == 1) {
			String rlz = params[0].getValue(engine).getStringValue();
			Defrule r = (Defrule)engine.getCurrentFocus().findRule(rlz);
			ArrayList facts = GenerateFacts.generateFacts(r,engine);
			if (facts.size() > 0) {
				try {
					engine.setWatch(Rete.WATCH_ALL);
					Iterator itr = facts.iterator();
					while (itr.hasNext()) {
						Object data = itr.next();
						if (data instanceof Deffact) {
							engine.assertFact( (Deffact)data );
						} else {
							engine.assertObject(data,null,false,true);
						}
					}
					engine.fire();
					engine.setUnWatch(Rete.WATCH_ALL);
					result = JamochaValue.TRUE;
				} catch (AssertException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	public String getName() {
		return TESTRULE;
	}

	/**
	 * The function does not take any parameters
	 */
	public Class[] getParameter() {
		return new Class[]{ValueParam.class};
	}

	public String toPPString(Parameter[] params, int indents) {
		if (indents > 0) {
			StringBuffer buf = new StringBuffer();
			for (int idx = 0; idx < indents; idx++) {
				buf.append(" ");
			}
			buf.append("(test-rule)");
			return buf.toString();
		} else {
			return "(test-rule [rule])\n" +
			"Function description:\n" +
			"\tGenerate the facts for a rule, assert them and\n" +
			"\tcall (fire).\n";
		}
	}
}
