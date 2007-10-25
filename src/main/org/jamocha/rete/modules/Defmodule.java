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
package org.jamocha.rete.modules;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Templates;

import org.jamocha.rete.Deftemplate;
import org.jamocha.rete.Template;
import org.jamocha.rete.TemplateSlot;
import org.jamocha.rete.eventhandling.ModuleChangedEvent;
import org.jamocha.rete.eventhandling.ModuleChangedListener;
import org.jamocha.rule.Rule;

/**
 * @author Peter Lin
 * 
 * A module represents a set of rulesets. The concept is from CLIPS and provides
 * a way to isolate the rule activation and pattern matching.
 */
public class Defmodule implements Module, Serializable {

	Modules modules;
	
	List<ModuleChangedListener> listeners;
	
	private static final long serialVersionUID = 0xDEADBEAFL;

	protected int id;


	protected void callListenersAddRule(Rule rule) {
		ModuleChangedEvent ev = new ModuleChangedEvent();
		ev.rule = rule;
		for (ModuleChangedListener listener : listeners) listener.ruleAdded(ev);
	}
	
	protected void callListenersRemoveRule(Rule rule) {
		ModuleChangedEvent ev = new ModuleChangedEvent();
		ev.rule = rule;
		for (ModuleChangedListener listener : listeners) listener.ruleRemoved(ev);
	}
	
	protected void callListenersAddTemplate(Template tmpl) {
		ModuleChangedEvent ev = new ModuleChangedEvent();
		ev.template = tmpl;
		for (ModuleChangedListener listener : listeners) listener.templateAdded(ev);
	}
	
	protected void callListenersRemoveTemplate(Template tmpl) {
		ModuleChangedEvent ev = new ModuleChangedEvent();
		ev.template = tmpl;
		for (ModuleChangedListener listener : listeners) listener.templateRemoved(ev);
	}
	
	/**
	 * The name of the module. A rule engine may have one or more modules with
	 * rules loaded
	 */
	protected String name = null;

	/**
	 * 
	 */
	public Defmodule(String name, Modules modules) {
		super();
		this.name = name;
		this.modules = modules;
		this.listeners = new ArrayList<ModuleChangedListener>();
	}

	/**
	 * Return the name of the module
	 * 
	 * @return
	 */
	public String getModuleName() {
		return this.name;
	}

	/**
	 * When clear is called, the module needs to clear all the internal lists
	 * for rules and activations. The handle to Rete should not be nulled.
	 */
	public void clear() {
		modules.clearModule(this);
	}
	
	public void clearRules() {
		modules.clearRules(this);
	}
	public void clearFacts(){
		modules.clearFacts(this);
	}

	public void addRule(Rule rl) {
		modules.addRule(this, rl);
		callListenersAddRule(rl);
	}

	public void removeRule(Rule rl) {
		callListenersRemoveRule(rl);
		modules.removeRule(this, rl);
	}

	public boolean containsRule(Rule rl) {
		return modules.containsRule(this,rl);
	}

	public List<Rule> getAllRules() {
		return modules.getRules(this);
	}

	public int getRuleCount() {
		return getAllRules().size();
	}

	public boolean containsTemplate(Template key) {
		return modules.containsTemplate(this,key);
	}

	@Override
	/**
	 * this method generates a deftemplate out of a
	 * java class. furthermore, it caches results, so,
	 * for repeated call with the same parameter class,
	 * it is guaranteed to receive the same template object.
	 * 
	 * NOTE: we use the canonical from the class c as
	 * the template's name.
	 */
	public Template getTemplate(Class c) {
		Template t = getTemplate(c.getCanonicalName());
		if (t == null) {

			List<TemplateSlot> slots = new ArrayList<TemplateSlot>();
			for (Field field : c.getFields()) {
				String slotName = field.getName();
				//TODO handle data type
				
				TemplateSlot tslot = new TemplateSlot(slotName);
				
				slots.add(tslot);
			}
			
			TemplateSlot[] slotsArray = new TemplateSlot[slots.size()];
			slotsArray = slots.toArray(slotsArray);
			
			Deftemplate newtempl = new Deftemplate(c.getCanonicalName(),"",slotsArray);
			
			addTemplate(newtempl);
			return newtempl;
		} else {
			return t;
		}
	}
	
	public Template getTemplate(String key) {
		return modules.getTemplate(this,key);
	}

	public boolean addTemplate(Template temp) {
		boolean result = modules.addTemplate(this, temp);
		callListenersAddTemplate(temp);
		return result;
	}

	public void removeTemplate(Template temp) {
		callListenersRemoveTemplate(temp);
		modules.removeTemplate(this,temp);
	}

	public List<Template> getTemplates() {
		return modules.getTemplates(this);
	}

	public int getTemplateCount() {
		return getTemplates().size();
	}

	public Rule findRule(String name) {
		return modules.findRule(this,name);
	}

	public String toString(){
		return "Module: " + this.getModuleName();
	}

	public void addModuleChangedEventListener(ModuleChangedListener listener) {
		listeners.add(listener);
	}

	public void removeModuleChangedEventListener(ModuleChangedListener listener) {
		listeners.remove(listener);
	}



}