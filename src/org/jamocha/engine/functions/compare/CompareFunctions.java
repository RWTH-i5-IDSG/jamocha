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

package org.jamocha.engine.functions.compare;

import org.jamocha.engine.functions.FunctionGroup;
import org.jamocha.engine.functions.FunctionMemory;

public class CompareFunctions extends FunctionGroup {

	private static final long serialVersionUID = 1L;

	public CompareFunctions() {
		super();
		name = "CompareFunctions";
		description = "This Group provides functions to compare one value to others.";
	}

	@Override
	public void loadFunctions(FunctionMemory functionMem) {

		addFunction(functionMem, new AnyEq());
		addFunction(functionMem, new Eq());
		addFunction(functionMem, new Greater());
		addFunction(functionMem, new GreaterOrEqual());
		addFunction(functionMem, new Less());
		addFunction(functionMem, new LessOrEqual());
		addFunction(functionMem, new Neq());
	}
}
