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
package org.jamocha.rete.functions.compare;

import java.util.ArrayList;
import java.util.List;

import org.jamocha.rete.Function;
import org.jamocha.rete.FunctionGroup;
import org.jamocha.rete.Rete;

public class CompareFunctions implements FunctionGroup {

	private static final long serialVersionUID = 1L;

	private ArrayList<Function> funcs = new ArrayList<Function>();

	public String getName() {
		return (CompareFunctions.class.getSimpleName());
	}

	public void loadFunctions(Rete engine) {
		EqFunction eqf = new EqFunction();
		engine.declareFunction(eqf);
		funcs.add(eqf);
		Greater gr = new Greater();
		engine.declareFunction(gr);
		funcs.add(gr);
		GreaterOrEqual gre = new GreaterOrEqual();
		engine.declareFunction(gre);
		funcs.add(gre);
		Less le = new Less();
		engine.declareFunction(le);
		funcs.add(le);
		LessOrEqual leoe = new LessOrEqual();
		engine.declareFunction(leoe);
		funcs.add(leoe);
		NeqFunction neq = new NeqFunction();
		engine.declareFunction(neq);
		funcs.add(neq);
		engine.declareFunction(">", gr);
		engine.declareFunction(">=", gre);
		engine.declareFunction("<", le);
		engine.declareFunction("<=", leoe);
		engine.declareFunction("afterdate", gr);
		engine.declareFunction("beforedate", le);
	}

	public List listFunctions() {
		return funcs;
	}
}
