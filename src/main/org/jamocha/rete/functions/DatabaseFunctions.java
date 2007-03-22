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

import java.util.ArrayList;
import java.util.List;

import org.jamocha.parser.JamochaType;
import org.jamocha.rete.Deftemplate;
import org.jamocha.rete.Function;
import org.jamocha.rete.FunctionGroup;
import org.jamocha.rete.Rete;
import org.jamocha.rete.Slot;
import org.jamocha.rete.Template;

public class DatabaseFunctions implements FunctionGroup {

	private static final long serialVersionUID = 1L;

	private ArrayList<Function> funcs = new ArrayList<Function>();

	public DatabaseFunctions() {
		super();
	}

	public String getName() {
		return (DatabaseFunctions.class.getSimpleName());
	}

	public void loadFunctions(Rete engine) {

		/* Defining configuration template */
		String templateName="jdbclink";
		String defclass=null;
		Slot[] slots = new Slot[6];
		slots[0] = new Slot("ConnectionName");
		slots[1] = new Slot("TableName");
		slots[2] = new Slot("TemplateName");
		slots[3] = new Slot("Username");
		slots[4] = new Slot("Password");
		slots[5] = new Slot("JDBCurl");
		for( int i=0 ; i<6 ; i++ ) slots[i].setValueType( JamochaType.STRING );
		Template jdbcConfigTemplate = new Deftemplate(templateName,defclass,slots);
		
		engine.findModule("MAIN").addTemplate(jdbcConfigTemplate, engine, engine.getWorkingMemory());
		
		JDBClink jdbclink = new JDBClink();
		engine.declareFunction(jdbclink);
		funcs.add(jdbclink);
		
		
	}

	public List listFunctions() {
		return funcs;
	}

}
