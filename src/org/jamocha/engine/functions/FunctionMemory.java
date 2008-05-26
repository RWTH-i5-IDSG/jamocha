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

package org.jamocha.engine.functions;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

/**
 * @author Sebastian Reinartz
 * 
 * Interface defining function memory
 */
public interface FunctionMemory extends Serializable {

	void init();

	void clear();

	public void registerBuildInFunctionGroup(FunctionGroup funcGroup);

	public void registerBuildInFunction(Function func);

	public void clearBuiltInFunctions();

	public Collection<Function> getFunctionsOfGroup(String name);

	public Function findFunction(String name) throws FunctionNotFoundException;

	public void declareFunction(Function func);

	public void declareFunction(Function func, String functionGroupName);

	// public void declareFunction(String alias, Function func);

	public void declareFunctionGroup(String name);

	public void declareFunction(String name) throws ClassNotFoundException;

	public void declareFunctionGroup(FunctionGroup functionGroup);

	public Map<String, FunctionGroup> getFunctionGroups();

	public Collection<Function> getAllFunctions();
}
