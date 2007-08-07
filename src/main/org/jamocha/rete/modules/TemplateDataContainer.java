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
import java.util.Iterator;
import java.util.List;

import org.jamocha.rete.Template;

/**
 * @author Josef Alexander Hahn, Sebastian Reinartz
 * 
 */
public class TemplateDataContainer extends ModulesDataContainer {

	public TemplateDataContainer() {
		super();
		idToCLIPSElement = new HashMap<String, Template>();
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
			return true;
		}
	}

	public Template remove(String templateName, Module module) {
		return (Template) idToCLIPSElement.remove(toKeyString(templateName, module.getModuleName()));
	}

	private String toKeyString(String templateName, String moduleName) {
		return moduleName + "::" + templateName;
	}

	public boolean containsTemplate(Module defmodule, Template template) {
		return idToCLIPSElement.containsKey(toKeyString(template.getName(), defmodule.getModuleName()));
	}

	public List<Template> getTemplates(Module defmodule) {
		List<Template> templates = new ArrayList<Template>();
		// clearadd all templates from hashmap to resulting list:
		Iterator itr = this.idToCLIPSElement.keySet().iterator();
		while (itr.hasNext()) {
			Object key = itr.next();
			Template templ = (Template) this.idToCLIPSElement.get(key);
			templates.add(templ);
		}
		return templates;
	}
}
