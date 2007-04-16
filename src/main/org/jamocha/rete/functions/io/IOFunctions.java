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
package org.jamocha.rete.functions.io;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jamocha.rete.Function;
import org.jamocha.rete.FunctionGroup;
import org.jamocha.rete.Rete;

/**
 * @author Peter Lin
 * 
 * IO Functions will initialize the IO related functions like printout, batch,
 * etc.
 */
public class IOFunctions implements FunctionGroup, Serializable {

	private static final long serialVersionUID = 1L;

	private List<Function> funcs = new ArrayList<Function>();

	public String getName() {
		return (IOFunctions.class.getSimpleName());
	}

	public void loadFunctions(Rete engine) {
		Batch batch = new Batch();
		engine.declareFunction(batch);
		funcs.add(batch);
		LoadFacts loadFacts = new LoadFacts();
		engine.declareFunction(loadFacts);
		funcs.add(loadFacts);
		Printout printout = new Printout();
		engine.declareFunction(printout);
		funcs.add(printout);
	}

	public List listFunctions() {
		return funcs;
	}

}
