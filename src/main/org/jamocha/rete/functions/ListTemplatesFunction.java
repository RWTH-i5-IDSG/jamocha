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
import java.util.Collection;
import java.util.Iterator;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.Template;


/**
 * @author Peter Lin
 * 
 * ListTemplates will list all the templates and print them out.
 */
public class ListTemplatesFunction implements Function, Serializable {

	public static final String LISTTEMPLATES = "list-deftemplates";

	/**
	 * 
	 */
	public ListTemplatesFunction() {
		super();
	}

	public JamochaType getReturnType() {
		return Constants.RETURN_VOID_TYPE;
	}

	/**
	 * The current implementation will print out all the templates in
	 * no specific order. The function does basically the same thing
	 * as CLIPS (list-deftemplates)
	 */
	public JamochaValue executeFunction(Rete engine, Parameter[] params) throws EvaluationException {
		Collection templ = engine.getCurrentFocus().getTemplates();
		Iterator itr = templ.iterator();
		while (itr.hasNext()) {
			Template tp = (Template) itr.next();
			engine.writeMessage(tp.toPPString() + "\r\n", "t");
		}
		return new DefaultReturnVector();
	}

	public String getName() {
		return LISTTEMPLATES;
	}

	public Class[] getParameter() {
		return new Class[] { String.class };
	}

	public String toPPString(Parameter[] params, int indents) {
		if (params != null && params.length > 0) {
			StringBuffer buf = new StringBuffer();
			return buf.toString();
		} else {
			return "(list-deftemplates)";
		}
	}
}
