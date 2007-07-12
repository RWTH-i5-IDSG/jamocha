/*
 * Copyright 2002-2006 Peter Lin, 2007 Alexander Wilden, Uta Christoph
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
import org.jamocha.parser.IllegalParameterException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.configurations.DefmoduleConfiguration;
import org.jamocha.rete.functions.FunctionDescription;

/**
 * @author Peter Lin
 * 
 * Defines a new module in the engine. Defmodule enables the division of rules
 * and facts into distinct groups called modules. Modules help to physically
 * organize large numbers of rules into logical groups. The commands for listing
 * constructs (rules, facts, and so on) let you specify the name of a module and
 * can then operate on one module at a time, e.g. (rules MOD1), (facts WORK),
 * (list-deftemplates MAIN).. Furthermore modules provide a control mechanism:
 * The rules in a module fire only when that module has the focus, and only one
 * module can be in focus at a time.
 */
public class Defmodule implements Function, Serializable {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Defines a new module in the engine.\n"
					+ "Defmodule enables the division of rules and facts into distinct groups called modules. "
					+ "Modules help to physically organize large numbers of rules into logical groups. "
					+
					// TODO uncomment when the following holds for Jamocha (when
					// defmodule is implemented..)
					// "The commands for listing constructs (rules, facts, and
					// so on) let you specify the name of a module " +
					// "and can then operate on one module at a time, e.g.
					// (rules MOD1), (facts WORK), (list-deftemplates MAIN).." +
					"Furthermore modules provide a control mechanism: "
					+ "The rules in a module fire only when that module has the focus, and only one module can be "
					+ "in focus at a time.";
		}

		public int getParameterCount() {
			return 1;
		}

		public String getParameterDescription(int parameter) {
			return "Name for the new module";
		}

		public String getParameterName(int parameter) {
			return "name";
		}

		public JamochaType[] getParameterTypes(int parameter) {
			return JamochaType.IDENTIFIERS;
		}

		public JamochaType[] getReturnType() {
			return JamochaType.BOOLEANS;
		}

		public boolean isParameterCountFixed() {
			return true;
		}

		public boolean isParameterOptional(int parameter) {
			return false;
		}

		public String getExample() {
			return "(defmodule PIZZAENV)\n" + "(get-current-module)";
		}

		public boolean isResultAutoGeneratable() {
			// TODO Auto-generated method stub
			return false;
		}
	}

	private static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "defmodule";

	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	public String getName() {
		return NAME;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		JamochaValue result = JamochaValue.FALSE;
		if (params != null && params.length == 1) {
			DefmoduleConfiguration defmodconf = (DefmoduleConfiguration) params[0];
			engine.addModule(defmodconf.getModuleName());
			result = JamochaValue.TRUE;
		} else {
			throw new IllegalParameterException(1);
		}
		return result;
	}
}