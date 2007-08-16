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

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.IllegalParameterException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.functions.Function;
import org.jamocha.rete.functions.FunctionDescription;

/**
 * @author Peter Lin
 * 
 * Defines a new template in the currently focused module of the engine.
 */
public class Deftemplate extends Function {

	private static final class Description implements
			FunctionDescription {

		public String getDescription() {
			return "Defines a new template in the currently focused module of the engine.\n" +
					"A template has the following syntax:\n" +
					"(deftemplate templ		;name of deftemplate relation\n" +
					"	\"comment\"			;optional comment in quotes\n" +
					"	(slot slot1name		;name of 1st field\n" +
					"	(type STRING)		;type of field (optional)\n" +
					"	(default ?someVar))	;default value of 1st field (optional)\n" +
					"	(slot slot2name		;name of 2nd field\n" +
					"	(type SYMBOL))		;type of field\n" +
					"		...\n" +
					")						;close deftemplate";
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
			return "(deftemplate transact\n" +
					"  (slot accountId (type STRING))\n" +
					"  (slot countryCode (type STRING))\n" +
					"  (slot currentPrice (type DOUBLE))\n" +
					"  (slot issuer (type STRING))\n" +
					"  (slot lastPrice (type DOUBLE))\n" +
					"  (slot purchaseDate (type STRING))\n" +
					"  (slot total (type DOUBLE))\n" +
					")";
		}

		public boolean isResultAutoGeneratable() {
			return true;
		}
	}

	public static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "deftemplate";

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
			JamochaValue firstParam = params[0].getValue(engine);
			if (firstParam.getObjectValue() instanceof org.jamocha.rete.Deftemplate) {
				org.jamocha.rete.Deftemplate tpl = (org.jamocha.rete.Deftemplate) firstParam
						.getObjectValue();
					//add template:
					result = engine.addTemplate(tpl) ? JamochaValue.TRUE
							: JamochaValue.FALSE;
			}
		} else {
			throw new IllegalParameterException(1);
		}
		return result;
	}
}