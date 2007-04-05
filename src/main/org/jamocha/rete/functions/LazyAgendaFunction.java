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
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Constants;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;


/**
 * @author Peter Lin
 * 
 * LazyAgenda is used to turn on/off lazy agenda. That means the
 * activations are not sorted when added to the agenda. Instead,
 * it's sorted when they are removed.
 */
public class LazyAgendaFunction implements Function, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String LAZY_AGENDA = "lazy-agenda";

	/**
	 * 
	 */
	public LazyAgendaFunction() {
		super();
	}

	public JamochaType getReturnType() {
		return JamochaType.NIL;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params) throws EvaluationException {
		if (params != null && params.length == 1) {
			JamochaValue firstParam = params[0].getValue(engine);
			if (firstParam.getBooleanValue()) {
				engine.getCurrentFocus().setLazy(true);
				engine.writeMessage("TRUE");
			} else {
				engine.getCurrentFocus().setLazy(false);
				engine.writeMessage("FALSE");
			}
		}
		return JamochaValue.NIL;
	}

	public String getName() {
		return LAZY_AGENDA;
	}


	public String toPPString(Parameter[] params, int indents) {
		return "(lazy-agenda [TRUE|FALSE])";
	}

}
