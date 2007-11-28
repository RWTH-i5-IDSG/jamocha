/*
 * Copyright 2002-2006 Peter Lin
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
package org.jamocha.rete;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jamocha.Constants;
import org.jamocha.logging.DefaultLogger;
import org.jamocha.messagerouter.MessageEvent;
import org.jamocha.messagerouter.MessageRouter;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaValue;
import org.jamocha.parser.RuleException;
import org.jamocha.rete.agenda.Agendas;
import org.jamocha.rete.configurations.ModifyConfiguration;
import org.jamocha.rete.configurations.SlotConfiguration;
import org.jamocha.rete.eventhandling.EngineEvent;
import org.jamocha.rete.eventhandling.EngineEventListener;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.exception.ExecuteException;
import org.jamocha.rete.exception.RetractException;
import org.jamocha.rete.functions.FunctionMemory;
import org.jamocha.rete.functions.FunctionMemoryImpl;
import org.jamocha.rete.functions.io.Batch;
import org.jamocha.rete.memory.WorkingMemory;
import org.jamocha.rete.modules.Module;
import org.jamocha.rete.modules.Modules;
import org.jamocha.rete.nodes.BaseNode;
import org.jamocha.rete.nodes.ReteNet;
import org.jamocha.rete.nodes.TerminalNode;
import org.jamocha.rete.util.ProfileStats;
import org.jamocha.rule.Rule;
import org.jamocha.settings.JamochaSettings;
import org.jamocha.settings.SettingsChangedListener;
import org.jamocha.settings.SettingsConstants;

/**
 * @author Peter Lin
 * 
 * This is the main Rete engine class. For now it's called Rete, but I may
 * change it to Engine to be more generic.
 */
public class Rete implements SettingsChangedListener, PropertyChangeListener, CompilerListener, Serializable {

	private static final long serialVersionUID = 1L;

	private String[] interestedProperties = { SettingsConstants.ENGINE_GENERAL_SETTINGS_PROFILE_ASSERT, SettingsConstants.ENGINE_GENERAL_SETTINGS_PROFILE_RETRACT };

	protected ReteNet net = null;

	protected FunctionMemory functionMem = null;

	protected Map<String, PrintWriter> outputStreams = new HashMap<String, PrintWriter>();

	/**
	 * Container for Defglobals
	 */
	protected DefglobalMap defglobals = new DefglobalMap();

	private Scope scopes = new BlockingScope();

	/*
	 * an ArrayList for the listeners
	 */
	protected List<EngineEventListener> listeners = new ArrayList<EngineEventListener>();

	private Agendas agendas = null;

	private Modules modules = null;

	private boolean debug = false;

	private boolean profileAssert = false;

	private boolean profileRetract = false;

	private DefaultLogger log = new DefaultLogger(Rete.class);

	private MessageRouter router = new MessageRouter(this);

	protected InitialFact initFact = new InitialFact();

	private static long ticket = 0;
	
	/**
	 * 
	 */
	public Rete() {
		super();

		this.net = new ReteNet(this);
		this.functionMem = new FunctionMemoryImpl(this);
		this.agendas = new Agendas(this);
		init();
		startLog();
	}

	/**
	 * initialization logic should go here
	 */
	protected void init() {
		modules = new Modules(this);
		functionMem.init();
		declareInitialFact();
		JamochaSettings.getInstance().addListener(this, interestedProperties);
	}

	protected void startLog() {
		log.info("Jamocha started");
	}

	protected void declareInitialFact() {
		try {
			this.addTemplate(initFact);
			Fact ifact = initFact.getInitialFact();
			this.assertFact(ifact);
		} catch (Exception e) {
			// an error should not occur
			log.info(e);
		}
	}

	public InitialFact getInitialTemplate() {
		return this.initFact;
	}

	public Fact getInitialFact() {
		return initFact.getInitialFact();
	}

	// ----- methods for clearing rules and facts ----- //

	/**
	 * Clear the objects from the working memory
	 */
	public void clearObjects() {
		// TODO: we don't support objects
	}

	/**
	 * clear the deffacts from the working memory. This does not include facts
	 * asserted using assertObject.
	 */
	public void clearFacts() {
		this.modules.clearAllFacts();
	}

