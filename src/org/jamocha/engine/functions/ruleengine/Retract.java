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
import org.jamocha.engine.RetractException;
import org.jamocha.engine.functions.AbstractFunction;
import org.jamocha.engine.functions.FunctionDescription;
import org.jamocha.engine.workingmemory.elements.Deffact;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.IllegalParameterException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;

/**
 * 
 * @author Peter Lin
 * 
 * The retract action allows the user to remove facts from the fact-list.
 * Multiple facts may be retracted with a single retract statement. The
 * retraction of a fact also removes all rules that depended upon that fact for
 * activation from the agenda. Retraction of a fact may also cause the
 * retraction of other facts which receive logical support from the retracted
 * fact. If the facts item is being watched, then an informational message will
 * be printed each time a fact is retracted.
 * 
 */
public class Retract extends AbstractFunction {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Allows the user to remove facts from the fact-list. Multiple facts may be retracted "
					+ "with a single retract statement. The retraction of a fact also removes all rules that "
					+ "depend upon that fact for activation from the agenda. Retraction of a fact may also "
					+ "cause the retraction of other facts which receive logical support from the retracted "
					+ "fact. If the facts item is being watched, then an informational message will be printed "
					+ "each time a fact is retracted.";
		}

		public int getParameterCount() {
			return 1;
		}

		public String getParameterDescription(int parameter) {
			return "Fact-ID or fact to be retracted.";
		}

		public String getParameterName(int parameter) {
			return "fact";
		}

		public JamochaType[] getParameterTypes(int parameter) {
			JamochaType[] res = new JamochaType[JamochaType.FACT_IDS.length
					+ JamochaType.FACTS.length + JamochaType.LONGS.length];
			int count = 0;
			for (int i = 0; i < JamochaType.FACT_IDS.length; ++i)
				res[count++] = JamochaType.FACT_IDS[i];
			for (int i = 0; i < JamochaType.FACTS.length; ++i)
				res[count++] = JamochaType.FACTS[i];
			for (int i = 0; i < JamochaType.LONGS.length; ++i)
				res[count++] = JamochaType.LONGS[i];
			return res;
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
			return "(clear)" + "(deftemplate car (slot color)(slot speed))\n"
					+ "(assert (car (color \"red\")(speed 200)))\n"
					+ "(assert (car (color \"blue\")(speed 150)))\n"
					+ "(assert (car (color \"green\")(speed 100)))\n"
					+ "(facts)\n" + "(retract 2)\n" + "(facts)";
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

	public static final String NAME = "retract";

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
		if (params != null && params.length >= 1)
			for (int idx = 0; idx < params.length; idx++) {
				JamochaValue param = params[idx].getValue(engine);
				if (param.is(JamochaType.FACT_ID) || param.is(JamochaType.LONG)) {
					long factId = param.getFactIdValue();
					try {
						engine.retractById(factId);
						result = JamochaValue.TRUE;
					} catch (RetractException e) {
					}
				} else if (param.getType().equals(JamochaType.FACT)) {
					Deffact fact = (Deffact) param.getFactValue();
					try {
						engine.retractFact(fact);
						result = JamochaValue.TRUE;
					} catch (RetractException e) {
					}
				} else if (param.getType().equals(JamochaType.OBJECT)) {
					Deffact fact = (Deffact) param.getObjectValue();
					try {
						engine.retractFact(fact);
						result = JamochaValue.TRUE;
					} catch (RetractException e) {
					}
				}
				
			}
		else
			throw new IllegalParameterException(1, true);
		return result;
	}
}