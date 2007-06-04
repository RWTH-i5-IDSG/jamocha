/*
 * Copyright 2007 Alexander Wilden
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
package org.jamocha.rete.functions.help;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jamocha.rete.Function;
import org.jamocha.rete.FunctionGroup;
import org.jamocha.rete.functions.FunctionMemory;

/**
 * @author Alexander Wilden
 * 
 * Help Functions give additional information to the rule engine and the
 * functions and provides some examples.
 */
public class HelpFunctions implements FunctionGroup, Serializable {

	private static final long serialVersionUID = 1L;

	private List<Function> funcs = new ArrayList<Function>();

	public String getName() {
		return (HelpFunctions.class.getSimpleName());
	}

	public void loadFunctions(FunctionMemory functionMem) {

		Example example = new Example();
		functionMem.declareFunction(example);
		funcs.add(example);

		Usage usage = new Usage();
		functionMem.declareFunction(usage);
		funcs.add(usage);
	}

	public List listFunctions() {
		return funcs;
	}
	
	public void addFunction(Function function) {
		this.funcs.add(function);
	}

}
