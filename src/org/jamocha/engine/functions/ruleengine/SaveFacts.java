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

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.jamocha.Constants;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.engine.Parameter;
import org.jamocha.engine.Engine;
import org.jamocha.engine.functions.AbstractFunction;
import org.jamocha.engine.functions.FunctionDescription;
import org.jamocha.engine.util.FactUtils;
import org.jamocha.engine.workingmemory.elements.Deffact;
import org.jamocha.engine.workingmemory.elements.Fact;

/**
 * @author Peter Lin
 * 
 * Saves all facts in the engine to a file, specified in the first argument.
 * Facts can be sorted according to their deftemplates or fact-ids.
 */
public class SaveFacts extends AbstractFunction {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Saves all facts in the engine to a file, specified in the first argument."
					+ "Facts can be sorted according to their deftemplates or fact-ids.";
		}

		public int getParameterCount() {
			return 2;
		}

		public String getParameterDescription(int parameter) {
			switch (parameter) {
			case 0:
				return "Name of the file to store the facts in.";
			case 1:
				return "If equal to \"template\" the facts are sorted by their deftemplate otherwise by their fact-ids.";
			}
			return "";
		}

		public String getParameterName(int parameter) {
			switch (parameter) {
			case 0:
				return "fileName";
			case 1:
				return "sorting";
			}
			return "";
		}

		public JamochaType[] getParameterTypes(int parameter) {
			switch (parameter) {
			case 0:
				return JamochaType.STRINGS;
			case 1:
				return JamochaType.IDENTIFIERS;
			}
			return JamochaType.NONE;
		}

		public JamochaType[] getReturnType() {
			return JamochaType.BOOLEANS;
		}

		public boolean isParameterCountFixed() {
			return false;
		}

		public boolean isParameterOptional(int parameter) {
			return parameter > 0;
		}

		public String getExample() {
			return "(deftemplate car (slot color)(slot speed))\n"
					+ "(assert (car (color \"red\")(speed 200)))\n"
					+ "(assert (car (color \"blue\")(speed 150)))\n"
					+ "(assert (car (color \"green\")(speed 100)))\n"
					+ "(save-facts /var/tmp/savetest.clp)\n"
					+ "(save-facts /var/tmp/savetest2.clp template)";
		}

		public boolean isResultAutoGeneratable() {
			return false;
		}

		public Object getExpectedResult() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "save-facts";

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
		boolean sortid = true;
		if (params != null && params.length >= 1) {
			JamochaValue firstParam = params[0].getValue(engine);
			if (params.length > 1
					&& params[1].getValue(engine).getIdentifierValue().equals(
							"template"))
				sortid = false;
			try {
				FileWriter writer = new FileWriter(firstParam.getStringValue());
				List<Fact> facts = engine.getModules().getAllFacts();
				Object[] sorted = null;
				if (sortid)
					sorted = FactUtils.sortFacts(facts);
				else
					sorted = FactUtils.sortFactsByTemplate(facts);
				for (int idx = 0; idx < sorted.length; idx++) {
					Deffact ft = (Deffact) sorted[idx];
					writer.write(ft.toString() + Constants.LINEBREAK);
				}
				writer.close();
				result = JamochaValue.TRUE;
			} catch (IOException e) {
				throw new EvaluationException(e);
			}
		}
		return result;
	}
}