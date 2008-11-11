/*
 * Copyright 2002-2008 The Jamocha Team
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jamocha.Constants;
import org.jamocha.communication.events.ModulesChangedEvent;
import org.jamocha.communication.events.ModulesChangedEventListener;
import org.jamocha.communication.events.ModulesChangedEvent.ModulesChangedEventType;
import org.jamocha.engine.Engine;
import org.jamocha.engine.configurations.AssertConfiguration;
import org.jamocha.engine.configurations.TemporalValidityConfiguration;
import org.jamocha.engine.workingmemory.elements.Deftemplate;
import org.jamocha.engine.workingmemory.elements.Fact;
import org.jamocha.engine.workingmemory.elements.OrderedTemplate;
import org.jamocha.engine.workingmemory.elements.Template;
import org.jamocha.parser.EvaluationException;
import org.jamocha.rules.Rule;
import org.jamocha.settings.JamochaSettings;
import org.jamocha.settings.SettingsChangedListener;
import org.jamocha.settings.SettingsConstants;

/**
 * @author Sebastian Reinartz
 * 
 */
public class Modules implements SettingsChangedListener, Serializable {

	private static final long serialVersionUID = 1L;

	private boolean watchFact = false;

	private boolean watchRules = false;

	private final String[] interestedProperties = {
			SettingsConstants.ENGINE_GENERAL_SETTINGS_WATCH_FACTS,
			SettingsConstants.ENGINE_GENERAL_SETTINGS_WATCH_RULES };

	private Module currentModule = null;

	private final List<ModulesChangedEventListener> listeners;

	/**
	 * this is the main module
	 */
	private Module main = null;

	private final FactDataContainer facts = new FactDataContainer();

	private final RuleDataContainer rules = new RuleDataContainer();

	private final TemplateDataContainer templates = new TemplateDataContainer();

	protected Engine engine;
	
	/**
	 * The HashMap for the modules.
	 */
	protected Map<String, Module> modules = new HashMap<String, Module>();
	
	
	public Collection<Module> getModuleList() {
		return modules.values();
	}

	public Modules(final Engine engine) {
		super();
		initMain();
		this.engine = engine;
		listeners = new ArrayList<ModulesChangedEventListener>();
		JamochaSettings.getInstance().addListener(this, interestedProperties);
	}

	public void addModulesChangeListener(final ModulesChangedEventListener l) {
		listeners.add(l);
	}

	public void removeModulesChangeListener(final ModulesChangedEventListener l) {
		listeners.remove(l);
	}

	protected void initMain() {
		main = new Defmodule(Constants.MAIN_MODULE, this);
		modules.put(main.getName(), main);
		// by default, we set the current module to main
		currentModule = main;
	}

	public Module getCurrentModule() {
		return currentModule;
	}

	/**
	 * tries to set the current focus onto a module, which is given by a string.
	 * if this module cannot be found, focus is unchanged and the function
	 * returns false.
	 * 
	 * @param name
	 * @return
	 */
	public boolean setCurrentModule(final String name) {
		final Module mod = findModule(name);
		if (mod == null)
			return false;
		setCurrentModule(mod);
		return true;
	}

	/**
	 * sets the current focus onto a module. it returns true, iff mod != null
	 * 
	 * @param mod
	 * @return
	 */
	public boolean setCurrentModule(final Module mod) {
		if (mod != null) {
			currentModule = mod;
			return true;
		} else
			return false;
	}

	public Module getMainModule() {
		return main;
	}

	/**
	 * method will create for given name, if it does not exist. otherwise, it
	 * returns null
	 * 
	 * @param name
	 */
	public Module addModule(final String name) {
		if (findModule(name) == null) {
			final Defmodule mod = new Defmodule(name, this);
			modules.put(name, mod);
			final ModulesChangedEvent event = new ModulesChangedEvent(this,
					mod, ModulesChangedEventType.MODULE_ADDED);
			for (final ModulesChangedEventListener l : listeners)
				l.modulesChanged(event);
			return mod;
		} else
			return null;
	}

	/**
	 * this method will return a module with the given name. it it does not
	 * exist, it creates a new one.
	 * 
	 * @param name
	 * @return
	 */
	public Module getModule(final String name) {
		final Module result = findModule(name);
		if (result == null)
			return addModule(name);
		return result;
	}

	public Module removeModule(final Module module) {
		final ModulesChangedEvent event = new ModulesChangedEvent(this, module,
				ModulesChangedEventType.MODULE_REMOVED);
		for (final ModulesChangedEventListener l : listeners)
			l.modulesChanged(event);
		clearModule(module);
		return modules.remove(module.getName());
	}

	/**
	 * return the module. if it doesn't exist, method returns null.
	 * 
	 * @param name
	 * @return
	 */
	public Module findModule(final String name) {
		return modules.get(name);
	}

