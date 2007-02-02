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
import org.jamocha.rete.BoundParam;
import org.jamocha.rete.Deffact;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.exception.RetractException;

public class RetractFunction implements Function, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String RETRACT = "retract";

	public RetractFunction() {
		super();
	}

	public JamochaType getReturnType() {
		return JamochaType.BOOLEAN;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		JamochaValue result = JamochaValue.FALSE;
		if (params != null && params.length >= 1) {
			for (int idx = 0; idx < params.length; idx++) {
				JamochaValue param = params[idx].getValue(engine);
				if (param.is(JamochaType.FACT_ID) || param.is(JamochaType.LONG)) {
					long factId = param.getFactIdValue();
					try {
						engine.retractById(factId);
						result = JamochaValue.TRUE;
					} catch (RetractException e) {
					}
				} else if (param.getType().equals(JamochaType.FACT)) {
					Deffact fact = (Deffact) param.getFactValue();
					try {
						if (params[idx].isObjectBinding()) {
							engine.retractObject(fact.getObjectInstance());
						} else {
							engine.retractFact(fact);
						}
						result = JamochaValue.TRUE;
					} catch (RetractException e) {
					}
				}
			}
		} else {
			throw new IllegalParameterException(1, true);
		}
		return result;
	}

	public String getName() {
		return RETRACT;
	}

	public Class[] getParameter() {
		return new Class[] { BoundParam.class };
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(retract [?binding|fact-id])\n" + "Function description:\n"
				+ "\tAllows the user to remove facts from the fact-list.";
	}

}
