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
package org.jamocha.rete.functions.java;

import java.util.ArrayList;
import java.util.List;

import org.jamocha.rete.Function;
import org.jamocha.rete.FunctionGroup;
import org.jamocha.rete.functions.FunctionMemory;

public class JavaFunctions implements FunctionGroup {

	private static final long serialVersionUID = 1L;

	private ArrayList<Function> funcs = new ArrayList<Function>();

	public String getName() {
		return (JavaFunctions.class.getSimpleName());
	}

	public void loadFunctions(FunctionMemory functionMem) {
		ClassnameResolver classnameResolver = new ClassnameResolver();

		Instanceof instanceOf = new Instanceof(classnameResolver);
		functionMem.declareFunction(instanceOf);
		funcs.add(instanceOf);

		LoadPackage loadPackage = new LoadPackage(classnameResolver);
		functionMem.declareFunction(loadPackage);
		funcs.add(loadPackage);

		Member member = new Member();
		functionMem.declareFunction(member);
		funcs.add(member);

		New newf = new New(classnameResolver);
		functionMem.declareFunction(newf);
		funcs.add(newf);
	}

	public List listFunctions() {
		return funcs;
	}
	
	public void addFunction(Function function) {
		this.funcs.add(function);
	}

}
