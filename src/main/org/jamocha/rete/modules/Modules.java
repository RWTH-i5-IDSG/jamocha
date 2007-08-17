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
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.jamocha.parser.EvaluationException;
import org.jamocha.rete.Constants;
import org.jamocha.rete.Deftemplate;
import org.jamocha.rete.Fact;
import org.jamocha.rete.Rete;
import org.jamocha.rete.Template;
import org.jamocha.rete.configurations.SlotConfiguration;
import org.jamocha.rete.eventhandling.ModulesChangeListener;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rule.Rule;

/**
 * @author Sebastian Reinartz
 * 
 */
public class Modules implements Serializable {

	private static final long serialVersionUID = 1L;

	private Module currentModule = null;

	private Vector<ModulesChangeListener> listeners;

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
		this.listeners = new Vector<ModulesChangeListener>();
	}

	public void addModulesChangeListener(ModulesChangeListener l) {
		listeners.add(l);
	}

	public void removeModulesChangeListener(ModulesChangeListener l) {
		listeners.remove(l);
	}

	protected void initMain() {
		this.main = new Defmodule(Constants.MAIN_MODULE, this);
		this.modules.put(this.main.getModuleName(), this.main);
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
	public Module addModule(String name, boolean autoFocus) {
		if (findModule(name) == null) {
			Defmodule mod = new Defmodule(name, this);
			if (autoFocus)
				this.currentModule = mod;
			this.modules.put(name, mod);
			for (ModulesChangeListener l : listeners)
				l.evModuleAdded(mod);
			return mod;
		} else
			return null;
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
		for (ModulesChangeListener l : listeners)
			l.evModuleRemoved(module);
		this.clearModule(module);
		return (Module) this.modules.remove(module.getModuleName());
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

	public Fact createFact(Object data, String template) throws AssertException {
		Template tmpl = this.getTemplate(currentModule, template);
		if (tmpl == null)
			throw new AssertException("Template " + template
					+ " could not be found");
		Fact ft = null;
		try {
			ft = ((Deftemplate) tmpl).createFact(data, engine);
			facts.add(ft);
		} catch (EvaluationException e) {
			throw new AssertException(e);
		}
		return ft;
	}

	public Fact createFact(SlotConfiguration[] scs, String template)
			throws EvaluationException {
		Template tmpl = this.getTemplate(currentModule, template);
		if (tmpl == null)
			throw new AssertException("Template " + template
					+ " could not be found");
		Fact ft = null;
		ft = ((Deftemplate) tmpl).createFact(scs, engine);
		facts.add(ft);
		return ft;
	}

	public Rule findRule(Module module, String ruleName) {
		return this.rules.get(ruleName, module);
	}

	public void removeTemplate(Module module, Template temp) {
		this.templates.remove(temp.getName(), module);

	}

	public boolean addTemplate(Module defmodule, Template temp) {
		return this.templates.add(temp, defmodule);
	}

	public Template getTemplate(Module defmodule, String template) {
		return templates.get(template, defmodule);
	}

	public boolean containsTemplate(Module defmodule, Template template) {
		return templates.containsTemplate(defmodule, template);
	}

	public List<Rule> getRules(Module module) {
		return this.rules.getRules(module);
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

	public List<Template> getTemplates(Module defmodule) {
		return this.templates.getTemplates(defmodule);
	}

	public List<Fact> getAllFacts() {
		return this.facts.getFacts();
	}

	public Fact getFactById(long id) {
		return this.facts.getFactById(id);
	}

	public Fact getFactByFact(Fact fact) {
		return this.facts.getFactByFact(fact);
	}

	public long addFact(Fact fact) {
		return facts.add(fact);
	}

	public void removeFact(Fact fact) {
		this.facts.remove(fact.getFactId());
	}

	public String toString() {
		return "Modules Current Module:"
				+ this.getCurrentModule().getModuleName();

	}

	/**
	 * Clear will clear all the modules and remove all activations
	 */
	public void clearAll() {
		this.clearAllRules();
		this.clearAllFacts();
		this.clearAllTemplates();
		this.modules.clear();

		// reinit main module:
		initMain();
	}

	public void clearModule(Module module) {
		this.clearFacts(module);
		this.clearRules(module);
		this.clearTemplates(module);
	}

	private void clearTemplates(Module module) {
		// TODO Auto-generated method stub
	}

	public void clearRules(Module module) {
		// TODO Auto-generated method stub
	}

	public void clearFacts(Module module) {
		// TODO Auto-generated method stub
	}

	public void clearAllRules() {
		rules.clear();
	}

	public void clearAllFacts() {
		facts.clear();
	}

	public void clearAllTemplates() {
		templates.clear();
	}
}
