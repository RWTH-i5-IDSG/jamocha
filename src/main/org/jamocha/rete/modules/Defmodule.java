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
import java.util.List;

import org.jamocha.rete.Template;
import org.jamocha.rule.Rule;

/**
 * @author Peter Lin
 * 
 * A module represents a set of rulesets. The concept is from CLIPS and provides
 * a way to isolate the rule activation and pattern matching.
 */
public class Defmodule implements Module, Serializable {

	Modules modules;
	
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
	public Defmodule(String name, Modules modules) {
		super();
		this.name = name;
		this.modules = modules;
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
	}

	public void removeRule(Rule rl) {
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

	public Template getTemplate(String key) {
		return modules.getTemplate(this,key);
	}

	public boolean addTemplate(Template temp) {
		return modules.addTemplate(this, temp);
	}

	public void removeTemplate(Template temp) {
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

}