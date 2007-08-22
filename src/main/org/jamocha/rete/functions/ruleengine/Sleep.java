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
package org.jamocha.rete.functions.ruleengine;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.IllegalParameterException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.functions.AbstractFunction;
import org.jamocha.rete.functions.FunctionDescription;

/**
 * @author Alexander Wilden
 * 
 * Lets the CommandThread of the MessageRouter sleep for a given amount of time.
 */
public class Sleep extends AbstractFunction {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Lets the CommandThread of the MessageRouter sleep for a given amount of time.";
		}

		public int getParameterCount() {
			return 1;
		}

		public String getParameterDescription(int parameter) {
			return "Time to sleep (in milliseconds).";
		}

		public String getParameterName(int parameter) {
			return "sleepTime";
		}

		public JamochaType[] getParameterTypes(int parameter) {
			return JamochaType.LONGS;
		}

		public JamochaType[] getReturnType() {
			return JamochaType.NONE;
		}

		public boolean isParameterCountFixed() {
			return true;
		}

		public boolean isParameterOptional(int parameter) {
			return false;
		}

		public String getExample() {
			return "(sleep 1000)";
		}

		public boolean isResultAutoGeneratable() {
			return false;
		}
	}

	public static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "sleep";

	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	public String getName() {
		return NAME;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		if (params != null && params.length == 1) {
			long ms = params[0].getValue(engine).getLongValue();
			try {
				Thread.sleep(ms);
			} catch (InterruptedException e) {
				throw new EvaluationException("Sleep was interrupted.", e);
			}
		} else {
			throw new IllegalParameterException(1);
		}
		return JamochaValue.NIL;
	}
}