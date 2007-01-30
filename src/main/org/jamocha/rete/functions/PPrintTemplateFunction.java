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
import java.util.HashMap;
import java.util.Iterator;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.Template;
import org.jamocha.rete.ValueParam;


/**
 * @author Peter Lin
 * 
 * PPrintTemplate stands for Pretty Print deftemplate. It does the same
 * thing as (ppdeftemplate <deftemplate-name>) in CLIPS.
 */
public class PPrintTemplateFunction implements Function, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String PPTEMPLATES = "ppdeftemplate";
	
	/**
	 * 
	 */
	public PPrintTemplateFunction() {
		super();
	}

	public JamochaType getReturnType() {
        return JamochaType.NIL;
	}

	/**
	 * the function will printout one or more templates. This implementation
	 * is slightly different than CLIPS in that it can take one or more
	 * template names. The definition in CLIPS beginners guide states the 
	 * function does the following: (ppdeftemplate &lt;deftemplate-name>)
	 */
	public JamochaValue executeFunction(Rete engine, Parameter[] params) throws EvaluationException {
		HashMap filter = new HashMap();
		if (params != null && params.length > 0) {
			for (int idx=0; idx < params.length; idx++) {
					Object df = ((ValueParam)params[idx]).getValue(engine).getIdentifierValue();
					filter.put(df,df);
			}
		}
		Collection templ = engine.getCurrentFocus().getTemplates();
		Iterator itr = templ.iterator();
		while (itr.hasNext()) {
			Template tp = (Template)itr.next();
			if (filter.get(tp.getName()) != null) {
				engine.writeMessage(tp.toPPString() + "\r\n","t");
			}
		}
		return JamochaValue.NIL;
	}

	public String getName() {
		return PPTEMPLATES;
	}

	public Class[] getParameter() {
		return new Class[]{String.class};
	}

	public String toPPString(Parameter[] params, int indents) {
		StringBuffer buf = new StringBuffer();
		return buf.toString();
	}
}
