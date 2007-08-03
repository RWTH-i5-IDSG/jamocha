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
package org.jamocha.rete;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.jamocha.rete.eventhandling.ModuleChangedEvent;
import org.jamocha.rete.eventhandling.ModuleChangedListener;
import org.jamocha.rete.exception.RetractException;
import org.jamocha.rete.memory.WorkingMemory;
import org.jamocha.rete.nodes.ObjectTypeNode;
import org.jamocha.rule.Defrule;
import org.jamocha.rule.Rule;

/**
 * @author Peter Lin
 * 
 * A module represents a set of rulesets. The concept is from CLIPS and provides
 * a way to isolate the rule activation and pattern matching.
 */
public class Defmodule implements Module, Serializable {

	/**
	 * 
	 */
	Vector<ModuleChangedListener> listeners;
	
	private static final long serialVersionUID = 0xDEADBEAFL;

	protected int id;

	/**
	 * The name of the module. A rule engine may have one or more modules with
	 * rules loaded
	 */
	protected String name = null;

	/**
	 * A simple list of the rules in this module. Before an activation is added
	 * to the module, the class should check to see if the rule is in the module
	 * first.
	 */
	protected Map<String,Rule> rules = new HashMap<String,Rule>();

	/**
	 * The key is either the template name if it was created from the shell, or
	 * the defclass if it was created from an Object.
	 */
	protected Map<String,Template> deftemplates = new HashMap<String,Template>();

	private int templateCount = 0;

	/**
	 * 
	 */
	public Defmodule(String name) {
		super();
		this.name = name;
		listeners = new Vector<ModuleChangedListener>();
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
		this.clearRules();
		this.deftemplates.clear();
	}
	
	public void clearRules() {
		Iterator itr = this.rules.values().iterator();
		while (itr.hasNext()) {
			Defrule rl = (Defrule) itr.next();
			rl.clear();
		}
		this.rules.clear();
	}


	/**
	 * Add a compiled rule to the module
	 */
	public void addRule(Rule rl) {
		this.rules.put(rl.getName(), rl);
		callAddRuleListeners(rl);
	}

	/**
	 * Remove a rule from this module
	 * 
	 * @throws RetractException
	 */
	public void removeRule(Rule rl, Rete engine, WorkingMemory mem) {
		try {
			if ( rl.getSubRules().isEmpty()) {
				this.rules.remove(rl.getName());
				rl.getTerminalNode().destroy(engine);
			} else {
				for (Rule subRule : rl.getSubRules()) {
					this.rules.remove(subRule.getName());
					subRule.getTerminalNode().destroy(engine);
				}
			}
			callRemoveRuleListeners(rl);
		} catch (RetractException e) {
			e.printStackTrace();
		}
	}

	/**
	 * If the module already contains the rule, it will return true. The lookup
	 * uses the rule name, so rule names are distinct within a single module.
	 * The same rule name may be used in multiple modules.
	 */
	public boolean containsRule(Rule rl) {
		return this.rules.containsKey(rl.getName());
	}

	/**
	 * implementation returns the Values of the HashMap
	 */
	public Collection getAllRules() {
		return this.rules.values();
	}

	/**
	 * implementation returns HashMap.size()
	 */
	public int getRuleCount() {
		return this.rules.size();
	}

	/**
	 * The key is either the Defclass or a string name
	 */
	public boolean containsTemplate(Object key) {
		if (key instanceof Defclass) {
			Defclass dc = (Defclass) key;
			return this.deftemplates.containsKey(dc.getClassObject().getName());
		} else {
			return this.deftemplates.containsKey(key);
		}
	}

	/**
	 * implementation looks up the template and assumes the key is the classname
	 * or the user define name.
	 */
	public Template getTemplate(Defclass key) {
		return (Template) this.deftemplates.get(key.getClassObject().getName());
	}

	public Template getTemplate(String key) {
		return (Template) this.deftemplates.get(key);
	}

