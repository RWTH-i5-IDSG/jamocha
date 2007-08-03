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
package org.jamocha.rete.Modules;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jamocha.rete.Constants;
import org.jamocha.rete.Defmodule;
import org.jamocha.rete.Module;
import org.jamocha.rete.Template;

/**
 * @author Sebastian Reinartz
 * 
 */
public class Modules implements Serializable {

	private static final long serialVersionUID = 1L;

	private Module currentModule = null;

	/**
	 * this is the main module
	 */
	private Module main = null;

	private FactDataContainer facts = new FactDataContainer();

	private RuleDataContainer rules = new RuleDataContainer();

	private TemplateDataContainer templates = new TemplateDataContainer();

	/**
	 * The HashMap for the modules.
	 */
	protected Map<String, Module> modules = new HashMap<String, Module>();

	public Modules() {
		super();
		initMain();
	}

	protected void initMain() {
		this.main = new Defmodule(Constants.MAIN_MODULE);
		// by default, we set the current module to main
		this.currentModule = this.main;
	}

	public Module getCurrentModule() {
		return this.currentModule;
	}

	public boolean setCurrentModule(String name) {
		Module mod = findModule(name);
		if (mod != null) {
			this.currentModule = mod;
			return true;
		} else
			return false;

	}

	public Module getMainModule() {
		return this.main;
	}

	/**
	 * method will create for given name, if it does not exist. The current
	 * module is changed to the new one
	 * 
	 * @param name,
	 *            autoFocus
	 */
	public boolean addModule(String name, boolean autoFocus) {
		if (findModule(name) == null) {
			Defmodule mod = new Defmodule(name);
			if (autoFocus)
				this.currentModule = mod;
			this.modules.put(name, mod);
			return true;
		} else
			return false;
	}

	public Module getModule(String name, boolean autoFocus) {
		Module result = findModule(name);
		if (result == null) {
			result = new Defmodule(name);
			if (autoFocus)
				this.currentModule = result;
			this.modules.put(name, result);
		}
		return result;
	}

	public Module removeModule(Module module) {
		return (Module) this.modules.remove(module.getModuleName());
	}

	public Module removeModule(String name) {
		return (Module) this.modules.remove(name);
	}

	/**
	 * Return the values from the HashMap
	 * 
	 * @return
	 */
	public Collection getModules() {
		return this.modules.values();
	}

	/**
	 * return the module. if it doesn't exist, method returns null.
	 * 
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
		// clear all modules:
		Iterator itr = this.modules.keySet().iterator();
		while (itr.hasNext()) {
			Object key = itr.next();
			Module mod = (Module) this.modules.get(key);
			mod.clear();
		}
		this.modules.clear();

		// reinit min module:
		initMain();
	}

	public void clearAllRules() {
		rules.clear();
		// Iterator itr = this.modules.keySet().iterator();
		// while (itr.hasNext()) {
		// Object key = itr.next();
		// Module mod = (Module) this.modules.get(key);
		// mod.clearRules();
		// }
	}

	public void clearAllFacts() {
		facts.clear();
	}

	public Template findTemplates(String templName) {
		return (Template) templates.find(templName);
		// Template tmpl = null;
		// Iterator itr = modules.values().iterator();
		// while (itr.hasNext()) {
		// Module mod = (Module) itr.next();
		// tmpl = mod.getTemplate(templName);
		// if (tmpl != null){
		// break;
		// }
		// }
		// return tmpl;
	}
}
