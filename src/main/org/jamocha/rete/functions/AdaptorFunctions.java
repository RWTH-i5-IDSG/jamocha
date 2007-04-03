/* Copyright 2002-2006 Peter Lin
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
import org.jamocha.rete.Template;
import org.jamocha.rete.TemplateSlot;

public class AdaptorFunctions implements FunctionGroup {

	private static final long serialVersionUID = 1L;

	private ArrayList<Function> funcs = new ArrayList<Function>();

	public AdaptorFunctions() {
		super();
	}

	public String getName() {
		return (AdaptorFunctions.class.getSimpleName());
	}

	public void loadFunctions(Rete engine) {

		/* Defining configuration template */
		String templateName="jdbclink";
		String defclass=null;
		TemplateSlot[] slots = new TemplateSlot[6];
		slots[0] = new TemplateSlot("ConnectionName");
		slots[1] = new TemplateSlot("TableName");
		slots[2] = new TemplateSlot("TemplateName");
		slots[3] = new TemplateSlot("Username");
		slots[4] = new TemplateSlot("Password");
		slots[5] = new TemplateSlot("JDBCurl");
		slots[0] = new TemplateSlot("JDBCdriver");
		for( int i=0 ; i<6 ; i++ ) slots[i].setValueType( JamochaType.STRING );
		Template jdbcConfigTemplate = new Deftemplate(templateName,defclass,slots);
		
		engine.findModule("MAIN").addTemplate(jdbcConfigTemplate, engine, engine.getWorkingMemory());
		
		JDBClink jdbclink = new JDBClink();
		engine.declareFunction(jdbclink);
		funcs.add(jdbclink);
		
		IteratorImporter iteratorimporter = new IteratorImporter();
		engine.declareFunction(iteratorimporter);
		funcs.add(iteratorimporter);
		
		
	}

	public List listFunctions() {
		return funcs;
	}

}
