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
import java.util.List;

import org.jamocha.rete.functions.FunctionMemory;

/**
 * @author Peter Lin
 *
 * FunctionGroup is an organizational feature to group functions. For example,
 * one might want to group mathematic functions together. Or IO functions together
 * into a group.
 */
public interface FunctionGroup extends Serializable {
	String getName();

	/**
	 * classes implementing the interface need to implement this method to
	 * create instances of the function and register them the rule engine.
	 * @param engine
	 */
	void loadFunctions(FunctionMemory functionMem);

	/**
	 * A convienance method for listing the functions in a given group.
	 * @return
	 */
	List listFunctions();
}
