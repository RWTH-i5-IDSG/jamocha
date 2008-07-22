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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Peter Lin
 * 
 * FunctionGroup is an organizational feature to group functions. For example,
 * one might want to group mathematic functions together. Or IO functions
 * together into a group.
 */
public abstract class FunctionGroup implements Serializable {

	protected String name = "undefined";

	protected String description = "none";

	protected List<Function> funcs = new ArrayList<Function>();

	public FunctionGroup() {
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	/**
	 * classes implementing the interface need to implement this method to
	 * create instances of the function and register them the rule engine.
	 * 
	 * @param engine
	 */
	public abstract void loadFunctions(FunctionMemory functionMem);

	/**
	 * A convienance method for listing the functions in a given group.
	 * 
	 * @return
	 */
	public final List<Function> listFunctions() {
		return funcs;
	}

	/**
	 * This method is for InterpretedFunctions (deffunctions) mostly. It will
	 * just add the Function to the FunctionGroup but not to the FunctionMemory.
	 * 
	 * @param function
	 *            The Function to add.
	 */
	public final void addFunction(Function function) {
		funcs.add(function);
		function.addToFunctionGroup(this);
	}

	/**
	 * This method adds the Functions to this Group and to the FunctionMemory.
	 * 
	 * @param functionMem
	 *            The FunctionMemory the Function should be added to.
	 * @param function
	 *            The Function to add.
	 */
	public final void addFunction(FunctionMemory functionMem, Function function) {
		functionMem.declareFunction(function);
		addFunction(function);
	}
}
