/*
 * Copyright 2002-2006 Peter Lin, 2007 Alexander Wilden
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
package org.jamocha.rete.functions.compare;

import java.util.ArrayList;
import java.util.List;

import org.jamocha.rete.Function;
import org.jamocha.rete.FunctionGroup;
import org.jamocha.rete.functions.FunctionMemory;

public class CompareFunctions implements FunctionGroup {

	private static final long serialVersionUID = 1L;

	private ArrayList<Function> funcs = new ArrayList<Function>();

	public String getName() {
		return (CompareFunctions.class.getSimpleName());
	}

	public void loadFunctions(FunctionMemory functionMem) {
		functionMem.declareFunction(AnyEq.getInstance());
		funcs.add(AnyEq.getInstance());

		functionMem.declareFunction(Eq.getInstance());
		funcs.add(Eq.getInstance());

		functionMem.declareFunction(Greater.getInstance());
		funcs.add(Greater.getInstance());

		functionMem.declareFunction(GreaterOrEqual.getInstance());
		funcs.add(GreaterOrEqual.getInstance());

		functionMem.declareFunction(Less.getInstance());
		funcs.add(Less.getInstance());

		functionMem.declareFunction(LessOrEqual.getInstance());
		funcs.add(LessOrEqual.getInstance());

		functionMem.declareFunction(Neq.getInstance());
		funcs.add(Neq.getInstance());
	}

	public List<Function> listFunctions() {
		return funcs;
	}

	public void addFunction(Function function) {
		this.funcs.add(function);
	}
}