	public void clearRules() {
		this.modules.clearAllRules();
	}

	/**
	 * clear all objects and deffacts
	 */
	public void clearAll() {
		this.net.clear();
		this.functionMem.clear();
		// now we clear all the rules and templates
		this.agendas.clear();
		ProfileStats.reset();
		this.modules.clearAll();
		declareInitialFact();
	}

	/**
	 * Method will clear the engine of all rules, facts and objects.
	 */
	public void close() {
		this.modules.clearAll();
		this.net.clear();
		this.functionMem.clearBuiltInFunctions();
		this.listeners.clear();
	}

	/**
	 * this is useful for debugging purposes. clips allows the user to fire 1
	 * rule at a time.
	 * 
	 * @param count
	 * @return
	 */
	public int fire(int count) throws ExecuteException {
		return agendas.fireFocus(count);
	}

	/**
	 * this is the normal fire. it will fire all the rules that have matched
	 * completely.
	 * 
	 * @return
	 * @throws ExecuteException
	 */
	public int fire() throws ExecuteException {
		return agendas.fireFocus(-1);
	}

	// ----- defmodule related methods ----- //

	/**
	 * Method returns the current focus. Only the rules in the current focus
	 * will be fired. Activations in other modules will not be fired until the
	 * focus is changed to it.
	 * 
	 * @return
	 */
	public Module getCurrentFocus() {
		return this.modules.getCurrentModule();
	}

	/**
	 * method will create for given name, if it does not exist. The current
	 * module is changed to the new one
	 * 
	 * @param act
	 */
	public Module addModule(String name) {
		return this.modules.addModule(name, true);
	}

	/**
	 * method will return module for given name, if does not exist, a new one
	 * will be created The current module is changed to the new one
	 * 
	 * @param act
	 */
	public Module getModule(String name) {
		return this.modules.getModule(name, true);
	}

	public Module findModule(String name) {
		return this.modules.findModule(name);
	}

	/**
	 * find the template by name
	 * 
	 * @param name
	 * @return Template
	 */
	public Template findTemplate(String name) {
		return this.modules.getCurrentModule().getTemplate(name);
	}

	/**
	 * Users can write query adapters to execute an external query. The method
	 * will find all Query adapters for a given Template. There can be zero or
	 * more adapter registered for a given template. If the template hasn't been
	 * defined, the method return null.
	 * 
	 * @param template
	 * @return
	 */
	public Query findQueryAdapter(Template template) {
		return null;
	}

	public void registerQueryAdapter(Query adapter) {

	}

	/**
	 * Implementation will lookup the defclass for a given object by using the
	 * Class as the key.
	 * 
	 * @param key
	 * @return
	 */
	public Defclass findDefclass(Object key) {
		// TODO: we don't support Classes
		return null;
	}

	/**
	 * pass a filename to load the rules. The implementation uses BatchFunction
	 * to load the file.
	 * 
	 * @param filename
	 * @throws EvaluationException
	 */
	public void loadRuleset(String filename) throws EvaluationException {
		Batch bf = (Batch) this.functionMem.findFunction(Batch.NAME);
		Parameter[] params = new Parameter[] { JamochaValue.newString(filename) };
		bf.executeFunction(this, params);
	}

