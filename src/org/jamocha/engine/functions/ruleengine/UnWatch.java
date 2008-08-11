/*
 * Copyright 2002-2008 The Jamocha Team
 * 
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

package org.jamocha.engine.functions.ruleengine;

import org.jamocha.Constants;
import org.jamocha.engine.Engine;
import org.jamocha.engine.Parameter;
import org.jamocha.engine.functions.AbstractFunction;
import org.jamocha.engine.functions.FunctionDescription;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;

/**
 * @author Peter Lin
 * 
 * unwatch allows users to remove the watch of different engine processes, like
 * activations, facts and rules.
 */
public class UnWatch extends AbstractFunction {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Allows users to remove the watch of different engine processes, like activations, "
					+ "facts and rules.";
		}

		public int getParameterCount() {
			return 1;
		}

		public String getParameterDescription(int parameter) {
			return "What to unwatch. One or several of: all, facts, activations or rules.";
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
			return parameter > 0;
		}

		public String getExample() {
			return "(watch facts)\n" + "(deftemplate templ2 (slot name))\n"
					+ "(assert (templ2 (name test1)) (templ2 (name test2)))\n"
					+ "(unwatch facts)\n" + "(assert (templ2 (name test3)))";
		}

		public boolean isResultAutoGeneratable() {
			return true;
		}

		public Object getExpectedResult() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	protected static final String NAME = "unwatch";

	@Override
	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public JamochaValue executeFunction(Engine engine, Parameter[] params)
			throws EvaluationException {
		if (params != null)
			// the params are not null, now check the parameter count
			if (params.length > 0)
				for (int idx = 0; idx < params.length; idx++) {
					String cmd = params[idx].getValue(engine)
							.getIdentifierValue();
					setUnWatch(engine, cmd);
				}
		return JamochaValue.NIL;
	}

	protected void setUnWatch(Engine engine, String cmd) {
		if (cmd.equals("all"))
			engine.setUnWatch(Constants.WATCH_ALL);
		else if (cmd.equals("facts"))
			engine.setUnWatch(Constants.WATCH_FACTS);
		else if (cmd.equals("activations"))
			engine.setUnWatch(Constants.WATCH_ACTIVATIONS);
		else if (cmd.equals("rules"))
			engine.setUnWatch(Constants.WATCH_RULES);
	}
}
