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
package org.jamocha.rete.modules;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jamocha.parser.EvaluationException;
import org.jamocha.rete.Constants;
import org.jamocha.rete.Deftemplate;
import org.jamocha.rete.Fact;
import org.jamocha.rete.Rete;
import org.jamocha.rete.Template;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rule.Rule;

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

	protected Rete engine;
	/**
	 * The HashMap for the modules.
	 */
	protected Map<String, Module> modules = new HashMap<String, Module>();

	public Collection<Module> getModuleList() {
		return modules.values();
	}
	
	public Modules(Rete engine) {
		super();
		initMain();
		this.engine = engine;
	}

	protected void initMain() {
		this.main = new Defmodule(Constants.MAIN_MODULE,this);
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
			Defmodule mod = new Defmodule(name, this);
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
			result = new Defmodule(name, this);
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
	}

	public void clearAllFacts() {
		facts.clear();
	}

	
	protected Fact createFact(Object data, String template) throws AssertException {
		Template tmpl = this.getTemplate(currentModule, template);
		Fact ft = null;
		try {
			ft = ((Deftemplate) tmpl).createFact(data, engine);
			facts.add(ft);
		} catch (EvaluationException e) {
			throw new AssertException(e);
		}
		return ft;
	}

	public Rule findRule(Module defmodule, String name) {
		// TODO Auto-generated method stub
		return null;
	}

	public void removeTemplate(Module defmodule, Template temp) {
		// TODO Auto-generated method stub
		
	}

	public boolean addTemplate(Module defmodule, Template temp) {
		// TODO Auto-generated method stub
		return false;
	}

	public Template getTemplate(Module defmodule, String template) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean containsTemplate(Module defmodule, Template template) {
		// TODO Auto-generated method stub
		return false;
	}

	public List<Rule> getAllRules(Module defmodule) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean containsRule(Module defmodule, Rule rl) {
		return rules.containsRule(rl, defmodule);
	}

	public void removeRule(Module defmodule, Rule rl) {
		rules.remove(rl.getName(), defmodule);
		
	}

	public void addRule(Module defmodule, Rule rl) {
		rules.add(rl, defmodule);
		
	}

	public void flushRules(Module defmodule) {
		// TODO Auto-generated method stub
		
	}

	public void flush(Module defmodule) {
		// TODO Auto-generated method stub
		
	}

	public List<Template> getTemplates(Module defmodule) {
		// TODO Auto-generated method stub
		return null;
	}
}
