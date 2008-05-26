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
import java.util.List;

import org.jamocha.communication.events.ModuleChangedEvent;
import org.jamocha.communication.events.ModuleChangedListener;
import org.jamocha.engine.workingmemory.elements.Template;
import org.jamocha.rules.Rule;

/**
 * @author Peter Lin
 * @author Josef Alexander Hahn
 * 
 * A module represents a set of rulesets. The concept is from CLIPS and provides
 * a way to isolate the rule activation and pattern matching.
 */
public class Defmodule implements Module {

	Modules modules;

	List<ModuleChangedListener> listeners;

	private static final long serialVersionUID = 0xDEADBEAFL;

	protected int id;

	protected void callListenersAddRule(final Rule rule) {
		final ModuleChangedEvent ev = new ModuleChangedEvent(this, this, rule);
		for (final ModuleChangedListener listener : listeners)
			listener.ruleAdded(ev);
	}

	protected void callListenersRemoveRule(final Rule rule) {
		final ModuleChangedEvent ev = new ModuleChangedEvent(this, this, rule);
		for (final ModuleChangedListener listener : listeners)
			listener.ruleRemoved(ev);
	}

	protected void callListenersAddTemplate(final Template tmpl) {
		final ModuleChangedEvent ev = new ModuleChangedEvent(this, this, tmpl);
		for (final ModuleChangedListener listener : listeners)
			listener.templateAdded(ev);
	}

	protected void callListenersRemoveTemplate(final Template tmpl) {
		final ModuleChangedEvent ev = new ModuleChangedEvent(this, this, tmpl);
		for (final ModuleChangedListener listener : listeners)
			listener.templateRemoved(ev);
	}

	/**
	 * The name of the module. A rule engine may have one or more modules with
	 * rules loaded
	 */
	protected String name = null;

	/**
	 * 
	 */
	public Defmodule(final String name, final Modules modules) {
		super();
		this.name = name;
		this.modules = modules;
		listeners = new ArrayList<ModuleChangedListener>();
	}

	/**
	 * Return the name of the module
	 * 
	 * @return
	 */
	public String getName() {
		return name;
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

	public void clearFacts() {
		modules.clearFacts(this);
	}

	public void addRule(final Rule rl) {
		modules.addRule(this, rl);
		callListenersAddRule(rl);
	}

	public void removeRule(final Rule rl) {
		callListenersRemoveRule(rl);
		modules.removeRule(this, rl);
	}

	public boolean containsRule(final Rule rl) {
		return modules.containsRule(this, rl);
	}

	public List<Rule> getAllRules() {
		return modules.getRules(this);
	}

	public int getRuleCount() {
		return getAllRules().size();
	}

	public boolean containsTemplate(final Template key) {
		return modules.containsTemplate(this, key);
	}

	public Template getTemplate(final String key) {
		return modules.getTemplate(this, key);
	}

	public boolean addTemplate(final Template temp) {
		final boolean result = modules.addTemplate(this, temp);
		callListenersAddTemplate(temp);
		return result;
	}

	public void removeTemplate(final Template temp) {
		callListenersRemoveTemplate(temp);
		modules.removeTemplate(this, temp);
	}

	public List<Template> getTemplates() {
		return modules.getTemplates(this);
	}

	public int getTemplateCount() {
		return getTemplates().size();
	}

	public Rule findRule(final String name) {
		return modules.findRule(this, name);
	}

	public void addModuleChangedEventListener(
			final ModuleChangedListener listener) {
		listeners.add(listener);
	}

	public void removeModuleChangedEventListener(
			final ModuleChangedListener listener) {
		listeners.remove(listener);
	}

	public String getDump() {
		final StringBuilder sb = new StringBuilder();

		sb.append("(def-module \"").append(getName()).append("\")");

		for (final Rule r : getAllRules())
			sb.append(r.getDump());

		return sb.toString();
	}

}