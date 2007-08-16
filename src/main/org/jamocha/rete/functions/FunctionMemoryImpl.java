/*
 * Copyright 2007 Sebastian Reinartz
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
package org.jamocha.rete.functions;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jamocha.rete.Function;
import org.jamocha.rete.Rete;
import org.jamocha.rete.functions.adaptor.AdaptorFunctions;
import org.jamocha.rete.functions.compare.CompareFunctions;
import org.jamocha.rete.functions.datetime.DateTimeFunctions;
import org.jamocha.rete.functions.help.HelpFunctions;
import org.jamocha.rete.functions.io.IOFunctions;
import org.jamocha.rete.functions.java.JavaFunctions;
import org.jamocha.rete.functions.list.ListFunctions;
import org.jamocha.rete.functions.math.MathFunctions;
import org.jamocha.rete.functions.ruleengine.RuleEngineFunctions;
import org.jamocha.rete.functions.strings.StringFunctions;

/**
 * @author Sebastian Reinartz
 * 
 * FunctionMemoryImpl is a basic implementation of the FunctionMemory interface.
 * Holds Maps of functions and function groups.
 */
public class FunctionMemoryImpl implements FunctionMemory {

	private static final long serialVersionUID = 1L;

	protected Rete engine = null;

	/**
	 * this is the HashMap for all functions. This means all function names are
	 * unique.
	 */
	protected Map<String, Function> functions = new HashMap<String, Function>();

	/**
	 * This Map holds aliases for the functions that have them. This is needed
	 * because otherwise we can't differentiate between the original function
	 * and the aliases.
	 */
	protected Map<String, Function> aliases = new HashMap<String, Function>();

	protected final String UNDEFINED_GROUP_NAME = "UNDEFINED";

	private Map<String, FunctionGroup> functionGroups = new HashMap<String, FunctionGroup>();

	public FunctionMemoryImpl(Rete engine) {
		super();
		this.engine = engine;
	}

	public void init() {
		loadBuiltInFunctions();
	}

	public void clear() {
		this.clearBuiltInFunctions();
		this.loadBuiltInFunctions();
	}

	public Function findFunction(String name) {
		Function result = this.functions.get(name);
		if(result == null)
			result = this.aliases.get(name);
		return result;
	}

	/**
	 * To explicitly deploy a custom function, call the method with an instance
	 * of the function
	 * 
	 * @param func
	 */
	public void declareFunction(Function func) {
		this.functions.put(func.getName(), func);
		if (func instanceof InterpretedFunction) {
			this.declareFunctionInDefaultGroup(func);
		}
	}

	public void declareFunction(Function func, String functionGroupName) {
		this.functions.put(func.getName(), func);
		if (func instanceof InterpretedFunction)
			this.declareFunctionInGroup(func, functionGroupName);
	}

	/**
	 * In some cases, we may want to declare a function under an alias. For
	 * example, Add can be alias as "+".
	 * 
	 * @param alias
	 * @param func
	 */
	public void declareFunction(String alias, Function func) {
		this.functions.put(alias, func);
		if (func instanceof InterpretedFunction)
			declareFunctionInDefaultGroup(func);
	}

	/**
	 * Method will create an instance of the function and declare it. Once a
	 * function is declared, it can be used. All custom functions must be
	 * declared before they can be used.
	 * 
	 * @param name
	 */
	@SuppressWarnings("unchecked")
	public void declareFunction(String name) throws ClassNotFoundException {
		try {
			Class fclaz = Class.forName(name);
			Function func = (Function) fclaz.newInstance();
			declareFunction(func);
		} catch (ClassNotFoundException e) {
			engine.getLogger().debug(e);
			throw e;
		} catch (IllegalAccessException e) {
			engine.getLogger().debug(e);
		} catch (InstantiationException e) {
			engine.getLogger().debug(e);
		}
	}

	/**
	 * Method will create in instance of the FunctionGroup class and load the
	 * functions.
	 * 
	 * @param name
	 */
	@SuppressWarnings("unchecked")
	public void declareFunctionGroup(String name) {
		try {
			Class fclaz = Class.forName(name);
			FunctionGroup group = (FunctionGroup) fclaz.newInstance();
			declareFunctionGroup(group);
		} catch (ClassNotFoundException e) {
			engine.getLogger().debug(e);
		} catch (IllegalAccessException e) {
			engine.getLogger().debug(e);
		} catch (InstantiationException e) {
			engine.getLogger().debug(e);
		}
	}

	/**
	 * Method will register the function of the FunctionGroup .
	 * 
	 * @param functionGroup
	 *            FunctionGroup with the functions to register.
	 */
	public void declareFunctionGroup(FunctionGroup functionGroup) {
		functionGroups.put(functionGroup.getName(), functionGroup);
		functionGroup.loadFunctions(this);
	}

	protected void loadBuiltInFunctions() {
		// load the IO functions
		declareFunctionGroup(new IOFunctions());

		// load the math functions
		declareFunctionGroup(new MathFunctions());

		// load the Compare functions
		declareFunctionGroup(new CompareFunctions());

		// load the date/time functions
		declareFunctionGroup(new DateTimeFunctions());

		// load the list functions
		declareFunctionGroup(new ListFunctions());

		// load the database functions
		declareFunctionGroup(new AdaptorFunctions());

		// load the engine relate functions like declaring rules, templates, etc
		declareFunctionGroup(new RuleEngineFunctions());

		// load string functions
		declareFunctionGroup(new StringFunctions());

		// load java functions
		declareFunctionGroup(new JavaFunctions());

		// load java functions
		declareFunctionGroup(new HelpFunctions());

		// Other builtin constructs
		declareFunction(new If());
		declareFunction(new LoopForCount());
		declareFunction(new Return());
		declareFunction(new While());
	}

	public void clearBuiltInFunctions() {
		this.functions.clear();
		this.aliases.clear();
	}

	/**
	 * Returns a list of the function groups. If a function is not in a group,
	 * get the complete list of functions using getAllFunctions instead.
	 * 
	 * @return
	 */
	public Map<String, FunctionGroup> getFunctionGroups() {
		return this.functionGroups;
	}

	/**
	 * Returns a collection of the function instances
	 * 
	 * @return
	 */
	public Collection<Function> getAllFunctions() {
		return this.functions.values();
	}

	public List<Function> getFunctionsOfGroup(String name) {
		FunctionGroup group = this.functionGroups.get(name);
		if (group != null)
			return group.listFunctions();
		return Collections.emptyList();
	}

	protected void declareFunctionInGroup(Function function, String groupName) {
		FunctionGroup group = getOrCreateDefaultFunctionGroup(groupName);
		group.addFunction(function);
	}

	protected FunctionGroup getOrCreateDefaultFunctionGroup(String groupName) {
		if (groupName == null || groupName == "")
			groupName = UNDEFINED_GROUP_NAME;
		FunctionGroup group = this.functionGroups.get(groupName);
		if (group == null) {
			group = new DeffunctionGroup(groupName);
			this.functionGroups.put(groupName, group);
		}
		return group;
	}

	protected void declareFunctionInDefaultGroup(Function function) {
		FunctionGroup group = getOrCreateDefaultFunctionGroup(UNDEFINED_GROUP_NAME);
		group.addFunction(function);
	}
}
