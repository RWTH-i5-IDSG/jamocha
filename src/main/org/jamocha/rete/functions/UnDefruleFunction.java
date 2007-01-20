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
import org.jamocha.rete.Deftemplate;
import org.jamocha.rete.Function;
import org.jamocha.rete.Module;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;
import org.jamocha.rete.Template;
import org.jamocha.rete.ValueParam;
import org.jamocha.rete.exception.CompileRuleException;
import org.jamocha.rule.Defrule;


/**
 * @author Peter Lin
 *
 */
public class UnDefruleFunction implements Function, Serializable {

	public static final String UNDEFRULE = "undefrule";

	public UnDefruleFunction() {
		super();
	}

	public int getReturnType() {
		return Constants.BOOLEAN_OBJECT;
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		boolean removed = true;
		if (params.length == 1) {
			String rl = params[0].getStringValue();
			Defrule defrl = (Defrule)engine.getCurrentFocus().findRule(rl);
			if (defrl != null) {
				engine.getCurrentFocus().removeRule(defrl,engine,engine.getWorkingMemory());
			} else {
				removed = false;
			}
		} else {
			removed = false;
		}
		DefaultReturnVector ret = new DefaultReturnVector();
		DefaultReturnValue rv = new DefaultReturnValue(
				Constants.BOOLEAN_OBJECT, new Boolean(removed));
		ret.addReturnValue(rv);
		return ret;
	}

	public String getName() {
		return UNDEFRULE;
	}

	/**
	 * The expected parameter is a single ValueParam containing a deftemplate
	 * instance. The function gets the deftemplate using Parameter.getValue().
	 */
	public Class[] getParameter() {
		return new Class[] { ValueParam.class };
	}

	public String toPPString(Parameter[] params, int indents) {
		if (params != null) {
			StringBuffer buf = new StringBuffer();
			return buf.toString();
		} else {
			return "(undefrule name)";
		}
	}
}
