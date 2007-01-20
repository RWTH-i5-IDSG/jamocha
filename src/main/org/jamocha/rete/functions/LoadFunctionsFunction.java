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
import org.jamocha.rete.ValueParam;


/**
 * @author Peter Lin
 *
 */
public class LoadFunctionsFunction implements Function, Serializable {

	public static final String LOAD_FUNCTION = "load-function";
	
	public LoadFunctionsFunction() {
		super();
	}

	public int getReturnType() {
		return Constants.BOOLEAN_OBJECT;
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		boolean load = false;
		if (params != null && params.length > 0) {
			for (int idx=0; idx < params.length; idx++) {
				String func = params[idx].getStringValue();
                try {
                    engine.declareFunction(func);
                    load = true;
                } catch (ClassNotFoundException e) {
                    load = false;
                }
			}
		}
		DefaultReturnVector ret = new DefaultReturnVector();
		DefaultReturnValue rv = new DefaultReturnValue(
				Constants.BOOLEAN_OBJECT, new Boolean(load));
		ret.addReturnValue(rv);
		return ret;
	}

	public String getName() {
		return LOAD_FUNCTION;
	}

	public Class[] getParameter() {
		return new Class[]{ValueParam.class};
	}

	/* (non-Javadoc)
	 * @see woolfel.engine.rete.Function#toPPString(woolfel.engine.rete.Parameter[], int)
	 */
	public String toPPString(Parameter[] params, int indents) {
		return "(load-function [classname])";
	}

}
