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
 * unwatch allows users to remove the watch of different engine processes, like
 * activations, facts and rules.
 */
public class UnWatch implements Function, Serializable {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "unwatch allows users to remove the watch of different engine processes, like activations, facts and rules.";
		}

		public int getParameterCount() {
			return 1;
		}

		public String getParameterDescription(int parameter) {
			return "What to unwatch. Can be one or more of: all, facts, activations or rules.";
		}

		public String getParameterName(int parameter) {
			return "unwatch";
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
	}

	private static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	protected static final String NAME = "unwatch";

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
					setUnWatch(engine, cmd);
				}
			}
		}
		return JamochaValue.NIL;
	}

	protected void setUnWatch(Rete engine, String cmd) {
		if (cmd.equals("all")) {
			engine.setUnWatch(Rete.WATCH_ALL);
		} else if (cmd.equals("facts")) {
			engine.setUnWatch(Rete.WATCH_FACTS);
		} else if (cmd.equals("activations")) {
			engine.setUnWatch(Rete.WATCH_ACTIVATIONS);
		} else if (cmd.equals("rules")) {
			engine.setUnWatch(Rete.WATCH_RULES);
		}
	}
}
