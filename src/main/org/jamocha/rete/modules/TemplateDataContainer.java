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

import java.util.HashMap;

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
		//TODO: we have to remove from all modules
		// TODO Auto-generated method stub
	}
	
	public Template get(String templateName, String moduleName){
		return (Template)idToCLIPSElement.get(toKeyString(templateName,moduleName));
	}
	
	public void add(Template template, String moduleName){
		this.idToCLIPSElement.put(toKeyString(template.getName(),moduleName), template);
	}
	
	public Template remove(String templateName, String moduleName){
		return (Template)idToCLIPSElement.remove(toKeyString(templateName,moduleName));
	}
	
	private String toKeyString(String templateName, String moduleName){
		return moduleName + "::" + templateName;
	}
}
