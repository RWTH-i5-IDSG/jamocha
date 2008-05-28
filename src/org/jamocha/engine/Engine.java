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

package org.jamocha.engine;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jamocha.Constants;
import org.jamocha.communication.events.MessageEvent;
import org.jamocha.communication.logging.Logging;
import org.jamocha.communication.logging.Logging.JamochaLogger;
import org.jamocha.communication.messagerouter.MessageRouter;
import org.jamocha.engine.agenda.Agendas;
import org.jamocha.engine.configurations.ModifyConfiguration;
import org.jamocha.engine.configurations.SlotConfiguration;
import org.jamocha.engine.functions.FunctionMemory;
import org.jamocha.engine.functions.FunctionMemoryImpl;
import org.jamocha.engine.modules.Module;
import org.jamocha.engine.modules.Modules;
import org.jamocha.engine.scope.BlockingScope;
import org.jamocha.engine.scope.DefaultScope;
import org.jamocha.engine.scope.Scope;
import org.jamocha.engine.util.ProfileStats;
import org.jamocha.engine.workingmemory.WorkingMemory;
import org.jamocha.engine.workingmemory.elements.Deffact;
import org.jamocha.engine.workingmemory.elements.Deftemplate;
import org.jamocha.engine.workingmemory.elements.Fact;
import org.jamocha.engine.workingmemory.elements.InitialFact;
import org.jamocha.engine.workingmemory.elements.Template;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaValue;
import org.jamocha.parser.RuleException;
import org.jamocha.rules.Rule;
import org.jamocha.settings.JamochaSettings;
import org.jamocha.settings.SettingsChangedListener;
import org.jamocha.settings.SettingsConstants;

/**
 * @author Peter Lin
 * @author Josef Alexander Hahn
 * 
 * This is the main engine class.
 */
public class Engine implements Dumpable {

	private class EngineSettingsChangedListener implements
			SettingsChangedListener {

		public void settingsChanged(String propertyName) {
			JamochaSettings settings = JamochaSettings.getInstance();
			// watch activations
			if (propertyName
					.equals(SettingsConstants.ENGINE_GENERAL_SETTINGS_PROFILE_ASSERT))
				profileAssert = settings.getBoolean(propertyName);
			else if (propertyName
					.equals(SettingsConstants.ENGINE_GENERAL_SETTINGS_PROFILE_RETRACT))
				profileRetract = settings.getBoolean(propertyName);
		}
	}

	private static final long serialVersionUID = 1L;

	protected String[] interestedProperties = {
			SettingsConstants.ENGINE_GENERAL_SETTINGS_PROFILE_ASSERT,
			SettingsConstants.ENGINE_GENERAL_SETTINGS_PROFILE_RETRACT };

	protected ReteNet net = null;

	protected JamochaLogger log;

	protected FunctionMemory functionMem = null;

	protected Map<String, PrintWriter> outputStreams = new HashMap<String, PrintWriter>();

	protected Scope scopes = new BlockingScope();

	protected Agendas agendas = null;

	protected Modules modules = null;

	protected boolean profileAssert = false;

	protected boolean profileRetract = false;

	protected MessageRouter router = new MessageRouter(this);

	protected InitialFact initFact = new InitialFact();

	protected Map<String, JamochaValue> defglobals;

	/**
	 * 
	 */
	public Engine() {
		super();
		net = new ReteNet(this);
		functionMem = new FunctionMemoryImpl(this);
		agendas = new Agendas(this);
		modules = new Modules(this);
		defglobals = new HashMap<String, JamochaValue>();
		functionMem.init();
		establishInitialFact();
		JamochaSettings.getInstance().addListener(
				new EngineSettingsChangedListener(), interestedProperties);
		log = Logging.logger(this.getClass());
		log.info("Jamocha started");
	}

	protected void establishInitialFact() {
		try {
			modules.getMainModule().addTemplate(initFact);
			Fact ifact = initFact.getInitialFact();
			modules.addFact(ifact);
			assertFact(ifact);
		} catch (Exception e) {
			log.fatal(e);
		}
	}

