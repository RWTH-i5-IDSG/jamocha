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
 * Sets rule validation to true or false. Returns true on success.
 */
public class ValidateRule extends AbstractFunction {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Sets rule validation to true or false. Returns true on success.";
		}

		public int getParameterCount() {
			return 1;
		}

		public String getParameterDescription(int parameter) {
			return "'true' or 'false'";
		}

		public String getParameterName(int parameter) {
			return "switch";
		}

		public JamochaType[] getParameterTypes(int parameter) {
			return JamochaType.BOOLEANS;
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
			return "(validate-rule true)\n" +
					"(deftemplate customer\n" +
					"  (slot first)\n" +
					"  (slot last)\n" +
					"  (slot title)\n" +
					"  (slot address)\n" +
					")\n" +
					"(defrule rule\n" +
					"  (customer\n" +
					"    (first \"john\")\n" +
					"  )\n" +
					"  =>\n" +
					"  (printout t \"rule0 was fired\" )\n" +
					")\n" +
					"(assert (customer (first \"john\")(last \"doe\")(address \"moon\") ) )\n" +
					"(fire)";
		}

		public boolean isResultAutoGeneratable() {
			return true;
		}
	}

	public static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	protected static final String NAME = "validate-rule";

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
			JamochaValue param = params[0].getValue(engine);
			engine.setValidateRules(param.getBooleanValue());
			result = JamochaValue.TRUE;
		}
		return result;
	}
}