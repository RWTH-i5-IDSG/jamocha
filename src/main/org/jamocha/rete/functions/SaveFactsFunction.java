/*
 * Copyright 2002-2006 Peter Lin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://ruleml-dev.sourceforge.net/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.jamocha.rete.functions;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Constants;
import org.jamocha.rete.Deffact;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.util.FactUtils;

/**
 * @author Peter Lin
 * 
 * Facts function will printout all the facts, not including any initial facts
 * which are internal to the rule engine.
 */
public class SaveFactsFunction implements Function, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String SAVE_FACTS = "save-facts";

	/**
	 * 
	 */
	public SaveFactsFunction() {
		super();
	}

	public JamochaType getReturnType() {
		return JamochaType.BOOLEAN;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		JamochaValue result = JamochaValue.FALSE;
		boolean sortid = true;
		if (params != null && params.length >= 1) {
			JamochaValue firstParam = params[0].getValue(engine);
			if (params.length > 1
					&& params[1].getValue(engine).getIdentifierValue().equals(
							"template")) {
				sortid = false;
			}
			try {
				FileWriter writer = new FileWriter(firstParam.getStringValue());
				List facts = engine.getAllFacts();
				Object[] sorted = null;
				if (sortid) {
					sorted = FactUtils.sortFacts(facts);
				} else {
					sorted = FactUtils.sortFactsByTemplate(facts);
				}
				for (int idx = 0; idx < sorted.length; idx++) {
					Deffact ft = (Deffact) sorted[idx];
					writer.write(ft.toPPString() + Constants.LINEBREAK);
				}
				writer.close();
				result = JamochaValue.TRUE;
			} catch (IOException e) {
				throw new EvaluationException(e);
			}
		}
		return result;
	}

	public String getName() {
		return SAVE_FACTS;
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(save-facts [filename] [sort(id|template)])";
	}
}
