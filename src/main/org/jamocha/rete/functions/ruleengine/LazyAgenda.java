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
 * Used to turn on/off lazy agenda. That means the activations are not sorted 
 * when added to the agenda. Instead it is sorted when they are removed.
 */
public class LazyAgenda implements Function, Serializable {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Used to turn on/off lazy agenda. That means the activations are not sorted when added to the agenda. Instead it is sorted when they are removed.";
		}

		public int getParameterCount() {
			return 1;
		}

		public String getParameterDescription(int parameter) {
			return "If true lazy agenda is turned on.";
		}

		public String getParameterName(int parameter) {
			return "lazy";
		}

		public JamochaType[] getParameterTypes(int parameter) {
			return JamochaType.BOOLEANS;
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
			return "(lazy-agenda)";
		}

		public boolean isResultAutoGeneratable() {
			// TODO Auto-generated method stub
			return true;
		}
	}

	private static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "lazy-agenda";

	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	public String getName() {
		return NAME;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		if (params != null && params.length == 1) {
			JamochaValue firstParam = params[0].getValue(engine);
//			if (firstParam.getBooleanValue()) {
//				engine.getAgendas().getAgenda(engine.getCurrentFocus()).
//				engine.getCurrentFocus().setLazy(true);
//				engine.writeMessage("TRUE");
//			} else {
//				engine.getCurrentFocus().setLazy(false);
//				engine.writeMessage("FALSE");
//			}
		}
		return JamochaValue.NIL;
	}
}