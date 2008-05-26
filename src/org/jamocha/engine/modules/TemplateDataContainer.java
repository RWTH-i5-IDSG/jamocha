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

package org.jamocha.engine.modules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jamocha.engine.workingmemory.elements.Template;

/**
 * @author Josef Alexander Hahn, Sebastian Reinartz
 * 
 */
public class TemplateDataContainer extends ModulesDataContainer {

	private final Map<String, Set<Template>> moduleToTemplates;

	public TemplateDataContainer() {
		super();
		idToCLIPSElement = new HashMap<String, Template>();
		moduleToTemplates = new HashMap<String, Set<Template>>();
	}

	@Override
	protected void handleClear() {
		final Set<String> keys = moduleToTemplates.keySet();
		for (final String key : keys)
			moduleToTemplates.remove(key);
	}

	public Template get(final String templateName, final Module module) {
		return (Template) idToCLIPSElement.get(toKeyString(templateName, module
				.getName()));
	}

	public boolean add(final Template template, final Module module) {
		final String templateKey = toKeyString(template.getName(), module
				.getName());
		if (idToCLIPSElement.containsKey(templateKey))
			return false;
		else {
			idToCLIPSElement.put(templateKey, template);
			// add to modules templateset
			Set moduleSet = moduleToTemplates.get(module.getName());
			// Does this Set exists?
			if (moduleSet == null) {
				moduleSet = new HashSet<Template>();
				moduleToTemplates.put(module.getName(), moduleSet);
			}
			moduleSet.add(template);
			return true;
		}
	}

	public Template remove(final String templateName, final Module module) {
		final Template result = (Template) idToCLIPSElement.remove(toKeyString(
				templateName, module.getName()));
		final Set moduleSet = moduleToTemplates.get(module.getName());
		moduleSet.remove(result);
		return result;
	}

	private String toKeyString(final String templateName,
			final String moduleName) {
		return moduleName + "::" + templateName;
	}

	public boolean containsTemplate(final Module defmodule,
			final Template template) {
		return idToCLIPSElement.containsKey(toKeyString(template.getName(),
				defmodule.getName()));
	}

	public List<Template> getTemplates(final Module defmodule) {
		final List<Template> templates = new ArrayList<Template>();
		// get set of templates from hashmap
		final Set<Template> templs = moduleToTemplates.get(defmodule.getName());
		if (templs != null) {
			final Iterator<Template> itr = templs.iterator();
			while (itr.hasNext()) {
				final Template templ = itr.next();
				templates.add(templ);
			}
		}
		return templates;
	}
}
