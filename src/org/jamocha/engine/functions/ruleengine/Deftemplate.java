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

import org.jamocha.engine.Engine;
import org.jamocha.engine.Parameter;
import org.jamocha.engine.functions.AbstractFunction;
import org.jamocha.engine.functions.FunctionDescription;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.IllegalParameterException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;

/**
 * @author Peter Lin
 * 
 * Defines a new template in the currently focused module of the engine.
 */
public class Deftemplate extends AbstractFunction {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Defines a new template in the currently focused module of the engine.\n"
					+ "A template has the following syntax:\n"
					+ "(deftemplate templ		;name of deftemplate relation\n"
					+ "	\"comment\"			;optional comment in quotes\n"
					+ "	(slot slot1name		;name of 1st field\n"
					+ "	(type STRING)		;type of field (optional)\n"
					+ "	(default ?someVar))	;default value of 1st field (optional)\n"
					+ "	(slot slot2name		;name of 2nd field\n"
					+ "	(type SYMBOL))		;type of field\n"
					+ "		...\n"
					+ ")						;close deftemplate\n"
					+ "\n"
					+ "You can use (silent slot SLOTNAME) instead of (slot SLOTNAME).\n"
					+ "A so-called silent slot is not observed by the rete engine, i.e."
					+ "a change in a silent slot will not lead to a new evaluation whether\n"
					+ "the fact matches to other rules now.";
		}

		public int getParameterCount() {
			return 1;
		}

		public String getParameterDescription(int parameter) {
			return "Template to be defined.";
		}

		public String getParameterName(int parameter) {
			return "template";
		}

		public JamochaType[] getParameterTypes(int parameter) {
			return JamochaType.OBJECTS;
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
			return "(deftemplate transact\n"
					+ "  (slot accountId (type STRING))\n"
					+ "  (slot countryCode (type STRING))\n"
					+ "  (slot currentPrice (type DOUBLE))\n"
					+ "  (slot issuer (type STRING))\n"
					+ "  (slot lastPrice (type DOUBLE))\n"
					+ "  (silent slot purchaseDate (type STRING))\n"
					+ "  (slot total (type DOUBLE))\n" + ")";
		}

		public boolean isResultAutoGeneratable() {
			return true;
		}

		public Object getExpectedResult() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "deftemplate";

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
		JamochaValue result = JamochaValue.FALSE;
		if (params != null && params.length == 1) {
			JamochaValue firstParam = params[0].getValue(engine);
			if (firstParam.getObjectValue() instanceof org.jamocha.engine.workingmemory.elements.Deftemplate) {
				org.jamocha.engine.workingmemory.elements.Deftemplate tpl = (org.jamocha.engine.workingmemory.elements.Deftemplate) firstParam
						.getObjectValue();
				// add template:
				result = engine.addTemplate(tpl) ? JamochaValue.TRUE
						: JamochaValue.FALSE;
			}
		} else
			throw new IllegalParameterException(1);
		return result;
	}
}