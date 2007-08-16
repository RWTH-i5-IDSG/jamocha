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

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.functions.AbstractFunction;
import org.jamocha.rete.functions.FunctionDescription;

/**
 * @author Peter Lin
 * 
 * Sets the focus to the given module. If no argument is given the focus 
 * doesn't change. In either case the identifier of the current focus is returned.
 */
public class SetFocus extends AbstractFunction {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Sets the focus to the given module. If no argument is given the focus doesn't change. " +
					"In either case the identifier of the current focus is returned.";
		}

		public int getParameterCount() {
			return 1;
		}

		public String getParameterDescription(int parameter) {
			return "Focus to set in the engine.";
		}

		public String getParameterName(int parameter) {
			return "focus";
		}

		public JamochaType[] getParameterTypes(int parameter) {
			return JamochaType.IDENTIFIERS;
		}

		public JamochaType[] getReturnType() {
			return JamochaType.IDENTIFIERS;
		}

		public boolean isParameterCountFixed() {
			return false;
		}

		public boolean isParameterOptional(int parameter) {
			return true;
		}

		public String getExample() {
			return "(defmodule MOCHA)\n" +
					"(modules)\n" +
					"(get-current-module)\n" +
					"(set-focus MAIN)\n" +
					"(get-current-module)";
		}

		public boolean isResultAutoGeneratable() {
			return true;
		}
	}

	public static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "set-focus";

	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	public String getName() {
		return NAME;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		String focus = null;
		if (params != null && params.length == 1) {
			focus = params[0].getValue(engine).getIdentifierValue();
			engine.setFocus(focus);
			focus = engine.getCurrentFocus().getModuleName();
		} else {
			focus = engine.getCurrentFocus().getModuleName();
		}
		return JamochaValue.newIdentifier(focus);
	}
}