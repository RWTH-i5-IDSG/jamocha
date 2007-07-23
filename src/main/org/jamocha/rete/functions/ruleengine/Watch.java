/*
 * Copyright 2002-2006 Peter Lin, 2007 Alexander Wilden
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.jamocha.org/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.jamocha.rete.functions.ruleengine;

import java.io.Serializable;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.functions.FunctionDescription;

/**
 * @author Peter Lin
 * 
 * watch allows users to watch different engine processes, like activations,
 * facts and rules.
 */
public class Watch implements Function, Serializable {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Allows users to watch different engine processes, likes activations, facts and rules.";
		}

		public int getParameterCount() {
			return 1;
		}

		public String getParameterDescription(int parameter) {
			return "What to watch. One or several of: all, facts, activations or rules.";
		}

		public String getParameterName(int parameter) {
			return "watch";
		}

		public JamochaType[] getParameterTypes(int parameter) {
			return JamochaType.IDENTIFIERS;
		}

		public JamochaType[] getReturnType() {
			return JamochaType.NONE;
		}

		public boolean isParameterCountFixed() {
			return false;
		}

		public boolean isParameterOptional(int parameter) {
			return (parameter > 0);
		}

		public String getExample() {
			return "(watch facts)\n" +
					"(deftemplate templ2 (slot name))\n" +
					"(assert (templ2 (name test1)) (templ2 (name test2)))";
		}

		public boolean isResultAutoGeneratable() {
			return true;
		}
	}

	private static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	protected static final String NAME = "watch";

	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	public String getName() {
		return NAME;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		if (params != null) {
			// the params are not null, now check the parameter count
			if (params.length > 0) {
				for (int idx = 0; idx < params.length; idx++) {
					String cmd = params[idx].getValue(engine)
							.getIdentifierValue();
					setWatch(engine, cmd);
				}
			} else {
				// we do nothing, maybe we should return a message
			}
		}
		return JamochaValue.NIL;
	}

	protected void setWatch(Rete engine, String cmd) {
		if (cmd.equals("all")) {
			engine.setWatch(Rete.WATCH_ALL);
		} else if (cmd.equals("facts")) {
			engine.setWatch(Rete.WATCH_FACTS);
		} else if (cmd.equals("activations")) {
			engine.setWatch(Rete.WATCH_ACTIVATIONS);
		} else if (cmd.equals("rules")) {
			engine.setWatch(Rete.WATCH_RULES);
		}
	}
}
