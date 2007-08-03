/*
 * Copyright 2007 Sebastian Reinartz
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
package org.jamocha.rete;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Sebastian Reinartz
 *
 */
public class Modules implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/**
	 * The HashMap for the modules.
	 */
	protected Map<String,Module> modules = new HashMap<String,Module>();
	
	public void addModule(Module module) {
		this.modules.put(module.getModuleName(), module);
	}

	public Module removeModule(Module module) {
		return (Module)this.modules.remove(module.getModuleName());
	}

	public Module removeModule(String name) {
		return (Module)this.modules.remove(name);
	}
	
	/**
	 * Return the values from the HashMap
	 * @return
	 */
	public Collection getModules() {
		return this.modules.values();
	}
	
	/**
	 * return the module. if it doesn't exist, method returns null.
	 * @param name
	 * @return
	 */
	public Module findModule(String name) {
		return (Module) this.modules.get(name);
	}

	/**
	 * Clear will clear all the modules and remove all activations
	 */
	public void clearAll() {
		Iterator itr = this.modules.keySet().iterator();
		while (itr.hasNext()) {
			Object key = itr.next();
			Module mod = (Module) this.modules.get(key);
			mod.clear();
		}
		this.modules.clear();
	}

	public void clearAllRules(){
		Iterator itr = this.modules.keySet().iterator();
		while (itr.hasNext()) {
			Object key = itr.next();
			Module mod = (Module) this.modules.get(key);
			mod.clearRules();
		}
	}

	public Template findTemplates(String templName) {
		Template tmpl = null;
		Iterator itr = modules.values().iterator();
		while (itr.hasNext()) {
			Module mod = (Module) itr.next();
			tmpl = mod.getTemplate(templName);
			if (tmpl != null){
				break;
			}
		}
		return tmpl;
	}
}
