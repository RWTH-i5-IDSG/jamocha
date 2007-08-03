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
import java.util.Iterator;
import java.util.List;

import org.jamocha.rete.eventhandling.ModuleChangedEvent;
import org.jamocha.rete.eventhandling.ModuleChangedListener;
import org.jamocha.rete.exception.RetractException;
import org.jamocha.rete.memory.WorkingMemory;
import org.jamocha.rete.nodes.ObjectTypeNode;
import org.jamocha.rule.Rule;

/**
 * @author Peter Lin
 * 
 * A module represents a set of rulesets. The concept is from CLIPS and provides
 * a way to isolate the rule activation and pattern matching.
 */
public class Defmodule implements Module, Serializable {

	Rete engine;
	
	private static final long serialVersionUID = 0xDEADBEAFL;

	protected int id;

	/**
	 * The name of the module. A rule engine may have one or more modules with
	 * rules loaded
	 */
	protected String name = null;

	/**
	 * 
	 */
	public Defmodule(String name, Rete engine) {
		super();
		this.name = name;
		this.engine = engine;
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
		engine.getModules().flush(this);
	}
	
	public void clearRules() {
		engine.getModules().flushRules(this);
	}

	public void addRule(Rule rl) {
		engine.getModules().addRule(this, rl);
	}

	public void removeRule(Rule rl) {
		engine.getModules().removeRule(this, rl);
	}

	public boolean containsRule(Rule rl) {
		return engine.getModules().containsRule(this,rl);
	}

	public List<Rule> getAllRules() {
		return engine.getModules().getAllRules(this);
	}

	public int getRuleCount() {
		return getAllRules().size();
	}

	public boolean containsTemplate(Template key) {
		return engine.getModules().containsTemplate(this,key);
	}

	public Template getTemplate(String key) {
		return engine.getModules().getTemplate(this,key);
	}

	public boolean addTemplate(Template temp) {
		return engine.getModules().addTemplate(this, temp);
	}

	public void removeTemplate(Template temp) {
		engine.getModules().removeTemplate(this,temp);
	}

	public List<Template> getTemplates() {
		return engine.getModules().getTemplates(this);
	}

	public int getTemplateCount() {
		return getTemplates().size();
	}

	public Rule findRule(String name) {
		return engine.getModules().findRule(this,name);
	}



}