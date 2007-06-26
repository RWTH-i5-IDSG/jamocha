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
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jamocha.logging.DefaultLogger;
import org.jamocha.messagerouter.MessageEvent;
import org.jamocha.messagerouter.MessageRouter;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.configurations.ModifyConfiguration;
import org.jamocha.rete.configurations.SlotConfiguration;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.exception.ExecuteException;
import org.jamocha.rete.exception.RetractException;
import org.jamocha.rete.functions.FunctionMemory;
import org.jamocha.rete.functions.FunctionMemoryImpl;
import org.jamocha.rete.functions.io.Batch;
import org.jamocha.rete.nodes.BaseNode;
import org.jamocha.rete.nodes.TerminalNode;
import org.jamocha.rete.strategies.DepthStrategy;
import org.jamocha.rete.util.ProfileStats;
import org.jamocha.rule.Rule;

/**
 * @author Peter Lin
 * 
 * This is the main Rete engine class. For now it's called Rete, but I may
 * change it to Engine to be more generic.
 */
public class Rete implements PropertyChangeListener, CompilerListener,
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final int WATCH_ACTIVATIONS = 001;

	public static final int WATCH_ALL = 002;

	public static final int WATCH_FACTS = 003;

	public static final int WATCH_RULES = 004;

	public static final int PROFILE_ADD_ACTIVATION = 101;

	public static final int PROFILE_ASSERT = 102;

	public static final int PROFILE_ALL = 103;

	public static final int PROFILE_FIRE = 104;

	public static final int PROFILE_RETRACT = 105;

	public static final int PROFILE_RM_ACTIVATION = 106;

	/**
	 * The initial facts the rule engine needs at startup
	 */
	protected ArrayList initialFacts = new ArrayList();

	protected Hashtable contexts = new Hashtable();

	protected ArrayList focusStack = new ArrayList();

	protected boolean halt = true;

	protected int firingcount = 0;

	protected boolean prettyPrint = false;

	protected WorkingMemory workingMem = null;

	protected FunctionMemory functionMem = null;

	/**
	 * the keySystem.out.println(rs.getString(1)); is the Class object. The
	 * value is the defclass. the defclass is then used to lookup the
	 * deftemplate in the current Module.
	 */
	protected Map<Class, Defclass> defclass = new HashMap<Class, Defclass>();

	protected Map<String, Defclass> templateToDefclass = new HashMap<String, Defclass>();

	/**
	 * We keep a map between the object instance and the corresponding shadown
	 * fact. If an object is added as static, it is added to this map. When the
	 * rule engine is notified of changes, it will check this list. If the
	 * object instance is in this list, we ignore it.
	 */
	protected Map<Object, Fact> staticFacts = new HashMap<Object, Fact>();

	/**
	 * We keep a map of the dynamic object instances. When the rule engine is
	 * notified
	 */
	protected Map<Object, Fact> dynamicFacts = new HashMap<Object, Fact>();

	/**
	 * We use a HashMap to make it easy to determine if an existing deffact
	 * already exists in the working memory. this is only used for deffacts and
	 * not for objects
	 */
	protected Map<EqualityIndex, Fact> deffactMap = new HashMap<EqualityIndex, Fact>();

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

	/**
	 * Each engine instance only has 1 agenda
	 */
	private Agenda theAgenda = null;

	private Module currentModule = null;

	/**
	 * this is the main module
	 */
	private Module main = null;

	private Strategy theStrat = null;

	private long lastFactId = 1;

	private int lastNodeId = 0;

	private boolean debug = false;

	private boolean watchFact = false;

	private boolean watchRules = false;

	private boolean profileFire = false;

	private boolean profileAssert = false;

	private boolean profileRetract = false;

	private DefaultLogger log = new DefaultLogger(Rete.class);

	private MessageRouter router = new MessageRouter(this);

	protected Deftemplate initFact = new InitialFact();

	/**
	 * 
	 */
	public Rete() {
		super();
		this.workingMem = new WorkingMemoryImpl(this);
		this.functionMem = new FunctionMemoryImpl(this);
		this.theAgenda = new Agenda(this);
		this.theStrat = new DepthStrategy();
		init();
		startLog();
	}

	/**
	 * initialization logic should go here
	 */
	protected void init() {
		initMain();
		functionMem.init();
		declareInitialFact();
	}

	protected void initMain() {
		this.main = new Defmodule(Constants.MAIN_MODULE, this.theStrat);
		// by default, we set the current module to main
		this.currentModule = this.main;
		this.theAgenda.addModule(this.main);
	}

	protected void startLog() {
		log.info("Jamocha started");
	}

	protected void declareInitialFact() {
		this.declareTemplate(initFact);
		try {
			Deffact ifact = (Deffact) initFact.createFact(null, null, 0, this);
			this.assertFact(ifact);
		} catch (AssertException e) {
			// an error should not occur
			log.info(e);
		} catch (EvaluationException e) {
			log.info(e);
		}
	}

	// ----- methods for clearing rules and facts ----- //

	/**
	 * Clear the objects from the working memory
	 */
	public void clearObjects() {
		if (dynamicFacts.size() > 0) {
			try {
				Iterator itr = dynamicFacts.keySet().iterator();
				while (itr.hasNext()) {
					Object obj = itr.next();
					if (!(obj instanceof Deffact)) {
						retractObject(obj);
					}
				}
			} catch (RetractException e) {
				log.debug(e);
			}
		}
		if (staticFacts.size() > 0) {
			try {
				Iterator itr = staticFacts.keySet().iterator();
				while (itr.hasNext()) {
					Object obj = itr.next();
					if (!(obj instanceof Deffact)) {
						retractObject(obj);
					}
				}
			} catch (RetractException e) {
				log.debug(e);
			}
		}
	}

	/**
	 * clear the deffacts from the working memory. This does not include facts
	 * asserted using assertObject.
	 */
	public void clearFacts() {
		if (dynamicFacts.size() > 0) {
			try {
				Iterator itr = dynamicFacts.keySet().iterator();
				while (itr.hasNext()) {
					Object obj = itr.next();
					if (obj instanceof Fact) {
						this.workingMem.retractObject((Fact) obj);
					}
				}
				this.dynamicFacts.clear();
			} catch (RetractException e) {
				log.debug(e);
			}
		}
		if (staticFacts.size() > 0) {
			try {
				Iterator itr = staticFacts.keySet().iterator();
				while (itr.hasNext()) {
					Object obj = itr.next();
					if (obj instanceof Fact) {
						this.workingMem.retractObject((Fact) obj);
					}
				}
				this.staticFacts.clear();
			} catch (RetractException e) {
				log.debug(e);
			}
		}
	}

	/**
	 * clear all objects and deffacts
	 */
	public void clearAll() {
		this.dynamicFacts.clear();
		this.staticFacts.clear();
		this.deffactMap.clear();
		this.workingMem.clear();
		this.functionMem.clear();
		// now we clear all the rules and templates
		this.theAgenda.clear();
		this.defclass.clear();
		ProfileStats.reset();
		this.lastFactId = 1;
		this.lastNodeId = 1;
		this.main.clear();
		this.currentModule.clear();
		this.theAgenda.addModule(this.main);
		declareInitialFact();
	}

	/**
	 * Method will clear the engine of all rules, facts and objects.
	 */
	public void close() {
		this.workingMem.clear();
		this.contexts.clear();
		this.defclass.clear();
		this.deffactMap.clear();
		this.dynamicFacts.clear();
		this.focusStack.clear();
		this.functionMem.clearBuiltInFunctions();
		this.initialFacts.clear();
		this.listeners.clear();
		this.staticFacts.clear();
	}

	/**
	 * this is useful for debugging purposes. clips allows the user to fire 1
	 * rule at a time.
	 * 
	 * @param count
	 * @return
	 */
	public int fire(int count) throws ExecuteException {
		int counter = 0;
		if (this.currentModule.getActivationCount() > 0) {
			Activation actv = null;
			if (profileFire) {
				ProfileStats.startFire();
			}
			while ((actv = this.currentModule.nextActivation(this)) != null
					&& counter < count) {
				try {
					if (watchRules) {
						this.writeMessage("==> fire: " + actv.toPPString()
								+ "\r\n", "t");
					}
					pushScope(actv.getRule());
					try {
						actv.executeActivation(this);
						actv.clear();
					} finally {
						popScope();
					}
					counter++;
				} catch (ExecuteException e) {
					// we need to report the exception
					log.debug(e);
					// we break out of the for loop
					break;
				}
			}
			if (profileFire) {
				ProfileStats.endFire();
			}
		}
		return counter;
	}

	/**
	 * this is the normal fire. it will fire all the rules that have matched
	 * completely.
	 * 
	 * @return
	 * @throws ExecuteException
	 */
	public int fire() throws ExecuteException {
		if (this.currentModule.getActivationCount() > 0) {
			// we reset the rules fire count
			this.firingcount = 0;
			Activation actv = null;
			if (profileFire) {
				ProfileStats.startFire();
			}
			while ((actv = this.currentModule.nextActivation(this)) != null) {
				this.firingcount++;
				try {
					if (watchRules) {
						this.writeMessage("==> fire: " + actv.toPPString()
								+ "\r\n", "t");
					}
					// we set the active rule, this means only one rule
					// can be active at a time.
					pushScope(actv.getRule());
					try {
						actv.executeActivation(this);
						actv.clear();
					} finally {
						popScope();
					}
				} catch (ExecuteException e) {
					log.debug(e);
					throw e;
				}
			}
			if (profileFire) {
				ProfileStats.endFire();
			}
			return this.firingcount;
		} else {
			return 0;
		}
	}

	/**
	 * method is used to fire an activation immediately
	 * 
	 * @param act
	 */
	protected void fireActivation(Activation act) {
		if (act != null) {
			this.firingcount++;
			try {
				pushScope(act.getRule());
				try {
					act.executeActivation(this);
					act.clear();
				} finally {
					popScope();
				}
			} catch (ExecuteException e) {
				log.debug(e);
			}
		}
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
		return this.currentModule;
	}

	public boolean addModule(String name) {
		if (findModule(name) == null) {
			Defmodule mod = new Defmodule(name, theStrat);
			this.currentModule = mod;
			this.theAgenda.addModule(mod);
			return true;
		} else {
			return false;
		}
	}

	public Module addModule(String name, boolean setfocus) {
		if (findModule(name) == null) {
			Defmodule mod = new Defmodule(name, theStrat);
			if (setfocus) {
				this.currentModule = mod;
			}
			this.theAgenda.addModule(mod);
			return mod;
		} else {
			return findModule(name);
		}
	}

	public Module removeModule(String name) {
		return this.theAgenda.removeModule(name);
	}

	public Module findModule(String name) {
		return this.theAgenda.findModule(name);
	}

	/**
	 * Method will look up the Template using the class
	 * 
	 * @param clazz
	 * @return
	 */
	public Template findTemplate(Class clazz) {
		Object templ = this.defclass.get(clazz);
		if (templ != null) {
			return (Template) templ;
		} else {
			return null;
		}
	}

	/**
	 * find the template starting with other modules and ending with the main
	 * module.
	 * 
	 * @param name
	 * @return
	 */
	public Template findTemplate(String name) {
		Template tmpl = null;
		Iterator itr = this.theAgenda.modules.values().iterator();
		while (itr.hasNext()) {
			Object val = itr.next();
			if (val != this.main) {
				tmpl = ((Defmodule) val).getTemplate(name);
			}
			if (tmpl != null) {
				break;
			}
		}
		// if it wasn't found in any other module, check main
		if (tmpl == null) {
			tmpl = this.main.getTemplate(name);
		}
		return tmpl;
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

	public void declareObject(String className, String templateName,
			String parent) throws ClassNotFoundException {
		try {
			Class clzz = Class.forName(className);
			declareObject(clzz, templateName, parent);
		} catch (ClassNotFoundException e) {
			// for now do nothing, but we should report the error for real
			log.debug(e);
			throw e;
		}
	}

	/**
	 * @param obj
	 * @param templateName
	 * @param parent -
	 *            the parent template
	 */
	public void declareObject(Class obj, String templateName, String parent) {
		// if the class hasn't already been declared, we create a defclass
		// and deftemplate for the class.
		if (!this.defclass.containsKey(obj)) {
			Defclass dclass = new Defclass(obj);
			this.defclass.put(obj, dclass);
			if (templateName == null) {
				templateName = obj.getName();
			}
			this.templateToDefclass.put(templateName, dclass);
			if (!getCurrentFocus().containsTemplate(dclass)) {
				Template dtemp = null;
				// if the parent is found, we set it
				if (parent != null) {
					Template ptemp = this.currentModule
							.findParentTemplate(parent);
					if (ptemp != null) {
						dtemp = dclass.createDeftemplate(templateName, ptemp);
						dtemp.setParent(ptemp);
					} else {
						// we need to throw an exception to let users know the
						// parent template wasn't found
					}
				} else {
					dtemp = dclass.createDeftemplate(templateName);
				}
				// the key for the deftemplate is the declass, this means
				// that when we assert an object instance to the engine,
				// we need to use the Class to lookup defclass and then
				// use the defclass to lookup the deftemplate. Once we
				// have the deftemplate, we can use it to create the shadow
				// fact for the object instance.
				getCurrentFocus().addTemplate(dtemp, this, this.workingMem);
				writeMessage(dtemp.getName(), "t");
			}
		}
	}

	/**
	 * Return a Set of the declass instances
	 * 
	 * @return
	 */
	public Set getDefclasses() {
		return this.defclass.entrySet();
	}

	/**
	 * Implementation will lookup the defclass for a given object by using the
	 * Class as the key.
	 * 
	 * @param key
	 * @return
	 */
	public Defclass findDefclass(Object key) {
		return (Defclass) this.defclass.get(key.getClass());
	}

	/**
	 * method is specifically for templates that are declared in the shell and
	 * do not have a corresponding java class.
	 * 
	 * @param temp
	 */
	public void declareTemplate(Template temp) {
		// The check if the Template exists is done in the addTemplate function
		getCurrentFocus().addTemplate(temp, this, this.workingMem);
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
		Map<String,JamochaValue> scopeBindings = scopes.getBindings();
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
	public void setFocus(String moduleName) {
		if (this.theAgenda.findModule(moduleName) != null) {
			this.currentModule = this.theAgenda.findModule(moduleName);
		}
	}

	/**
	 * Rete class contains a list of items that can be watched. Call the method
	 * with one of the four types:<br/> activations<br/> all<br/> facts<br/>
	 * rules<br/>
	 * 
	 * @param type
	 */
	public void setWatch(int type) {
		if (type == WATCH_ACTIVATIONS) {
			this.theAgenda.setWatch(true);
		} else if (type == WATCH_ALL) {
			this.theAgenda.setWatch(true);
			this.watchFact = true;
			this.watchRules = true;
		} else if (type == WATCH_FACTS) {
			this.watchFact = true;
		} else if (type == WATCH_RULES) {
			this.watchRules = true;
		}
	}

	/**
	 * Call the method with the type to unwatch activations<br/> facts<br/>
	 * rules<br/>
	 * 
	 * @param type
	 */
	public void setUnWatch(int type) {
		if (type == WATCH_ACTIVATIONS) {
			this.theAgenda.setWatch(false);
		} else if (type == WATCH_ALL) {
			this.theAgenda.setWatch(false);
			this.watchFact = false;
			this.watchRules = false;
		} else if (type == WATCH_FACTS) {
			this.watchFact = false;
		} else if (type == WATCH_RULES) {
			this.watchRules = false;
		}
	}

	/**
	 * To turn on profiling, call the method with the appropriate parameter. The
	 * parameters are defined in Rete class as static int values.
	 * 
	 * @param type
	 */
	public void setProfile(int type) {
		if (type == PROFILE_ADD_ACTIVATION) {
			this.theAgenda.setProfileAdd(true);
		} else if (type == PROFILE_ASSERT) {
			this.profileAssert = true;
		} else if (type == PROFILE_ALL) {
			this.theAgenda.setProfileAdd(true);
			this.profileAssert = true;
			this.profileFire = true;
			this.profileRetract = true;
			this.theAgenda.setProfileRemove(true);
		} else if (type == PROFILE_FIRE) {
			this.profileFire = true;
		} else if (type == PROFILE_RETRACT) {
			this.profileRetract = true;
		} else if (type == PROFILE_RM_ACTIVATION) {
			this.theAgenda.setProfileRemove(true);
		}
	}

	/**
	 * To turn off profiling, call the method with the appropriate parameter.
	 * The parameters are defined in Rete class as static int values.
	 * 
	 * @param type
	 */
	public void setProfileOff(int type) {
		if (type == PROFILE_ADD_ACTIVATION) {
			this.theAgenda.setProfileAdd(false);
		} else if (type == PROFILE_ASSERT) {
			this.profileAssert = false;
		} else if (type == PROFILE_ALL) {
			this.theAgenda.setProfileAdd(false);
			this.profileAssert = false;
			this.profileFire = false;
			this.profileRetract = false;
			this.theAgenda.setProfileRemove(false);
		} else if (type == PROFILE_FIRE) {
			this.profileFire = false;
		} else if (type == PROFILE_RETRACT) {
			this.profileRetract = false;
		} else if (type == PROFILE_RM_ACTIVATION) {
			this.theAgenda.setProfileRemove(false);
		}
	}

	/**
	 * return a list of all the facts including deffacts and shadow of objects
	 * 
	 * @return
	 */
	public List<Object> getAllFacts() {
		List<Object> facts = new ArrayList<Object>();
		facts.addAll(this.getObjects());
		facts.addAll(this.getDeffacts());
		return facts;
	}

	/**
	 * Return a list of the objects asserted in the working memory
	 * 
	 * @return
	 */
	public List<Object> getObjects() {
		List<Object> objects = new ArrayList<Object>();
		Iterator itr = this.dynamicFacts.keySet().iterator();
		while (itr.hasNext()) {
			Object key = itr.next();
			if (!(key instanceof Fact)) {
				objects.add(key);
			}
		}
		itr = this.staticFacts.keySet().iterator();
		while (itr.hasNext()) {
			Object key = itr.next();
			if (!(key instanceof Fact)) {
				objects.add(key);
			}
		}
		return objects;
	}

	/**
	 * Return a list of all facts which are not shadows of Objects.
	 * 
	 * @return
	 */
	public List<Fact> getDeffacts() {
		List<Fact> objects = new ArrayList<Fact>();
		Iterator<Fact> itr = this.deffactMap.values().iterator();
		while (itr.hasNext()) {
			Fact fact = itr.next();
			objects.add(fact);
		}
		return objects;
	}

	/**
	 * return just the number of deffacts
	 * 
	 * @return
	 */
	public int getDeffactCount() {
		return this.deffactMap.size();
	}

	/**
	 * get the shadow for the object
	 * 
	 * @param key
	 * @return
	 */
	public Fact getShadowFact(Object key) {
		Fact f = (Fact) this.dynamicFacts.get(key);
		if (f == null) {
			f = (Fact) this.staticFacts.get(key);
		}
		return f;
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
		Fact df = null;
		Iterator itr = this.deffactMap.values().iterator();
		while (itr.hasNext()) {
			df = (Deffact) itr.next();
			if (df.getFactId() == id) {
				return df;
			}
		}
		// now search the object facts
		if (df == null) {
			// check dynamic facts
			Iterator itr2 = this.dynamicFacts.values().iterator();
			while (itr2.hasNext()) {
				df = (Fact) itr2.next();
				if (df.getFactId() == id) {
					return df;
				}
			}
			if (df == null) {
				itr2 = this.staticFacts.values().iterator();
				while (itr2.hasNext()) {
					df = (Fact) itr2.next();
					if (df.getFactId() == id) {
						return df;
					}
				}
			}
		}
		return null;
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
		router.postMessageEvent(new MessageEvent(MessageEvent.ENGINE, msg, "t"
				.equals(output) ? router.getCurrentChannelId() : output));
		if (this.outputStreams.size() > 0) {
			Iterator itr = this.outputStreams.values().iterator();
			while (itr.hasNext()) {
				PrintWriter wr = (PrintWriter) itr.next();
				wr.write(msg);
				wr.flush();
			}
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
			System.out.println("\"assert at nodeid=" + node.getNodeId() + " - "
					+ node.toString().replaceAll("\"", "'") + ":: with fact -"
					+ fact.toFactString().replaceAll("\"", "'") + "::\"");
		}
		Iterator itr = this.listeners.iterator();
		while (itr.hasNext()) {
			EngineEventListener eel = (EngineEventListener) itr.next();
			eel.eventOccurred(new EngineEvent(this, EngineEvent.ASSERT_EVENT,
					node, new Fact[] { fact }));
		}
	}

	public void newRuleEvent(Rule rule) {
		if (debug) {
			System.out.println("\"new rule added=" + rule.toString());
		}
		Iterator itr = this.listeners.iterator();
		while (itr.hasNext()) {
			EngineEventListener eel = (EngineEventListener) itr.next();
			eel.eventOccurred(new EngineEvent(this, EngineEvent.NEWRULE_EVENT,
					rule));
		}
	}

	public void assertEvent(BaseNode node, Fact[] facts) {
		if (debug) {
			if (node instanceof TerminalNode) {
				System.out.println(((TerminalNode) node).getRule().getName()
						+ " fired");
			} else {

			}
		}
		Iterator itr = this.listeners.iterator();
		while (itr.hasNext()) {
			EngineEventListener eel = (EngineEventListener) itr.next();
			eel.eventOccurred(new EngineEvent(this, EngineEvent.ASSERT_EVENT,
					node, facts));
		}
	}

	/**
	 * Method will process the retractEvent, referably using an event queue
	 * 
	 * @param node
	 * @param fact
	 */
	public void retractEvent(BaseNode node, Fact fact) {
		Iterator itr = this.listeners.iterator();
		while (itr.hasNext()) {
			EngineEventListener eel = (EngineEventListener) itr.next();
			eel.eventOccurred(new EngineEvent(this, EngineEvent.RETRACT_EVENT,
					node, new Fact[] { fact }));
		}
	}

	/**
	 * 
	 * @param node
	 * @param facts
	 */
	public void retractEvent(BaseNode node, Fact[] facts) {
		Iterator itr = this.listeners.iterator();
		while (itr.hasNext()) {
			EngineEventListener eel = (EngineEventListener) itr.next();
			eel.eventOccurred(new EngineEvent(this, EngineEvent.ASSERT_EVENT,
					node, facts));
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
	public void assertObject(Object data, String template, boolean statc,
			boolean shadow) throws AssertException {
		Defclass dc = null;
		if (template == null) {
			dc = (Defclass) this.defclass.get(data.getClass());
		} else {
			dc = (Defclass) this.templateToDefclass.get(template);
		}
		if (dc != null) {
			if (statc && !this.staticFacts.containsKey(data)) {
				Fact shadowfact = createFact(data, dc, template, nextFactId());
				// add it to the static fact map
				this.staticFacts.put(data, shadowfact);
				this.workingMem.assertObject(shadowfact);
			} else if (!this.dynamicFacts.containsKey(data)) {
				if (shadow) {
					// first add the rule engine as a listener
					if (dc.isJavaBean()) {
						try {
							dc.getAddListenerMethod().invoke(data,
									new Object[] { this });
						} catch (InvocationTargetException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						}
					}
					// second, lookup the deftemplate and create the
					// shadow fact
					Fact shadowfact = createFact(data, dc, template,
							nextFactId());
					// add it to the dynamic fact map
					this.dynamicFacts.put(data, shadowfact);
					this.workingMem.assertObject(shadowfact);
				} else {
					Fact nsfact = createNSFact(data, dc, nextFactId());
					this.dynamicFacts.put(data, nsfact);
					this.workingMem.assertObject(nsfact);
				}
			}
		}
	}

	/**
	 * By default assertObjects will assert with shadow and dynamic. It also
	 * assumes the classes aren't using an user defined template name.
	 * 
	 * @param objs
	 * @throws AssertException
	 */
	public void assertObjects(List objs) throws AssertException {
		Iterator itr = objs.iterator();
		while (itr.hasNext()) {
			assertObject(itr.next(), null, false, false);
		}
	}

	/**
	 * 
	 * @param data
	 */
	public void retractObject(Object data) throws RetractException {
		if (this.staticFacts.containsKey(data)) {
			Fact ft = (Fact) this.staticFacts.get(data);
			this.workingMem.retractObject(ft);
			this.staticFacts.remove(data);
			// we should probably recyle the factId before we
			// clean the fact
			ft.clear();
		} else if (this.dynamicFacts.containsKey(data)) {
			Fact ft = (Fact) this.dynamicFacts.get(data);
			this.workingMem.retractObject(ft);
			this.dynamicFacts.remove(data);
			// we should probably recyle the factId before we
			// clean the fact
			ft.clear();
		}
	}

	/**
	 * Modify will call retract with the old fact, followed by updating the fact
	 * instance and asserting the fact.
	 * 
	 * @param data
	 */
	public void modifyObject(Object data) throws AssertException,
			RetractException {
		if (this.dynamicFacts.containsKey(data)) {
			Defclass dc = (Defclass) this.defclass.get(data);
			// first we retract the fact
			Fact ft = (Fact) this.dynamicFacts.get(data);
			String tname = ft.getTemplate().getName();
			long fid = ft.getFactId();
			this.workingMem.retractObject(ft);
			this.dynamicFacts.remove(data);
			// create a new fact with the same ID
			ft = createFact(data, dc, tname, fid);
			this.dynamicFacts.put(data, ft);
			this.workingMem.assertObject(ft);
		}

	}

	/**
	 * This method is explicitly used to assert facts.
	 * 
	 * @param fact
	 * @param statc -
	 *            if the fact should be static, assert with true
	 */
	public Fact assertFact(Fact fact) throws AssertException {
		// we need to check if there's already a fact with the
		// same values
		Fact oldFact = getFact(fact);
		if (oldFact == null) {
			long factID = fact.getFactId();
			if (factID == -1 || this.getFactById(factID) != null)
				fact.setFactId(this.nextFactId());
			this.deffactMap.put(fact.equalityIndex(), fact);
			if (this.profileAssert) {
				this.assertFactWProfile(fact);
			} else {
				if (watchFact) {
					this.writeMessage("==> " + fact.toFactString()
							+ Constants.LINEBREAK, "t");
				}
				this.workingMem.assertObject(fact);
			}
			return fact;
		} else {
			return oldFact;
		}
	}

	public Fact getFact(Fact fact) {
		Fact result = (Fact) this.deffactMap.get(((Deffact) fact)
				.equalityIndex());
		return result;
	}

	/**
	 * Assert with profiling will use the ProfileStats class to measure the time
	 * to assert facts
	 * 
	 * @param fact
	 * @throws AssertException
	 */
	protected void assertFactWProfile(Fact fact) throws AssertException {
		ProfileStats.startAssert();
		this.workingMem.assertObject(fact);
		ProfileStats.endAssert();
	}

	/**
	 * retract by fact id is slower than retracting by the deffact instance. the
	 * method will find the fact and then call retractFact(Deffact)
	 * 
	 * @param id
	 */
	public void retractById(long id) throws RetractException {
		Iterator itr = this.deffactMap.values().iterator();
		Deffact ft = null;
		while (itr.hasNext()) {
			Deffact f = (Deffact) itr.next();
			if (f.getFactId() == id) {
				ft = f;
				break;
			}
		}
		if (ft != null) {
			retractFact(ft);
		}
	}

	/**
	 * Retract a fact directly
	 * 
	 * @param fact
	 * @throws RetractException
	 */
	public void retractFact(Fact fact) throws RetractException {
		this.deffactMap.remove(fact.equalityIndex());
		if (this.profileRetract) {
			this.retractFactWProfile(fact);
		} else {
			if (watchFact) {
				this.writeMessage("<== " + fact.toFactString()
						+ Constants.LINEBREAK, "t");
			}
			this.workingMem.retractObject(fact);
		}
	}

	/**
	 * 
	 * @param fact
	 * @throws RetractException
	 */
	protected void retractFactWProfile(Fact fact) throws RetractException {
		ProfileStats.startRetract();
		this.workingMem.retractObject(fact);
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

	// TODO not really efficient to pre-traverse the whole slot-list just for
	// determining whether we must retract/assert or not
	public void modifyFact(Fact old, ModifyConfiguration mc)
			throws EvaluationException {
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
			assertFact(modifiedFact);
		}
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
		try {
			Iterator itr = this.staticFacts.values().iterator();
			while (itr.hasNext()) {
				Deffact ft = (Deffact) itr.next();
				this.workingMem.retractObject(ft);
			}
			itr = this.dynamicFacts.values().iterator();
			while (itr.hasNext()) {
				Deffact ft = (Deffact) itr.next();
				this.workingMem.retractObject(ft);
			}
			// now assert
			itr = this.staticFacts.values().iterator();
			while (itr.hasNext()) {
				Deffact ft = (Deffact) itr.next();
				this.workingMem.assertObject(ft);
			}
			itr = this.dynamicFacts.values().iterator();
			while (itr.hasNext()) {
				Deffact ft = (Deffact) itr.next();
				this.workingMem.assertObject(ft);
			}
		} catch (RetractException e) {
			log.debug(e);
		} catch (AssertException e) {
			log.debug(e);
		}
	}

	/**
	 * Method will retract all the deffacts and then re-assert them. Reset does
	 * not reset the objects. To reset both the facts and objects, call
	 * resetAll. resetFacts handles deffacts which are not derived from objects.
	 */
	public void resetFacts() {
		try {
			Iterator itr = this.deffactMap.values().iterator();
			while (itr.hasNext()) {
				Deffact ft = (Deffact) itr.next();
				this.workingMem.retractObject(ft);
			}
			itr = this.deffactMap.values().iterator();
			while (itr.hasNext()) {
				Deffact ft = (Deffact) itr.next();
				this.workingMem.assertObject(ft);
			}
		} catch (RetractException e) {
			log.debug(e);
		} catch (AssertException e) {
			log.debug(e);
		}
	}

	/**
	 * The implementation will look in the current module in focus. If it isn't
	 * found, it will search the other modules. The last module it checks should
	 * be the main module.
	 * 
	 * @param data
	 * @param id
	 * @return
	 */
	protected Fact createFact(Object data, Defclass dclass, String template,
			long id) throws AssertException {
		Fact ft = null;
		Template dft = null;
		if (template == null) {
			dft = getCurrentFocus().getTemplate(
					dclass.getClassObject().getName());
		} else {
			dft = getCurrentFocus().getTemplate(template);
		}
		// if the deftemplate is null, check the other modules
		if (dft == null) {
			// get the entry set from the agenda and iterate
			Iterator itr = this.theAgenda.getModules().iterator();
			while (itr.hasNext()) {
				Module mod = (Module) itr.next();
				if (mod.containsTemplate(dclass)) {
					dft = mod.getTemplate(dclass);
				}
			}
			// we've searched every module, so now check main
			if (dft == null && this.main.containsTemplate(dclass)) {
				dft = this.main.getTemplate(dclass);
			} else {
				// throw an exception
				throw new AssertException("Could not find the template");
			}
		}
		try {
			ft = ((Deftemplate) dft).createFact(data, dclass, id, this);
		} catch (EvaluationException e) {
			throw new AssertException(e);
		}
		return ft;
	}

	/**
	 * convienance method for creating a Non-Shadow fact.
	 * 
	 * @param data
	 * @param id
	 * @return
	 */
	protected Fact createNSFact(Object data, Defclass dclass, long id) {
		Deftemplate dft = (Deftemplate) getCurrentFocus().getTemplate(dclass);
		NSFact fact = new NSFact(dft, dclass, data, dft.getAllSlots(), id);
		return fact;
	}

	/**
	 * This is temporary, it should be replaced with something like the current
	 * factHandleFactory().newFactHandle()
	 * 
	 * @return
	 */
	public long nextFactId() {
		return this.lastFactId++;
	}

	public Agenda getAgenda() {
		return this.theAgenda;
	}

	/**
	 * return the next rete node id for a new node
	 * 
	 * @return
	 */
	public int nextNodeId() {
		return ++this.lastNodeId;
	}

	/**
	 * peak at the next node id. Do not use this method to get an id for the
	 * next node. only nextNodeId() should be used to create new rete nodes.
	 * 
	 * @return
	 */
	public int peakNextNodeId() {
		return this.lastNodeId + 1;
	}

	public RuleCompiler getRuleCompiler() {
		return this.workingMem.getRuleCompiler();
	}

	public FunctionMemory getFunctionMemory() {
		return this.functionMem;
	}

	public WorkingMemory getWorkingMemory() {
		return this.workingMem;
	}

	public DefaultLogger getLogger() {
		return this.log;
	}

	public Strategy getStrategy() {
		return this.theStrat;
	}

	public ActivationList getActivationList() {
		return this.currentModule.getAllActivations();
	}

	public int getObjectCount() {
		return this.dynamicFacts.size() + this.staticFacts.size();
	}

	public void setValidateRules(boolean val) {
		this.workingMem.getRuleCompiler().setValidateRule(val);
	}

	public boolean getValidateRules() {
		return this.workingMem.getRuleCompiler().getValidateRule();
	}

	/**
	 * not implemented yet
	 * 
	 * @param event
	 */
	public void propertyChange(PropertyChangeEvent event) {
		Object source = event.getSource();
		try {
			this.modifyObject(source);
		} catch (RetractException e) {
			log.debug(e);
		} catch (AssertException e) {
			log.debug(e);
		}
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
		return mod.addTemplate(tpl, this, getWorkingMemory());
	}
}
