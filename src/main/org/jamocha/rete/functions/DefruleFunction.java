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
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rule.Defrule;

/**
 * @author Peter Lin
 * 
 */
public class DefruleFunction implements Function, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String NAME = "defrule";

	public DefruleFunction() {
		super();
	}

	public JamochaType getReturnType() {
		return JamochaType.BOOLEAN;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		JamochaValue result = JamochaValue.FALSE;
		if (params != null && params.length == 1) {
			JamochaValue firstParam = params[0].getValue(engine);
			if (firstParam.getObjectValue() instanceof Defrule) {
				Defrule rl = (Defrule) firstParam.getObjectValue();
				if (!engine.getCurrentFocus().containsRule(rl)) {
					if (engine.getRuleCompiler().addRule(rl)) {
						result = JamochaValue.TRUE;
					}
				}
			} else {
				throw new EvaluationException("Parameter 1 is no defrule.");
			}
		} else {
			throw new IllegalParameterException(1);
		}
		return result;
	}

	public String getName() {
		return NAME;
	}

	public String toPPString(Parameter[] params, int indents) {
		if (params != null) {
			StringBuffer buf = new StringBuffer();
			return buf.toString();
		} else {
			return "(defrule <rule-name> (declare (properties)+?) (CE)+ => ([function]))"
					+ ""
					+ "(defrule <rule-name> \"optional_comment\" "
					+ "	(pattern_1) 		; Left-Hand Side (LHS)"
					+ "	(pattern_2) 		; of the rule consisting of elements"
					+ "	...					; before the \"=>\""
					+ "	...					"
					+ "	...					"
					+ "	(pattern_N)"
					+ "	=>"
					+ "	(action_1) 			; Right-Hand Side (RHS)"
					+ "	(action_2) 			; of the rule consisting of elements"
					+ "	...					; after the \"=>\""
					+ "	...					"
					+ "	...					"
					+ "	(action_M)) 		; The last \")\" balances the opening"
					+ "						; \")\" to the left of \"defrule\"."
					+ ""
					+ "Be sure all your parentheses balance or you will get error messages!";
		}
	}
}
