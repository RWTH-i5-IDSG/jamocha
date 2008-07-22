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

package org.jamocha.engine.functions;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.IllegalParameterException;
import org.jamocha.parser.IllegalTypeException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.engine.BoundParam;
import org.jamocha.engine.Parameter;
import org.jamocha.engine.Engine;
import org.jamocha.engine.configurations.LoopForCountConfiguration;

/**
 * @author Alexander Wilden
 * 
 * loop-for-count counts up a given variable from a start index to an end index
 * and executes a list of given actions. Returns the result of the last action
 * executed.
 */
public class LoopForCount extends AbstractFunction {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "loop-for-count counts up a given variable from a start index to an end index and executes a list of given actions. Returns the result of the last action executed.";
		}

		public int getParameterCount() {
			return 0;
		}

		public String getParameterDescription(int parameter) {
			return "";
		}

		public String getParameterName(int parameter) {
			return "";
		}

		public JamochaType[] getParameterTypes(int parameter) {
			return JamochaType.NONE;
		}

		public JamochaType[] getReturnType() {
			return JamochaType.ANY;
		}

		public boolean isParameterCountFixed() {
			return true;
		}

		public boolean isParameterOptional(int parameter) {
			return false;
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

	private static final long serialVersionUID = 1L;

	public static final FunctionDescription DESCRIPTION = new Description();

	public static final String NAME = "loop-for-count";

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
		JamochaValue result = JamochaValue.NIL;
		if (params != null && params.length == 1) {
			engine.pushScope();
			LoopForCountConfiguration lfcConf = (LoopForCountConfiguration) params[0];
			BoundParam countVar = lfcConf.getLoopVar();
			JamochaValue startValue = lfcConf.getStartIndex().getValue(engine);
			if (!startValue.is(JamochaType.LONG))
				throw new IllegalTypeException(JamochaType.LONGS, startValue
						.getType());
			JamochaValue endValue = lfcConf.getEndIndex().getValue(engine);
			if (!endValue.is(JamochaType.LONG))
				throw new IllegalTypeException(JamochaType.LONGS, startValue
						.getType());
			long endIndex = endValue.getLongValue();
			for (long i = startValue.getLongValue(); i <= endIndex; i++) {
				engine.setBinding(countVar.getVariableName(), JamochaValue
						.newLong(i));
				result = lfcConf.getActions().getValue(engine);
			}
			engine.popScope();
		} else
			throw new IllegalParameterException(1);
		return result;
	}
}