	/**
	 * find a parent template using the string template name
	 */
	public Template findParentTemplate(String key) {
		Template tmpl = null;
		Iterator itr = this.deftemplates.keySet().iterator();
		while (itr.hasNext()) {
			Object keyval = itr.next();
			Template entry = (Template) this.deftemplates.get(keyval);
			if (entry.getName().equals(key)) {
				tmpl = entry;
				break;
			}
		}
		return tmpl;
	}

	/**
	 * The implementation will use either the defclass or the template name for
	 * the key. The templates are stored in a HashMap.
	 */
	public boolean addTemplate(Template temp, Rete engine, WorkingMemory mem) {
		if (!this.deftemplates.containsKey(temp.getName())) {
			// we have to set the template's module
			if (temp.getClassName() != null) {
				this.deftemplates.put(temp.getName(), temp);
				this.deftemplates.put(temp.getClassName(), temp);
				this.templateCount++;
			} else {
				this.deftemplates.put(temp.getName(), temp);
				this.templateCount++;
			}
			mem.getRuleCompiler().addObjectTypeNode(temp);
			callAddTemplateListeners(temp);
			return true;
		}
		return false;
	}

	protected void callRemoveTemplateListeners(Template temp) {
		ModuleChangedEvent ev = new ModuleChangedEvent();
		ev.module = this;
		ev.template = temp;
		for (ModuleChangedListener l : listeners) l.templateRemoved(ev);
	}

	protected void callRemoveRuleListeners(Rule rule) {
		ModuleChangedEvent ev = new ModuleChangedEvent();
		ev.module = this;
		ev.rule = rule;
		for (ModuleChangedListener l : listeners) l.ruleRemoved(ev);
	}

	protected void callRemoveFactListeners(Fact fact) {
		ModuleChangedEvent ev = new ModuleChangedEvent();
		ev.module = this;
		ev.fact = fact;
		for (ModuleChangedListener l : listeners) l.factRemoved(ev);
	}
	
	protected void callAddTemplateListeners(Template temp) {
		ModuleChangedEvent ev = new ModuleChangedEvent();
		ev.module = this;
		ev.template = temp;
		for (ModuleChangedListener l : listeners) l.templateAdded(ev);
	}

	protected void callAddRuleListeners(Rule rule) {
		ModuleChangedEvent ev = new ModuleChangedEvent();
		ev.module = this;
		ev.rule = rule;
		for (ModuleChangedListener l : listeners) l.ruleAdded(ev);
	}

	protected void callAddFactListeners(Fact fact) {
		ModuleChangedEvent ev = new ModuleChangedEvent();
		ev.module = this;
		ev.fact = fact;
		for (ModuleChangedListener l : listeners) l.factAdded(ev);
	}
	
	/**
	 * implementation will remove the template from the HashMap and it will
	 * remove the ObjectTypeNode from the network.
	 */
	public void removeTemplate(Template temp, Rete engine, WorkingMemory mem) {
		this.deftemplates.remove(temp.getName());
		callRemoveTemplateListeners(temp);
		if (temp.getClassName() != null) {
			this.deftemplates.remove(temp.getClassName());
		}
		ObjectTypeNode otn = mem.getRuleCompiler().getObjectTypeNode(temp);
		try {
			mem.getRuleCompiler().removeObjectTypeNode(otn);
		} catch (RetractException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Method returns the entrySet of the HashMap containing the Deftemplates.
	 * Because of how we map the deftemplates, the number of entries will not
	 * correspond to the number of actual deftemplates
	 */
	public Collection getTemplates() {
		return this.deftemplates.values();
	}

	public int getTemplateCount() {
		return this.templateCount;
	}

	/**
	 * implementation looks up the rule in the HashMap
	 */
	public Rule findRule(String name) {
		return (Rule) this.rules.get(name);
	}

	public void addModuleChangedListener(ModuleChangedListener listener) {
		listeners.add(listener);
	}

	public void removeModuleChangedListener(ModuleChangedListener listener) {
		listeners.remove(listener);
	}


}
