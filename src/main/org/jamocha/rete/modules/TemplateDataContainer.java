/*
 * Copyright 2007 Josef Alexander Hahn, Sebastian Reinartz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://jamocha.org/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.jamocha.rete.modules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jamocha.rete.Template;

/**
 * @author Josef Alexander Hahn, Sebastian Reinartz
 * 
 */
public class TemplateDataContainer extends ModulesDataContainer {

	private Map<String, Set> moduleToTemplates;

	public TemplateDataContainer() {
		super();
		idToCLIPSElement = new HashMap<String, Template>();
		moduleToTemplates = new HashMap<String, Set>();
	}

	@Override
	protected void handleClear() {
		// TODO: we have to remove from all modules
		// TODO Auto-generated method stub
	}

	public Template get(String templateName, Module module) {
		return (Template) idToCLIPSElement.get(toKeyString(templateName, module.getModuleName()));
	}

	public boolean add(Template template, Module module) {
		String templateKey = toKeyString(template.getName(), module.getModuleName());
		if (this.idToCLIPSElement.containsKey(templateKey))
			return false;
		else {
			this.idToCLIPSElement.put(templateKey, template);
			// add to modules templateset
			Set moduleSet = (Set) this.moduleToTemplates.get(module.getModuleName());
			// Does this Set exists?
			if (moduleSet == null) {
				moduleSet = new HashSet<Template>();
				this.moduleToTemplates.put(module.getModuleName(), moduleSet);
			}
			moduleSet.add(template);
			return true;
		}
	}

	public Template remove(String templateName, Module module) {
		Template result = (Template) idToCLIPSElement.remove(toKeyString(templateName, module.getModuleName()));
		Set moduleSet = (Set) this.moduleToTemplates.get(module.getModuleName());
		moduleSet.remove(result);
		return result;
	}

	private String toKeyString(String templateName, String moduleName) {
		return moduleName + "::" + templateName;
	}

	public boolean containsTemplate(Module defmodule, Template template) {
		return idToCLIPSElement.containsKey(toKeyString(template.getName(), defmodule.getModuleName()));
	}

	public List<Template> getTemplates(Module defmodule) {
		List<Template> templates = new ArrayList<Template>();
		// get set of templates from hashmap
		Set templs = (Set) this.moduleToTemplates.get(defmodule.getModuleName());
		if (templs != null) {
			Iterator itr = templs.iterator();
			while (itr.hasNext()) {
				Template templ = (Template) itr.next();
				templates.add(templ);
			}
		}
		return templates;
	}
}
