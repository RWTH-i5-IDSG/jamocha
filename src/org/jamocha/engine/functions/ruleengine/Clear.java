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
import org.jamocha.parser.IllegalParameterException;
import org.jamocha.parser.IllegalTypeException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.engine.Parameter;
import org.jamocha.engine.Engine;
import org.jamocha.engine.functions.AbstractFunction;
import org.jamocha.engine.functions.FunctionDescription;

/**
 * @author Peter Lin
 * 
 * Clears the Rete engine. Removes all constructs and all associated data
 * structures (such as facts and objects) from the Rete environment.
 * <p>
 * In case of a given argument it is parsed as identifier and only the
 * constructs that are identified by it are removed.
 * </p>
 * Returns true on success.
 */
public class Clear extends AbstractFunction {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Clears the Rete engine. Removes all constructs and all associated data structures (such as facts "
					+ "and objects) from the Rete environment.\n"
					+ "In case of a given argument it is parsed as identifier and only the constructs that are "
					+ "identified by it are removed.\n"
					+ "Returns true on success.";
		}

		public int getParameterCount() {
			return 1;
		}

		public String getParameterDescription(int parameter) {
			if (parameter > 0)
				return "";
			else
				return "Optional identifier saying what to clear (\"deffacts\" or \"objects\"), if not specified: all is cleared.";
		}

		public String getParameterName(int parameter) {
			if (parameter > 0)
				return "";
			else
				return "what";
		}

		public JamochaType[] getParameterTypes(int parameter) {

			if (parameter > 0)
				return JamochaType.NONE;
			else
				return JamochaType.IDENTIFIERS;
		}

		public JamochaType[] getReturnType() {
			return JamochaType.BOOLEANS;
		}

		public boolean isParameterCountFixed() {
			return false;
		}

		public boolean isParameterOptional(int parameter) {
			return true;
		}

		public String getExample() {
			return "(clear)";
		}

		public boolean isResultAutoGeneratable() {
			// TODO Auto-generated method stub
			return true;
		}

		public Object getExpectedResult() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "clear";

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

		if (params != null)
			if (params.length == 1) {
				JamochaValue param = params[0].getValue(engine);
				if (param.getType().equals(JamochaType.IDENTIFIER)) {
					String identifier = param.getIdentifierValue();
					if (identifier.equals("deffacts")
							|| identifier.equals("facts"))
						engine.clearFacts();
					else if (identifier.equals("defrules")
							|| identifier.equals("rules"))
						engine.clearRules();
					else
						throw new EvaluationException("Unknown argument "
								+ param.getIdentifierValue());
					return JamochaValue.TRUE;
				} else
					throw new IllegalTypeException(JamochaType.IDENTIFIERS,
							param.getType());
			} else if (params.length == 0) {
				engine.clearAll();
				return JamochaValue.TRUE;
			}
		throw new IllegalParameterException(0);
	}
}