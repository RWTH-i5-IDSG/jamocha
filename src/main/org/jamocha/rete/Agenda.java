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

import org.jamocha.rete.Modules.Modules;
import org.jamocha.rete.util.ProfileStats;


/**
 * @author Peter Lin
 *
 * The design of the agenda is based on CLIPS, which uses modules to
 * contain different rulesets. When a new activation is added to the
 * agenda, it is added to a specific module. By default, the rule
 * engine creates a main module. If no additional modules are created,
 * all activations are added to the main module. If there are multiple
 * modules, the activation is added to the activation list of that
 * given module.
 * Only the activations of the current module will be fired.
 */
public class Agenda implements Serializable {

	private static final long serialVersionUID = 1L;

	private Rete engine = null;

	private boolean watch = false;

	private boolean profAdd = false;

	private boolean profRm = false;

	/**
	 * The agenda takes an instance of Rete. the agenda needs a
	 * handle to the engine to do work.
	 */
	public Agenda(Rete engine) {
		super();
		this.engine = engine;
	}

	public void setWatch(boolean w) {
		this.watch = w;
	}

	public boolean watch() {
		return this.watch;
	}

	public void setProfileAdd(boolean prof) {
		this.profAdd = prof;
	}

	public boolean profileAdd() {
		return this.profAdd;
	}

	public void setProfileRemove(boolean prof) {
		this.profRm = prof;
	}

	public boolean profileRemove() {
		return this.profRm;
	}

	public void addModule(Module module) {
		this.modules.addModule(module);
	}

	public Module removeModule(Module module) {
		return this.modules.removeModule(module);
	}

	public Module removeModule(String name) {
		return this.modules.removeModule(name);
	}

	/**
	 * Return the values from the HashMap
	 * @return
	 */
	public Collection getModules() {
		return this.modules.getModules();
	}

	/**
	 * return the module. if it doesn't exist, method returns null.
	 * @param name
	 * @return
	 */
	public Module findModule(String name) {
		return (Module) this.modules.findModule(name);
	}

	/**
	 * Add an activation to the agenda.
	 * @param actv
	 */
	public void addActivation(Activation actv) {
		// the implementation should get the current focus from Rete
		// and then add the activation to the Module.
		if (profAdd) {
			addActivationWProfile(actv);
		} else {
			if (watch) {
				engine.writeMessage("=> " + actv.toPPString() + "\r\n", "t");
			}
			actv.getRule().getModule().addActivation(actv);
		}
	}

	/**
	 * if profiling is turned on, the method is called to add
	 * new activations to the agenda
	 * @param actv
	 */
	public void addActivationWProfile(Activation actv) {
		ProfileStats.startAddActivation();
		actv.getRule().getModule().addActivation(actv);
		ProfileStats.endAddActivation();
	}

	/**
	 * Method is called to remove an activation from the agenda.
	 * @param actv
	 */
	public void removeActivation(Activation actv) {
		if (profRm) {
			removeActivationWProfile(actv);
		} else {
			if (watch) {
				engine.writeMessage("<= " + actv.toPPString() + "\r\n", "t");
			}
			actv.getRule().getModule().removeActivation(actv);
		}
	}

	/**
	 * if the profiling is turned on for remove, the method is
	 * called to remove activations.
	 * @param actv
	 */
	public void removeActivationWProfile(Activation actv) {
		ProfileStats.startRemoveActivation();
		actv.getRule().getModule().removeActivation(actv);
		ProfileStats.endRemoveActivation();
	}

	/**
	 * Clear will clear all the modules and remove all activations
	 */
	public void clear() {
		this.modules.clearAll();
	}
	
	public void clearRules(){
		this.modules.clearAllRules();
	}
	
	public Template findTemplate(String templName){
		return this.modules.findTemplates(templName);
	}
}