	/**
	 * gets the engines initial fact template
	 * 
	 * @return
	 */
	public InitialFact getInitialTemplate() {
		return initFact;
	}

	/**
	 * gets the engines initial fact
	 * 
	 * @return
	 */
	public Fact getInitialFact() {
		return initFact.getInitialFact();
	}

	/**
	 * clear the deffacts from the working memory.
	 */
	public void clearFacts() {
		modules.clearAllFacts();
	}

	/**
	 * clear the rules from the engine.
	 */
	public void clearRules() {
		modules.clearAllRules();
	}

	/**
	 * clear all templates, modules, rules and facts. reset the profiler data.
	 * redeclare the initial fact.
	 */
	public void clearAll() {
		clearFacts();
		clearRules();
		modules.clearAll();
		ProfileStats.reset();
		establishInitialFact();
	}

	/**
	 * clear everything and kills the threads
	 */
	public void dispose() {
		clearAll();
		router.dispose();
	}

	/**
	 * this is useful for debugging purposes. clips allows the user to fire 1
	 * rule at a time. even here, only activations in the module, which has the
	 * focus will be fired.
	 * 
	 * @param count
	 * @return
	 */
	public int fire(int count) throws ExecuteException {
		return agendas.fireFocus(count);
	}

	/**
	 * this is the normal fire. it will fire all the rules that have matched
	 * completely, but only in the module which currently has the focus.
	 * 
	 * @return
	 * @throws ExecuteException
	 */
	public int fire() throws ExecuteException {
		return agendas.fireFocus();
	}

	/**
	 * Method returns the current focus. Only the rules in the current focus
	 * will be fired. Activations in other modules will not be fired until the
	 * focus is changed to it.
	 * 
	 * @return
	 */
	public Module getCurrentFocus() {
		return modules.getCurrentModule();
	}

	/**
	 * method will create for given name and returns that, if it does not exist.
	 * The current module is changed to the new one. if such a module already
	 * exist, it returns null and the focus will not change.
	 * 
	 * @param act
	 */
	public Module addModule(String name) {
		Module newMod = modules.addModule(name);
		if (newMod == null)
			return null;
		modules.setCurrentModule(newMod);
		return newMod;
	}

	/**
	 * method will return module for given name, if does not exist, a new one
	 * will be created. The current module is changed to the new one
	 * 
	 * @param act
	 */
	public Module getModule(String name) {
		Module newMod = modules.getModule(name);
		modules.setCurrentModule(newMod);
		return newMod;
	}

	/**
	 * method will return module for given name, if does not exist, it return
	 * null. the focus is unchanged in each case.
	 */
	public Module findModule(String name) {
		return modules.findModule(name);
	}

	/**
	 * find the template by name in the current focus
	 * 
	 * @param name
	 * @return Template
	 */
	public Template findTemplate(String name) {
		return modules.getCurrentModule().getTemplate(name);
	}

	/**
	 * declares (or overrides a defglobal)
	 */
	public void declareDefglobal(String name, JamochaValue value) {
		defglobals.put(name, value);
	}

	/**
	 * gets the value of a defglobal
	 */
	public JamochaValue getDefglobalValue(String name) {
		return defglobals.get(name);
	}

	/**
	 * returns a list of all bindings. this calculation is somewhat expensive
	 * and should only be used if it is really needed!
	 * 
	 * @return
	 */
	public Map<String, JamochaValue> getBindings() {
		Map<String, JamochaValue> result = new HashMap<String, JamochaValue>();
		for (String defglobalKey : defglobals.keySet())
			result.put(defglobalKey, defglobals.get(defglobalKey));
		for (String scopeKey : scopes.getBindings().keySet())
			result.put(scopeKey, scopes.getBindingValue(scopeKey));
		return result;
	}

