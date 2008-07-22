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

package org.jamocha.engine.functions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jamocha.communication.logging.Logging;
import org.jamocha.engine.Engine;
import org.jamocha.engine.functions.adaptor.AdaptorFunctions;
import org.jamocha.engine.functions.compare.CompareFunctions;
import org.jamocha.engine.functions.datetime.DateTimeFunctions;
import org.jamocha.engine.functions.help.HelpFunctions;
import org.jamocha.engine.functions.io.IOFunctions;
import org.jamocha.engine.functions.java.JavaFunctions;
import org.jamocha.engine.functions.list.ListFunctions;
import org.jamocha.engine.functions.math.MathFunctions;
import org.jamocha.engine.functions.ruleengine.RuleEngineFunctions;
import org.jamocha.engine.functions.strings.StringFunctions;

/**
 * @author Sebastian Reinartz
 * 
 * FunctionMemoryImpl is a basic implementation of the FunctionMemory interface.
 * Holds Maps of functions and function groups.
 */
public class FunctionMemoryImpl implements FunctionMemory {

	private static final long serialVersionUID = 1L;

	protected Engine engine = null;

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

	private final Map<String, FunctionGroup> functionGroups = new HashMap<String, FunctionGroup>();

	private List<FunctionGroup> builtInFunctionGroups;
	private List<Function> builtInFunctions;

	public FunctionMemoryImpl(Engine engine) {
		super();
		this.engine = engine;
	}

	public void init() {
		builtInFunctionGroups = new ArrayList<FunctionGroup>();
		builtInFunctions = new ArrayList<Function>();
		registerStaticallyBuiltInFunctions();
		loadBuiltInFunctions();
	}

	public void registerBuildInFunctionGroup(FunctionGroup funcGroup) {
		builtInFunctionGroups.add(funcGroup);
	}

	public void registerBuildInFunction(Function func) {
		builtInFunctions.add(func);
	}

	private void registerStaticallyBuiltInFunctions() {
		builtInFunctionGroups.add(new IOFunctions());

		// load the math functions
		builtInFunctionGroups.add(new MathFunctions());

		// load the Compare functions
		builtInFunctionGroups.add(new CompareFunctions());

		// load the date/time functions
		builtInFunctionGroups.add(new DateTimeFunctions());

		// load the list functions
		builtInFunctionGroups.add(new ListFunctions());

		// load the database functions
		builtInFunctionGroups.add(new AdaptorFunctions());

		// load the engine relate functions like declaring rules, templates, etc
		builtInFunctionGroups.add(new RuleEngineFunctions());

		// load string functions
		builtInFunctionGroups.add(new StringFunctions());

		// load java functions
		builtInFunctionGroups.add(new JavaFunctions());

		// load java functions
		builtInFunctionGroups.add(new HelpFunctions());

		// Other builtin constructs
		builtInFunctions.add(new If());
		builtInFunctions.add(new LoopForCount());
		builtInFunctions.add(new Return());
		builtInFunctions.add(new While());
	}

	public void clear() {
		clearBuiltInFunctions();
		loadBuiltInFunctions();
	}

	public Function findFunction(String name) throws FunctionNotFoundException {
		Function result = functions.get(name);
		if (result == null)
			result = aliases.get(name);
		if (result == null) {
			Logging.logger(this.getClass()).warn(
					"Cannot find requested function '" + name + "'");
			throw new FunctionNotFoundException(name);
		}
		return result;
	}

	/**
	 * To explicitly deploy a custom function, call the method with an instance
	 * of the function
	 * 
	 * @param func
	 */
	public void declareFunction(Function func) {
		functions.put(func.getName(), func);
		List<String> aliases = func.getAliases();
		for (String alias : aliases)
			this.aliases.put(alias, func);
		if (func instanceof InterpretedFunction)
			declareFunctionInDefaultGroup(func);
	}

	public void declareFunction(Function func, String functionGroupName) {
		functions.put(func.getName(), func);
		List<String> aliases = func.getAliases();
		for (String alias : aliases)
			this.aliases.put(alias, func);
		if (func instanceof InterpretedFunction)
			declareFunctionInGroup(func, functionGroupName);
	}

	/**
	 * In some cases, we may want to declare a function under an alias. For
	 * example, Add can be alias as "+".
	 * 
	 * @param alias
	 * @param func
	 */
	// public void declareFunction(String alias, Function func) {
	// this.functions.put(alias, func);
	// if (func instanceof InterpretedFunction)
	// declareFunctionInDefaultGroup(func);
	// }
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
			Logging.logger(this.getClass()).warn(e);
			throw e;
		} catch (IllegalAccessException e) {
			Logging.logger(this.getClass()).warn(e);
		} catch (InstantiationException e) {
			Logging.logger(this.getClass()).warn(e);
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
			Logging.logger(this.getClass()).warn(e);
		} catch (IllegalAccessException e) {
			Logging.logger(this.getClass()).warn(e);
		} catch (InstantiationException e) {
			Logging.logger(this.getClass()).warn(e);
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
		for (FunctionGroup funcGroup : builtInFunctionGroups)
			declareFunctionGroup(funcGroup);
		for (Function func : builtInFunctions)
			declareFunction(func);
	}

	public void clearBuiltInFunctions() {
		functions.clear();
		functionGroups.clear();
		aliases.clear();
	}

	/**
	 * Returns a list of the function groups. If a function is not in a group,
	 * get the complete list of functions using getAllFunctions instead.
	 * 
	 * @return
	 */
	public Map<String, FunctionGroup> getFunctionGroups() {
		return functionGroups;
	}

	/**
	 * Returns a collection of the function instances
	 * 
	 * @return
	 */
	public Collection<Function> getAllFunctions() {
		return functions.values();
	}

	public List<Function> getFunctionsOfGroup(String name) {
		FunctionGroup group = functionGroups.get(name);
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
		FunctionGroup group = functionGroups.get(groupName);
		if (group == null) {
			group = new DeffunctionGroup(groupName);
			functionGroups.put(groupName, group);
		}
		return group;
	}

	protected void declareFunctionInDefaultGroup(Function function) {
		FunctionGroup group = getOrCreateDefaultFunctionGroup(UNDEFINED_GROUP_NAME);
		group.addFunction(function);
	}
}
