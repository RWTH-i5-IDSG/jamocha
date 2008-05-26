/*
 * Copyright 2002-2008 Peter Lin & The Jamocha Team
 * 
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

package org.jamocha.engine.functions.adaptor;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.engine.Parameter;
import org.jamocha.engine.Engine;
import org.jamocha.engine.functions.AbstractFunction;
import org.jamocha.engine.functions.FunctionDescription;
import org.jamocha.engine.workingmemory.elements.Deftemplate;
import org.jamocha.engine.workingmemory.elements.Template;
import org.jamocha.engine.workingmemory.elements.TemplateSlot;

/**
 * @author Alexander Wilden
 * 
 * Initializes the JDBC adaptor by defining the jdbclink template. Returns true
 * on success.
 */
public class JDBCLinkInit extends AbstractFunction {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Initializes the JDBC adaptor by defining the jdbclink and jdbccondition template. Returns true on success.";
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

		public String getExample() {
			return "(deftemplate templ (slot a) (slot b) (slot c) (slot foo) )\n"
					+ "(jdbclink-init)\n"
					+ "(assert\n"
					+ "	(jdbclink\n"
					+ "		(JDBCdriver \"com.mysql.jdbc.Driver\")\n"
					+ "		(ConnectionName \"db\")\n"
					+ "		(TableName \"test\")\n"
					+ "		(TemplateName \"templ\")\n"
					+ "		(Username \"jamocha\")\n"
					+ "		(Password \"secret\")\n"
					+ "		(JDBCurl \"jdbc:mysql://134.130.113.67:3306/jamocha\")\n"
					+ "	)\n"
					+ ")\n"
					+ "(assert\n"
					+ "	(jdbccondition\n"
					+ "		(SlotName \"foo\")\n"
					+ "		(BooleanOperator \">\")\n"
					+ "		(Value 2007-04-27 19:00+1)\n" + "	)\n" + ")";
		}

		public boolean isResultAutoGeneratable() {
			// TODO Auto-generated method stub
			return false;
		}

		public Object getExpectedResult() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	private static final long serialVersionUID = 1L;

	public static final FunctionDescription DESCRIPTION = new Description();

	public static final String NAME = "jdbclink-init";

	@Override
	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public JamochaValue executeFunction(Engine engine, Parameter[] params)
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
			engine.findModule("MAIN").addTemplate(jdbcConfigTemplate);
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
			engine.findModule("MAIN").addTemplate(jdbcConfigTemplate);
		}
		return JamochaValue.TRUE;
	}
}
