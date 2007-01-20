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

import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnValue;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;
import org.jamocha.rete.exception.ExecuteException;


/**
 * @author Peter Lin
 * 
 */
public class FireFunction implements Function, Serializable {

	public static final String FIRE = "fire";

	/**
	 * 
	 */
	public FireFunction() {
		super();
	}

	public int getReturnType() {
		return Constants.INTEGER_OBJECT;
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		int count = 0;
		if (params != null && params.length == 1) {
			int fc = params[0].getIntValue();
			try {
				count = engine.fire(fc);
			} catch (ExecuteException e) {
				e.printStackTrace();
			}
		} else {
			count = engine.fire();
		}
		// engine.writeMessage(String.valueOf(count) + Constants.LINEBREAK,"t");
		DefaultReturnVector ret = new DefaultReturnVector();
		DefaultReturnValue rv = new DefaultReturnValue(
				Constants.INTEGER_OBJECT, new Integer(count));
		ret.addReturnValue(rv);
		return ret;
	}

	public String getName() {
		return FIRE;
	}

	public Class[] getParameter() {
		return new Class[0];
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(fire)";
	}
}
