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

import java.io.Serializable;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.IllegalParameterException;
import org.jamocha.parser.IllegalTypeException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;


/**
 * @author Peter Lin
 *
 * ClearFunction will call Rete.clear()
 */
public class ClearFunction implements Function, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String CLEAR = "clear";

	/**
	 * 
	 */
	public ClearFunction() {
		super();
	}

	public JamochaType getReturnType() {
		return JamochaType.BOOLEAN;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params) throws EvaluationException {
		
		if (params != null) {
			if(params.length == 1) {
				JamochaValue param = params[0].getValue(engine);
				if(param.getType().equals(JamochaType.IDENTIFIER)) {
					if (param.getIdentifierValue().equals("objects")) {
						engine.clearObjects();
					} else if (param.getIdentifierValue().equals("deffacts")) {
						engine.clearFacts();
					} else {
						throw new EvaluationException("Unknown argument "+param.getIdentifierValue());
					}
	 				return JamochaValue.TRUE;
				} else {
					throw new IllegalTypeException(JamochaType.IDENTIFIERS, param.getType());
				}
 			} else if(params.length == 0) {
 				engine.clearAll();
 				return JamochaValue.TRUE;
 			}
		}
		throw new IllegalParameterException(0);
	}

	public String getName() {
		return CLEAR;
	}

	/**
	 * The function does not take any parameters
	 */
	public Class[] getParameter() {
		return new Class[0];
	}

	public String toPPString(Parameter[] params, int indents) {
		if (indents > 0) {
			StringBuffer buf = new StringBuffer();
			for (int idx = 0; idx < indents; idx++) {
				buf.append(" ");
			}
			buf.append("(clear)");
			return buf.toString();
		} else {
			return "(clear [objects | deffacts])\n" +
			"Function description:\n" +
			"\tRemoves all the facts from memory and resets the fact index\n" +
			"\tif no argument is provided.\n" + 
			"\tThe argument \"objects\" removes all the facts and\n" + 
			"\tthe argument \"deffacts\" clears all the defined facts.\n"; 
		}
	}
}
