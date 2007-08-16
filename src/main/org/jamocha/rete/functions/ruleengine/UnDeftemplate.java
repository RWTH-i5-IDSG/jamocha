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
import org.jamocha.rete.Template;
import org.jamocha.rete.functions.AbstractFunction;
import org.jamocha.rete.functions.FunctionDescription;

/**
 * @author Peter Lin
 * 
 * Removes a template wich is currentlich not in use from the engine.
 * Returns true on success and false otherwise (e.g. if the template 
 * is still in use by a fact or a rule)..
 */
public class UnDeftemplate extends AbstractFunction {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Removes a template wich is currentlich not in use from the engine. Returns true on success " +
					"and false otherwise (e.g. if the template is still in use by a fact or a rule).";
		}

		public int getParameterCount() {
			return 1;
		}

		public String getParameterDescription(int parameter) {
			return "Template to be removed.";
		}

		public String getParameterName(int parameter) {
			return "templateName";
		}

		public JamochaType[] getParameterTypes(int parameter) {
			return JamochaType.STRINGS;
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
			return "(deftemplate transact\n" +
					"  (slot accountId (type STRING))\n" +
					"  (slot countryCode (type STRING))\n" +
					"  (slot currentPrice (type DOUBLE))\n" +
					"  (slot issuer (type STRING))\n" +
					"  (slot lastPrice (type DOUBLE)\n)" +
					"  (slot purchaseDate (type STRING))\n" +
					"  (slot total (type DOUBLE))\n" +
					")\n" +
					"(templates)\n" +
					"(undeftemplate transact)\n" +
					"(templates)";
		}

		public boolean isResultAutoGeneratable() {
			return true;
		}
	}

	public static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "undeftemplate";

	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	public String getName() {
		return NAME;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		JamochaValue result = JamochaValue.FALSE;
		if (params.length == 1) {
			String template = params[0].getValue(engine).getStringValue();
			Template t = engine.getCurrentFocus().getTemplate(template);
			if (!t.inUse()) {
				engine.getCurrentFocus().removeTemplate(t);
				result = JamochaValue.TRUE;
			}
		}
		return result;
	}
}