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
import org.jamocha.parser.IllegalParameterException;
import org.jamocha.parser.IllegalTypeException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.functions.FunctionDescription;

/**
 * @author Peter Lin
 * 
 * Clears the Rete engine. Removes all constructs and all associated data
 * structures (such as facts and objects) from the Rete environment.
 * <p>
 * If a parameter is given it is parsed as identifier and only the constructs
 * that are identified by it are removed.
 * <p>
 * This function returns TRUE.
 */
public class Clear implements Function, Serializable {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Clears the Rete engine. Removes all constructs and all associated data structures (such as facts and objects) from the Rete environment. If a parameter is given it is parsed as identifier and only the constructs that are identified by it are removed. This function returns TRUE.";
		}

		public int getParameterCount() {
			return 1;
		}

		public String getParameterDescription(int parameter) {
			if (parameter > 0)
				return "";
			else
				return "Identifier saying what to clear (deffacts or objects).";
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
	}

	private static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "clear";

	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	public String getName() {
		return NAME;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {

		if (params != null) {
			if (params.length == 1) {
				JamochaValue param = params[0].getValue(engine);
				if (param.getType().equals(JamochaType.IDENTIFIER)) {
					if (param.getIdentifierValue().equals("objects")) {
						engine.clearObjects();
					} else if (param.getIdentifierValue().equals("deffacts")) {
						engine.clearFacts();
					} else {
						throw new EvaluationException("Unknown argument "
								+ param.getIdentifierValue());
					}
					return JamochaValue.TRUE;
				} else {
					throw new IllegalTypeException(JamochaType.IDENTIFIERS,
							param.getType());
				}
			} else if (params.length == 0) {
				engine.clearAll();
				return JamochaValue.TRUE;
			}
		}
		throw new IllegalParameterException(0);
	}
}