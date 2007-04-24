/*
 * Copyright 2007 Alexander Wilden
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
package org.jamocha.rete.functions;

import java.io.Serializable;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.IllegalParameterException;
import org.jamocha.parser.IllegalTypeException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.BoundParam;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.configurations.LoopForCountConfiguration;

/**
 * @author Alexander Wilden
 * 
 * loop-for-count counts up a given variable from a start index to an end index
 * and executes a list of given actions. Returns the result of the last action
 * executed.
 */
public class LoopForCount implements Serializable, Function {

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
	}

	private static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "loop-for-count";

	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	public String getName() {
		return NAME;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		JamochaValue result = JamochaValue.NIL;
		if (params != null && params.length == 1) {
			LoopForCountConfiguration lfcConf = (LoopForCountConfiguration) params[0];
			BoundParam countVar = lfcConf.getLoopVar();
			JamochaValue startValue = lfcConf.getStartIndex().getValue(engine);
			if (!startValue.is(JamochaType.LONG))
				throw new IllegalTypeException(JamochaType.LONGS, startValue
						.getType());
			long startIndex = startValue.getLongValue();
			JamochaValue endValue = lfcConf.getEndIndex().getValue(engine);
			if (!endValue.is(JamochaType.LONG))
				throw new IllegalTypeException(JamochaType.LONGS, startValue
						.getType());
			long endIndex = endValue.getLongValue();
			engine.pushScope();
			while (startIndex <= endIndex) {
				engine.setBinding(countVar.getVariableName(), JamochaValue
						.newLong(startIndex));
				result = lfcConf.getActions().getValue(engine);
				startIndex++;
			}
			engine.popScope();
		} else {
			throw new IllegalParameterException(1);
		}
		return result;
	}
}