	/**
	 * The current implementation will check to see if the variable is a
	 * defglobal. If it is, it will return the value. If not, it will see if
	 * there is an active rule and try to get the local bound value.
	 * 
	 * @param key
	 * @return
	 */
	// TODO understand and document
	public JamochaValue getBinding(String key) {
		if (key.startsWith("*"))
			return getDefglobalValue(key);
		else
			return scopes.getBindingValue(key);
	}

	/**
	 * This is the main method for setting the bindings. The current
	 * implementation will check to see if the name of the variable begins and
	 * ends with "*". If it does, it will declare it as a defglobal. Otherwise,
	 * it will try to add it to the rule being fired. Note: might need to have
	 * add one for shell variables later.
	 * 
	 * @param key
	 * @param value
	 */
	// TODO understand and document
	public void setBinding(String key, JamochaValue value) {
		if (key.startsWith("*"))
			declareDefglobal(key, value);
		else
			scopes.setBindingValue(key, value);
	}

	/**
	 * set the focus to a different module and returns true, iff success
	 * 
	 * @param moduleName
	 */
	public boolean setFocus(String moduleName) {
		return modules.setCurrentModule(moduleName);
	}

	/**
	 * Rete class contains a list of items that can be watched. Call the method
	 * with one of the four types:<br/> activations<br/> all<br/> facts<br/>
	 * rules<br/>
	 * 
	 * @param type
	 */
	public void setWatch(int type) {
		if (type == Constants.WATCH_ALL) {
			agendas.setWatchActivations(true);
			modules.setWatchFact(true);
			modules.setWatchRules(true);
		} else if (type == Constants.WATCH_ACTIVATIONS)
			agendas.setWatchActivations(true);
		else if (type == Constants.WATCH_FACTS)
			modules.setWatchFact(true);
		else if (type == Constants.WATCH_RULES)
			modules.setWatchRules(true);
	}

	/**
	 * Call the method with the type to unwatch activations<br/> facts<br/>
	 * rules<br/>
	 * 
	 * @param type
	 */
	public void setUnWatch(int type) {
		if (type == Constants.WATCH_ALL) {
			agendas.setWatchActivations(false);
			modules.setWatchFact(false);
			modules.setWatchRules(false);
		} else if (type == Constants.WATCH_ACTIVATIONS)
			agendas.setWatchActivations(false);
		else if (type == Constants.WATCH_FACTS)
			modules.setWatchFact(false);
		else if (type == Constants.WATCH_RULES)
			modules.setWatchRules(false);
	}

	/**
	 * To turn on profiling, call the method with the appropriate parameter. The
	 * parameters are defined in Rete class as static int values.
	 * 
	 * @param type
	 */
	public void setProfile(int type) {
		if (type == Constants.PROFILE_ALL) {
			agendas.setProfileAddActivation(true);
			agendas.setProfileRemoveActivation(true);
			agendas.setProfileFire(true);
			profileAssert = true;
			profileRetract = true;
		} else if (type == Constants.PROFILE_ASSERT)
			profileAssert = true;
		else if (type == Constants.PROFILE_RETRACT)
			profileRetract = true;
		else if (type == Constants.PROFILE_ADD_ACTIVATION)
			agendas.setProfileAddActivation(true);
		else if (type == Constants.PROFILE_RM_ACTIVATION)
			agendas.setProfileRemoveActivation(true);
		else if (type == Constants.PROFILE_FIRE)
			agendas.setProfileFire(true);
	}

	/**
	 * To turn off profiling, call the method with the appropriate parameter.
	 * The parameters are defined in Rete class as static int values.
	 * 
	 * @param type
	 */
	public void setProfileOff(int type) {
		if (type == Constants.PROFILE_ALL) {
			agendas.setProfileAddActivation(false);
			agendas.setProfileRemoveActivation(false);
			agendas.setProfileFire(false);
			profileAssert = false;
			profileRetract = false;
		} else if (type == Constants.PROFILE_ASSERT)
			profileAssert = false;
		else if (type == Constants.PROFILE_RETRACT)
			profileRetract = false;
		else if (type == Constants.PROFILE_ADD_ACTIVATION)
			agendas.setProfileAddActivation(false);
		else if (type == Constants.PROFILE_RM_ACTIVATION)
			agendas.setProfileRemoveActivation(false);
		else if (type == Constants.PROFILE_FIRE)
			agendas.setProfileFire(false);
	}

