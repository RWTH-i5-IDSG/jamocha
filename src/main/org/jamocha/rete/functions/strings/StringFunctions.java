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
package org.jamocha.rete.functions.strings;

import org.jamocha.rete.functions.FunctionGroup;
import org.jamocha.rete.functions.FunctionMemory;

/**
 * @author Peter Lin
 * 
 * This is the FunctionGroup for String-specific functions.
 */
public class StringFunctions extends FunctionGroup {

	private static final long serialVersionUID = 1L;

	public StringFunctions() {
		super();
		name = "StringFunctions";
		description = "This Group provides functions to modify Strings or access some of their properties.";
	}

	public void loadFunctions(FunctionMemory functionMem) {
		addFunction(functionMem, new StringCat());
		addFunction(functionMem, new StringCompare());
		addFunction(functionMem, new StringIndex());
		addFunction(functionMem, new StringLength());
		addFunction(functionMem, new StringLower());
		addFunction(functionMem, new StringReplace());
		addFunction(functionMem, new StringReplaceAll());
		addFunction(functionMem, new StringTrim());
		addFunction(functionMem, new StringUpper());
		addFunction(functionMem, new SubString());
	}

}