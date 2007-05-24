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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jamocha.rete.Function;
import org.jamocha.rete.FunctionGroup;
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
	protected Map<String,Function> functions = new HashMap<String, Function>();
	
	private DeffunctionGroup deffunctions = new DeffunctionGroup();
	
	private ArrayList<FunctionGroup>  functionGroups = new ArrayList<FunctionGroup>();
	
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
		return (Function) this.functions.get(name);
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
			this.deffunctions.addFunction(func);
		}
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
	}
	
	/**
	 * Method will create an instance of the function and declare it. Once a
	 * function is declared, it can be used. All custom functions must be
	 * declared before they can be used.
	 * 
	 * @param name
	 */
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
		functionGroup.loadFunctions(this);
	}
	
	protected void loadBuiltInFunctions() {
		// load the IO functions
		IOFunctions iof = new IOFunctions();
		functionGroups.add(iof);
		iof.loadFunctions(this);

		// load the math functions
		MathFunctions mathf = new MathFunctions();
		functionGroups.add(mathf);
		mathf.loadFunctions(this);

		// load the math functions
		CompareFunctions comparef = new CompareFunctions();
		functionGroups.add(comparef);
		comparef.loadFunctions(this);

		// load the date/time functions
		DateTimeFunctions datetimef = new DateTimeFunctions();
		functionGroups.add(datetimef);
		datetimef.loadFunctions(this);

		// load the list functions
		ListFunctions listf = new ListFunctions();
		functionGroups.add(listf);
		listf.loadFunctions(this);

		// load the database functions
		AdaptorFunctions databasef = new AdaptorFunctions();
		functionGroups.add(databasef);
		databasef.loadFunctions(this);

		// load the engine relate functions like declaring rules, templates, etc
		RuleEngineFunctions rulefs = new RuleEngineFunctions();
		functionGroups.add(rulefs);
		rulefs.loadFunctions(this);

		// load string functions
		StringFunctions strfs = new StringFunctions();
		functionGroups.add(strfs);
		strfs.loadFunctions(this);

		// load java functions
		JavaFunctions javafs = new JavaFunctions();
		functionGroups.add(javafs);
		javafs.loadFunctions(this);

		// load java functions
		HelpFunctions helpfs = new HelpFunctions();
		functionGroups.add(helpfs);
		helpfs.loadFunctions(this);

		// Other builtin constructs
		declareFunction(new If());
		declareFunction(new LoopForCount());
		declareFunction(new Return());
		declareFunction(new While());

		// add the group for deffunctions
		functionGroups.add(deffunctions);
	}

	public void clearBuiltInFunctions() {
		this.functions.clear();
	}
	
	
	/**
	 * Returns a list of the function groups. If a function is not in a group,
	 * get the complete list of functions using getAllFunctions instead.
	 * 
	 * @return
	 */
	public List getFunctionGroups() {
		return this.functionGroups;
	}

	/**
	 * Returns a collection of the function instances
	 * 
	 * @return
	 */
	public Collection getAllFunctions() {
		return this.functions.values();
	}

}