	/**
	 * gets the fact with the given id
	 * 
	 * @param factID
	 * @return
	 */
	public Fact getFactById(JamochaValue factID) {
		return getFactById(factID.getFactIdValue());
	}

	/**
	 * gets the fact with the given id
	 * 
	 * @param factID
	 * @return
	 */
	public Fact getFactById(long id) {
		return modules.getFactById(id);
	}

	// ----- method for adding output streams for spools ----- //
	/**
	 * this method is for adding printwriters for spools. the purpose of the
	 * spool function is to dump everything out to a file.
	 */
	public void addPrintWriter(String name, PrintWriter writer) {
		outputStreams.put(name, writer);
	}

	/**
	 * It is up to spool function to make sure it removes the printer writer and
	 * closes it properly.
	 * 
	 * @param name
	 * @return
	 */
	public PrintWriter removePrintWriter(String name) {
		return outputStreams.remove(name);
	}

	// ----- method for writing messages out ----- //
	/**
	 * The method is called by classes to write watch, profiling and other
	 * messages to the output stream. There maybe 1 or more outputstreams.
	 * 
	 * @param msg
	 */
	public void writeMessage(String msg) {
		writeMessage(msg, "t");
	}

	/**
	 * writeMessage will create a MessageEvent and pass it along to any
	 * channels. It will also write out all messages to all registered
	 * PrintWriters. For example, if there's a spool setup, it will write the
	 * messages to the printwriter.
	 * 
	 * @param msg
	 * @param output
	 */
	public void writeMessage(String msg, String output) {
		MessageRouter router = getMessageRouter();
		router.postMessageEvent(new MessageEvent(
				MessageEvent.MessageEventType.ENGINE, msg,
				"t".equals(output) ? router.getCurrentChannelId() : output));
		for (PrintWriter wr : outputStreams.values()) {
			wr.write(msg);
			wr.flush();
		}
	}

	/**
	 * This method is explicitly used to assert facts.
	 */
	public Fact assertFact(Fact o) throws AssertException {
		if (profileAssert)
			ProfileStats.startAssert();
		modules.addFact(o);
		net.assertObject(o);
		if (profileAssert)
			ProfileStats.endAssert();
		return o;
	}

	/**
	 * retracts a fact by id
	 */
	public void retractById(long id) throws RetractException {
		Fact ft = modules.getFactById(id);
		retractFact(ft);
	}

	/**
	 * retracts a fact directly
	 */
	public void retractFact(Fact fact) throws RetractException {
		if (profileAssert)
			ProfileStats.startRetract();
		net.retractObject(fact);
		if (profileAssert)
			ProfileStats.endRetract();
	}

	/**
	 * Modify retracts the old fact and asserts the new fact. Unlike assertFact,
	 * modifyFact will not check to see if the fact already exists. This is
	 * because the old fact would already be unique.
	 * 
	 * @param old
	 * @param newfact
	 * @throws EvaluationException
	 */
	public void modifyFact(Fact old, ModifyConfiguration mc)
			throws EvaluationException {
		boolean allSilent = true;

		for (SlotConfiguration slot : mc.getSlots())
			allSilent &= old.isSlotSilent(slot.getId());

		if (allSilent)
			// for only silent slots changed, we dont have to put some
			// activations into the agenda, so we can do it this way
			old.updateSlots(this, mc.getSlots());
		else {
			Fact modifiedFact = ((Deffact) old).cloneFact(this);
			modifiedFact.setFactId(old.getFactId());
			modifiedFact.updateSlots(this, mc.getSlots());
			retractFact(old);
			// TODO remove this.modules.addFact(modifiedFact);
			assertFact(modifiedFact);
		}
	}

