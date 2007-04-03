/*
 * Copyright 2002-2007 Peter Lin
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

import java.util.List;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.ExpressionSequence;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;

/**
 * Deffunction is used for functions that are declared in the shell. It is
 * different than a function written in java. Deffunction run interpreted and
 * are mapped to existing functions.
 * 
 * @author Peter Lin
 */
public class DeffunctionFunction implements Function {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String NAME = "deffunction";

	protected Class[] functionParams = null;

	protected JamochaType returnType;

	/**
	 * 
	 */
	public DeffunctionFunction() {
	}

	@SuppressWarnings("unchecked")
	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		JamochaValue result = JamochaValue.FALSE;
		JamochaValue firstParam = params[0].getValue(engine);
		String name = firstParam.getIdentifierValue();
		if (engine.findFunction(name) == null) {
			JamochaValue secondParam = params[1].getValue(engine);
			Parameter[] functionParameters = (Parameter[]) secondParam.getObjectValue();
			JamochaValue thirdParam = params[2].getValue(engine);
			ExpressionSequence functionList;
			String description = "";
			/* TODO description/comment for user defined functions */
//			if(thirdParam.getType().equals(JamochaType.STRING)) {
//				description = thirdParam.getStringValue();
//				JamochaValue fourthParam = params[3].getValue(engine);
//				functionList = (List) fourthParam.getObjectValue();
//			} else {
			if(thirdParam.getObjectValue() instanceof ExpressionSequence) {
				functionList = (ExpressionSequence) thirdParam.getObjectValue();
			} else {
			    List<ShellFunction> actions = (List) thirdParam.getObjectValue();
			    functionList = new ExpressionSequence();
			    for(int i=0; i<actions.size(); ++i) {
				functionList.add(actions.get(i));
			    }
			}
			InterpretedFunction intrfunc = new InterpretedFunction(name, description,
					functionParameters, functionList);
			engine.declareFunction(intrfunc);
			result = JamochaValue.TRUE;
		}
		return result;
	}

	public Class[] getParameter() {
		return this.functionParams;
	}

	public JamochaType getReturnType() {
		return this.returnType;
	}

	public String toPPString(Parameter[] params, int indents) {
		return "";
	}

	public String getName() {
		return NAME;
	}
}
