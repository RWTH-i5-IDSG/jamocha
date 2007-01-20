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

import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;
import org.jamocha.rete.Template;
import org.jamocha.rule.Rule;


/**
 * @author Peter Lin
 * 
 * The purpose of the function is to print out the names of the rules
 * and the comment.
 */
public class TemplatesFunction implements Function, Serializable {

	public static final String TEMPLATES = "templates";
	public static final String LISTTEMPLATES = "list-deftemplates";
	
	public TemplatesFunction() {
		super();
	}

	public int getReturnType() {
		return Constants.RETURN_VOID_TYPE;
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		Collection templates = engine.getCurrentFocus().getTemplates();
		int count = templates.size();
		Iterator itr = templates.iterator();
		while (itr.hasNext()) {
			Template r = (Template)itr.next();
			engine.writeMessage(r.getName() + Constants.LINEBREAK, "t");
		}
		engine.writeMessage("for a total of " + count + Constants.LINEBREAK,"t");
		DefaultReturnVector rv = new DefaultReturnVector();
		return rv;
	}

	public String getName() {
		return TEMPLATES;
	}

	public Class[] getParameter() {
		return new Class[0];
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(templates)";
	}
}
