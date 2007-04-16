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
import java.util.Collection;
import java.util.Iterator;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rule.Rule;


/**
 * @author Peter Lin
 * 
 * The purpose of the function is to print out the names of the rules
 * and the comment.
 */
public class RulesFunction implements Function, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String RULES = "rules";
	public static final String LISTRULES = "list-defrules";
	
	public RulesFunction() {
		super();
	}

	public JamochaType getReturnType() {
		return JamochaType.NIL;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params) throws EvaluationException {
		Collection rules = engine.getCurrentFocus().getAllRules();
		int count = rules.size();
		Iterator itr = rules.iterator();
		while (itr.hasNext()) {
			Rule r = (Rule)itr.next();
			engine.writeMessage(r.getName() + " \"" + r.getDescription() +
					"\" salience:" + r.getSalience() +
					" version:" + r.getVersion() +
					" no-agenda:" + r.getNoAgenda() + "\r\n", "t");
		}
		engine.writeMessage("for a total of " + count +"\r\n","t");
		return JamochaValue.NIL;
	}

	public String getName() {
		return RULES;
	}

	public Class[] getParameter() {
		return new Class[0];
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(rules)";
	}
}