	private Fact createFact(final AssertConfiguration ac, final Deftemplate tmpl)
			throws EvaluationException {
		Fact ft = null;
		ft = tmpl.createFact(ac.getSlotConfigurations(), engine);
		// TODO remove this.addFact(ft);
		return ft;
	}

	private Fact createFact(final AssertConfiguration ac,
			final OrderedTemplate tmpl) throws EvaluationException {
		Fact ft = null;
		ft = tmpl.createFact(ac.getData(), engine);
		// TODO remove this.addFact(ft);
		return ft;
	}

	public Fact createFact(final AssertConfiguration ac)
			throws EvaluationException {
		final Template tmpl = getTemplate(currentModule, ac.getTemplateName());
		Fact result;
		if (tmpl == null)
			result= createFact(ac, new OrderedTemplate(ac.getTemplateName()));
		else if (tmpl instanceof Deftemplate)
			result= createFact(ac, (Deftemplate) tmpl);
		else
			result= createFact(ac, (OrderedTemplate) tmpl);
		
		if (ac.getTemporalValidityConfiguration() != null) {
			TemporalValidityConfiguration tvc = 
					(TemporalValidityConfiguration) ac.getTemporalValidityConfiguration();
			result.setTemporalValidity(tvc.getTemporalValidity(engine));
		} else if (ac.getTemporalValidity() != null) {
			result.setTemporalValidity(ac.getTemporalValidity());
		}
		
		return result;
	}

	public Rule findRule(final Module module, final String ruleName) {
		return rules.get(ruleName, module);
	}

	protected void removeTemplate(final Module module, final Template temp) {
		templates.remove(temp.getName(), module);

	}

	protected boolean addTemplate(final Module defmodule, final Template temp) {
		return templates.add(temp, defmodule);
	}

	public Template getTemplate(final Module defmodule, final String template) {
		return templates.get(template, defmodule);
	}

	public boolean containsTemplate(final Module defmodule,
			final Template template) {
		return templates.containsTemplate(defmodule, template);
	}

	public List<Rule> getRules(final Module module) {
		return rules.getRules(module);
	}

	public List<Rule> getAllRules() {
		return rules.getRules();
	}

	public boolean containsRule(final Module defmodule, final Rule rl) {
		return rules.containsRule(rl, defmodule);
	}

	public void removeRule(final Module defmodule, final Rule rl) {
		if (watchRules)
			engine.writeMessage("<== Rule: " + rl.getName()
					+ Constants.LINEBREAK, "t");
		rules.remove(rl.getName(), defmodule);

	}

	public void addRule(final Module defmodule, final Rule rl) {
		if (watchRules)
			engine.writeMessage("==> Rule: " + rl.getName()
					+ Constants.LINEBREAK, "t");
		rules.add(rl, defmodule);

	}

	public List<Template> getTemplates(final Module defmodule) {
		return templates.getTemplates(defmodule);
	}

	public List<Fact> getAllFacts() {
		return facts.getFacts();
	}

	public Fact getFactById(final long id) {
		return facts.getFactById(id);
	}

	public Fact getFactByFact(final Fact fact) {
		return facts.getFactByFact(fact);
	}

	public long addFact(final Fact fact) {
		if (watchFact)
			engine.writeMessage("==> " + fact.toString() + Constants.LINEBREAK,
					"t");

		return facts.add(fact);
	}

	public void removeFact(final Fact fact) {
		if (watchFact)
			engine.writeMessage("<== " + fact.toString() + Constants.LINEBREAK,
					"t");

		facts.remove(fact.getFactId());
	}

	@Override
	public String toString() {
		return "Modules Current Module:" + getCurrentModule().getName();

	}

	/**
	 * Clear will clear all the modules and remove all activations
	 */
	public void clearAll() {
		clearAllRules();
		clearAllFacts();
		clearAllTemplates();
		modules.clear();

		// reinit main module:
		initMain();
	}

	public void clearModule(final Module module) {
		clearFacts(module);
		clearRules(module);
		clearTemplates(module);
	}

	private void clearTemplates(final Module module) {
		// TODO Auto-generated method stub
	}

	public void clearRules(final Module module) {
		// TODO Auto-generated method stub
	}

	public void clearFacts(final Module module) {
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

	public void setWatchFact(final boolean watchFact) {
		this.watchFact = watchFact;
	}

	public void setWatchRules(final boolean watchRules) {
		this.watchRules = watchRules;
	}

	public void settingsChanged(final String propertyName) {
		final JamochaSettings settings = JamochaSettings.getInstance();
		// watch facts
		if (propertyName
				.equals(SettingsConstants.ENGINE_GENERAL_SETTINGS_WATCH_FACTS))
			setWatchFact(settings.getBoolean(propertyName));
		// watch rules
		else if (propertyName
				.equals(SettingsConstants.ENGINE_GENERAL_SETTINGS_WATCH_RULES))
			setWatchRules(settings.getBoolean(propertyName));
	}

}