	/**
	 * load the rules from an inputstream. The implementation uses the Batch
	 * function to load the input.
	 * 
	 * @param ins
	 */
	public void loadRuleset(InputStream ins) {
		Batch bf = (Batch) this.functionMem.findFunction(Batch.NAME);
		try {
			bf.parse(this, ins);
		} catch (EvaluationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void declareDefglobal(String name, Object value) {
		this.defglobals.declareDefglobal(name, value);
	}

	public JamochaValue getDefglobalValue(String name) {
		return (JamochaValue) this.defglobals.getValue(name);
	}

	// -------------- Get / Set methods --------------------- //

	public Map<String, Object> getBindings() {
		Map<String, Object> bindings = new HashMap<String, Object>();
		Map<String, Object> globalBindings = defglobals.getDefglobals();
		Set<String> keys = globalBindings.keySet();
		for (String key : keys) {
			bindings.put(key, globalBindings.get(key));
		}
		Map<String, JamochaValue> scopeBindings = scopes.getBindings();
		keys = scopeBindings.keySet();
		for (String key : keys) {
			bindings.put(key, scopeBindings.get(key));
		}
		return bindings;
	}

	/**
	 * The current implementation will check to see if the variable is a
	 * defglobal. If it is, it will return the value. If not, it will see if
	 * there is an active rule and try to get the local bound value.
	 * 
	 * @param key
	 * @return
	 */
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
	public void setBinding(String key, JamochaValue value) {
		if (key.startsWith("*"))
			this.declareDefglobal(key, value);
		else
			scopes.setBindingValue(key, value);
	}

	/**
	 * set the focus to a different module
	 * 
	 * @param moduleName
	 */
	public boolean setFocus(String moduleName) {
		return this.modules.setCurrentModule(moduleName);
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
			this.agendas.setWatchActivations(true);
			this.modules.setWatchFact(true);
			this.modules.setWatchRules(true);
		} else if (type == Constants.WATCH_ACTIVATIONS) {
			this.agendas.setWatchActivations(true);
		} else if (type == Constants.WATCH_FACTS) {
			this.modules.setWatchFact(true);
		} else if (type == Constants.WATCH_RULES) {
			this.modules.setWatchRules(true);
		}
	}

	/**
	 * Call the method with the type to unwatch activations<br/> facts<br/>
	 * rules<br/>
	 * 
	 * @param type
	 */
	public void setUnWatch(int type) {
		if (type == Constants.WATCH_ALL) {
			this.agendas.setWatchActivations(false);
			this.modules.setWatchFact(false);
			this.modules.setWatchRules(false);
		} else if (type == Constants.WATCH_ACTIVATIONS) {
			this.agendas.setWatchActivations(false);
		} else if (type == Constants.WATCH_FACTS) {
			this.modules.setWatchFact(false);
		} else if (type == Constants.WATCH_RULES) {
			this.modules.setWatchRules(false);
		}
	}

	/**
	 * To turn on profiling, call the method with the appropriate parameter. The
	 * parameters are defined in Rete class as static int values.
	 * 
	 * @param type
	 */
	public void setProfile(int type) {
		if (type == Constants.PROFILE_ALL) {
			this.agendas.setProfileAddActivation(true);
			this.agendas.setProfileRemoveActivation(true);
			this.agendas.setProfileFire(true);
			this.profileAssert = true;
			this.profileRetract = true;
		} else if (type == Constants.PROFILE_ASSERT) {
			this.profileAssert = true;
		} else if (type == Constants.PROFILE_RETRACT) {
			this.profileRetract = true;
		} else if (type == Constants.PROFILE_ADD_ACTIVATION) {
			this.agendas.setProfileAddActivation(true);
		} else if (type == Constants.PROFILE_RM_ACTIVATION) {
			this.agendas.setProfileRemoveActivation(true);
		} else if (type == Constants.PROFILE_FIRE) {
			this.agendas.setProfileFire(true);
		}
	}

	/**
	 * To turn off profiling, call the method with the appropriate parameter.
	 * The parameters are defined in Rete class as static int values.
	 * 
	 * @param type
	 */
	public void setProfileOff(int type) {
		if (type == Constants.PROFILE_ALL) {
			this.agendas.setProfileAddActivation(false);
			this.agendas.setProfileRemoveActivation(false);
			this.agendas.setProfileFire(false);
			this.profileAssert = false;
			this.profileRetract = false;
		} else if (type == Constants.PROFILE_ASSERT) {
			this.profileAssert = false;
		} else if (type == Constants.PROFILE_RETRACT) {
			this.profileRetract = false;
		} else if (type == Constants.PROFILE_ADD_ACTIVATION) {
			this.agendas.setProfileAddActivation(false);
		} else if (type == Constants.PROFILE_RM_ACTIVATION) {
			this.agendas.setProfileRemoveActivation(false);
		} else if (type == Constants.PROFILE_FIRE) {
			this.agendas.setProfileFire(false);
		}
	}

	public Fact getFactById(JamochaValue factID) {
		return getFactById(factID.getFactIdValue());
	}

	/**
	 * changed the implementation so it searches for the fact by id. Starting
	 * with the HashMap for deffact, dynamic facts and finally static facts.
	 * 
	 * @param id
	 * @return
	 */
	public Fact getFactById(long id) {
		return this.modules.getFactById(id);
	}

	// ----- method for adding output streams for spools ----- //
	/**
	 * this method is for adding printwriters for spools. the purpose of the
	 * spool function is to dump everything out to a file.
	 */
	public void addPrintWriter(String name, PrintWriter writer) {
		this.outputStreams.put(name, writer);
	}

	/**
	 * It is up to spool function to make sure it removes the printer writer and
	 * closes it properly.
	 * 
	 * @param name
	 * @return
	 */
	public PrintWriter removePrintWriter(String name) {
		return (PrintWriter) this.outputStreams.remove(name);
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
		router.postMessageEvent(new MessageEvent(MessageEvent.ENGINE, msg, "t".equals(output) ? router.getCurrentChannelId() : output));
		for (PrintWriter wr : outputStreams.values()) {
			wr.write(msg);
			wr.flush();
		}
	}

	/**
	 * The method will print out the node. It is up to the method to check if
	 * pretty printer is true and call the appropriate node method to get the
	 * string. TODO - need to implement this
	 * 
	 * @param node
	 */
	public void writeMessage(BaseNode node) {

	}

	/**
	 * Method will process the retractEvent, preferably with an event queue
	 * 
	 * @param node
	 * @param fact
	 */
	public void assertEvent(BaseNode node, Fact fact) {
		if (debug) {
			System.out.println("\"assert at nodeid=" + node.getNodeId() + " - " + node.toString().replaceAll("\"", "'") + ":: with fact -" + fact.toFactString().replaceAll("\"", "'") + "::\"");
		}
		for (EngineEventListener eel : listeners) {
			eel.eventOccurred(new EngineEvent(this, EngineEvent.ASSERT_EVENT, node, new Fact[] { fact }));
		}
	}

	public void newRuleEvent(Rule rule) {
		if (debug) {
			System.out.println("\"new rule added=" + rule.toString());
		}
		for (EngineEventListener eel : listeners) {
			eel.eventOccurred(new EngineEvent(this, EngineEvent.NEWRULE_EVENT, rule));
		}
	}

	public void assertEvent(BaseNode node, Fact[] facts) {
		if (debug) {
			if (node instanceof TerminalNode) {
				System.out.println(((TerminalNode) node).getRule().getName() + " fired");
			} else {

			}
		}
		Iterator<EngineEventListener> itr = this.listeners.iterator();
		while (itr.hasNext()) {
			EngineEventListener eel = itr.next();
			eel.eventOccurred(new EngineEvent(this, EngineEvent.ASSERT_EVENT, node, facts));
		}
	}

	/**
	 * Method will process the retractEvent, referably using an event queue
	 * 
	 * @param node
	 * @param fact
	 */
	public void retractEvent(BaseNode node, Fact fact) {
		for (EngineEventListener eel : listeners) {
			eel.eventOccurred(new EngineEvent(this, EngineEvent.RETRACT_EVENT, node, new Fact[] { fact }));
		}
	}

	/**
	 * 
	 * @param node
	 * @param facts
	 */
	public void retractEvent(BaseNode node, Fact[] facts) {
		for (EngineEventListener eel : listeners) {
			eel.eventOccurred(new EngineEvent(this, EngineEvent.ASSERT_EVENT, node, facts));
		}
	}

	/**
	 * The current implementation of assertObject is simple, but flexible. This
	 * version is not multi-threaded and doesn't use an event queue. Later on a
	 * multi-threaded version will be written which overrides the base
	 * implementation. If the user passes a specific template name, the engine
	 * will attempt to only propogate the fact down that template. if no
	 * template name is given, the engine will propogate the fact down all input
	 * nodes, including parent templates.
	 * 
	 * @param data
	 * @param template
	 * @param statc
	 * @param shadow
	 * @throws AssertException
	 */
	public void assertObject(Object data, String template, boolean statc, boolean shadow) throws AssertException {
		// TODO: we don't support Classes
	}

	/**
	 * By default assertObjects will assert with shadow and dynamic. It also
	 * assumes the classes aren't using an user defined template name.
	 * 
	 * @param objs
	 * @throws AssertException
	 */
	public void assertObjects(List<?> objs) throws AssertException {
		for (Object obj : objs) {
			assertObject(obj, null, false, false);
		}
	}

	/**
	 * 
	 * @param data
	 */
	public void retractObject(Object data) throws RetractException {
		// TODO: we don't support Classes
	}

	/**
	 * This method is explicitly used to assert facts.
	 * 
	 * @param fact
	 * @param statc -
	 *            if the fact should be static, assert with true
	 */
	public Fact assertFact(Fact fact) throws AssertException {
		if (this.profileAssert) {
			ProfileStats.startAssert();
			this.net.assertObject(fact);
			ProfileStats.endAssert();
		} else {
			this.net.assertObject(fact);
		}
		return fact;
	}
	
	public Fact assertFact(Object o) throws AssertException {
		Template t = getCurrentFocus().getTemplate(o.getClass());
		
		List<Slot> valuesList = new LinkedList<Slot>();
		for (Field f : o.getClass().getFields()) {
			
			try {
				Slot s = t.getSlot(f.getName()).createSlot(this);
				s.value = JamochaValue.newValueAutoType(f.get(o));
				valuesList.add(s);
			} catch (EvaluationException e) {
				throw new AssertException(e);
			} catch (IllegalArgumentException e) {
				throw new AssertException(e);
			} catch (IllegalAccessException e) {
				throw new AssertException(e);
			}
		}
		
		Slot[] values = new Slot[valuesList.size()];
		values = valuesList.toArray(values);
		
		Fact newFact = new Deffact(t,null,values);

		assertFact(newFact);
		
		return newFact;
	}

	/**
	 * retract by fact id is slower than retracting by the deffact instance. the
	 * method will find the fact and then call retractFact(Deffact)
	 * 
	 * @param id
	 */
	public void retractById(long id) throws RetractException {
		Fact ft = this.modules.getFactById(id);
		retractFact(ft);
	}

	/**
	 * Retract a fact directly
	 * 
	 * @param fact
	 * @throws RetractException
	 */
	public void retractFact(Fact fact) throws RetractException {
		this.modules.removeFact(fact);
		if (this.profileRetract) {
			ProfileStats.startRetract();
			this.net.retractObject(fact);
			ProfileStats.endRetract();
		} else {
			this.net.retractObject(fact);
		}
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

	// TODO not really efficient to pre-traverse the whole slot-list just for
	// determining whether we must retract/assert or not
	public void modifyFact(Fact old, ModifyConfiguration mc) throws EvaluationException {
		boolean allSilent = true;

		for (SlotConfiguration slot : mc.getSlots()) {
			allSilent &= old.getSlotSilence(slot.getSlotName());
		}

		if (allSilent) {
			old.updateSlots(this, mc.getSlots());
		} else {
			Fact modifiedFact = ((Deffact) old).cloneFact(this);
			modifiedFact.setFactId(old.getFactId());
			modifiedFact.updateSlots(this, mc.getSlots());
			retractFact(old);
			this.modules.addFact(modifiedFact);
			assertFact(modifiedFact);
		}
	}

	public static long drawTicket() {
		return ticket++;
	}

	/**
	 * Method will call resetObjects first, followed by resetFacts.
	 */
	public void resetAll() {
		resetObjects();
		resetFacts();
	}

	/**
	 * Method will retract the objects and re-assert them. It does not reset the
	 * deffacts.
	 */
	public void resetObjects() {
		// TODO: we don't support Classes
	}

	/**
	 * Method will retract all the deffacts and then re-assert them. Reset does
	 * not reset the objects. To reset both the facts and objects, call
	 * resetAll. resetFacts handles deffacts which are not derived from objects.
	 */
	public void resetFacts() {
		try {
			List<Fact> facts = this.modules.getAllFacts();
			for (Fact ft : facts) {
				this.net.retractObject(ft);
			}
			for (Fact ft : facts) {
				this.net.assertObject(ft);
			}
		} catch (RetractException e) {
			log.debug(e);
		} catch (AssertException e) {
			log.debug(e);
		}
	}

	public Modules getModules() {
		return modules;
	}

	/**
	 * convienance method for creating a Non-Shadow fact.
	 * 
	 * @param data
	 * @param id
	 * @return
	 */
	protected Fact createNSFact(Object data, Defclass dclass, long id) {
		Deftemplate dft = (Deftemplate) getCurrentFocus().getTemplate(dclass.getClassObject().getName());
		NSFact fact = new NSFact(dft, dclass, data, dft.getAllSlots(), id);
		return fact;
	}

	public Agendas getAgendas() {
		return this.agendas;
	}

	public FunctionMemory getFunctionMemory() {
		return this.functionMem;
	}

	public DefaultLogger getLogger() {
		return this.log;
	}

	public void setValidateRules(boolean val) {
		this.net.setValidateRule(val);
	}

	public boolean getValidateRules() {
		return this.net.getValidateRule();
	}

	/**
	 * not implemented yet
	 * 
	 * @param event
	 */
	public void propertyChange(PropertyChangeEvent event) {
		// Object source = event.getSource();
		// try {
		//			
		// } catch (RetractException e) {
		// log.debug(e);
		// } catch (AssertException e) {
		// log.debug(e);
		// }
		// TODO :reimplement it
	}

	/**
	 * Add a listener if it isn't already a listener
	 * 
	 * @param listen
	 */
	public void addEngineEventListener(EngineEventListener listen) {
		if (!this.listeners.contains(listen)) {
			this.listeners.add(listen);
		}
	}

	/**
	 * remove a listener
	 * 
	 * @param listen
	 */
	public void removeEngineEventListener(EngineEventListener listen) {
		this.listeners.remove(listen);
	}

	/**
	 * For now, this is not implemented
	 * 
	 * @param event
	 */
	public void ruleAdded(CompileEvent event) {
		this.log.info("added: " + event.getMessage());
	}

	/**
	 * For now, this is not implemented
	 * 
	 * @param event
	 */
	public void ruleRemoved(CompileEvent event) {
		this.log.info("removed: " + event.getMessage());
	}

	/**
	 * For now, this is not implemented
	 * 
	 * @param event
	 */
	public void compileError(CompileEvent event) {
		this.log.warn(event.getMessage());
	}

	public MessageRouter getMessageRouter() {
		return router;
	}

	public void pushScope() {
		pushScope(new DefaultScope());
	}

	public void popScope() {
		// We don't pop the last scope
		if (scopes.getOuterScope() != null)
			scopes = scopes.popScope();
	}

	public void pushScope(Scope scope) {
		scope.pushScope(scopes);
		scopes = scope;
	}

	public boolean addTemplate(Deftemplate tpl) throws EvaluationException {
		tpl.evaluateStaticDefaults(this);
		Module mod = tpl.checkName(this);
		if (mod == null) {
			mod = getCurrentFocus();
		}
		Boolean result = mod.addTemplate(tpl);
		if (result)
			this.net.addTemplate(tpl);
		return result;
	}

	public boolean addRule(Rule rule) throws AssertException, RuleException {
		boolean result = false;
		if (!getCurrentFocus().containsRule(rule)) {
			result = net.addRule(rule);
		}
		return result;
	}

	public ReteNet getNet() {
		return this.net;
	}

	public static String[] getJamochaSearchPaths() {
		String path = System.getenv("JAMOCHA_PATH");
		if (path != null && path.length() > 0) {
			return path.split(System.getProperty("path.separator"));
		}
		return null;
	}

	public void settingsChanged(String propertyName) {
		JamochaSettings settings = JamochaSettings.getInstance();
		// watch activations
		if (propertyName.equals(SettingsConstants.ENGINE_GENERAL_SETTINGS_PROFILE_ASSERT)) {
			this.profileAssert = (settings.getBoolean(propertyName));
		}
		// profile:
		else if (propertyName.equals(SettingsConstants.ENGINE_GENERAL_SETTINGS_PROFILE_RETRACT)) {
			this.profileRetract = (settings.getBoolean(propertyName));
		}
	}

	/**
	 * this is a conveniance method for getting the network's working
	 * memory
	 * @return
	 */
	public WorkingMemory getWorkingMemory() {
		return net.getWorkingMemory();
	}


}
