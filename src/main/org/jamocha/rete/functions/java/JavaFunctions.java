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

import org.jamocha.rete.functions.FunctionGroup;
import org.jamocha.rete.functions.FunctionMemory;

public class JavaFunctions extends FunctionGroup {

	private static final long serialVersionUID = 1L;

	public JavaFunctions() {
		super();
		name = "JavaFunctions";
		description = "This Group provides Functions to access parts of the Java-API.";
	}

	public void loadFunctions(FunctionMemory functionMem) {
		ClassnameResolver classnameResolver = new ClassnameResolver();

		addFunction(functionMem, new Instanceof(classnameResolver));
		addFunction(functionMem, new LoadPackage(classnameResolver));
		addFunction(functionMem, new Member());
		addFunction(functionMem, new New(classnameResolver));

	}

}
