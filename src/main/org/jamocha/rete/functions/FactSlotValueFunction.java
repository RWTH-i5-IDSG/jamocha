/*
 * Copyright 2007 Christoph Emonds
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
package org.jamocha.rete.functions;

import java.io.Serializable;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.IllegalParameterException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Fact;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;

/**
 * @author Christoph Emonds
 * 
 * Returns the value of a slot of a specific fact.
 */
public class FactSlotValueFunction implements Function, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String NAME = "fact-slot-value";

	/**
	 * 
	 */
	public FactSlotValueFunction() {
		super();
	}

	public JamochaType getReturnType() {
		return JamochaType.UNDEFINED;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		if(params != null && params.length == 2) {
			JamochaValue factId = params[0].getValue(engine);
			JamochaValue slotName = params[1].getValue(engine);
			Fact fact = engine.getFactById(factId.getFactIdValue());
			int slotId = fact.getSlotId(slotName.getIdentifierValue());
			if(slotId < 0) {
				throw new EvaluationException("Error no slot "+slotName);
			}
			return fact.getSlotValue(slotId);
		} else {
			throw new IllegalParameterException(2);
		}
	}

	public String getName() {
		return NAME;
	}

	public Class[] getParameter() {
		return new Class[0];
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(fact-slot-value <fact-id> <slot-name>)\n" + "Function description:\n"
				+ "\tReturns the value of the given slot.";
	}
}
