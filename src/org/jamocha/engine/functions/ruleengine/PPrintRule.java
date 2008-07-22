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

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.parser.ParserFactory;
import org.jamocha.rules.Rule;
import org.jamocha.engine.Parameter;
import org.jamocha.engine.Engine;
import org.jamocha.engine.functions.AbstractFunction;
import org.jamocha.engine.functions.FunctionDescription;

/**
 * @author Peter Lin
 * 
 * The function will print out one or more rules in a pretty format. Note the
 * format may not be identical to what the user wrote. It is a normalized and
 * cleaned up format.
 */
public class PPrintRule extends AbstractFunction {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Prints out one or more rules in a pretty format. Note the format may not "
					+ "be identical to what the user wrote. It is a normalized and cleaned up format.";
		}

		public int getParameterCount() {
			return 1;
		}

		public String getParameterDescription(int parameter) {
			return "One or more rules to print out in a pretty format.";
		}

		public String getParameterName(int parameter) {
			return "ruleName";
		}

		public JamochaType[] getParameterTypes(int parameter) {
			return JamochaType.IDENTIFIERS;
		}

		public JamochaType[] getReturnType() {
			return JamochaType.NONE;
		}

		public boolean isParameterCountFixed() {
			return false;
		}

		public boolean isParameterOptional(int parameter) {
			return parameter > 1;
		}

		public String getExample() {
			return "(deftemplate customer\n" + "  (slot first)\n"
					+ "  (slot last)\n" + "  (slot title)\n"
					+ "  (slot address)\n" + ")\n" + "(defrule rule\n"
					+ "  (customer\n" + "    (first \"john\")\n" + "  )\n"
					+ "  =>\n" + "  (printout t \"rule0 was fired\" )\n"
					+ ")\n" + "(ppdefrule rule)";
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

	public static final String NAME = "ppdefrule";

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
		if (params != null && params.length > 0)
			for (int idx = 0; idx < params.length; idx++) {
				Rule rls = engine.getCurrentFocus().findRule(
						params[idx].getValue(engine).getIdentifierValue());
				engine.writeMessage(ParserFactory.getFormatter().visit(rls),
						"t");
			}
		return JamochaValue.NIL;
	}
}
