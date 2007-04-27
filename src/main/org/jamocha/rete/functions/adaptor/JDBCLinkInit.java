/*
 * Copyright 2007 Alexander Wilden
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
package org.jamocha.rete.functions.adaptor;

import java.io.Serializable;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Deftemplate;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.Template;
import org.jamocha.rete.TemplateSlot;
import org.jamocha.rete.functions.FunctionDescription;

/**
 * @author Alexander Wilden
 * 
 * Initializes the JDBC adaptor by defining the jdbclink template. Returns true.
 */
public class JDBCLinkInit implements Function, Serializable {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Initializes the JDBC adaptor by defining the jdbclink and jdbccondition template. Returns true.";
		}

		public int getParameterCount() {
			return 0;
		}

		public String getParameterDescription(int parameter) {
			return "";
		}

		public String getParameterName(int parameter) {
			return "";
		}

		public JamochaType[] getParameterTypes(int parameter) {
			return JamochaType.NONE;
		}

		public JamochaType[] getReturnType() {
			return JamochaType.BOOLEANS;
		}

		public boolean isParameterCountFixed() {
			return true;
		}

		public boolean isParameterOptional(int parameter) {
			return false;
		}
	}

	private static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "jdbclink-init";

	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	public String getName() {
		return NAME;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		// define deftemplate jdbclink
		String templateName = "jdbclink";
		if (engine.findModule("MAIN").getTemplate(templateName) == null) {
			TemplateSlot[] slots = new TemplateSlot[7];
			slots[0] = new TemplateSlot("ConnectionName");
			slots[1] = new TemplateSlot("TableName");
			slots[2] = new TemplateSlot("TemplateName");
			slots[3] = new TemplateSlot("Username");
			slots[4] = new TemplateSlot("Password");
			slots[5] = new TemplateSlot("JDBCurl");
			slots[6] = new TemplateSlot("JDBCdriver");
			for (int i = 0; i < slots.length; i++)
				slots[i].setValueType(JamochaType.STRING);
			Template jdbcConfigTemplate = new Deftemplate(templateName, null,
					slots);
			engine.findModule("MAIN").addTemplate(jdbcConfigTemplate, engine,
					engine.getWorkingMemory());
		}
		// define deftemplate jdbccondition	
		templateName = "jdbccondition";
		if (engine.findModule("MAIN").getTemplate(templateName) == null) {
			TemplateSlot[] slots = new TemplateSlot[3];
			slots[0] = new TemplateSlot("SlotName");
			slots[1] = new TemplateSlot("BooleanOperator");
			slots[2] = new TemplateSlot("Value");
			slots[0].setValueType(JamochaType.STRING);
			slots[1].setValueType(JamochaType.STRING);
			Template jdbcConfigTemplate = new Deftemplate(templateName, null,
					slots);	
			engine.findModule("MAIN").addTemplate(jdbcConfigTemplate, engine,
					engine.getWorkingMemory());
		}
		return JamochaValue.TRUE;
	}
}
