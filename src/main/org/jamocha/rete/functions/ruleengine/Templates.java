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

import java.util.Collection;
import java.util.Iterator;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Constants;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.Template;
import org.jamocha.rete.functions.AbstractFunction;
import org.jamocha.rete.functions.FunctionDescription;

/**
 * @author Peter Lin
 * 
 * templates prints out the names of the deftemplates.
 */
public class Templates extends AbstractFunction {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Prints out the names of the deftemplates.";
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
			return JamochaType.NONE;
		}

		public boolean isParameterCountFixed() {
			return true;
		}

		public boolean isParameterOptional(int parameter) {
			return false;
		}

		public String getExample() {
			return "(deftemplate car (slot color)(slot speed))\n" +
					"(deftemplate bike (slot color)(slot kind))\n" +
					"(templates)";
		}

		public boolean isResultAutoGeneratable() {
			return true;
		}
	}

	private static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "templates";

	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	public String getName() {
		return NAME;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		Collection templates = engine.getCurrentFocus().getTemplates();
		int count = templates.size();
		Iterator itr = templates.iterator();
		while (itr.hasNext()) {
			Template r = (Template) itr.next();
			engine.writeMessage(r.getName() + Constants.LINEBREAK, "t");
		}
		engine.writeMessage("for a total of " + count + Constants.LINEBREAK,
				"t");
		return JamochaValue.NIL;
	}
}