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
		AnyEq anyeq = new AnyEq();
		functionMem.declareFunction(anyeq);
		funcs.add(anyeq);

		Eq eq = new Eq();
		functionMem.declareFunction(eq);
		funcs.add(eq);

		Greater greater = new Greater();
		functionMem.declareFunction(greater);
		funcs.add(greater);

		GreaterOrEqual greaterOrEqual = new GreaterOrEqual();
		functionMem.declareFunction(greaterOrEqual);
		funcs.add(greaterOrEqual);

		Less less = new Less();
		functionMem.declareFunction(less);
		funcs.add(less);

		LessOrEqual lessOrEqual = new LessOrEqual();
		functionMem.declareFunction(lessOrEqual);
		funcs.add(lessOrEqual);

		Neq neq = new Neq();
		functionMem.declareFunction(neq);
		funcs.add(neq);

		functionMem.declareFunction(">", greater);
		functionMem.declareFunction(">=", greaterOrEqual);
		functionMem.declareFunction("<", less);
		functionMem.declareFunction("<=", lessOrEqual);
		functionMem.declareFunction("afterdate", greater);
		functionMem.declareFunction("beforedate", less);
	}

	public List listFunctions() {
		return funcs;
	}
}
