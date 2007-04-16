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
package org.jamocha.rete.functions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jamocha.rete.Function;
import org.jamocha.rete.FunctionGroup;
import org.jamocha.rete.Rete;
import org.jamocha.rete.functions.ruleengine.LoadFacts;

/**
 * @author Peter Lin
 * 
 * IO Functions will initialize the IO related functions like printout, batch,
 * etc.
 */
public class IOFunctions implements FunctionGroup, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<Function> funcs = new ArrayList<Function>();

	/**
	 * 
	 */
	public IOFunctions() {
		super();
	}

	public String getName() {
		return (IOFunctions.class.getSimpleName());
	}

	public void loadFunctions(Rete engine) {
		BatchFunction b = new BatchFunction();
		engine.declareFunction(b);
		funcs.add(b);
		LoadFacts load = new LoadFacts();
		engine.declareFunction(load);
		funcs.add(load);
		PrintFunction pf = new PrintFunction();
		engine.declareFunction(pf);
		funcs.add(pf);
	}

	public List listFunctions() {
		return funcs;
	}

}