	/**
	 * Method will retract all the deffacts and then re-assert them. this will
	 * be done for all modules
	 */
	public void resetFacts() {
		try {
			List<Fact> facts = modules.getAllFacts();
			for (Fact ft : facts)
				net.retractObject(ft);
			for (Fact ft : facts)
				net.assertObject(ft);
		} catch (RetractException e) {
			log.warn(e);
		} catch (AssertException e) {
			log.warn(e);
		}
	}

	/**
	 * gets the modules-manager
	 * 
	 * @return
	 */
	public Modules getModules() {
		return modules;
	}

	/**
	 * gets the agendas-manager
	 * 
	 * @return
	 */
	public Agendas getAgendas() {
		return agendas;
	}

	/**
	 * gets the function memory
	 * 
	 * @return
	 */
	public FunctionMemory getFunctionMemory() {
		return functionMem;
	}

	/**
	 * gets the message router
	 * 
	 * @return
	 */
	public MessageRouter getMessageRouter() {
		return router;
	}

	// TODO understand and document
	public void pushScope() {
		pushScope(new DefaultScope());
	}

	// TODO understand and document
	public void popScope() {
		// We don't pop the last scope
		if (scopes.getOuterScope() != null)
			scopes = scopes.popScope();
	}

	// TODO understand and document
	public void pushScope(Scope scope) {
		scope.pushScope(scopes);
		scopes = scope;
	}

	/**
	 * adds a template to the module, which is given by the deftemplates name
	 * (if fully qualified template name). otherwise adds the template in the
	 * current focus. returns true, iff a new template was generated.
	 * 
	 * @param tpl
	 * @return
	 * @throws org.jamocha.parser.EvaluationException
	 */
	public boolean addTemplate(Deftemplate tpl) throws EvaluationException {
		tpl.evaluateStaticDefaults(this);
		Module mod = tpl.checkName(this);
		if (mod == null)
			mod = getCurrentFocus();
		Boolean result = mod.addTemplate(tpl);
		if (result)
			net.addTemplate(tpl);
		return result;
	}

	/**
	 * adds the rule and compiles it into the rete network. also, the listeners
	 * are signalled for the newrule-event
	 * 
	 * @param rule
	 * @return
	 * @throws org.jamocha.parser.EvaluationException
	 * @throws org.jamocha.parser.RuleException
	 */
	public boolean addRule(Rule rule) throws EvaluationException, RuleException {
		boolean result = false;

		// compile the rule
		if (!getCurrentFocus().containsRule(rule))
			result = net.addRule(rule);

		// return true, iff we succedded
		return result;
	}

	/**
	 * gets the rete network
	 * 
	 * @return
	 */
	public ReteNet getNet() {
		return net;
	}

	/**
	 * gets the jamocha search path
	 * 
	 * @return
	 */
	public static String[] getJamochaSearchPaths() {
		String path = System.getenv("JAMOCHA_PATH");
		if (path != null && path.length() > 0)
			return path.split(System.getProperty("path.separator"));
		return null;
	}

	/**
	 * this is a conveniance method for getting the network's working memory
	 * 
	 * @return
	 */
	public WorkingMemory getWorkingMemory() {
		return net.getWorkingMemory();
	}

	/**
	 * returns text-dump of the engine (off all modules)
	 */
	public String getDump() {
		StringBuilder sb = new StringBuilder();
		for (Module m : getModules().getModuleList()) {
			sb.append("\n%dump module ").append(m.getName()).append("\n");
			sb.append(m.getDump());
		}
		for (Fact f : getModules().getAllFacts())
			sb.append(f.getDump()).append("\n");
		return sb.toString();
	}

	public void pushScope(Rule rule) {
		// TODO implement that stuff here scopes.pushScope(rule);
	}

	public RuleCompiler getRuleCompiler() {
		return net.compiler;
	}

}
