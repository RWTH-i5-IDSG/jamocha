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
import org.jamocha.rete.Deffact;
import org.jamocha.rete.ExpressionList;
import org.jamocha.rete.Fact;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.Slot;
import org.jamocha.rete.Template;
import org.jamocha.rete.functions.FunctionDescription;

/**
 * @author Peter Lin
 * 
 * The assert action allows the user to add a fact to the fact-list. Multiple
 * facts may be asserted with each call. If the facts item is being watched,
 * then an informational message will be printed each time a fact is asserted.
 */
public class Assert implements Function, Serializable {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "The assert action allows the user to add a fact to the fact-list. Multiple facts may be asserted with each call. If the facts item is being watched, then an informational message will be printed each time a fact is asserted.";
		}

		public int getParameterCount() {
			return 1;
		}

		public String getParameterDescription(int parameter) {
			return "RHS of an assert.";
		}

		public String getParameterName(int parameter) {
			return "rhs";
		}

		public JamochaType[] getParameterTypes(int parameter) {
			return JamochaType.ANY;
		}

		public JamochaType[] getReturnType() {
			return JamochaType.FACT_IDS;
		}

		public boolean isParameterCountFixed() {
			return false;
		}

		public boolean isParameterOptional(int parameter) {
			return false;
		}
	}

	private static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "assert";

	protected Fact[] triggerFacts = null;

	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	public String getName() {
		return NAME;
	}

	public void setTriggerFacts(Fact[] facts) {
		this.triggerFacts = facts;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		JamochaValue result = JamochaValue.FALSE;
		if (params.length > 0) {
			Deffact fact = null;
			JamochaValue firstParam = params[0].getValue(engine);
			if (firstParam.getType().equals(JamochaType.IDENTIFIER)) {
				Template tmpl = (Template) engine.getCurrentFocus()
						.getTemplate(firstParam.getIdentifierValue());

				Object[] data = null;
				// jamocha.list?
				if (params.length > 1 && params[1] instanceof JamochaValue) {
					data = (Object[]) params[1].getValue(engine)
							.getObjectValue();
				}
				// all other parameters are slot values: (slots
				// have to be created)
				else {
					ExpressionList list = null;
					String slotName = null;
					data = new Object[params.length - 1];
					for (int i = 1; i < params.length; i++) {
						list = (ExpressionList) params[i];
						slotName = list.get(0).getValue(engine)
								.getStringValue();
						if (list.size() == 2) {
							// single slot:
							data[i - 1] = new Slot(slotName, list.get(1)
									.getValue(engine));
						} else {
							// mutli Slot:
							// data[i-1] = new MultiSlot(slotName, )
						}
					}
				}

				fact = (Deffact) tmpl.createFact(data, -1, engine);
			} else if (firstParam.getType().equals(JamochaType.FACT)) {
				fact = (Deffact) firstParam.getFactValue();
			} else {
				throw new IllegalTypeException(new JamochaType[] {
						JamochaType.FACT, JamochaType.IDENTIFIER }, firstParam
						.getType());
			}
			if (fact.hasBinding()) {
				fact.resolveValues(engine, this.triggerFacts);
				fact = fact.cloneFact(engine);
			}
			Fact assertedFact = engine.assertFact(fact);
			// if the fact id is still -1, it means it wasn't
			// asserted
			// if it was asserted, we return the fact id, otherwise
			// we return "false".
			if (assertedFact == fact) {
				result = JamochaValue.newFactId(fact.getFactId());
			}
		} else {
			throw new IllegalParameterException(1);
		}
		return result;
	}
}