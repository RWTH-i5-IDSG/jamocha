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

package org.jamocha.engine.functions.io;

import org.jamocha.engine.functions.FunctionGroup;
import org.jamocha.engine.functions.FunctionMemory;

/**
 * @author Peter Lin
 * 
 * IO Functions will initialize the IO related functions like printout, batch,
 * etc.
 */
public class IOFunctions extends FunctionGroup {

	private static final long serialVersionUID = 0xBABABABABABABEL;

	public IOFunctions() {
		super();
		name = "IOFunctions";
		description = "This Group provides functions that give access to the filesystem or just print out data on the Shell.";
	}

	@Override
	public void loadFunctions(FunctionMemory functionMem) {
		addFunction(functionMem, new Batch());
		addFunction(functionMem, new Load());
		addFunction(functionMem, new LoadFacts());
		addFunction(functionMem, new Printout());
		addFunction(functionMem, new Dump());

	}
}
