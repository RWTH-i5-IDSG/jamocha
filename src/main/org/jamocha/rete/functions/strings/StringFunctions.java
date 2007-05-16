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

import java.util.ArrayList;
import java.util.List;

import org.jamocha.rete.Function;
import org.jamocha.rete.FunctionGroup;
import org.jamocha.rete.functions.FunctionMemory;

/**
 * @author Peter Lin
 * 
 * This is the FunctionGroup for String-specific functions.
 */
public class StringFunctions implements FunctionGroup {
	
	private static final long serialVersionUID = 1L;
	
	private List<Function> funcs = new ArrayList<Function>();
	
	public String getName() {
		return (StringFunctions.class.getSimpleName());
	}

	public void loadFunctions(FunctionMemory functionMem) {
		StringCat stringCat = new StringCat();
		functionMem.declareFunction(stringCat);
		funcs.add(stringCat);
		
		StringCompare stringCompare = new StringCompare();
		functionMem.declareFunction(stringCompare);
		funcs.add(stringCompare);
		
		StringIndex stringIndex = new StringIndex();
		functionMem.declareFunction(stringIndex);
		funcs.add(stringIndex);
		
		StringLength stringLength = new StringLength();
		functionMem.declareFunction(stringLength);
		funcs.add(stringLength);
		
		StringLower stringLower = new StringLower();
		functionMem.declareFunction(stringLower);
		funcs.add(stringLower);
		
		StringReplace stringReplace = new StringReplace();
		functionMem.declareFunction(stringReplace);
		funcs.add(stringReplace);
		
		StringTrim stringTrim = new StringTrim();
		functionMem.declareFunction(stringTrim);
		funcs.add(stringTrim);
		
		StringUpper stringUpper = new StringUpper();
		functionMem.declareFunction(stringUpper);
		funcs.add(stringUpper);
		
		SubString subString = new SubString();
		functionMem.declareFunction(subString);
		funcs.add(subString);
	}

	public List listFunctions() {
		return funcs;
	}

}