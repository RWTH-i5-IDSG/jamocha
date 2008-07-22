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

import java.util.HashMap;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.engine.Parameter;
import org.jamocha.engine.Engine;
import org.jamocha.engine.functions.AbstractFunction;
import org.jamocha.engine.functions.FunctionDescription;

/**
 * @author Peter Lin
 * 
 * MatchesFunction will print out all partial matches including alpha and beta
 * nodes. If the function is called without any parameters, it prints out all
 * the memories. If parameters are passed, the output will be filtered. Returns
 * NIL.
 */
public class Matches extends AbstractFunction {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "MatchesFunction will print out all partial matches including alpha and beta nodes. If the function is called without any parameters, it prints out all the memories. If parameters are passed, the output will be filtered. Returns NIL.";
		}

		public int getParameterCount() {
			return 1;
		}

		public String getParameterDescription(int parameter) {
			return "Filter to apply to the output.";
		}

		public String getParameterName(int parameter) {
			return "filter";
		}

		public JamochaType[] getParameterTypes(int parameter) {
			return JamochaType.STRINGS;
		}

		public JamochaType[] getReturnType() {
			return JamochaType.NONE;
		}

		public boolean isParameterCountFixed() {
			return false;
		}

		public boolean isParameterOptional(int parameter) {
			return true;
		}

		public String getExample() {
			// TODO Auto-generated method stub
			return null;
		}

		public boolean isResultAutoGeneratable() {
			// TODO Auto-generated method stub
			return false;
		}

		public Object getExpectedResult() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "matches";

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
		HashMap<String, Object> filter = new HashMap<String, Object>();
		if (params != null && params.length > 0)
			// now we populate the filter
			for (int idx = 0; idx < params.length; idx++)
				filter.put(params[idx].getValue(engine).getStringValue(), null);
		engine.writeMessage("sorry, i have to become reimplemented");
		// TODO find an equivalent of that
		// engine.getWorkingMemory().printWorkingMemory(filter);
		return JamochaValue.NIL;
	}
}
